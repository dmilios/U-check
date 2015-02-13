package parsers;

import mitl.*;
import expr.*;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

public class MitlFactory {

	private MitlPropertiesList propertiesList = new MitlPropertiesList();
	private Context modelNamespace;

	public MitlFactory(Context modelNamespace) {
		this.modelNamespace = modelNamespace;
	}

	public MitlPropertiesList constructProperties(String text) {
		propertiesList = new MitlPropertiesList();

		MiTLLexer lex = new MiTLLexer(new ANTLRStringStream(text));
		CommonTokenStream tokens = new CommonTokenStream(lex);
		MiTLParser parser = new MiTLParser(tokens);
		MiTLParser.eval_return r = null;
		try {
			r = parser.eval();
		} catch (RecognitionException e) {
			e.printStackTrace();
		}
		Tree ast = (Tree) r.getTree();

		if (ast.getType() == 0)
			constructModel(ast);
		else if (ast.getType() == MiTLParser.CONST)
			constructDeclaration(ast);
		else
			constructMiTLStatement(ast);

		return propertiesList;
	}

	public void constructDeclaration(String text) throws RecognitionException {
		MiTLLexer lex = new MiTLLexer(new ANTLRStringStream(text));
		CommonTokenStream tokens = new CommonTokenStream(lex);
		MiTLParser parser = new MiTLParser(tokens);
		MiTLParser.declaration_return r = parser.declaration();
		Tree ast = (Tree) r.getTree();
		constructDeclaration(ast);
	}

	public boolean constructProperty(String text) throws RecognitionException {
		MiTLLexer lex = new MiTLLexer(new ANTLRStringStream(text));
		CommonTokenStream tokens = new CommonTokenStream(lex);
		MiTLParser parser = new MiTLParser(tokens);
		MiTLParser.exprOR_return r = parser.exprOR();		
		Tree ast = (Tree) r.getTree();
		constructMiTLStatement(ast);
		
		// TODO: return the error message
		return parser.getNumberOfSyntaxErrors() == 0;
	}

	//

	// ======================================================

	//

	private void constructModel(Tree astRoot) {
		for (int i = 0; i < astRoot.getChildCount(); i++) {
			final Tree statement = astRoot.getChild(i);
			final int type = statement.getType();
			if (type == MiTLParser.CONST)
				constructDeclaration(statement);
			else
				constructMiTLStatement(statement);
		}
	}

	private void constructDeclaration(Tree node) {
		final int type = node.getChild(0).getType();
		final String name = node.getChild(1).getText();
		if (modelNamespace.containsVariable(name))
			throw new IllegalStateException("Variable already declared!");

		switch (type) {
		case MiTLParser.DOUBLE:
			propertiesList.addConstant(name, "double");
			break;
		case MiTLParser.INT:
			propertiesList.addConstant(name, "int");
			break;
		case MiTLParser.BOOL:
			propertiesList.addConstant(name, "bool");
			break;
		default:
			return;
		}
		if (node.getChildCount() == 3)
			propertiesList.setConstant(name, node.getChild(2).getText());
	}

	private void constructMiTLStatement(Tree node) {
		MiTL mitl = constructMiTL(node);
		propertiesList.addProperty(mitl);
	}

	//

	// ======================================================

	//

	private RelationalExpression constructRelational(Tree node) {
		final int type = node.getType();
		final ArithmeticExpression l = constructExpression(node.getChild(0));
		final ArithmeticExpression r = constructExpression(node.getChild(1));
		switch (type) {
		case MiTLParser.EQ:
			return new RelationalExpression(RelationalOperator.EQ, l, r);
		case MiTLParser.NEQ:
			return new RelationalExpression(RelationalOperator.NEQ, l, r);
		case MiTLParser.GT:
			return new RelationalExpression(RelationalOperator.GT, l, r);
		case MiTLParser.GE:
			return new RelationalExpression(RelationalOperator.GE, l, r);
		case MiTLParser.LT:
			return new RelationalExpression(RelationalOperator.LT, l, r);
		case MiTLParser.LE:
			return new RelationalExpression(RelationalOperator.LE, l, r);
		default:
			return null;
		}
	}

