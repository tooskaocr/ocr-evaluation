import java.io.File;

import org.apache.commons.io.FileUtils;

import ir.viratech.commons.nlp_utils.commons.StringCompare;

public class Evaluation {
//	static String[] datasets = { "simple", "medium", "difficult", "very_difficult" };
	static String[] datasets = { "medium", "difficult" };
			
	public static void main(String[] args) throws Exception {
		File[] runDirs = new File("runs").listFiles();
		for (String dataset: datasets) {
			System.out.print(dataset + ": ");
			File datasetDir = new File("benchmark_data/"+dataset);
			if (!datasetDir.exists())
				throw new Exception("Dataset " + datasetDir.getAbsolutePath() + " does not exist!");
			for (File runDir: runDirs) {
				if (runDir.getName().startsWith("."))
					continue;
				double sumScore = 0;
				int count = 0;
				for (File file: datasetDir.listFiles()) {
					if (file.getName().endsWith(".txt")) {
						String correct = FileUtils.readFileToString(file, "utf8");
						String runOutput = FileUtils.readFileToString(new File(runDir, file.getName()), "utf8");
						sumScore += StringCompare.similarityScore(runOutput, correct, true);
						count++;
					}
				}
				double meanScore = 100*sumScore / count;
				System.out.print(runDir.getName() + " " + meanScore +"%");
			}
			System.out.println();
		}
	}
}
