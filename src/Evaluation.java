import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import ir.viratech.commons.nlp_utils.commons.StringCompare;

public class Evaluation {
//	static String[] datasets = { "simple", "medium", "difficult", "very_difficult" };
	static String[] datasets = { "books1"};
	static ArrayList<String> runs = new ArrayList<>();
			
	public static void main(String[] args) throws Exception {	
		Collections.addAll(runs, new String[] {"vira950207", "persianegar", "googledrive"});
		System.out.println("runs: " + runs);
		for (String dataset: datasets) {
			Map<String, RunData> runsData = new HashMap<>();
			File[] runDirs = new File("runs").listFiles();
			for (File runDir: runDirs) {
				if (runs.contains(runDir.getName()))
					runsData.put(runDir.getName(), new RunData(runDir));
			}
			
			System.out.println(dataset + ":");
			File datasetDir = new File("benchmark_data/"+dataset);
			if (!datasetDir.exists())
				throw new Exception("Dataset " + datasetDir.getAbsolutePath() + " does not exist!");

			for (File file: datasetDir.listFiles()) {
				if (file.getName().endsWith(".txt")) {
					String name = FilenameUtils.getBaseName(file.getName());
					System.out.print(name + ": "); 
					boolean available = true;
					for (RunData runData: runsData.values()) {
						String correct = FileUtils.readFileToString(file, "utf8");
						File runOutputFile = new File(runData.getRunDir(), file.getName());
						if (!runOutputFile.exists()) {
							runData.setIncompleteDataset(true);
							runData.setIncompleteName(file.getName());
							available = false;
						}
						if (available) {
							String runOutput = FileUtils.readFileToString(runOutputFile, "utf8");
							runOutput = normalize(runOutput);
							correct = normalize(correct);
							double score = StringCompare.similarityScore(runOutput, correct, true, 3);
							runData.addScore(score);
							System.out.printf("%s %.1f%% ", runData.getName(), score*100);
						} else {
							runData.addMissing();
							System.out.printf("%s NA ", runData.getName());
						}
					}
					System.out.println();
				}				
			}			
			for (RunData runData: runsData.values()) {
				double meanScore = runData.getMeanScore();
				if (runData.getCountMissing() > 0)
					System.out.printf("%s %.1f%% (I %d/%d) ", runData.getName(), meanScore, runData.getCount(), runData.getCountMissing()+runData.getCount());
				else
					System.out.printf("%s %.1f%% ", runData.getName(), meanScore);
			}
			System.out.println();
		}
	}

	private static String[] normalFrom = new String[] { "\n", "\u200c", "\u06f0", "\u06f1", "\u06f2", "\u06f3", "\u06f4", "\u06f5", "\u06f6", "\u06f7", "\u06f8", "\u06f9" };
	private static String[] normalTo   = new String[] { " ", " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	
	private static String normalize(String text) {
		for (int i = 0; i < normalFrom.length; ++i) {
			text = text.replaceAll(normalFrom[i], normalTo[i]);
		}
		return text;
	}
}
