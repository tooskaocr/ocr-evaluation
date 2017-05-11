import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

import ir.viratech.commons.nlp_utils.commons.helper.ConfusionMatrix;

public class RunData {
	private File runDir;
	private String name;

	private boolean incompleteDataset = false;
	private String incompleteName = null;

	private double sumScore = 0;
	private int count = 0;
	private int countMissing = 0;
	private ConfusionMatrix misspelledMatrix;
	private Map<String, Double> runValues;
	
	public RunData(File runDir) {
		this.setRunDir(runDir);
		name = runDir.getName();
		misspelledMatrix = new ConfusionMatrix();

		runValues = new TreeMap<>();
	}
	public File getRunDir() {
		return runDir;
	}
	public void setRunDir(File runDir) {
		this.runDir = runDir;
	}
	public String getName() {
		return name;
	}
	public double getMeanScore() {
		return 100*sumScore / getCount();
	}
	public void addScore(double score) {
		sumScore += score;
		setCount(getCount() + 1);
	}
	
	public void addRunScore(double runScore, String runName) {
		runValues.put(runName, runScore);
	}
	
	public void addMatrix(ConfusionMatrix matrix) {
		misspelledMatrix.addMatrix(matrix);
	}
	
	public void writeScoresCSV(OutputStream output) throws IOException
	{
		StringBuilder result = new StringBuilder();
		int cnt = 0;
		for(String runName: runValues.keySet()) {
			if(cnt++ != 0)
				result.append(",");
			result.append(runName);
		}
		result.append("\n");
		output.write(result.toString().getBytes());
		
		result = new StringBuilder();
		cnt = 0;
		for(Double runScore: runValues.values()) {
			if(cnt++ != 0)
				result.append(",");
			result.append(String.format("%.4f", runScore));
		}
		result.append("\n");
		output.write(result.toString().getBytes());
	}
	
	public void writeMatrixCSV(OutputStream output, boolean includeMatches) throws IOException
	{
		int[][] content = misspelledMatrix.getContent();
		char[] chars = misspelledMatrix.getLabels();
		
		StringBuilder result = new StringBuilder();

		int nChars = chars.length;		
		
		result.append("<>,");
		for(int i = 0;i < nChars;i++)
			result.append(String.format("\\u%04x", (int)chars[i])+",");
		result.append("del\n");
		output.write(result.toString().getBytes());
		
		for(int i = 0;i < nChars+1;i++)
		{
			result = new StringBuilder();
			double rowSum = misspelledMatrix.sumOfOneRow(i, includeMatches);
			if(i < nChars)
				result.append(String.format("\\u%04x", (int)chars[i]));
			else
				result.append("ins");
			for(int j = 0;j < nChars+1;j++)
			{
				if((!includeMatches || rowSum == 0) && i == j)
					result.append(","+String.format("%.4f", 1.0));
				else if(rowSum > 0)
					result.append(","+String.format("%.4f", (content[i][j]/rowSum)));
				else
					result.append(","+String.format("%.4f", 0.0));
			}
			result.append("\n");
			output.write(result.toString().getBytes());
		}	
	}
	public String getIncompleteName() {
		return incompleteName;
	}
	public void setIncompleteName(String incompleteName) {
		this.incompleteName = incompleteName;
	}
	public boolean isIncompleteDataset() {
		return incompleteDataset;
	}
	public void setIncompleteDataset(boolean incompleteDataset) {
		this.incompleteDataset = incompleteDataset;
	}
	public int getCountMissing() {
		return countMissing;
	}
	public void addMissing() {
		this.countMissing++;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
