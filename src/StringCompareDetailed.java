

public class StringCompareDetailed {
	/**
	 * @param retrieved
	 * @param expected
	 * @param compressSpace if true => multiple space:=>single space
	 * @return percentage of equality
	 */
	
	// beta is used for harmonic mean in F_beta measure
	private final double DEFAULT_BETA = 1.0;
	
	public double similarityScore(String retrieved, String expected, boolean compressSpace){
		return runStringComparison(retrieved, expected, compressSpace, DEFAULT_BETA);
	}
	public double runStringComparison(String retrieved, String expected, boolean compressSpace, double beta)
	{
		// change tabs to spaces
		retrieved = retrieved.replace('\t', ' ');
		expected = expected.replace('\t', ' ');
		
		if (compressSpace) {
			// remove spaces
			retrieved = retrieved.replaceAll("[ ]+", " "); 
			expected = expected.replaceAll("[ ]+", " ");
		}
		
		int expectedLength = expected.length();
		int retrievedLength = retrieved.length();
		int lcs[][] = new int[2][expectedLength + 1]; // longest common subsequence
		int result = 0;

		for (int i = 1; i <= retrievedLength; i++) {
			for (int j = 1; j <= expectedLength; j++) {
				if (retrieved.charAt(i - 1) == expected.charAt(j - 1))
					lcs[1][j] = lcs[0][j - 1] + 1;
				else
					lcs[1][j] = Math.max(lcs[0][j], lcs[1][j - 1]);
				result = Math.max(result, lcs[1][j]);
			}
			for (int nmx = 0; nmx < lcs[0].length; nmx++) {
				lcs[0][nmx] = lcs[1][nmx];
				lcs[1][nmx] = 0;
			}
		}

		if (result == 0 || retrievedLength == 0 || expectedLength == 0)
			return 0;
		
		double precision = (double) result / retrievedLength;
		double recall = (double) result / expectedLength;
		
		double FBetaScore = (1 + beta * beta) * precision * recall / (beta * beta * precision + recall);
		return FBetaScore;
	}
}