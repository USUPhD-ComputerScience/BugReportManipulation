package Crawler;
import java.awt.Choice;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.json.Json;
import javax.json.stream.JsonParser;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import Issue.Util;
import au.com.bytecode.opencsv.CSVWriter;

public class GitHubCrawler {
	// String[] mGitUsers;
	String mEncodedAuthorization;
	public static final String GITHUB_SEARCH_URL = "https://api.github.com/search/repositories?q=user:";
	public static final String GITHUB_SEARCHTAIL_URL = "+language:Java&sort=stars&order=desc&per_page=100";
	public static final String GITHUB_SEARCHUSER_URL = "https://api.github.com/search/users?q=type:user+language:Java+repos:%3E20&sort=stars&order=desc&per_page=100&page=";
	public static final String GITHUB_SEARCHORG_URL = "https://api.github.com/search/users?q=type:org+language:Java+repos:%3E20&sort=stars&order=desc&per_page=100&page=";

	public GitHubCrawler(String login, String password) {
		// mGitUsers = users;

		String authorization = (login + ':' + password);
		mEncodedAuthorization = "Basic "
				+ new String(Base64.encodeBase64(authorization.getBytes()));
	}

	public HashSet<String> crawlUser() {
		HashSet<String> choosenUsers = new HashSet<>();
		// https://api.github.com/users/google/repos
		URL url;
		try {
			// 10 pages
			for (int i = 1; i < 10; i++) {
				// Example:
				// https://api.github.com/search/repositories?q=user:google+language:Java&sort=stars&order=desc
				url = new URL(GITHUB_SEARCHUSER_URL+i);
				HttpURLConnection uc = setupConnection(url);
				choosenUsers.addAll(parseUsers(uc));
				TimeUnit.SECONDS.sleep(3);
				url = new URL(GITHUB_SEARCHORG_URL+i);
				uc = setupConnection(url);
				choosenUsers.addAll(parseUsers(uc));
				TimeUnit.SECONDS.sleep(3);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String user : choosenUsers) {
			System.out.println(user);
		}
		System.out.println(choosenUsers.size());
		return choosenUsers;
	}

	private HashSet<String> parseUsers(HttpURLConnection uc) throws IOException {
		InputStreamReader r = null;
		HashSet<String> choosenUsers = new HashSet<>();
		try {
			r = new InputStreamReader(wrapStream(uc, uc.getInputStream()),
					"UTF-8");
			String data = IOUtils.toString(r);
			JsonParser parser = Json.createParser(new StringReader(data));
			boolean bWriteNext = false;
			while (parser.hasNext()) {
				JsonParser.Event event = parser.next();
				switch (event) {
				case START_ARRAY:
				case END_ARRAY:
				case START_OBJECT:
				case END_OBJECT:
					// System.out.println(event.toString());
					break;
				case VALUE_FALSE:
					break;
				case VALUE_TRUE:
					break;
				case VALUE_NULL:
					break;
				case KEY_NAME:
					if (parser.getString().equals("login")) {
						// System.out.print(event.toString() + " "
						// + parser.getString() + " - ");
						bWriteNext = true;
					}
					break;
				case VALUE_STRING:
					if (bWriteNext) {
						// System.out.println(event.toString() + " "
						// + parser.getString());
						choosenUsers.add(parser.getString());
						bWriteNext = false;
					}
					break;
				case VALUE_NUMBER:
					break;
				}
			}
		} finally {
			IOUtils.closeQuietly(r);
		}
		return choosenUsers;
	}

	public List<String> crawlRepos(HashSet<String> mGitUsers) {
		List<String> choosenRepos = new ArrayList<>();
		for (String user : mGitUsers) {
			// https://api.github.com/users/google/repos
			URL url;
			try {
				// Example:
				// https://api.github.com/search/repositories?q=user:google+language:Java&sort=stars&order=desc
				url = new URL(GITHUB_SEARCH_URL + user + GITHUB_SEARCHTAIL_URL);
				HttpURLConnection uc = setupConnection(url);
				choosenRepos.addAll(parseRepos(uc));
				TimeUnit.SECONDS.sleep(3);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return choosenRepos;
	}

	private List<String> parseRepos(HttpURLConnection uc) throws IOException {
		InputStreamReader r = null;
		List<String> choosenRepos = new ArrayList<>();
		try {
			r = new InputStreamReader(wrapStream(uc, uc.getInputStream()),
					"UTF-8");
			String data = IOUtils.toString(r);
			JsonParser parser = Json.createParser(new StringReader(data));
			boolean bWriteNext = false, bCheckIssues = false;
			String repoBuffer = "";
			while (parser.hasNext()) {
				JsonParser.Event event = parser.next();
				switch (event) {
				case START_ARRAY:
				case END_ARRAY:
				case START_OBJECT:
				case END_OBJECT:
					// System.out.println(event.toString());
					break;
				case VALUE_FALSE:
					if (bCheckIssues) {
						// System.out.println("<nothing here>");
						bCheckIssues = false;
					}
					break;
				case VALUE_TRUE:
					if (bCheckIssues) {
						System.out.println(repoBuffer);
						bCheckIssues = false;
						choosenRepos.add(repoBuffer);
					}
					break;
				case VALUE_NULL:
					break;
				case KEY_NAME:
					if (parser.getString().equals("full_name")) {
						// System.out.print(event.toString() + " "
						// + parser.getString() + " - ");
						bWriteNext = true;
					} else if (parser.getString().equals("has_issues")) {
						bCheckIssues = true;
					}
					break;
				case VALUE_STRING:
					if (bWriteNext) {
						// System.out.println(event.toString() + " "
						// + parser.getString());
						repoBuffer = parser.getString();
						bWriteNext = false;
					}
					break;
				case VALUE_NUMBER:
					break;
				}
			}
		} finally {
			IOUtils.closeQuietly(r);
		}
		return choosenRepos;
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
	public void loadRepositoriesToLocal(List<String> repoDetails) {
		try {
			GitHub github;
			github = GitHub.connectUsingPassword("phong1990", "phdcs2014");
			List<String> choosenRepos = new ArrayList<>();
			for (int i = 0; i < repoDetails.size(); i++) {
				GHRepository repo = github.getRepository(repoDetails.get(i));
				List<GHPullRequest> pullRequest = repo
						.getPullRequests(GHIssueState.CLOSED);
				// //
				List<GHIssue> issues = repo.getIssues(GHIssueState.CLOSED);
				if (issues.size() - pullRequest.size() > 50) {
					CSVWriter writer = new CSVWriter(new FileWriter("\\IssueData\\"
							+ repoDetails.get(i).split("/")[1] + "_issues.csv"));
					choosenRepos.add(repoDetails.get(i));
					String[] entries = "id,title,body".split(",");
					writer.writeNext(entries);
					for (GHIssue issue : issues) {
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
					System.out.println("Done with " + repoDetails.get(i));
					TimeUnit.SECONDS.sleep(1);
				}
			}
			// Write to a file contains list of all repository names
			CSVWriter reposWriter = new CSVWriter(new FileWriter(
					"\\IssueData\\project_list.csv"));
			String[] entries = new String[1];
			for (String project : choosenRepos) {
				entries[0] = project;
				reposWriter.writeNext(entries);
			}
			reposWriter.close();
			System.out.println(">>Fetched: " + choosenRepos.size()
					+ " repositories.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
