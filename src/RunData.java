import java.io.File;

public class RunData {
	private File runDir;
	private String name;

	private boolean incompleteDataset = false;
	private String incompleteName = null;

	private double sumScore = 0;
	private int count = 0;
	private int countMissing = 0;

	public RunData(File runDir) {
		this.setRunDir(runDir);
		name = runDir.getName();
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
