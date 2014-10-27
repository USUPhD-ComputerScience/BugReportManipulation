package Issue;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import Util.Issue;
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

	public void processLocalRepositories(List<String> repoDetails) {
		// TODO Auto-generated constructor stub
		System.out.println("Preparing data..");
		for (int i = 0; i < repoDetails.size(); i++) {
			String prjName = repoDetails.get(i).split("/")[1];
			try {
				CSVReader reader = new CSVReader(new FileReader("\\IssueData\\"
						+ prjName + "_issues.csv"));
				String[] entries = reader.readNext();
				while ((entries = reader.readNext()) != null) {

					if (entries[2].length() < 5 || entries[1].length() < 5)
						continue;

					issueData.add(new Issue(entries[0], entries[1], entries[2],
							repoDetails.get(i)));
				}
				reader.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println("Error: Cannot read files "
						+ repoDetails.get(i) + "_issues.csv");
				e1.printStackTrace();
			}

		}
		System.out.println("-----Done with preparing data-----");
	}

	private List<String> getRepositories(){
		// getting the list of repository name
		List<String> repoDetails = new ArrayList<>();
		try {
			CSVReader reader = new CSVReader(new FileReader(
					"\\IssueData\\project_list.csv"));
			String[] entries = reader.readNext();
			while ((entries = reader.readNext()) != null) {
				repoDetails.add(entries[0]);
			}
			reader.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return repoDetails;
		// /////////////////////////
	}
	public int processLocalRepositories_ST() {
		// TODO Auto-generated constructor stub
		System.out.println(">>Preparing data..");

		List<String> repoDetails = getRepositories();
		int issueCount = 0;
		for (int i = 0; i < repoDetails.size(); i++) {
			String prjName = repoDetails.get(i).split("/")[1];
			try {
				CSVReader reader = new CSVReader(new FileReader("\\IssueData\\"
						+ prjName + "_issues.csv"));
				String[] entries = reader.readNext();
				while ((entries = reader.readNext()) != null) {

					if (entries[2].length() < 5 || entries[1].length() < 5)
						continue;

					List<StackTrace> mStackTraces = Util
							.splitStackTrace(entries[2]);
					if (mStackTraces != null) {
						issueData.add(new Issue(entries[0], entries[1],
								entries[2], repoDetails.get(i)));
					}
					issueCount++;
				}
				reader.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
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


}
