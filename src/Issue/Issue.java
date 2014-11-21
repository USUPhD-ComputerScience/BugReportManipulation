package Issue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

	public List<Integer> contains(List<String> something) {
		List<Integer> countedList = null;
		if (mStackTraces != null)
			countedList = mStackTraces.get(0).contains(something);
		return countedList;
	}

	public void count(Map<String, Integer> frequency,
			List<String> libsOfInterest) {
		for (StackTrace st : mStackTraces) {
			List<String> calls = st.getmListCalls();
			for (String call : calls) {
				for (String lib : libsOfInterest) {
					if (call.contains(lib)) {
						int lastDotPos = call.lastIndexOf(".");
						String classname = call.substring(0, lastDotPos);
						Integer count = frequency.get(classname);
						if (count == null)
							frequency.put(classname, 1);
						else
							frequency.put(classname, count + 1);
					}

				}
			}
		}
	}

	public void countMethodCall(Map<String, Integer> frequency,
			List<String> libsOfInterest) {
		for (StackTrace st : mStackTraces) {
			List<String> calls = st.getmListCalls();
			for (String call : calls) {
				for (String CoI : libsOfInterest) {
					if (call.contains(CoI)) {
						Integer count = frequency.get(call);
						if (count == null)
							frequency.put(call, 1);
						else
							frequency.put(call, count + 1);
					}

				}
			}
		}
	}

	// 1. determine how much Android-ish each stacktrace is
	// 2. weights: return the sum of all stacktraces's android-ish weights
	// 3. frequency: count the number of stacktraces that have android libs
	// (3 <=> android-ish weight > 0)
	// NOTE: 2/3 = overall android-ish weight for a type of exception
	// MAYBE WRONG!
	public void rankException(Map<String, Integer> frequency,
			Map<String, Double> weights, List<String> libsOfInterest) {
		for (StackTrace st : mStackTraces) {
			List<String> calls = st.getmListCalls();
			String excpt = Util.getException(st.getmException());
			if (excpt == null) {
				if (calls.size() > 0)
					excpt = Util.getException(calls.get(0));
				else
					continue;
			}
			if (excpt == null)
				continue;
			boolean clarified = false;
			Double expectedWeight = 0.0;
			Double realWeight = 0.0;
			for (int i = 0; i < calls.size(); i++) {
				String call = calls.get(i);
				for (String CoI : libsOfInterest) {
					if (call.contains(CoI)) {
						if (!clarified) {
							clarified = true;
							Integer count = frequency.get(excpt);
							if (count == null)
								frequency.put(excpt, 1);
							else
								frequency.put(excpt, count + 1);
						}

						realWeight = realWeight + Math.exp(-0.5 * (i + 1));
						break;
					}

				}
				expectedWeight = expectedWeight + Math.exp(-0.5 * (i + 1));
			}
			if (clarified) {
				Double weight = weights.get(excpt);
				if (weight == null)
					weights.put(excpt, realWeight / expectedWeight);
				else
					weights.put(excpt, weight + (realWeight / expectedWeight));
			}
		}
	}

	public void relateAndroidPackageWithExcpt(BufferedWriter bw,
			List<String> classesOfInterest) throws IOException {
		{

			for (StackTrace st : mStackTraces) {
				List<String> calls = st.getmListCalls();
				String excpt = Util.getException(st.getmException());
				if (excpt == null) {
					if (calls.size() > 0)
						excpt = Util.getException(calls.get(0));
					else
						continue;
				}
				if (excpt == null)
					continue;
				boolean stop = false;
				for (int i = calls.size() - 1; i >= 0; i--) {
					String call = calls.get(i);
					for (String CoI : classesOfInterest) {
						if (call.contains(CoI)) {
							if (i + 1 < calls.size())
								bw.write(excpt + "," + call + ","
										+ calls.get(i + 1) + "," + i + ","
										+ Util.buildHyperLink(this) + "\n");
							stop = true;
							break;
						}

					}
					if (stop)
						break;
				}
			}

		}

	}

	public void countException(Map<String, Integer> frequency,
			List<String> libsOfInterest) {
		for (StackTrace st : mStackTraces) {
			List<String> calls = st.getmListCalls();
			String excpt = Util.getException(st.getmException());
			if (excpt == null) {
				if (calls.size() > 0)
					excpt = Util.getException(calls.get(0));
				else
					continue;
			}
			if (excpt == null)
				continue;
			boolean stop = false;
			for (String call : calls) {
				for (String CoI : libsOfInterest) {
					if (call.contains(CoI)) {
						Integer count = frequency.get(excpt);
						if (count == null)
							frequency.put(excpt, 1);
						else
							frequency.put(excpt, count + 1);
						stop = true;
						break;
					}

				}
				if (stop)
					break;
			}
		}
	}

	public void rankMethodCall(Map<String, Double> weights,
			List<String> classesOfInterest) {
		for (StackTrace st : mStackTraces) {
			List<String> calls = st.getmListCalls();
			for (int i = 0; i < calls.size(); i++) {
				String call = calls.get(i);
				for (String CoI : classesOfInterest) {
					if (call.contains(CoI)) {
						Double weight = weights.get(call);
						Double weightIncrease = Math.exp(-0.5 * i);
						if (weight == null)
							weights.put(call, weightIncrease);
						else
							weights.put(call, weight + weightIncrease);
					}

				}
			}
		}
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
