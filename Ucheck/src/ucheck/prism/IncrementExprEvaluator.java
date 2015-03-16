package ucheck.prism;

import parser.ast.ASTElement;
import parser.ast.ExpressionBinaryOp;
import parser.ast.ExpressionLiteral;
import parser.ast.ExpressionVar;
import parser.visitor.ASTTraverse;
import prism.PrismLangException;

/**
 * Visitor class that evaluates the increment {@code c} in an expression of the
 * form: {@code X+c} or {@code X-c}
 */
public class IncrementExprEvaluator extends ASTTraverse {

	final private String name;
	private int increment;
	private int variablesVisited;
	final private static String msg = "Only incremental updates are supported "
			+ "for PRISM models";

	/**
	 * Visitor class that evaluates the increment {@code c} in an expression of
	 * the form: {@code X+c} or {@code X-c}
	 * 
	 * @param name
	 *            of the variable to be modified by the increment
	 */
	public IncrementExprEvaluator(String name) {
		this.name = name;
	}

	@Override
	public void defaultVisitPost(ASTElement expr) throws PrismLangException {
		throw new PrismLangException(msg);
	}

	@Override
	public void visitPost(ExpressionLiteral expr) throws PrismLangException {
		if (variablesVisited == 0)
			throw new PrismLangException(msg);
		increment = expr.evaluateInt();
	}

	@Override
	public void visitPost(ExpressionVar expr) throws PrismLangException {
		if (variablesVisited++ > 0)
			throw new PrismLangException(msg);
		if (!expr.getName().equals(name))
			throw new PrismLangException(msg);
	}

	@Override
	public void visitPost(ExpressionBinaryOp expr) throws PrismLangException {
		if (expr.getOperator() == ExpressionBinaryOp.MINUS)
			increment = -increment;
		else if (expr.getOperator() != ExpressionBinaryOp.PLUS)
			throw new PrismLangException(msg);
	}

	public int getIncrement() {
		return increment;
	}

}
