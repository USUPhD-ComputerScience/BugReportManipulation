package Crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.lang.reflect.Field;

import javax.json.Json;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import Issue.Issue;
import Issue.IssueManager;
import Util.MySQLConnector;
import Util.Util;

import javax.json.stream.JsonParser;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class ApacheJiraCrawler {
	// String[] mGitUsers;
	String mEncodedAuthorization;
	int mMaxResults = 0;
	public static final String JIRA_SEARCH_URL_PART1 = "https://issues.apache.org/jira/rest/api/2/search?jql=project=";
	public static final String JIRA_SEARCH_URL_PART2 = "+AND+status+%3D+Resolved+AND+Resolution+%3D+Fixed&startAt=";
	public static final String JIRA_SEARCH_URL_PART3 = "&maxResults=";
	public static final String JIRA_SEARCH_URL_MAXRESULT = "-1";
	public static final String JIRA_SEARCH_URL_NORESULT = "0";

	public ApacheJiraCrawler(String login, String password) {
		// mGitUsers = users;

		String authorization = (login + ':' + password);
		mEncodedAuthorization = "Basic "
				+ new String(Base64.encodeBase64(authorization.getBytes()));

	}

	public int crawlProjects(String prj) {
		int choosenIssues = 0;

		MySQLConnector db = null;
		try {
			db = new MySQLConnector("phong1990", "phdcs2014",
					IssueManager.DATABASE);
			// https://api.github.com/users/google/repos

			String[] entries = { prj, "0" };
			String prjNo = String.valueOf(db.insert(
					IssueManager.PROJECTS_TABLE_DB, entries));
			
			URL url;
			int totalIssue = 0;
			int startAt = 0;
			// Example:
			// https://api.github.com/search/repositories?q=user:google+language:Java&sort=stars&order=desc
			totalIssue = getTotalIssue(prj);
			url = new URL(JIRA_SEARCH_URL_PART1 + prj + JIRA_SEARCH_URL_PART2
					+ startAt + JIRA_SEARCH_URL_PART3
					+ JIRA_SEARCH_URL_MAXRESULT);
			HttpURLConnection uc = setupConnection(url);
			choosenIssues += parseIssues(uc, db, prjNo);
			while (startAt < totalIssue - mMaxResults) {
				startAt += mMaxResults;
				url = new URL(JIRA_SEARCH_URL_PART1 + prj
						+ JIRA_SEARCH_URL_PART2 + startAt
						+ JIRA_SEARCH_URL_PART3 + JIRA_SEARCH_URL_MAXRESULT);
				uc = setupConnection(url);
				choosenIssues += parseIssues(uc, db, prjNo);
			}
			System.out.println("Done with project: " + prj);

			System.out.println("Max Result per Page = " + mMaxResults);
			System.out.println("Total issue = " + totalIssue);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return choosenIssues;
	}

	private void insertDB(MySQLConnector db, Issue issue, String prjNo)
			throws SQLException {
		String prjID = prjNo;
		if (issue.m_id == null)
			return;
		if (issue.m_body == null)
			return;
		if (issue.m_title == null)
			return;
		System.out.println("Issue: " + issue.m_id + "...");

		if (issue.m_title.length() < 5 || issue.m_body.length() < 5)
			return;
		String values[] = { prjID, issue.m_id, issue.m_title, issue.m_body,
				issue.m_comments };
		db.insert(IssueManager.ISSUES_TABLE_DB, values);
	}

	private int getTotalIssue(String prj) throws IOException,
			InterruptedException {
		int total = 0;
		boolean btotal = false;
		URL url = new URL(JIRA_SEARCH_URL_PART1 + prj + JIRA_SEARCH_URL_PART2
				+ "0" + JIRA_SEARCH_URL_PART3 + JIRA_SEARCH_URL_NORESULT);
		HttpURLConnection uc = setupConnection(url);

		InputStreamReader r = null;
		r = new InputStreamReader(wrapStream(uc, uc.getInputStream()), "UTF-8");
		String data = IOUtils.toString(r);
		JsonParser parser = Json.createParser(new StringReader(data));
		while (parser.hasNext()) {
			JsonParser.Event event = parser.next();
			switch (event) {
			case START_ARRAY:
			case END_ARRAY:
			case START_OBJECT:
			case END_OBJECT:
			case VALUE_FALSE:
			case VALUE_NULL:
			case VALUE_TRUE:
				break;
			case KEY_NAME:
				if (parser.getString().equals("total"))
					btotal = true;
				break;
			case VALUE_STRING:
			case VALUE_NUMBER:
				if (btotal) {
					btotal = false;
					total = parser.getInt();
				}
				break;
			}
		}
		TimeUnit.SECONDS.sleep(2);
		return total;
	}

	private int parseIssues(HttpURLConnection uc, MySQLConnector db,
			String prjNo) throws InterruptedException {
		int count = 0;
		InputStreamReader r = null;
		Issue currIssue = null;
		try {
			boolean bMaxResults = false;
			boolean bNext = false;
			boolean bEndObj = false;
			r = new InputStreamReader(wrapStream(uc, uc.getInputStream()),
					"UTF-8");
			String data = IOUtils.toString(r);
			JsonParser parser = Json.createParser(new StringReader(data));
			while (parser.hasNext()) {
				JsonParser.Event event = parser.next();
				switch (event) {
				case START_ARRAY:
					break;
				case END_ARRAY:
					break;
				case START_OBJECT:

					bEndObj = false;
					break;
				case END_OBJECT:
					bEndObj = true;
					break;

				case VALUE_FALSE:
					break;
				case VALUE_NULL:
					break;
				case VALUE_TRUE:
					break;
				case KEY_NAME:
					if (parser.getString().equals("maxResults"))
						bMaxResults = true;
					else if (parser.getString().equals("expand")) {
						if (currIssue != null)
							insertDB(db, currIssue, prjNo);
						currIssue = new Issue();
					} else {
						if (currIssue.m_title == null
								&& parser.getString().equals("summary")) {
							bNext = true;
						} else {
							if (currIssue.m_id == null
									&& parser.getString().equals("key")) {
								bNext = true;
							} else if (currIssue.m_body == null && bEndObj
									&& parser.getString().equals("description")) {
								bEndObj = false;
								bNext = true;
							}
						}
					}

					break;
				case VALUE_STRING:
					if (bNext) {
						bNext = false;
						if (currIssue.m_id == null)
							currIssue.m_id = Util.stripNonDigits(parser
									.getString().split("-")[1]);
						else if (currIssue.m_title == null)
							currIssue.m_title = parser.getString();
						else if (currIssue.m_body == null)
							currIssue.m_body = parser.getString();
					}
					break;
				case VALUE_NUMBER:
					if (bMaxResults) {
						bMaxResults = false;
						mMaxResults = parser.getInt();
					}
					break;
				}

			}

			if (currIssue != null)
				insertDB(db, currIssue, prjNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TimeUnit.SECONDS.sleep(2);
		return count;
	}

	/**
	 * Handles the "Content-Encoding" header.
	 */
	private InputStream wrapStream(HttpURLConnection uc, InputStream in)
			throws IOException {
		String encoding = uc.getContentEncoding();
		if (encoding == null || in == null)
			return in;
		if (encoding.equals("gzip"))
			return new GZIPInputStream(in);

		throw new UnsupportedOperationException("Unexpected Content-Encoding: "
				+ encoding);
	}

	private HttpURLConnection setupConnection(URL url) throws IOException {

		HttpURLConnection uc = (HttpURLConnection) url.openConnection();
		try {
			uc.setRequestMethod("GET"); // GET
		} catch (ProtocolException e) {
			// JDK only allows one of the fixed set of verbs. Try to override
			// that
			try {
				Field $method = HttpURLConnection.class
						.getDeclaredField("method");
				$method.setAccessible(true);
				$method.set(uc, "GET");
			} catch (Exception x) {
				throw (IOException) new IOException(
						"Failed to set the custom verb").initCause(x);
			}
		}
		uc.setRequestProperty("Authorization", mEncodedAuthorization);
		uc.setRequestProperty("Accept-Encoding", "gzip");
		return uc;
	}

}
