package ucheck.prism;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import parser.Values;
import parser.ast.Command;
import parser.ast.Declaration;
import parser.ast.Expression;
import parser.ast.Module;
import parser.ast.ModulesFile;
import parser.ast.Update;
import parser.ast.Updates;
import prism.PrismLangException;
import ssa.CTMCModel;
import ssa.CTMCReaction;
import expr.ArithmeticBinaryExpr;
import expr.ArithmeticBinaryOperator;
import expr.ArithmeticExpression;
import expr.Context;
import expr.Variable;

public final class Prism2PCTMC {

	private Context variables;
	private Context constants;

	public CTMCModel extractPCTMC(ModulesFile modules)
			throws PrismLangException {

		// Prism models may have undefined constants; define them here
		final Values valuesObject = new Values();
		for (String name : modules.getConstantList().getUndefinedConstants())
			valuesObject.setValue(name, 0);
		modules.setUndefinedConstants(valuesObject);

		variables = extractCtmcVariables(modules);
		constants = extractCtmcConstants(modules);

		final int[] initState = new int[variables.getVariables().length];
		for (int i = 0; i < initState.length; i++)
			initState[i] = (int) variables.getValue(i);

		final CTMCReaction[] react = extractReactions(modules);
		return new CTMCModel(variables, constants, react, initState);
	}

	private Context extractCtmcVariables(ModulesFile modules)
			throws PrismLangException {
		final Vector<String> names = modules.getAllVars();
		final int n = names.size();
		final Context context = new Context();
		for (int i = 0; i < n; i++) {
			final Variable var = new Variable(names.get(i), context);
			final int index = modules.getVarIndex(names.get(i));
			final Declaration dec = modules.getVarDeclaration(index);
			int init = dec.getStart().evaluateInt(modules.getConstantValues());
			context.setValue(var, init);
		}
		return context;
	}

	private Context extractCtmcConstants(ModulesFile modules)
			throws PrismLangException {

		final Vector<String> names = modules.getAllConstants();
		final int n = names.size();
		final Context context = new Context();
		for (int i = 0; i < n; i++) {
			final String s = names.get(i);
			final Variable var = new Variable(s, context);
			final double val = modules.getConstantValues().getDoubleValueOf(s);
			context.setValue(var, val);
		}
		return context;
	}

	private ArithmeticExpression extractArithmeticExpr(Expression prismExpr)
			throws PrismLangException {
		ArithmeticExprBuidler v = new ArithmeticExprBuidler(variables,
				constants);
		prismExpr.accept(v);
		return v.getExpression();
	}

	private Map<String, Integer> extractStoichiometry(Update update)
			throws PrismLangException {
		final int n = update.getNumElements();
		final Map<String, Integer> map = new HashMap<>(n);
		for (int i = 0; i < n; i++) {
			String name = update.getVar(i);
			Expression expr = update.getExpression(i);
			IncrementExprEvaluator v = new IncrementExprEvaluator(name);
			expr.accept(v);
			map.put(name, v.getIncrement());
		}
		return map;
	}

