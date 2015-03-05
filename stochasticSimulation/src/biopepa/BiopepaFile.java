package biopepa;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ssa.CTMCModel;
import ssa.CTMCReaction;
import uk.ac.ed.inf.biopepa.core.BioPEPA;
import uk.ac.ed.inf.biopepa.core.BioPEPAException;
import uk.ac.ed.inf.biopepa.core.compiler.CompiledDynamicComponent;
import uk.ac.ed.inf.biopepa.core.compiler.CompiledExpression;
import uk.ac.ed.inf.biopepa.core.compiler.CompiledExpressionVisitor;
import uk.ac.ed.inf.biopepa.core.compiler.CompiledFunction;
import uk.ac.ed.inf.biopepa.core.compiler.CompiledNumber;
import uk.ac.ed.inf.biopepa.core.compiler.CompiledOperatorNode;
import uk.ac.ed.inf.biopepa.core.compiler.CompiledSystemVariable;
import uk.ac.ed.inf.biopepa.core.compiler.ComponentNode;
import uk.ac.ed.inf.biopepa.core.compiler.ModelCompiler;
import uk.ac.ed.inf.biopepa.core.dom.DoNothingVisitor;
import uk.ac.ed.inf.biopepa.core.dom.Model;
import uk.ac.ed.inf.biopepa.core.dom.Statement;
import uk.ac.ed.inf.biopepa.core.dom.VariableDeclaration;
import uk.ac.ed.inf.biopepa.core.dom.internal.ParserException;
import uk.ac.ed.inf.biopepa.core.sba.ExperimentLine;
import uk.ac.ed.inf.biopepa.core.sba.SBAComponentBehaviour;
import uk.ac.ed.inf.biopepa.core.sba.SBAModel;
import uk.ac.ed.inf.biopepa.core.sba.SBAReaction;
import expr.ArithmeticBinaryExpr;
import expr.ArithmeticBinaryOperator;
import expr.ArithmeticConstant;
import expr.ArithmeticExpression;
import expr.ArithmeticFunction;
import expr.ArithmeticFunctionType;
import expr.Context;
import expr.Variable;

final public class BiopepaFile {

	private Model astModel;

