package Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LSHasher {
	public HashMap<String, Double> m_HashVector = new HashMap<String, Double>();
	public Random mRandomGausian = new Random(1);
	public HashMap<Integer, List<Issue>> mCategories = new HashMap<>();
	private double mWindow = 0.5;

	public LSHasher(double window) {
		mWindow = window;
	}

	public void generateHashVectors(HashMap<String, Integer> df) {
		for (String word : df.keySet()) {
			m_HashVector.put(word, mRandomGausian.nextGaussian());
		}
	}

	public int computeHash(HashMap<String, Double> tfidf) {
		double hashCode = 0;
		for (String word : tfidf.keySet()) {
			hashCode += tfidf.get(word) * m_HashVector.get(word);
		}
		return (int) Math.round((hashCode / mWindow));
	}

	public void categorizeLSH(List<Issue> issues) {
		for (Issue issue : issues) {
			int code = computeHash(issue.tfidf);
			List<Issue> list = mCategories.get(code);
			if (list == null) {
				list = new ArrayList<>();
				mCategories.put(code, list);
			}
			list.add(issue);
		}
	}
}
