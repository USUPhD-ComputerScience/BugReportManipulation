import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.regex.*;


public class Util {
	static private final Pattern mPattern = Pattern
			.compile("(?<StackTrace>(\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\"
					+ "d{2}\\.\\d{3} [A-Z]+\\] (?<Message>[\\s\\S]*?)\\n?)*((C"
					+ "aused by: )?(?<EXCEPTION>[a-zA-Z0-9_.]+Exception)(?<Emes"
					+ "sage>[^\\r^\\n]+)?(?<ATmessage>\\s*at\\s(?<METHOD>[a-zA"
					+ "-Z0-9_.$<>]*)\\((?<description>(?<fileLine>(?<FILE>[a-z"
					+ "A-Z0-9_.$]*):(?<LINE>[0-9]+))|(?<native>Native Method)|"
					+ "(?<unk>Unknown Source))\\))+([\\n]*\\s*(...)?\\s*[0-9]* more)?)+)");
	private static HashSet<String> customStopWordList = null;

	static String normalizeString(String s) {
		if (s == null)
			return "";
		else
			return s.replace('\n', ' ').replace('\r', ' ').trim();
	}

	static boolean isStopWord(String s) {
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

	static String removeStackTrace(String s) {
		Matcher matcher = mPattern.matcher(s);
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

	static String[] splitStackTrace(String s) {
		Matcher matcher = mPattern.matcher(s);
		StringBuilder strNormal = new StringBuilder();
		StringBuilder strStackTrace = new StringBuilder();
		int beginIndex = 0;
		while (matcher.find()) {
			strStackTrace.append(matcher.group("StackTrace") + "\n");
			strNormal.append(s.substring(beginIndex, matcher.start(1)));
			beginIndex = matcher.end(1);
		}

		strNormal.append(s.substring(beginIndex, s.length()));

		/*
		 * System.out.println("No stacktrace:");
		 * System.out.println(strNormal.toString());
		 * System.out.println("Stacktrace:");
		 * System.out.println(strStackTrace.toString());
		 */
		String[] strSplitted = { strNormal.toString(), strStackTrace.toString() };
		return strSplitted;
	}

	static String[] splitQuotedText(String s) {
		StringBuilder strNormal = new StringBuilder();
		StringBuilder strQuoted = new StringBuilder();
		//boolean bQuoting = false;
		int i = 0;
		while(i<s.length()){
			if(s.charAt(i) == '`'){
				int j = i;
				while(s.charAt(j)== '`') j++;
				while(s.charAt(j)!= '`') strQuoted.append(s.charAt(j++));
				strQuoted.append("\n");
				while(s.charAt(j)== '`') j++;
				i = j;
			}else{
				strNormal.append(s.charAt(i++));
			}
		}

		System.out.println("Normal:");
		System.out.println(strNormal.toString());
		System.out.println("Quoted");
		System.out.println(strQuoted.toString());

		String[] strSplitted = { strNormal.toString(), strQuoted.toString() };
		return strSplitted;
	}
}