	private ArithmeticExpression constructExpression(Tree node) {
		if (node == null)
			return null;
		final int type = node.getType();
		switch (type) {
		case MiTLParser.INTEGER:
		case MiTLParser.FLOAT:
			return new ArithmeticConstant(Double.parseDouble(node.getText()));

		case MiTLParser.ID:
			if (node.getChildCount() == 0) {
				final String name = node.getText();
				Variable var = propertiesList.getConstant(name);
				if (var != null)
					return var;
				return modelNamespace.getVariable(name);
			} else
				return constructFunction(node);

		case MiTLParser.PLUS:
		case MiTLParser.MULT:
		case MiTLParser.DIV:
			return constructBinExpression(node);

		case MiTLParser.MINUS:
			final int childCount = node.getChildCount();
			if (childCount == 1)
				return new ArithmeticUnaryExpr(ArithmeticUnaryOperator.UMINUS,
						constructExpression(node.getChild(0)));
			else if (childCount == 2)
				return constructBinExpression(node);
		}
		return null;
	}

	private ArithmeticExpression constructFunction(Tree node) {
		// TODO: implement functions
		return null;
	}

	private ArithmeticExpression constructBinExpression(Tree node) {
		final int type = node.getType();
		final ArithmeticExpression l = constructExpression(node.getChild(0));
		final ArithmeticExpression r = constructExpression(node.getChild(1));
		switch (type) {
		case MiTLParser.PLUS:
			return new ArithmeticBinaryExpr(ArithmeticBinaryOperator.PLUS, l, r);
		case MiTLParser.MINUS:
			return new ArithmeticBinaryExpr(ArithmeticBinaryOperator.MINUS, l,
					r);
		case MiTLParser.MULT:
			return new ArithmeticBinaryExpr(ArithmeticBinaryOperator.MULT, l, r);
		case MiTLParser.DIV:
			return new ArithmeticBinaryExpr(ArithmeticBinaryOperator.DIVIDE, l,
					r);
		default:
			return null;
		}
	}

	private MiTL constructMiTL(Tree node) {
		final int type = node.getType();
		switch (type) {
		case MiTLParser.U:
			return constructUntil(node);
		case MiTLParser.F:
			return constructFinally(node);
		case MiTLParser.G:
			return constructGlobally(node);

		case MiTLParser.AND:
			return new MitlConjunction(constructMiTL(node.getChild(0)),
					constructMiTL(node.getChild(1)));
		case MiTLParser.OR:
			return new MitlDisjunction(constructMiTL(node.getChild(0)),
					constructMiTL(node.getChild(1)));
		case MiTLParser.NOT:
			return new MitlNegation(constructMiTL(node.getChild(0)));

		case MiTLParser.TRUE:
			return new MitlTrue();
		case MiTLParser.FALSE:
			return new MitlFalse();
		default:
			return new MitlAtomic(constructRelational(node));
		}
	}

	private MitlUntil constructUntil(Tree node) {
		final double t1;
		final double t2;
		final MiTL f1 = constructMiTL(node.getChild(0));
		final Tree timebound = node.getChild(1);
		if (timebound.getType() == MiTLParser.COMMA) {
			t1 = constructExpression(timebound.getChild(0)).evaluate();
			t2 = constructExpression(timebound.getChild(1)).evaluate();
		} else {
			t1 = 0;
			t2 = constructExpression(timebound).evaluate();
		}
		final MiTL f2 = constructMiTL(node.getChild(2));
		return new MitlUntil(f1, t1, t2, f2);
	}

	private MitlFinally constructFinally(Tree node) {
		final double t1;
		final double t2;
		final Tree timebound = node.getChild(0);
		if (timebound.getType() == MiTLParser.COMMA) {
			t1 = constructExpression(timebound.getChild(0)).evaluate();
			t2 = constructExpression(timebound.getChild(1)).evaluate();
		} else {
			t1 = 0;
			t2 = constructExpression(timebound).evaluate();
		}
		final MiTL f = constructMiTL(node.getChild(1));
		return new MitlFinally(t1, t2, f);
	}

	private MitlGlobally constructGlobally(Tree node) {
		final double t1;
		final double t2;
		final Tree timebound = node.getChild(0);
		if (timebound.getType() == MiTLParser.COMMA) {
			t1 = constructExpression(timebound.getChild(0)).evaluate();
			t2 = constructExpression(timebound.getChild(1)).evaluate();
		} else {
			t1 = 0;
			t2 = constructExpression(timebound).evaluate();
		}
		final MiTL f = constructMiTL(node.getChild(1));
		return new MitlGlobally(t1, t2, f);

	}
}
