package Util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.*;

import Issue.Issue;
import Issue.StackTrace;

public class Util {
	static private final Pattern mStackTracePattern = Pattern
			.compile("(?<StackTrace>(\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\"
					+ "d{2}\\.\\d{3} [A-Z]+\\] (?<Message>[\\s\\S]*?)\\n?)*((C"
					+ "aused by: )?(?<EXCEPTION>[a-zA-Z0-9_.]+Exception):*(?<Emes"
					+ "sage>[^\\r^\\n]+)?(?<ATmessage>\\s*at\\s(?<METHOD>[a-zA"
					+ "-Z0-9_.$<>]*)\\((?<description>(?<fileLine>(?<FILE>[a-z"
					+ "A-Z0-9_.$]*):(?<LINE>[0-9]+))|(?<native>Native Method)|"
					+ "(?<unk>Unknown Source))\\))+([\\n]*)?)+)");
	static private final Pattern mStactTrace_at = Pattern
			.compile("(?<atMessage>\\s*at\\s(?<QualifierName>[a-zA-Z0-9_.$<>]*)"
					+ "\\((?<description>(?<fileLine>(?<CLASS>[a-zA-Z0-9_$]*)."
					+ "[a-z]+:(?<LINE>[0-9]*))|(?<native>Native Method)|(?<unk>Unkn"
					+ "own Source))\\)\\r*\\s*)");
	static private final Pattern mException = Pattern
			.compile("(?<exception>[a-zA-Z0-9_.]+Exception):?");
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

