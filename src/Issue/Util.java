package Issue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.*;

import Util.Issue;

public class Util {
	static private final Pattern mStackTracePattern = Pattern
			.compile("(?<StackTrace>(\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\"
					+ "d{2}\\.\\d{3} [A-Z]+\\] (?<Message>[\\s\\S]*?)\\n?)*((C"
					+ "aused by: )?(?<EXCEPTION>[a-zA-Z0-9_.]+Exception):*(?<Emes"
					+ "sage>[^\\r^\\n]+)?(?<ATmessage>\\s*at\\s(?<METHOD>[a-zA"
					+ "-Z0-9_.$<>]*)\\((?<description>(?<fileLine>(?<FILE>[a-z"
					+ "A-Z0-9_.$]*):(?<LINE>[0-9]+))|(?<native>Native Method)|"
					+ "(?<unk>Unknown Source))\\))+([\\n]*)?)+)");
	static private final Pattern mStactTraceDetail = Pattern
			.compile("(?<atMessage>\\s*at\\s(?<QualifierName>[a-zA-Z0-9_.$<>]*)"
					+ "\\((?<description>(?<fileLine>(?<CLASS>[a-zA-Z0-9_$]*).j"
					+ "ava:(?<LINE>[0-9]*))|(?<native>Native Method)|(?<unk>Unkn"
					+ "own Source))\\))");
	private static HashSet<String> customStopWordList = null;

	public static String normalizeString(String s) {
		if (s == null)
			return "";
		else
			return s.replace('\n', ' ').replace('\r', ' ').trim();
	}

	public static boolean isStopWord(String s) {
		if (customStopWordList == null) {
			customStopWordList = new HashSet<>();
			try {
				FileInputStream fstopword = new FileInputStream("english.stop");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fstopword));
				String line = "";
				while ((line = br.readLine()) != null) {
					customStopWordList.add(line);
				}
				// Done with the file
				if (br != null)
					br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			}
		}
		return customStopWordList.contains(s);
	}

	public static String removeStackTrace(String s) {
		Matcher matcher = mStackTracePattern.matcher(s);
		StringBuilder strBuilder = new StringBuilder();
		int beginIndex = 0;
		while (matcher.find()) {

			strBuilder.append(s.substring(beginIndex, matcher.start(1)));
			beginIndex = matcher.end(1);
		}

		strBuilder.append(s.substring(beginIndex, s.length()));

		// System.out.println("No stacktrace:");
		//
		// System.out.println(strBuilder.toString());
		//
		return strBuilder.toString();
	}

	public static List<StackTrace> splitStackTrace(String s) {
		Matcher matcher = mStackTracePattern.matcher(s);
		// StringBuilder strNormal = new StringBuilder();
		StringBuilder strStackTrace = new StringBuilder();
		// List<String> results = new ArrayList<>();
		List<StackTrace> stackTraces = new ArrayList<>();
		// int beginIndex = 0;
		String stackTraceName = "";
		String message = "";
		while (matcher.find()) {
			if (matcher.group("StackTrace").contains("Caused by: ")
					|| strStackTrace.length() == 0) {
				if (strStackTrace.length() == 0) {
					stackTraceName = matcher.group("EXCEPTION");
					message = matcher.group("Emessage");
				}
				strStackTrace.append(matcher.group("StackTrace") + "\n");
			} else {
				// results.add(strStackTrace.toString());
				stackTraces.add(new StackTrace(strStackTrace.toString(),
						stackTraceName, message));
				strStackTrace = new StringBuilder();
				strStackTrace.append(matcher.group("StackTrace") + "\n");
				stackTraceName = matcher.group("EXCEPTION");
				message = matcher.group("Emessage");
			}
			// strNormal.append(s.substring(beginIndex, matcher.start(1)));
			// beginIndex = matcher.end(1);
		}
		if (strStackTrace.length() != 0) {
			// results.add(strStackTrace.toString());
			stackTraces.add(new StackTrace(strStackTrace.toString(),
					stackTraceName, message));

		}
		// strNormal.append(s.substring(beginIndex, s.length()));
		// results.add(strNormal.toString());

		/*
		 * System.out.println("No stacktrace:");
		 * System.out.println(results.toArray()[results.size() - 1]);
		 * System.out.println("Stacktrace:"); for (int i = 0; i < results.size()
		 * - 1; i++) { System.out.println("===================");
		 * System.out.println(results.toArray()[i]); }
		 */
		if (stackTraces.size() == 0)
			return null;
		return stackTraces;
	}

	static String[] splitQuotedText(String s) {
		StringBuilder strNormal = new StringBuilder();
		StringBuilder strQuoted = new StringBuilder();
		// boolean bQuoting = false;
		int i = 0;
		while (i < s.length()) {
			if (s.charAt(i) == '`') {
				int j = i;
				while (s.charAt(j) == '`')
					j++;
				while (s.charAt(j) != '`')
					strQuoted.append(s.charAt(j++));
				strQuoted.append("\n");
				while (s.charAt(j) == '`')
					j++;
				i = j;
			} else {
				strNormal.append(s.charAt(i++));
			}
		}

		/*
		 * System.out.println("Normal:");
		 * System.out.println(strNormal.toString());
		 * System.out.println("Quoted");
		 * System.out.println(strQuoted.toString());
		 */

		String[] strSplitted = { strNormal.toString(), strQuoted.toString() };
		return strSplitted;
	}

	static List<String> splitCalls(String s) {
		Matcher matcher = mStactTraceDetail.matcher(s);
		List<String> results = new ArrayList<>();
		while (matcher.find()) {
			// List<String> wordSequence = new ArrayList<>();
			String qualifierNames = matcher.group("QualifierName");
			results.add(qualifierNames);
		}

		/*
		 * System.out.println("===================");
		 * System.out.println("Stacktrace:"); for (int i = 0; i <
		 * results.size(); i++) { System.out.println(results.toArray()[i]); }
		 */

		return results;
	}

	// Solving Common Longest Subsequence problem for two String Arrays.
	static double lcs(String[] X, String[] Y, int m, int n) {
		double[][] M = new double[X.length + 1][Y.length + 1];
		for (int i = 1; i <= X.length; i++) {
			for (int j = 1; j <= Y.length; j++) {
				if (X[i].equalsIgnoreCase(Y[j]))
					M[i][j] = M[i - 1][j - 1] + 1;
				else
					M[i][j] = Math.max(M[i][j - 1], M[i - 1][j]);
			}
		}
		return M[X.length][Y.length];
	}

	public static String buildHyperLink(Issue issue) {
		return "=HYPERLINK(\"" + "https://github.com/" + issue.m_project
				+ "/issues/" + issue.m_id + "\",\"" + issue.m_project + "-"
				+ issue.m_id + "\")";
	}
}
