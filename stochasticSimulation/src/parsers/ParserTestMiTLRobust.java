package parsers;

import expr.Context;
import expr.Variable;
import mitl.MiTL;
import mitl.MitlPropertiesList;
import model.Trajectory;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by ssilvetti on 17/02/15.
 */
public class ParserTestMiTLRobust {
    public static void main(String[] args) {
        String main = ParserTestMiTL.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String data = readFile(main+"parsers/trajTest0.csv");
        String lines[] = data.split("\n");

        int n = lines.length;
        String[] words = lines[0].trim().split(" ");
        double[] times = new double[n];
        double[][] values = new double[words.length-1][n];
        for (int i = 0; i < lines.length; i++) {
            String[] words1 = lines[i].trim().split(" ");
            times[i] = Double.parseDouble(words1[0]);
            for (int j = 0; j < words.length-1; j++) {
                values[j][i] = Double.parseDouble(words1[j+1]);

            }
        }

            Context ns = new Context();
            new Variable("A", ns);
            new Variable("B", ns);


            Trajectory x = new Trajectory(times, ns,  values );

            String s = main+"parsers/test0.mtl";
            String text = readFile(s);
            MitlFactory factory = new MitlFactory(ns);
            MitlPropertiesList l = factory.constructProperties(text);

            MiTL prop = l.getProperties().get(0);


            boolean b = prop.evaluate(x, 0);
            double bvalue = prop.evaluateValue(x, 0);

        System.out.println("Mitl Formula : " +b+"\nRobustness:"+bvalue);


    }

    public static final String readFile(String filename) {
        FileInputStream input;
        byte[] fileData;
        try {
            input = new FileInputStream(filename);
            fileData = new byte[input.available()];
            input.read(fileData);
            input.close();
        } catch (IOException e) {
            fileData = new byte[0];
        }
        return new String(fileData);
    }

    public static double[] createPath (int time, double init, double p){
        double[] res = new double[time];
        double evolu = init;
        for (int i = 0; i < res.length; i++) {
            if (evolu<p) {evolu = (p-evolu)/p;
            }else {evolu = (evolu-p)/(1-p);}
            res[i]=evolu;

        }
        return res;
    }

    public static double[] createtimes (int time){
        double[] res = new double[time];
        for (int i = 0; i < res.length; i++) {
            res[i]=i;

        }
        return res;
    }



}
