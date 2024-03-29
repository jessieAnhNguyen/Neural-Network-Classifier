package learn.lc.examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import learn.lc.core.Example;
import learn.lc.core.LearningRateSchedule;
import learn.lc.core.PerceptronClassifier;
import learn.lc.display.ClassifierDisplay;

/**
 * Program for testing the PerceptronClassifier and write accuracy on each step to a csv file
 */
public class PerceptronClassifierWithDisplay {

	/**
	 * Train a PerceptronClassifier on a file of examples and
	 * print its accuracy after each training step.
	 */
	public static void main(String[] argv) throws IOException {
		if (argv.length < 3) {
			System.out.println("usage: java PerceptronClassifierWithDisplay data-filename nsteps alpha");
			System.out.println("       specify alpha=0 to use decaying learning rate schedule (AIMA p725)");
			System.exit(-1);
		}
		String filename = argv[0];
		int nsteps = Integer.parseInt(argv[1]);
		double alpha = Double.parseDouble(argv[2]);
		System.out.println("filename: " + filename);
		System.out.println("nsteps: " + nsteps);
		System.out.println("alpha: " + alpha);
		
		ClassifierDisplay display = new ClassifierDisplay("PerceptronClassifier: " + filename);
		List<Example> examples = Data.readFromFile(filename);
		int ninputs = examples.get(0).inputs.length; 
		PerceptronClassifier classifier = new PerceptronClassifier(ninputs) {
			@Override
			public void trainingReport(List<Example> examples, int stepnum, int nsteps) {
				double accuracy = accuracy(examples);
				System.out.println(stepnum + "\t" + accuracy);
				display.addPoint(accuracy);
			}
		};
		if (alpha > 0) {
			classifier.train(examples, nsteps, alpha);
		} else {
			classifier.train(examples, 100000, new LearningRateSchedule() {
				public double alpha(int t) { return 1000.0/(1000.0+t); }
			});
		}
		// write the output to a csv file
		//display.writeDataFile("perceptronDataOut.csv");

		// write the output
		String[] nameSplit = filename.split("/");
		String newName = nameSplit[nameSplit.length-1].split("\\.")[0] + "-output.csv";

		display.writeDataFile(newName);
	}


}
