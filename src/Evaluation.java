import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import ir.viratech.commons.nlp_utils.commons.StringCompare;
import ir.viratech.commons.nlp_utils.commons.helper.ConfusionMatrix;
import ir.viratech.commons.nlp_utils.word_analysis.normalization.fa.FarsiNormalizerWordFilter;

public class Evaluation {
	static String[] datasets = {"books1", "books2", "simple", "medium", "difficult", "ganjoor"};
//	String[] datasets = { "ganjoor"};
	
	ArrayList<String> normalFrom = new ArrayList<String>();
	ArrayList<String> normalTo = new ArrayList<String>();
	ArrayList<String> runs = new ArrayList<>();
	
	FarsiNormalizerWordFilter advancedNormalizer = FarsiNormalizerWordFilter.getInstance();
	boolean ignoreSpaces = false;

	public static void main(String[] args) throws Exception {	
		new Evaluation().evaluate(new String[] {"googledrive", "persianegar", "vira9410", "vira950117", "vira950220-t50-180", "vira950220-t80-200", "ganjoor"});
	}
	
	public Evaluation() {
		initMap();
	}

	private void initMap() {
		normalFrom.addAll(Arrays.asList(new String[] { "\n", "\ufeff", "\u200c", "\u200f", "\u06f0", "\u06f1", "\u06f2", "\u06f3", "\u06f4", "\u06f5", "\u06f6", "\u06f7", "\u06f8", "\u06f9" }));
		normalTo.addAll(Arrays.asList(new String[] { " ", "", " ", " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}));
		if (ignoreSpaces) {
			normalFrom.add(" ");
			normalTo.add("");
		}
	}
	
	private void evaluate(String[] inputRuns) throws Exception, IOException {
		Collections.addAll(runs, inputRuns);
		System.out.println("runs: " + runs);
		Map<String, RunData> accumulatedRunsData = new HashMap<>();
		for (String dataset: datasets) {
			Map<String, RunData> runsData = new HashMap<>();
			File[] runDirs = new File("runs").listFiles();
			Arrays.sort(runDirs);
			for (File runDir: runDirs) {
				if (runs.contains(runDir.getName()))
				{
					runsData.put(runDir.getName(), new RunData(runDir));
					if (!accumulatedRunsData.containsKey(runDir.getName()))
						accumulatedRunsData.put(runDir.getName(), new RunData(runDir));
				}
			}
			
			System.out.println(dataset + ":");
			File datasetDir = new File("benchmark_data/"+dataset);
			if (!datasetDir.exists())
				throw new Exception("Dataset " + datasetDir.getAbsolutePath() + " does not exist!");

			for (File file: datasetDir.listFiles()) {
				if (file.getName().endsWith(".txt")) {
					String name = FilenameUtils.getBaseName(file.getName());
					System.out.print(name + ": "); 
					for (RunData runData: runsData.values()) {
						boolean available = true;
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
							ConfusionMatrix matrix = StringCompare.detailedEditDistance(runOutput, correct, true);
							double N = Math.max(runOutput.length(), correct.length());
							double score = (N-StringCompare.rawEditDistance(runOutput, correct, true))/N;
							// Accumulate the score and matrix for each run (related to the dataset of for loop)
							runData.addScore(score);
							runData.addRunScore(score, file.getName());
							runData.addMatrix(matrix);
							// Accumulate the score and matrix for each run (independent of the dataset)
							RunData accRunData = accumulatedRunsData.get(runData.getName());
							accRunData.addRunScore(score, runData.getName());
							accRunData.addMatrix(matrix);
							System.out.printf("%s %.1f%% ", runData.getName(), score*100);
						} else {
							runData.addMissing();
							System.out.printf("%s NA ", runData.getName());
						}
					}
					System.out.println();
				}				
			}
			
			// Make the results directory
			File resultDir = new File("result/"+dataset);
			resultDir.mkdirs();

			// Print scores and save confusion matrices into files
			for (RunData runData: runsData.values()) {
				double meanScore = runData.getMeanScore();
				if (runData.getCountMissing() > 0)
					System.out.printf("%s %.1f%% (Incomplete %d/%d) ", runData.getName(), meanScore, runData.getCount(), runData.getCountMissing()+runData.getCount());
				else
					System.out.printf("%s %.1f%% ", runData.getName(), meanScore);
				try(FileOutputStream fos = new FileOutputStream("result/"+dataset+"/"+runData.getName()+".scores.csv")){
					runData.writeScoresCSV(fos);
				}
				try(FileOutputStream fos = new FileOutputStream("result/"+dataset+"/"+runData.getName()+".confusion.csv")){
					runData.writeMatrixCSV(fos, false);
				}
			}
			System.out.println();
		}
		
		File resultDir = new File("result/accumulated");
		resultDir.mkdirs();
		// Print accumulated confusion matrices for each run
		for (RunData runData:accumulatedRunsData.values()) 
		{
			try(FileOutputStream fos = new FileOutputStream("result/accumulated"+runData.getName()+".scores.csv")){
				runData.writeScoresCSV(fos);
			}
			try(FileOutputStream fos = new FileOutputStream("result/accumulated"+runData.getName()+".confusion.csv")){
				runData.writeMatrixCSV(fos, false);
			}
		}
	}

	private String normalize(String text) {
		for (int i = 0; i < normalFrom.size(); ++i) {
			text = text.replaceAll(normalFrom.get(i), normalTo.get(i));
		}
		
		text = advancedNormalizer.filter(text, true);
		return text;
	}
}
