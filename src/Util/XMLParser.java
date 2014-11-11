package Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import Issue.Issue;
import Issue.IssueManager;

public class XMLParser {
	String mFileName = "";

	public XMLParser(String fileName) throws IOException,
			ParserConfigurationException, SAXException {
		// TODO Auto-generated constructor stub
		mFileName = fileName;

		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());

	}

	// Using StAX parser
	public int loadToDB_v2() {
		Issue currIssue = null;
		boolean resolved = false;
		int count = 0;
		MySQLConnector db = null;
		System.out.println(">>Starting to import new data..");
		StringBuilder comments = new StringBuilder();
		StringBuilder tagContent = new StringBuilder();
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
				Boolean.TRUE);
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
				Boolean.FALSE);
		// IS_COALESCING property t
		// IS_COALESCING property to true
		// gets whole text data as one event
		factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
		XMLStreamReader reader;
		try {
			FileInputStream fis = new FileInputStream(mFileName);
			reader = factory.createXMLStreamReader(fis);

			db = new MySQLConnector("phong1990", "phdcs2014",
					IssueManager.DATABASE);
			while (reader.hasNext()) {

				int event = reader.next();
				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					if ("bug".equals(reader.getLocalName())) {
						if (resolved) {

							if (Integer.valueOf(currIssue.m_id) > 372502) {

								currIssue.m_comments = comments.toString();
								comments = new StringBuilder();
								// currIssue.print();
								insertDB(db, currIssue, count);
								count++;

							}
							resolved = false;

						}
						currIssue = new Issue();
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					tagContent.append(reader.getText().trim());
					break;
				case XMLStreamConstants.END_ELEMENT:
					switch (reader.getLocalName()) {
					case "bug_id":
						currIssue.m_id = Util.stripNonDigits(tagContent
								.toString());
						break;
					case "short_desc":
						currIssue.m_title = tagContent.toString();
						break;
					case "text":
						if (currIssue.m_body == null)
							currIssue.m_body = tagContent.toString();
						else
							comments.append("<Comment>" + tagContent.toString()
									+ "</Comment>\n");
						break;
					case "bug_status":
						if (Util.stripNonChars(tagContent.toString()).equals(
								"RESOLVED"))
							resolved = true;
						break;
					}
					tagContent = new StringBuilder();
					break;
				case XMLStreamConstants.START_DOCUMENT:
					break;
				}
			}
			if (resolved) {
				currIssue.m_comments = comments.toString();
				insertDB(db, currIssue, count);
				count++;
			}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
		return count;
	}

	private void insertDB(MySQLConnector db, Issue issue, int count)
			throws SQLException {
		String prjID = "378";
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

		if (count % 1000 == 0) {

			System.out.println("Issue " + count + "th: " + issue.m_id + "...");
		}
	}

	// using DOM PARSER
	public int loadToDB() {
		MySQLConnector db = null;
		int count = 0;
		System.out.println(">>Starting to import new data..");
		try {

			File fXmlFile = new File(mFileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document mDoc = null;
			mDoc = dBuilder.parse(fXmlFile);
			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			mDoc.getDocumentElement().normalize();

			String prjID = "378";
			db = new MySQLConnector("phong1990", "phdcs2014",
					IssueManager.DATABASE);
			NodeList nList = mDoc.getElementsByTagName("bug");
			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					NodeList comments = eElement
							.getElementsByTagName("comment");
					Element body = (Element) comments.item(0);

					if (!Util.stripNonChars(
							eElement.getElementsByTagName("bug_status").item(0)
									.getTextContent()).equals("RESOLVED"))
						continue;

					StringBuilder commentText = new StringBuilder();
					for (int j = 1; j < comments.getLength(); j++) {
						Element cmt = (Element) comments.item(j);
						commentText.append(cmt.getElementsByTagName("text")
								.item(0).getTextContent());
					}
					String summary = eElement
							.getElementsByTagName("short_desc").item(0)
							.getTextContent();
					String issueID = Util.stripNonDigits(eElement
							.getElementsByTagName("bug_id").item(0)
							.getTextContent());
					String bodytext = body.getElementsByTagName("text").item(0)
							.getTextContent();

					if (bodytext == null)
						continue;
					if (summary == null)
						continue;

					if (summary.length() < 5 || bodytext.length() < 5)
						continue;
					String values[] = { prjID, issueID, summary, bodytext,
							commentText.toString() };
					db.insert(IssueManager.ISSUES_TABLE_DB, values);
					count++;
					if (count % 1000 == 0) {

						System.out.println("Issue " + count + "th: " + issueID
								+ "...");
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
		System.out.println(">>Done: " + count + " issues.");

		return count;
	}

}
