package smoothedMC;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class SmmcCli {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws ParseException, IOException {

		Option help = new Option("h", "help", false, "print this message");
		Option biopepa = OptionBuilder.withArgName("file").hasArg()
				.withDescription("Bio-PEPA file input").create("biopepa");
		Option mitl = OptionBuilder.withArgName("file").hasArg()
				.withDescription("MiTL file input").create("mitl");

		Option datapointsN = OptionBuilder
				.withArgName("datapoints")
				.hasArg()
				.withDescription(
						"Number of data-points for statistical model checking")
				.create("n");
		Option datapointsM = OptionBuilder
				.withArgName("datapoints")
				.hasArg()
				.withDescription(
						"Number of data-points for smoothed model checking (if"
								+ " not set, will be equal to n)").create("m");

		Option runs = OptionBuilder.withArgName("number").hasArg()
				.withDescription("Simulation runs per parameter")
				.create("runs");
		Option time = OptionBuilder.withArgName("value").hasArg()
				.withDescription("Simulation end time").create("time");
		Option timepoints = OptionBuilder
				.withArgName("number")
				.hasArg()
				.withDescription(
						"Number of points of the time-series produced by simulation;"
								+ " if not set, a full trajectory will be produced")
				.create("timepoints");

		Option ampl = OptionBuilder.withArgName("value").hasArg()
				.withDescription("Amplitude hyperparameter for GP")
				.create("amplitude");
		Option leng = OptionBuilder.withArgName("value").hasArg()
				.withDescription("Lengthscale hyperparameter for GP")
				.create("lengthscale");
		Option optimise = new Option("optimise", false,
				"Hyperparamer optimisation");

		Option param = OptionBuilder
				.withArgName("name")
				.hasArg()
				.withDescription(
						"The model parameter examined by smoothed model checking")
				.create("parameter");

		Option lb = OptionBuilder.withArgName("value").hasArg()
				.withDescription("The lower bound of the parameter")
				.create("lb");
		Option ub = OptionBuilder.withArgName("value").hasArg()
				.withDescription("The upper bound of the parameter")
				.create("ub");

		Options options = new Options();
		options.addOption(help);
		options.addOption(biopepa);
		options.addOption(mitl);
		options.addOption(datapointsN);
		options.addOption(datapointsM);
		options.addOption(runs);
		options.addOption(time);
		options.addOption(timepoints);
		options.addOption(ampl);
		options.addOption(leng);
		options.addOption(optimise);
		options.addOption(param);
		options.addOption(lb);
		options.addOption(ub);

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption("h") || !cmd.hasOption("biopepa")
				|| !cmd.hasOption("mitl") || !cmd.hasOption("n")
				|| !cmd.hasOption("time") || !cmd.hasOption("amplitude")
				|| !cmd.hasOption("lengthscale") || !cmd.hasOption("parameter")
				|| !cmd.hasOption("lb") || !cmd.hasOption("ub")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("SmMC", options, true);
		}

		else {

			final String modelFile = cmd.getOptionValue("biopepa");
			final String mitlFile = cmd.getOptionValue("mitl");
			final int n = Integer.parseInt(cmd.getOptionValue("n"));
			final int m = cmd.hasOption("m") ? Integer.parseInt(cmd
					.getOptionValue("m")) : n;
			final int repetitions = cmd.hasOption("runs") ? Integer
					.parseInt(cmd.getOptionValue("runs")) : 1;
			double tf = Double.parseDouble(cmd.getOptionValue("time"));
			double a = Double.parseDouble(cmd.getOptionValue("amplitude"));
			double l = Double.parseDouble(cmd.getOptionValue("lengthscale"));
			boolean opt = cmd.hasOption("optimise");
			int time_points = 0;
			if (cmd.hasOption("timepoints"))
				time_points = Integer
						.parseInt(cmd.getOptionValue("timepoints"));

			String[] names = cmd.getOptionValues("parameter");
			double[] lows = new double[names.length];
			double[] upps = new double[names.length];
			String[] lows_str = cmd.getOptionValues("lb");
			String[] upps_str = cmd.getOptionValues("ub");
			if (names.length != lows_str.length
					|| names.length != upps_str.length) {
				System.err
						.println("No bounds specified for some of the parameters!");
				return;
			}
			for (int i = 0; i < names.length; i++) {
				lows[i] = Double.parseDouble(lows_str[i]);
				upps[i] = Double.parseDouble(upps_str[i]);
			}

			SmmcUtils.runSmMC(modelFile, mitlFile, n, m, repetitions, tf,
					time_points, a, l, opt, names, lows, upps);
		}
	}
}
