import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class IssueManager {

	public ArrayList<Issue> issueData = new ArrayList<Issue>();
	static private IssueManager instance;

	public static IssueManager getInstance() {
		if (instance == null) {
			instance = new IssueManager();
		}
		return instance;
	}

	public void processLocalRepositories(String[] repoDetails) {
		// TODO Auto-generated constructor stub
		System.out.println("Preparing data..");
		for (int i = 0; i < repoDetails.length; i++) {
			String prjName = repoDetails[i].split("/")[1];
			try {
				CSVReader reader = new CSVReader(new FileReader("\\data\\"
						+ prjName + "_issues.csv"));
				String[] entries = reader.readNext();
				while ((entries = reader.readNext()) != null) {

					if (entries[2].length() < 5 || entries[1].length() < 5)
						continue;

					issueData.add(new Issue(entries[0], entries[1], entries[2],
							repoDetails[i]));
				}
				reader.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println("Error: Cannot read files " + repoDetails[i]
						+ "_issues.csv");
				e1.printStackTrace();
			}

		}
		System.out.println("-----Done with preparing data-----");
	}

	public int processLocalRepositories_ST(String[] repoDetails) {
		// TODO Auto-generated constructor stub
		System.out.println("Preparing data..");
		int issueCount = 0;
		for (int i = 0; i < repoDetails.length; i++) {
			String prjName = repoDetails[i].split("/")[1];
			try {
				CSVReader reader = new CSVReader(new FileReader("\\data\\"
						+ prjName + "_issues.csv"));
				String[] entries = reader.readNext();
				while ((entries = reader.readNext()) != null) {

					if (entries[2].length() < 5 || entries[1].length() < 5)
						continue;

					List<StackTrace> mStackTraces = Util
							.splitStackTrace(entries[2]);
					if (mStackTraces != null) {
						issueData.add(new Issue(entries[0], entries[1],
								entries[2], repoDetails[i]));
					}
					issueCount++;
				}
				reader.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println("Error: Cannot read files " + repoDetails[i]
						+ "_issues.csv");
				e1.printStackTrace();
			}

		}
		System.out.println("-----Done with preparing data-----");
		return issueCount;
	}

	public void buildVector() {
		for (Issue issue : issueData)
			issue.buildVector();
		for (Issue issue : issueData)
			issue.normalizeVector();
	}

	public void loadRepositoriesToLocal(String[] repoDetails) {
		try {
			GitHub github;
			github = GitHub.connectUsingPassword("phong1990", "phdcs2014");
			for (int i = 0; i < repoDetails.length; i++) {
				GHRepository repo = github.getRepository(repoDetails[i]);
				CSVWriter writer = new CSVWriter(new FileWriter("\\data\\"
						+ repoDetails[i].split("/")[1] + "_issues.csv"));
				String[] entries = "id,title,body".split(",");
				writer.writeNext(entries);
				List<GHPullRequest> pullRequest = repo
						.getPullRequests(GHIssueState.CLOSED);
				// //
				for (GHIssue issue : repo.getIssues(GHIssueState.CLOSED)) {
					/*
					 * //checking labels for (GHIssue.Label label :
					 * issue.getLabels()) { if (label.getName().contains("bug"))
					 * { isBug = true; break; } }
					 */
					// if (isBug) {
					boolean bcontinue = true;
					for (GHPullRequest pullr : pullRequest) {
						if (issue.getNumber() == pullr.getNumber()) {
							// System.out.println(issue.getNumber());
							bcontinue = false;
							break;
						}
					}
					if (bcontinue) {
						entries[0] = String.valueOf(issue.getNumber());
						entries[1] = Util.normalizeString(issue.getTitle());
						entries[2] = Util.normalizeString(issue.getBody());
						writer.writeNext(entries);
					}
					// }
					// isBug = false;
				}
				writer.close();
				System.out.println("Done with " + repoDetails[i]);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