	public static String getException(String s) {

		Matcher matcher = mException.matcher(s);
		while (matcher.find()) {
			return matcher.group("exception");
		}
		return null;
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

	public static String splitNormalText(String s) {
		// String potentialText = splitQuotedText(s)[1];
		// if (potentialText.length() < 50)
		String potentialText = s;
		List<String> Exceptions = new ArrayList<String>();
		List<String> stackTraces = new ArrayList<>();
		StringBuilder strStackTrace = new StringBuilder();
		StringBuilder strNormal = new StringBuilder();
		int i = 0;
		int potE_begin = 0;
		int potE_end = 0;
		int suspicious_begin = 0;
		int suspicious_end = 0;
		int suspicious_newBegin = 0;
		int startCall = 0;
		boolean confirmed_call = false;
		int call_cluster = 0;
		boolean normalLine = true;
		boolean justCausedBy = false;
		while (i < potentialText.length()) {
			if (potentialText.charAt(i) == '\n') {
				suspicious_end = i;
				suspicious_begin = suspicious_newBegin;
				suspicious_newBegin = i + 1;
				normalLine = true;
				if (confirmed_call) {
					strStackTrace.append(potentialText
							.subSequence(startCall, i) + "\n");
					confirmed_call = false;
					call_cluster++;
					normalLine = false;
					justCausedBy = false;
				} else {
					if (call_cluster > 1) {
						if (potentialText.substring(suspicious_begin,
								suspicious_end).contains("Caused by:")) {
							normalLine = false;
							justCausedBy = true;
						} else {
							if (potentialText.substring(suspicious_begin,
									suspicious_end).contains("  ...")
									|| potentialText.substring(
											suspicious_begin, suspicious_end)
											.contains("\t...")) {
								normalLine = false;
							} else {
								stackTraces.add(strStackTrace.toString());
								Exceptions.add(potentialText.substring(
										potE_begin, potE_end));
								strStackTrace = new StringBuilder();
								call_cluster = 0;
							}
						}
					} else {
						potE_begin = suspicious_begin;
						potE_end = suspicious_end;
					}

				}

				if (normalLine) {
					if (justCausedBy) {
						stackTraces.add(strStackTrace.toString());
						Exceptions.add(potentialText.substring(potE_begin,
								potE_end));
						strStackTrace = new StringBuilder();
						justCausedBy = false;
					}
					strNormal.append(potentialText.subSequence(
							suspicious_begin, suspicious_end));
				}
			}

			if (i + 4 <= potentialText.length()) {
				if (potentialText.substring(i, i + 4).equals(" at ")
						|| potentialText.substring(i, i + 4).equals("\tat ")) {
					startCall = i;
					i = i + 4;
					int j = i;
					boolean braket1 = false;
					int spaceCounter = 0;
					while (j < potentialText.length()
							&& potentialText.charAt(j) != '\n') {
						if (!braket1) {
							if (potentialText.charAt(j) == ' ')
								break;
							if (potentialText.charAt(j) == '(')
								braket1 = true;
						} else {
							if (potentialText.charAt(j) == '(')
								break;
							if (potentialText.charAt(j) == ' ')
								spaceCounter++;
							if (spaceCounter > 1) // only 1 space in (Unknown
													// Source) or (Native
													// Method)
								break;
							if (potentialText.charAt(j) == ')')
								confirmed_call = true;
						}
						j++;
					}
					if (j < potentialText.length())
						if (potentialText.charAt(j) != '\n'
								|| potentialText.charAt(j) != '\r')
							j--;
					i = j;
				}
			}
			i++;
		}
		if (strStackTrace.length() > 1) {
			stackTraces.add(strStackTrace.toString());
			Exceptions.add(potentialText.substring(potE_begin, potE_end));
			strStackTrace = new StringBuilder();
		}
		List<StackTrace> results = new ArrayList<>();
		for (int j = 0; j < stackTraces.size(); j++) {
			results.add(new StackTrace(stackTraces.get(j), Exceptions.get(j),
					""));
		}
		return strNormal.toString();
	}

	public static List<StackTrace> splitStackTrace_v2(String s) {
		// String potentialText = splitQuotedText(s)[1];
		// if (potentialText.length() < 50)
		String potentialText = s;
		List<String> Exceptions = new ArrayList<String>();
		List<String> stackTraces = new ArrayList<>();
		StringBuilder strStackTrace = new StringBuilder();
		StringBuilder strNormal = new StringBuilder();
		int i = 0;
		int potE_begin = 0;
		int potE_end = 0;
		int suspicious_begin = 0;
		int suspicious_end = 0;
		int suspicious_newBegin = 0;
		int startCall = 0;
		boolean confirmed_call = false;
		int call_cluster = 0;
		boolean normalLine = true;
		boolean justCausedBy = false;
		while (i < potentialText.length()) {
			if (potentialText.charAt(i) == '\n') {
				suspicious_end = i;
				suspicious_begin = suspicious_newBegin;
				suspicious_newBegin = i + 1;
				normalLine = true;
				if (confirmed_call) {
					strStackTrace.append(potentialText
							.subSequence(startCall, i) + "\n");
					confirmed_call = false;
					call_cluster++;
					normalLine = false;
					justCausedBy = false;
				} else {
					if (call_cluster > 1) {
						if (potentialText.substring(suspicious_begin,
								suspicious_end).contains("Caused by:")) {
							normalLine = false;
							justCausedBy = true;
						} else {
							if (potentialText.substring(suspicious_begin,
									suspicious_end).contains("  ...")
									|| potentialText.substring(
											suspicious_begin, suspicious_end)
											.contains("\t...")) {
								normalLine = false;
							} else {
								stackTraces.add(strStackTrace.toString());
								Exceptions.add(potentialText.substring(
										potE_begin, potE_end));
								strStackTrace = new StringBuilder();
								call_cluster = 0;
							}
						}
					} else {
						potE_begin = suspicious_begin;
						potE_end = suspicious_end;
					}

				}

				if (normalLine) {
					if (justCausedBy) {
						stackTraces.add(strStackTrace.toString());
						Exceptions.add(potentialText.substring(potE_begin,
								potE_end));
						strStackTrace = new StringBuilder();
						justCausedBy = false;
					}
					strNormal.append(potentialText.subSequence(
							suspicious_begin, suspicious_end));
				}
			}

			if (i + 4 <= potentialText.length()) {
				if (potentialText.substring(i, i + 4).equals(" at ")
						|| potentialText.substring(i, i + 4).equals("\tat ")) {
					startCall = i;
					i = i + 4;
					int j = i;
					boolean braket1 = false;
					int spaceCounter = 0;
					while (j < potentialText.length()
							&& potentialText.charAt(j) != '\n') {
						if (!braket1) {
							if (potentialText.charAt(j) == ' ')
								break;
							if (potentialText.charAt(j) == '(')
								braket1 = true;
						} else {
							if (potentialText.charAt(j) == '(')
								break;
							if (potentialText.charAt(j) == ' ')
								spaceCounter++;
							if (spaceCounter > 1) // only 1 space in (Unknown
													// Source) or (Native
													// Method)
								break;
							if (potentialText.charAt(j) == ')')
								confirmed_call = true;
						}
						j++;
					}
					if (j < potentialText.length())
						if (potentialText.charAt(j) != '\n'
								|| potentialText.charAt(j) != '\r')
							j--;
					i = j;
				}
			}
			i++;
		}
		if (strStackTrace.length() > 1) {
			stackTraces.add(strStackTrace.toString());
			Exceptions.add(potentialText.substring(potE_begin, potE_end));
			strStackTrace = new StringBuilder();
		}
		List<StackTrace> results = new ArrayList<>();
		for (int j = 0; j < stackTraces.size(); j++) {
			results.add(new StackTrace(stackTraces.get(j), Exceptions.get(j),
					""));
		}

		if (results.size() == 0)
			return null;
		return results;
	}

	public static String[] splitQuotedText(String s) {
		StringBuilder strNormal = new StringBuilder();
		StringBuilder strQuoted = new StringBuilder();
		// boolean bQuoting = false;
		int i = 0;
		while (i < s.length()) {
			if (s.charAt(i) == '`') {
				int j = i;
				while (j < s.length() && s.charAt(j) == '`')
					j++;
				while (j < s.length() && s.charAt(j) != '`')
					strQuoted.append(s.charAt(j++));
				strQuoted.append("\n");
				while (j < s.length() && s.charAt(j) == '`')
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

	public static List<String> splitCalls(String s) {
		Matcher matcher = mStactTrace_at.matcher(s);
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
		if (issue.m_project.contains("/")) {
			return "=HYPERLINK(\"" + "https://github.com/" + issue.m_project
					+ "/issues/" + issue.m_id + "\",\"" + issue.m_project + "-"
					+ issue.m_id + "\")";
		} else {
			if (issue.m_project.equals("eclipseBugzilla"))
				return "=HYPERLINK(\""
						+ "https://bugs.eclipse.org/bugs/show_bug.cgi?id="
						+ issue.m_id + "\",\"" + issue.m_project + "-"
						+ issue.m_id + "\")";
			else {
				return "=HYPERLINK(\""
						+ "https://issues.apache.org/jira/browse/"
						+ issue.m_project + "-" + issue.m_id + "\",\""
						+ issue.m_project + "-" + issue.m_id + "\")";
			}

		}

	}

	public static String stripNonDigits(final CharSequence input) {
		final StringBuilder sb = new StringBuilder(input.length());
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			if (c > 47 && c < 58) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String stripNonChars(final CharSequence input) {
		final StringBuilder sb = new StringBuilder(input.length());
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			if ((c > 64 && c < 91) || (c > 96 && c < 123)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * This method ensures that the output String has only valid XML unicode
	 * characters as specified by the XML 1.0 standard. For reference, please
	 * see <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
	 * standard</a>. This method will return an empty String if the input is
	 * null or empty.
	 *
	 * @param in
	 *            The String whose non-valid characters we want to remove.
	 * @return The in String, stripped of non-valid characters.
	 */
	public static String stripNonValidXMLCharacters(String in) {
		StringBuffer out = new StringBuffer(); // Used to hold the output.
		char current; // Used to reference the current character.

		if (in == null || ("".equals(in)))
			return ""; // vacancy test.
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught
									// here; it should not happen.
			if ((current == 0x9) || (current == 0xA) || (current == 0xD)
					|| ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString();
	}
}
