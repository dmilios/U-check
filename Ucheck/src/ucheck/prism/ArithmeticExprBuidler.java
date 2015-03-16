package ucheck.prism;

import expr.ArithmeticBinaryExpr;
import expr.ArithmeticBinaryOperator;
import expr.ArithmeticConstant;
import expr.ArithmeticExpression;
import expr.Context;
import parser.ast.ASTElement;
import parser.ast.ExpressionBinaryOp;
import parser.ast.ExpressionConstant;
import parser.ast.ExpressionLiteral;
import parser.ast.ExpressionUnaryOp;
import parser.ast.ExpressionVar;
import parser.visitor.ASTTraverse;
import prism.PrismLangException;

public class ArithmeticExprBuidler extends ASTTraverse {

	private ArithmeticExpression expr;
	final private Context variables;
	final private Context constants;

	public ArithmeticExprBuidler(Context variables, Context constants) {
		this.variables = variables;
		this.constants = constants;
	}

	public ArithmeticExpression getExpression() {
		return expr;
	}

	@Override
	public void defaultVisitPost(ASTElement e) throws PrismLangException {
	}

	@Override
	public void visitPost(ExpressionLiteral e) throws PrismLangException {
		expr = new ArithmeticConstant(Double.parseDouble(e.getValue()
				.toString()));
	}

	@Override
	public void visitPost(ExpressionVar e) throws PrismLangException {
		expr = variables.getVariable(e.getName());
	}

	@Override
	public void visitPost(ExpressionConstant e) throws PrismLangException {
		expr = constants.getVariable(e.getName());
	}

	public void visitPost(ExpressionUnaryOp e) throws PrismLangException {
	}

	@Override
	public void visitPost(ExpressionBinaryOp e) throws PrismLangException {
		e.getOperand1().accept(this);
		ArithmeticExpression arg1 = expr;
		e.getOperand2().accept(this);
		ArithmeticExpression arg2 = expr;
		switch (e.getOperator()) {
		case ExpressionBinaryOp.PLUS:
			expr = new ArithmeticBinaryExpr(ArithmeticBinaryOperator.PLUS,
					arg1, arg2);
			break;
		case ExpressionBinaryOp.MINUS:
			expr = new ArithmeticBinaryExpr(ArithmeticBinaryOperator.MINUS,
					arg1, arg2);
			break;
		case ExpressionBinaryOp.TIMES:
			expr = new ArithmeticBinaryExpr(ArithmeticBinaryOperator.MULT,
					arg1, arg2);
			break;
		case ExpressionBinaryOp.DIVIDE:
			expr = new ArithmeticBinaryExpr(ArithmeticBinaryOperator.DIVIDE,
					arg1, arg2);
			break;
		default:
			throw new PrismLangException(
					"Only arithmetic operators are supported in rate expressions");
		}
	}

}
