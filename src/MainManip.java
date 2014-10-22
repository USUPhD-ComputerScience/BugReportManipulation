import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainManip {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String[] repoDetails = {
				"Cbsoftware/PressureNet", //
				"zxing/zxing", "realm/realm-java",
				"ReactiveX/RxJava",//
				"elasticsearch/elasticsearch",//
				"nostra13/Android-Universal-Image-Loader",
				"koush/AndroidAsync",//
				"square/picasso", "bumptech/glide", "square/okhttp" };//
		// String[] repoDetails = { "elasticsearch/elasticsearch" };
		/*
		 * reposdetail[10] = "EsotericSoftware/kryo"; reposdetail[11] =
		 * "PhilJay/MPAndroidChart"; reposdetail[12] = "dropwizard/metrics";
		 * reposdetail[13] = "JakeWharton/butterknife"; reposdetail[14] =
		 * "spring-projects/spring-boot"; reposdetail[15] = "kuujo/copycat";
		 * reposdetail[16] = "libgdx/libgdx"; reposdetail[17] =
		 * "square/retrofit"; reposdetail[18] = "GlowstoneMC/Glowstone";
		 * reposdetail[19] = "excilys/androidannotations";
		 */
		String example = "im getting error below after killing one node in the cluster \n"
				+ "(exception is thrown on remaining nodes)\n"
				+ "\n"
				+ "org.elasticsearch.common.util.concurrent.UncategorizedExecutionException: Failed execution\n"
				+ "        at org.elasticsearch.action.support.AdapterActionFuture.rethrowExecutionException(AdapterActionFuture.java:90)\n"
				+ "        at org.elasticsearch.action.support.AdapterActionFuture.actionGet(AdapterActionFuture.java:49)\n"
				+ "        at org.elasticsearch.action.ActionRequestBuilder.get(ActionRequestBuilder.java:67)\n"
				+ "        ...\n"
				+ "Caused by: java.util.concurrent.ExecutionException: java.lang.NullPointerException\n"
				+ "        at org.elasticsearch.common.util.concurrent.BaseFuture$Sync.getValue(BaseFuture.java:288)\n"
				+ "        at org.elasticsearch.common.util.concurrent.BaseFuture$Sync.get(BaseFuture.java:275)\n"
				+ "        at org.elasticsearch.common.util.concurrent.BaseFuture.get(BaseFuture.java:113)\n"
				+ "        at org.elasticsearch.action.support.AdapterActionFuture.actionGet(AdapterActionFuture.java:45)\n"
				+ "        ... 15 more\n"
				+ "Caused by: java.lang.NullPointerException\n"
				+ "        at org.elasticsearch.cluster.routing.IndexShardRoutingTable.getActiveAttribute(IndexShardRoutingTable.java:441)\n"
				+ "        at org.elasticsearch.cluster.routing.IndexShardRoutingTable.preferAttributesActiveInitializingShardsIt(IndexShardRoutingTable.java:488)\n"
				+ "        at org.elasticsearch.cluster.routing.IndexShardRoutingTable.preferAttributesActiveInitializingShardsIt(IndexShardRoutingTable.java:483)\n"
				+ "        at org.elasticsearch.cluster.routing.operation.plain.PlainOperationRouting.preferenceActiveShardIterator(PlainOperationRouting.java:169)\n"
				+ "        at org.elasticsearch.cluster.routing.operation.plain.PlainOperationRouting.getShards(PlainOperationRouting.java:80)\n"
				+ "        at org.elasticsearch.action.get.TransportGetAction.shards(TransportGetAction.java:80)\n"
				+ "        at org.elasticsearch.action.get.TransportGetAction.shards(TransportGetAction.java:42)\n"
				+ "        at org.elasticsearch.action.support.single.shard.TransportShardSingleOperationAction$AsyncSingleAction.<init>(TransportShardSingleOperationAction.java:121)\n"
				+ "        at org.elasticsearch.action.support.single.shard.TransportShardSingleOperationAction$AsyncSingleAction.<init>(TransportShardSingleOperationAction.java:97)\n"
				+ "        at org.elasticsearch.action.support.single.shard.TransportShardSingleOperationAction.doExecute(TransportShardSingleOperationAction.java:74)\n"
				+ "        at org.elasticsearch.action.support.single.shard.TransportShardSingleOperationAction.doExecute(TransportShardSingleOperationAction.java:49)\n"
				+ "        at org.elasticsearch.action.support.TransportAction.execute(TransportAction.java:63)\n"
				+ "        at org.elasticsearch.client.node.NodeClient.execute(NodeClient.java:92)\n"
				+ "        at org.elasticsearch.client.support.AbstractClient.get(AbstractClient.java:179)\n"
				+ "        at org.elasticsearch.action.get.GetRequestBuilder.doExecute(GetRequestBuilder.java:112)\n"
				+ "        at org.elasticsearch.action.ActionRequestBuilder.execute(ActionRequestBuilder.java:85)\n"
				+ "        at org.elasticsearch.action.ActionRequestBuilder.execute(ActionRequestBuilder.java:59)\n"
				+ "        \n"
				+ "context:\n"
				+ "\n"
				+ "version: 0.90.9\n"
				+ "3 node cluster\n"
				+ "2 replicas\n"
				+ "10 shards per index\n"
				+ "im getting error below after killing one node in the cluster \n"
				+ "(exception is thrown on remaining nodes)\n"
				+ "\n"
				+ "org.elasticsearch.common.util.concurrent.UncategorizedExecutionException: Failed execution\n"
				+ "        at org.elasticsearch.action.support.AdapterActionFuture.rethrowExecutionException(AdapterActionFuture.java:90)\n"
				+ "        at org.elasticsearch.action.support.AdapterActionFuture.actionGet(AdapterActionFuture.java:49)\n"
				+ "        at org.elasticsearch.action.ActionRequestBuilder.get(ActionRequestBuilder.java:67)\n"
				+ "        ...\n"
				+ "Caused by: java.util.concurrent.ExecutionException: java.lang.NullPointerException\n"
				+ "        at org.elasticsearch.common.util.concurrent.BaseFuture$Sync.getValue(BaseFuture.java:288)\n"
				+ "        at org.elasticsearch.common.util.concurrent.BaseFuture$Sync.get(BaseFuture.java:275)\n"
				+ "        at org.elasticsearch.common.util.concurrent.BaseFuture.get(BaseFuture.java:113)\n"
				+ "        at org.elasticsearch.action.support.AdapterActionFuture.actionGet(AdapterActionFuture.java:45)\n"
				+ "        ... 15 more\n"
				+ "Caused by: java.lang.NullPointerException\n"
				+ "        at org.elasticsearch.cluster.routing.IndexShardRoutingTable.getActiveAttyribute(IndexShardRoutingTable.java:441)\n"
				+ "        at org.elasticsearch.cluster.routing.IndexShardRoutingTable.preferAttributesActiveInitializingShardsIt(IndexiShardRoutingTable.java:488)\n"
				+ "        at org.elasticsefarch.cluster.routing.IndexShardRoutingTable.preferAttributesActiveInitializingShardsIt(IndexShardRoutingTable.java:483)\n"
				+ "        at org.elasticsearch.cluster.routing.operation.plain.PlainOperationRouting.preferenceActiveShardIterator(PlainOperationRouting.java:169)\n"
				+ "        at org.elasticsearch.cluster.routing.operation.plain.PlainOperationRouting.getShards(PlainOperationRouting.java:80)\n"
				+ "        at org.elasticsearch.action.get.TransportGetAfction.shards(TransportGetAction.java:80)\n"
				+ "        at org.elasticsearch.actsion.support.single.shard.TransportShardSingleOperationAction$AsyncSingleAction.<init>(TransportShardSingleOperationAction.java:121)\n"
				+ "        at org.elasticsearch.action.support.single.shard.TransportShardSingleOperationAction$AsyncSingleAction.<init>(TransportShardSingleOperationAction.java:97)\n"
				+ "        at org.elasticsearch.action.support.single.shard.TransportShardSingleOperationAction.doExecute(TransportShardSingleOperationAction.java:74)\n"
				+ "        at org.elasticsearch.action.support.single.shard.TransportShardSingleOperationAction.doExecute(TransportShardSingleOperationAction.java:49)\n"
				+ "        at org.elasticsearch.action.support.TransportAction.execute(TransportAction.java:63)\n"
				+ "        at orag.elasticsearch.client.node.NodeClient.execute(NodeClient.java:92)\n"
				+ "		   at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n"
				+ "		   at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n"
				+ "		   at java.lang.reflect.Method.invoke(Method.java:606)\n"
				+ "        at org.elasticsearch.client.support.AbstractClient.get(AbstractClient.java:179)\n"
				+ "        at org.elasticsearch.action.get.GetRequestBuilder.doExecute(GetRequegstBuilder.java:112)\n"
				+ "        at org.elasticsearch.action.ActionRequestBuilder.execute(ActionRequestBuilder.java:85)\n"
				+ "        at org.elasticsearch.action.ActionRequestBueilder.execute(ActionRequestBuilder.java:59)\n"
				+ "        \n"
				+ "context:\n"
				+ "\n"
				+ "version: 0.90.9\n"
				+ "3 node cluster\n" + "2 replicas\n" + "10 shards per index";
		String quotedExample = "We have two kinds of nodes: those with ssds (used for indexing and search recent data), those with large spinning disks (used for archiving old indices).\r\n\r\nI'd like to setup a mechanism to move old indices from ssds to spinning disks.\r\n\r\nThe first solution uses reroute command in cluster api. However it feels unnatural since you have to do it shard by shard and decide the target node.\r\n\r\nWhat I want to achieve is the following:\r\n1. stick recent indices (the current one being written) to ssds. They have 2 copies.\r\n2. at some point (disk on ssds is above 65%), one copy is moved to larger boxes (1 copy is still on ssd to help search, 1 copy on large box)\r\n3. when disk is scarce on ssd boxes (90%), we simply drop the copy present on ssd. Since we don't care that much of old data having only one copy is not an issue.\r\n\r\nI have tried to implement this with shard awareness allocation and allocation filtering but it does not seem to work as expected.\r\n\r\nNodes have ```flavor``` attribute depending on their hardware (```ssd``` or ```iodisk```).\r\nCluster is using shard awareness based on ```flavor``` attribute (```cluster.routing.allocation.awareness.attributes: flavor```).\r\n\r\n1. My index template has ```routing.allocation.require: ssd``` to impose two have all copies on ssds first. \r\n2. At some point, I drop the requirement (effectively ``routing.allocation.require: *```). I expect flavor awareness to move one copy to large (iodisk) boxes.\r\n3. At a later point, I'll set ```number_of_replicas``` to 0 and change ```routing.allocation.require``` to ```iodisk``` to drop the shard copy on ssds\r\n\r\nSadly allocation filtering and shard awareness do not seem to cooperate well :\r\nwhen an new index is created, one copy goes to ssds and the other is not allocated anywhere (index stays in yellow state).\r\n\r\nUsing ```curl -XPUT localhost:9200/_cluster/settings -d '{\"transient\":{\"logger.cluster.routing.allocation\":\"trace\"}}'```,\r\nI have observed what happen when a new index is created.\r\n\r\n```\r\n[2014-10-16 06:53:19,462][TRACE][cluster.routing.allocation.decider] [bungeearchive01-par.storage.criteo.preprod] Can not allocate [[2014-10-16.01][3], node[null], [R], s[UNASSIGNED]] on node [qK34VLdhTferCQs2oNJOyg] due to [SameShardAllocationDecider]\r\n[2014-10-16 06:53:19,463][TRACE][cluster.routing.allocation.decider] [bungeearchive01-par.storage.criteo.preprod] Can not allocate [[2014-10-16.01][3], node[null], [R], s[UNASSIGNED]] on node [gE7OTgevSUuoj44RozxK0Q] due to [AwarenessAllocationDecider]\r\n[2014-10-16 06:53:19,463][TRACE][cluster.routing.allocation.decider] [bungeearchive01-par.storage.criteo.preprod] Can not allocate [[2014-10-16.01][3], node[null], [R], s[UNASSIGNED]] on node [Y2k9qXfsTx6X2iQTxg9RBQ] due to [AwarenessAllocationDecider]\r\n[2014-10-16 06:53:19,463][TRACE][cluster.routing.allocation.decider] [bungeearchive01-par.storage.criteo.preprod] Can not allocate [[2014-10-16.01][3], node[null], [R], s[UNASSIGNED]] on node [FwWc2XPPRWuje2KH6AlDEQ] due to [FilterAllocationDecider]\r\n[2014-10-16 06:53:19,492][TRACE][cluster.routing.allocation.allocator] [bungeearchive01-par.storage.criteo.preprod] No Node found to assign shard [[2014-10-16.01][3], node[null], [R], s[UNASSIGNED]]\r\n```\r\n\r\nThis transcript shows that \r\n- shard 3 primary replica is on node qK34VLdhTferCQs2oNJOyg (flavor:ssd) which prevent its copy to placed there\r\n- it cannot be placed on gE7OTgevSUuoj44RozxK0Q (ssd as well) because it tries to maximizes dispersion accross flavors\r\n- it cannot be placed on Y2k9qXfsTx6X2iQTxg9RBQ for the same reason\r\n- it cannot be placed on FwWc2XPPRWuje2KH6AlDEQ (flavor: iodisk) because of the filter\r\n\r\nQuestions:\r\n- am I doing it wrong?\r\n- should I stick with a set of reroute command?\r\n- are awareness and filtering supposed to cooperate?\r\n\r\n,";
		List<StackTrace> stacktraces = Util.splitStackTrace(example);
		// stacktraces.get(0).findSimilarPairs(stacktraces.get(0).buildSimilarityMatrix(stacktraces.get(1)));;
		long startTime = System.nanoTime();

		System.out.println("PDM Similarity = "
				+ stacktraces.get(0).compareTo(stacktraces.get(1)));

		long endTime = System.nanoTime();
		long duration = (endTime - startTime); // divide by 1000000 to get //
												// milliseconds.
		System.out.println("The whole process ate up: "
				+ (duration / 1000000 / 1000 ) + " seconds");
		// for (int i = 0; i < stacktraces.length-1; i++){
		// Util.splitException(stacktraces[i]);
		// }
		// Util.splitQuotedText(quotedExample);
		/*
		 * String[] issuesUnderObservation = { "4209", "3995", "4801", "1527",
		 * "6617", "2522", "7168" }; long startTime = System.nanoTime(); //
		 * IssueManager.getInstance().loadRepositoriesToLocal(repoDetails);
		 * IssueManager.getInstance().processLocalRepositories(repoDetails);
		 * IssueManager.getInstance().buildVector(); LSHasher hasher = new
		 * LSHasher(0.5); hasher.generateHashVectors(Issue.df);
		 * hasher.categorizeLSH(IssueManager.getInstance().issueData); int count
		 * = 0; CSVWriter writer = null; CSVWriter tfidfReportWriter = null; try
		 * { writer = new CSVWriter(new FileWriter("\\data\\" + "Similar_issue_"
		 * + System.currentTimeMillis() + ".csv"));
		 * 
		 * tfidfReportWriter = new CSVWriter(new FileWriter("\\data\\" +
		 * "tfidf_report_" + System.currentTimeMillis() + ".csv"));
		 * 
		 * for (Map.Entry<Integer, List<Issue>> entry : hasher.mCategories
		 * .entrySet()) { int key = entry.getKey(); List<Issue> issues =
		 * entry.getValue(); System.out .println("Category[" + key + "] has " +
		 * issues.size()); for (int i = 0; i < (issues.size() - 1); i++) { for
		 * (int j = i + 1; j < issues.size(); j++) { double sim =
		 * Issue.CosineSimilarity(issues.get(i), issues.get(j)); if (sim >= 0.8)
		 * { writePair(writer, issues.get(i), issues.get(j), sim,
		 * tfidfReportWriter, issuesUnderObservation); count++; if (count % 1000
		 * == 0) { System.out.println("..found " + count + " similar pairs..");
		 * // writer.flush(); // tfidfReportWriter.flush(); // return; } } } } }
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } finally { tfidfReportWriter.close();
		 * writer.close(); long endTime = System.nanoTime(); long duration =
		 * (endTime - startTime); // divide by 1000000 to get // milliseconds.
		 * System.out.println("..found " + count + " similar pairs..");
		 * 
		 * System.out.println("The whole process ate up: " + (duration / 1000000
		 * / 1000 / 60) + " minutes");
		 * 
		 * }
		 */

	}

	private static void writePair(CSVWriter writer, Issue issue1, Issue issue2,
			double sim, CSVWriter tfidfReportWriter,
			String[] issuesUnderObservation) {
		// TODO Auto-generated method stub

		String[] entries = new String[3];
		entries[0] = issue1.m_project + "-" + issue1.m_id;
		entries[1] = issue2.m_project + "-" + issue2.m_id;
		entries[2] = String.valueOf(sim);
		// entries[2] = "[" + issue1.m_title + "] " + issue1.m_body;
		// entries[3] = "[" + issue2.m_title + "] " + issue2.m_body;
		writer.writeNext(entries);
		// ghi ra mot file csv co' 4 columns: id1-id2,terms, tfidf1, tfidf2
		for (int i = 0; i < issuesUnderObservation.length; i++) {
			if (issue1.m_id.equalsIgnoreCase(issuesUnderObservation[i])) {
				String[] reports = new String[4];
				reports[0] = issue1.m_project + "-" + issue1.m_id + " >>><<< "
						+ issue2.m_project + "-" + issue2.m_id + " @" + sim;
				reports[1] = "Terms";
				reports[2] = "TFIDF-" + issue1.m_id;
				reports[3] = "TFIDF-" + issue2.m_id;
				tfidfReportWriter.writeNext(reports);
				for (Map.Entry<String, Double> entry : issue1.tfidf.entrySet()) {
					String key = entry.getKey();
					reports[0] = "";
					reports[1] = key;
					reports[2] = entry.getValue().toString();
					if (issue2.tfidf.containsKey(key)) {
						reports[3] = issue2.tfidf.get(key).toString();
					} else {
						reports[3] = "0";
					}
					tfidfReportWriter.writeNext(reports);
				}
				for (Map.Entry<String, Double> entry : issue2.tfidf.entrySet()) {
					String key = entry.getKey();
					if (!issue1.tfidf.containsKey(key)) {
						reports[0] = "";
						reports[1] = key;
						reports[2] = "0";
						reports[3] = entry.getValue().toString();
						tfidfReportWriter.writeNext(reports);
					}
				}
			}
		}
	}

}
