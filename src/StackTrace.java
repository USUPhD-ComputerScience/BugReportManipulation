import java.util.HashMap;
import java.util.List;

public class StackTrace {
	public List<String[]> mListCalls = null;
	public String[] mException;
	public String mMessage = null;
	public double mThreshold = 0.9;

	public StackTrace(String st, String ex, String mes) {
		mListCalls = Util.splitCalls(st);
		mException = ex.split("\\.");
		mMessage = mes;
	}

	// building the matrix of similarity of the calls between two stacktraces
	public double[][] buildSimilarityMatrix(StackTrace st2) {
		double[][] similarityMatrix = new double[mListCalls.size()][st2.mListCalls
				.size()];
		// List of all similar pairs...somehow
		for (int i = 0; i < mListCalls.size(); i++) {
			for (int j = 0; j < st2.mListCalls.size(); j++) {
				int len1 = mListCalls.get(i).length;
				int len2 = st2.mListCalls.get(j).length;
				similarityMatrix[i][j] = Util.lcs(mListCalls.get(i),
						st2.mListCalls.get(j), len1, len2) * 2 / (len1 + len2);
				System.out.print(similarityMatrix[i][j] + ",");
			}
			System.out.print("\n");
		}
		return similarityMatrix;
	}

	public HashMap<Integer, Integer> findSimilarPairs(
			double[][] similarityMatrix) {
		HashMap<Integer, Integer> similarPairs = new HashMap<>();
		for (int i = 0; i < similarityMatrix.length; i++) {
			for (int j = 0; j < similarityMatrix[i].length; j++) {
				if (similarityMatrix[i][j] >= mThreshold) {
					boolean bCapable = true;
					for (int c = i+1; c < similarityMatrix.length; c++) {
						if (similarityMatrix[c][j] > similarityMatrix[i][j]) {
							bCapable = false;
							break;
						}
					}
					if (bCapable) {
						if (similarPairs.containsKey(i)) {
							if (similarityMatrix[i][j] > similarityMatrix[i][similarPairs
									.get(i)]) {
								similarPairs.put(i, j);
							}
						} else {
							similarPairs.put(i, j);
						}
					}

				}
			}
			if (similarPairs.containsKey(i)) {
				int ignoredColumn = similarPairs.get(i);
				for (int j = 0; j < similarityMatrix.length; j++) {
					similarityMatrix[j][ignoredColumn]= 0; //toss an entire column out if a cell was chosen.
				}
			}
			
		}
		System.out.println("==============Similar Pairs==================");
		for (int i : similarPairs.keySet()) {
			System.out.println("Pair: " + i + "-" + similarPairs.get(i));
		}
		return similarPairs;
	}

	// compare similarity between two stackTraces using reBucket-icse2012
	// technique
	public double compareTo(StackTrace st2) {
		HashMap<Integer, Integer> similarPairs = findSimilarPairs(buildSimilarityMatrix(st2));
		return 0;
	}
}
