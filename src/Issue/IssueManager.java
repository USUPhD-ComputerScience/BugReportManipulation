package Issue;

import java.io.FileReader;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import Util.MySQLConnector;
import Util.Util;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class IssueManager {

	public ArrayList<Issue> issueData = new ArrayList<Issue>();
	static private IssueManager instance;
	public static final String DATABASE = "issuedb";
	public static final String ISSUES_TABLE_DB = "issues";
	public static final String PROJECTS_TABLE_DB = "projects";
	public static final String GITHUB_TABLE_DB = "github";

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

	private List<String> getRepositories() {
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
		MySQLConnector db = null;
		int issueCount = 0;
		try {
			db = new MySQLConnector("phong1990", "phdcs2014",
					IssueManager.DATABASE);
			String prjidfield[] = { "project_id" };
			ResultSet project_list = db.select(IssueManager.PROJECTS_TABLE_DB,
					prjidfield, null);
			ResultSet resultSet = null;
			List<Integer> project_id = new ArrayList<>();
			while (project_list.next()) {
				project_id.add(project_list.getInt("project_id"));
			}
			project_list.close();

			for (int prjID : project_id) {

				String fields[] = { "prj.name", "iss.issue_id",
						"iss.issue_summary", "iss.issue_body" };
				resultSet = db.select(IssueManager.PROJECTS_TABLE_DB + " prj, "
						+ IssueManager.ISSUES_TABLE_DB + " iss", fields,
						"prj.project_id=iss.project_id AND prj.project_id = '" + prjID + "'");
				while (resultSet.next()) {
					String summary = resultSet.getString("iss.issue_summary");
					String body = resultSet.getString("iss.issue_body");
					String issueID = String.valueOf(resultSet
							.getInt("iss.issue_id"));
					String projectName = resultSet.getString("prj.name");
					if (summary == null)
						continue;
					if (body == null)
							continue;
					if (summary.length() < 5 || body.length() < 5)
						continue;
					List<StackTrace> mStackTraces = Util.splitStackTrace_v2(body);
					// List<StackTrace> mStackTraces = Util
					// .splitStackTrace_v2(body);
					// only take issue with stacktrace
					if (mStackTraces != null) {
						issueData.add(new Issue(issueID, summary, body,
								projectName));
					}
					issueCount++;
				}
				resultSet.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
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
