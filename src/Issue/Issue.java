package Issue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Util.Util;

public class Issue {
	public HashMap<String, Double> tfidf = new HashMap<>();
	public static HashMap<String, Integer> df = new HashMap<>();
	public String m_id, m_title, m_body, m_project, m_comments;
	public List<StackTrace> mStackTraces = null;

	public Issue(String id, String title, String body, String project) {
		this.m_id = id;
		this.m_title = title;
		this.m_project = project;
		mStackTraces = Util.splitStackTrace_v2(body);
		this.m_body = Util.splitNormalText(Util.splitQuotedText(body)[0]);
	}

	public Issue() {
		// don't touch this method if you dont know what you are doing
		// lel
		this.m_id = null;
		this.m_title = null;
		this.m_body = null;
		this.m_project = null;
		m_comments = null;
	}

	public boolean contains(List<String> something) {
		if (mStackTraces != null)
			return mStackTraces.get(0).contains(something);
		return false;
	}

	public static double CosineSimilarity(Issue x1, Issue x2) {
		// TODO Auto-generated method stub
		Double d;
		double sum = 0;
		for (String word : x1.tfidf.keySet()) {
			d = x2.tfidf.get(word);
			if (d != null) {
				sum += d * x1.tfidf.get(word);
			}
		}
		return sum;
	}

	public boolean hasStackTrace() {
		if (mStackTraces == null)
			return false;
		return true;
	}

	public void print() {
		System.out.println("===============================================");
		System.out.println(">>ID:\n" + m_id);
		System.out.println(">>Title:\n" + m_title);
		System.out.println(">>Body:\n" + m_body);
		System.out.println(">>Comments:\n" + m_comments);
	}

	public void buildVector() {
		String text = m_body + m_title;
		String[] wordArray = Util.removeStackTrace(text).toLowerCase()
				.split("[^a-z]+");
		for (int i = 0; i < wordArray.length; i++) {
			String word = wordArray[i];
			if (!Util.isStopWord(word)) {
				Double count = tfidf.get(word);
				if (count == null) {
					tfidf.put(word, 1.0);
					Integer dfCount = df.get(word);
					if (dfCount == null) {
						df.put(word, 1);
					} else {
						df.put(word, dfCount + 1);
					}
				} else {
					tfidf.put(word, count + 1);
				}
			}
		}
	}

	public void normalizeVector() {
		double sum = 0;
		for (Map.Entry<String, Double> entry : tfidf.entrySet()) {
			entry.setValue(entry.getValue()
					/ (1 + Math.log(df.get(entry.getKey()))));
			sum += Math.pow(entry.getValue(), 2);
		}
		sum = Math.sqrt(sum);
		for (Map.Entry<String, Double> entry : tfidf.entrySet()) {
			entry.setValue(entry.getValue() / sum);

		}
	}
}
