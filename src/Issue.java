import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Issue {
	public HashMap<String, Double> tfidf = new HashMap<>();
	static HashMap<String, Integer> df = new HashMap<>();
	String m_id, m_title, m_body, m_project;
	List<StackTrace> mStackTraces = null;
	public Issue(String id, String title, String body, String project) {
		this.m_id = id;
		this.m_title = title;
		this.m_body = body;
		this.m_project = project;
		mStackTraces = Util.splitStackTrace(body);
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
	
	public boolean hasStackTrace(){
		if (mStackTraces == null) 
			return false;
		return true;
	}
	public void buildVector() {
		String text = m_body + m_title;
		String[] wordArray = Util.removeStackTrace(text).toLowerCase().split("[^a-z]+");
		for (int i = 0; i < wordArray.length; i++) {
			String word = wordArray[i];
			if (!Util.isStopWord(word)){
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
			sum += Math.pow(entry.getValue(),2);
		}
		sum = Math.sqrt(sum);
		for (Map.Entry<String, Double> entry : tfidf.entrySet()) {
			entry.setValue(entry.getValue()/sum);
			
		}
	}
}