	public BiopepaFile(String filename) throws IOException {
		String source = readFile(filename);
		try {
			astModel = BioPEPA.parse(source);
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// extractParameters(astModel);
	}

	static void extractParameters(Model model) {
		ArrayList<String> parameters = new ArrayList<String>();
		for (Statement statement : model.statements())
			
			if (statement instanceof VariableDeclaration) {
				VariableDeclaration dec = (VariableDeclaration) statement;
				if (dec.getKind() == VariableDeclaration.Kind.VARIABLE) {
					parameters.add(dec.getName().getIdentifier());
				}
			}
		
		// well... continue with that
	}

	public final boolean containsVariable(String var) {
		try {
			NameCollector v = new NameCollector(var);
			astModel.accept(v);
			return v.isNameFound();
		} catch (BioPEPAException e) {
			e.printStackTrace();
		}
		return false;
	}

	public final CTMCModel getModel() {
		ModelCompiler mc = BioPEPA.compile(astModel);
		mc.compile();
		SBAModel sbaModel = BioPEPA.generateSBA(mc);
		return sba2ctmc(sbaModel);
	}

	public final CTMCModel getModel(String[] names, double[] values) {
		final int dim = names.length;
		final ExperimentLine el = new ExperimentLine("");
		for (int i = 0; i < dim; i++)
			el.addRateValue(names[i], values[i]);
		try {
			el.applyToAst(astModel);
		} catch (BioPEPAException e) {
			e.printStackTrace();
		}
		ModelCompiler mc = BioPEPA.compile(astModel);
		mc.compile();
		SBAModel sbaModel = BioPEPA.generateSBA(mc);
		return sba2ctmc(sbaModel);
	}

	private final static CTMCModel sba2ctmc(SBAModel sbaModel) {

		final ComponentNode[] components = sbaModel.getComponents();
		final Map<String, Variable> componentMap;
		componentMap = new HashMap<String, Variable>(components.length);
		final Context context = new Context();
		final Variable[] variables = new Variable[components.length];
		final int[] init = new int[components.length];
		for (int i = 0; i < components.length; i++) {
			variables[i] = new Variable(components[i].getName(), context);
			init[i] = (int) components[i].getCount();
			componentMap.put(components[i].getName(), variables[i]);
		}

		SBAReaction[] sbaReactions = sbaModel.getReactions();
		CTMCReaction[] reactions = new CTMCReaction[sbaReactions.length];
		for (int i = 0; i < sbaReactions.length; i++) {
			ExpressionBuilder visitor = new ExpressionBuilder(sbaModel,
					sbaReactions[i], componentMap);
			sbaReactions[i].getRate().accept(visitor);
			ArithmeticExpression rateExpression = visitor.getResult();

			reactions[i] = new CTMCReaction(rateExpression);
			for (SBAComponentBehaviour comp : sbaReactions[i].getReactants())
				reactions[i].addReactant(componentMap.get(comp.getName()),
						comp.getStoichiometry());
			for (SBAComponentBehaviour comp : sbaReactions[i].getProducts())
				reactions[i].addProduct(componentMap.get(comp.getName()),
						comp.getStoichiometry());
		}

		return new CTMCModel(context, reactions, init);
	}

	static private class ExpressionBuilder extends CompiledExpressionVisitor {

		private ArithmeticExpression result;
		final private SBAModel sbaModel;
		final private SBAReaction sbaReaction;
		final private Map<String, Variable> map;

		public ExpressionBuilder(SBAModel sbaModel, SBAReaction sbaReaction,
				Map<String, Variable> map) {
			this.sbaModel = sbaModel;
			this.sbaReaction = sbaReaction;
			this.map = map;
		}

		public ArithmeticExpression getResult() {
			return result;
		}

		@Override
		public boolean visit(CompiledDynamicComponent arg0) {
			String name = arg0.getName();
			if (sbaModel.containsComponent(name)) {
				result = map.get(name);
				return false;
			}
			if (sbaModel.containsVariable(name)) {
				CompiledExpression varExp = sbaModel.getDynamicExpression(name);
				varExp.accept(this);
				return false;
			} else {
				throw new IllegalStateException();
			}
		}

		@Override
		public boolean visit(CompiledFunction arg0) {

			if (arg0.getFunction().isRateLaw()) {
				switch (arg0.getFunction()) {
				case fMA:
					arg0.getArguments().get(0).accept(this);
					List<SBAComponentBehaviour> reactants = sbaReaction
							.getReactants();
					for (SBAComponentBehaviour reactant : reactants) {
						Variable var = map.get(reactant.getName());
						ArithmeticConstant st = new ArithmeticConstant(
								reactant.getStoichiometry());
						ArithmeticBinaryExpr factor = new ArithmeticBinaryExpr(
								ArithmeticBinaryOperator.POW, var, st);
						result = new ArithmeticBinaryExpr(
								ArithmeticBinaryOperator.MULT, result, factor);
					}
					break;
				case fMM:
					throw new IllegalStateException(
							"Michaelis–Menten kinetics are currenly not supported!");

				default:
					throw new IllegalStateException();
				}
			}

			else if (arg0.getFunction().args() == 1) {
				arg0.getArguments().get(0).accept(this);
				ArithmeticExpression argument = result;
				switch (arg0.getFunction()) {
				case LOG:
					result = new ArithmeticFunction(ArithmeticFunctionType.LOG,
							argument);
					break;
				case EXP:
					result = new ArithmeticFunction(ArithmeticFunctionType.EXP,
							argument);
					break;
				case H:
					result = new ArithmeticFunction(ArithmeticFunctionType.H,
							argument);
					break;
				case FLOOR:
					result = new ArithmeticFunction(
							ArithmeticFunctionType.FLOOR, argument);
					break;
				case CEILING:
					result = new ArithmeticFunction(
							ArithmeticFunctionType.CEIL, argument);
					break;
				case TANH:
					result = new ArithmeticFunction(
							ArithmeticFunctionType.TANH, argument);
					break;
				default:
					throw new IllegalStateException();
				}
			} else {
				throw new IllegalStateException();
			}

			return false;
		}

		@Override
		public boolean visit(CompiledNumber arg0) {
			result = new ArithmeticConstant(arg0.getNumber().doubleValue());
			return false;
		}

		@Override
		public boolean visit(CompiledOperatorNode arg0) {
			arg0.getLeft().accept(this);
			ArithmeticExpression left = result;
			arg0.getRight().accept(this);
			ArithmeticExpression right = result;
			switch (arg0.getOperator()) {
			case PLUS:
				result = new ArithmeticBinaryExpr(
						ArithmeticBinaryOperator.PLUS, left, right);
				break;
			case MINUS:
				result = new ArithmeticBinaryExpr(
						ArithmeticBinaryOperator.MINUS, left, right);
				break;
			case DIVIDE:
				result = new ArithmeticBinaryExpr(
						ArithmeticBinaryOperator.DIVIDE, left, right);
				break;
			case MULTIPLY:
				result = new ArithmeticBinaryExpr(
						ArithmeticBinaryOperator.MULT, left, right);
				break;
			case POWER:
				result = new ArithmeticBinaryExpr(ArithmeticBinaryOperator.POW,
						left, right);
				break;
			default:
				throw new IllegalStateException();
			}
			return false;
		}

		@Override
		public boolean visit(CompiledSystemVariable arg0) {
			throw new IllegalStateException(
					"The \'time\' system variable is currently unsupported!");
		}

	}

	static private class NameCollector extends DoNothingVisitor {

		final private String name;
		private boolean nameFound = false;

		public NameCollector(String name) {
			this.name = name;
		}

		public boolean isNameFound() {
			return nameFound;
		}

		@Override
		public boolean visit(Model model) throws BioPEPAException {
			for (Statement statement : model.statements())
				statement.accept(this);
			return false;
		}

		@Override
		public boolean visit(VariableDeclaration variableDeclaration)
				throws BioPEPAException {
			if (variableDeclaration.getKind() == VariableDeclaration.Kind.VARIABLE)
				if (name.equals(variableDeclaration.getName().getIdentifier()))
					nameFound = true;
			return false;
		}

	}

	private static final String readFile(String filename) throws IOException {
		final FileInputStream input = new FileInputStream(filename);
		final byte[] fileData = new byte[input.available()];
		input.read(fileData);
		input.close();
		return new String(fileData);
	}
}