	private CTMCReaction[] extractReactions(ModulesFile modules)
			throws PrismLangException {
		final Map<String, Vector<CTMCReaction>> reactionMap = new HashMap<>();

		int localActions = 0;
		for (int i = 0; i < modules.getNumModules(); i++) {
			final Module module = modules.getModule(i);
			final List<Command> commands = module.getCommands();
			for (final Command command : commands) {

				String actionName = command.getSynch();
				if (actionName.isEmpty()) // local action; set unique name
					actionName = "local_action" + localActions++;

				final Updates updates = command.getUpdates();
				final int choices = updates.getNumUpdates();
				Map<String, Integer> stMap;

				if (choices == 1) {
					if (!reactionMap.containsKey(actionName)) {
						Expression prob = updates.getProbability(0);
						ArithmeticExpression law = extractArithmeticExpr(prob);
						Vector<CTMCReaction> vec = new Vector<>();
						reactionMap.put(actionName, vec);
						CTMCReaction react = new CTMCReaction(law);
						vec.add(react);

						stMap = extractStoichiometry(updates.getUpdate(0));
						for (Entry<String, Integer> pair : stMap.entrySet()) {
							final int st = pair.getValue();
							final String v = pair.getKey();
							if (st <= 0)
								react.addReactant(variables.getVariable(v), -st);
							else
								react.addProduct(variables.getVariable(v), st);
						}

					} else {
						Vector<CTMCReaction> vec = reactionMap.get(actionName);
						Vector<CTMCReaction> original = new Vector<>(vec);
						vec.clear();
						Expression p = updates.getProbability(0);
						for (CTMCReaction r : original) {
							ArithmeticExpression law1 = extractArithmeticExpr(p);
							ArithmeticExpression law2 = r.getRateExpression();
							ArithmeticBinaryExpr law = new ArithmeticBinaryExpr(
									ArithmeticBinaryOperator.MULT, law1, law2);
							CTMCReaction react = new CTMCReaction(law);
							vec.add(react);
							for (Variable var : r.getReactants())
								react.addReactant(var,
										r.getReactantStoichiometry(var));
							for (Variable var : r.getProducts())
								react.addProduct(var,
										r.getProductStoichiometry(var));

							stMap = extractStoichiometry(updates.getUpdate(0));
							for (Entry<String, Integer> pair : stMap.entrySet()) {
								final int st = pair.getValue();
								final String v = pair.getKey();
								if (st <= 0)
									react.addReactant(variables.getVariable(v),
											-st);
								else
									react.addProduct(variables.getVariable(v),
											st);
							}
						}
					}
				}

				if (choices > 1) {
					if (!reactionMap.containsKey(actionName)) {
						Vector<CTMCReaction> vec = new Vector<>();
						reactionMap.put(actionName, vec);
						for (int choice = 0; choice < choices; choice++) {
							Expression prob = updates.getProbability(choice);
							ArithmeticExpression law = extractArithmeticExpr(prob);
							CTMCReaction react = new CTMCReaction(law);
							vec.add(react);

							stMap = extractStoichiometry(updates
									.getUpdate(choice));
							for (Entry<String, Integer> pair : stMap.entrySet()) {
								final int st = pair.getValue();
								final String v = pair.getKey();
								if (st <= 0)
									react.addReactant(variables.getVariable(v),
											-st);
								else
									react.addProduct(variables.getVariable(v),
											st);
							}
						}

					} else {
						Vector<CTMCReaction> vec = reactionMap.get(actionName);
						Vector<CTMCReaction> original = new Vector<>(vec);
						vec.clear();
						for (int choice = 0; choice < choices; choice++) {
							Expression p = updates.getProbability(choice);
							for (CTMCReaction r : original) {
								ArithmeticExpression l1 = extractArithmeticExpr(p);
								ArithmeticExpression l2 = r.getRateExpression();
								ArithmeticBinaryExpr law = new ArithmeticBinaryExpr(
										ArithmeticBinaryOperator.MULT, l1, l2);
								CTMCReaction react = new CTMCReaction(law);
								vec.add(react);
								for (Variable var : r.getReactants())
									react.addReactant(var,
											r.getReactantStoichiometry(var));
								for (Variable var : r.getProducts())
									react.addProduct(var,
											r.getProductStoichiometry(var));
								// vec.add(aName + "_" + choice);

								stMap = extractStoichiometry(updates
										.getUpdate(choice));
								for (Entry<String, Integer> pair : stMap
										.entrySet()) {
									final int st = pair.getValue();
									final String v = pair.getKey();
									if (st <= 0)
										react.addReactant(
												variables.getVariable(v), -st);
									else
										react.addProduct(
												variables.getVariable(v), st);
								}
							}
						}
					}
				}

				// do something with this in the future
				command.getGuard();
			}
		}

		Vector<String> keys = new Vector<>(reactionMap.keySet());
		Collections.sort(keys);
		Vector<CTMCReaction> reactions = new Vector<>();
		for (String key : keys) {
			Vector<CTMCReaction> vec = reactionMap.get(key);
			for (CTMCReaction r : vec)
				reactions.add(r);
		}

		final CTMCReaction[] array = new CTMCReaction[reactions.size()];
		reactions.toArray(array);
		return array;
	}

}
