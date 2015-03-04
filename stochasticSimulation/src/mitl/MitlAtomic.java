package mitl;

import model.Trajectory;
import expr.LogicalExpression;
import expr.Context;

public final class MitlAtomic extends MiTL {

	private LogicalExpression expr;

	public MitlAtomic(LogicalExpression expr) {
		this.expr = expr;
	}

	@Override
	public boolean evaluate(Trajectory x, double t) {
		final double[] times = x.getTimes();
		final int index = timeIndexAfter_efficient(times, t);
		final Context context = x.getContext();
		int numberOfVariables = context.getVariables().length;
		for (int var = 0; var < numberOfVariables; var++)
			context.setValue(var, x.getValues(var)[index]);
		return expr.evaluate();
	}

    @Override
    public double evaluateValue(Trajectory x, double t) {
        final double[] times = x.getTimes();
        final int index = timeIndexAfter_efficient(times, t);
        final Context context = x.getContext();
        int numberOfVariables = context.getVariables().length;
        for (int var = 0; var < numberOfVariables; var++)
            context.setValue(var, x.getValues(var)[index]);
        return expr.evaluateValue();
    }

    @Override
	public String toString() {
		return expr.toString();
	}

	private int previouslyUsedIndex = 0;

	final private int timeIndexAfter_efficient(final double[] times,
			final double t) {
		if (previouslyUsedIndex >= times.length
				|| times[previouslyUsedIndex] > t)
			previouslyUsedIndex = 0;
		for (int i = previouslyUsedIndex; i < times.length; i++)
			if (times[i] >= t)
				return previouslyUsedIndex = i;
		return previouslyUsedIndex = times.length - 1;
	}

}
