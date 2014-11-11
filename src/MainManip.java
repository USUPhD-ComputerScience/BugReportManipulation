import Crawler.GitHubCrawler;
import Issue.Issue;
import Issue.IssueManager;
import Issue.StackTrace;
import Util.MySQLConnector;
import Util.Util;
import Util.XMLParser;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.plaf.synth.SynthTabbedPaneUI;
import javax.xml.parsers.ParserConfigurationException;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.xml.sax.SAXException;

public class MainManip {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String example = "im getting error below after killing one node in the cluster \n"
				+ "(exception is thrown on remaining nodes)\n"
				+ "\n```"
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
				+ "        \n```"
				+ "context:\n"
				+ "\n"
				+ "version: 0.90.9\n"
				+ "3 node cluster\n"
				+ "2 replicas\n"
				+ "10 shards per index\n"
				+ "im getting error below after killing one node in the cluster \n"
				+ "(exception is thrown on remaining nodes)\n"
				+ "\n```"
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
				+ "        \n```"
				+ "context:\n"
				+ "\n"
				+ "version: 0.90.9\n"
				+ "3 node cluster\n" + "2 replicas\n" + "10 shards per index";
		String example2 = "hello, I have an issue with one of my apps, and I have no idea how to resolve it: \n"
				+ "the url seems to be for downloading and not for showing the picture ( if i launch url in the browser the picture is downloaded instead of shown).\n"
				+ "my code :\n"
				+ "	\n"
				+ " Ion.with(ficheImageViewS9)\n"
				+ "               .load(Utils.getBaseUrl(getApplicationContext()) + getString(\n"
				+ "                       R.string.PIECE_JOINTE_URL) + signalement.getPhoto()\n"
				+ "                                                               .getIdPhoto() + Utils.getAuthentificationParams(\n"
				+ "                       getApplicationContext()));\n"
				+ "and verbose log :\n"
				+ "	\n"
				+ "06-12 11:26:56.708  24770-24770/com.mpm.osis D/PHOToDownalod﹕ (0 ms) https://test.extranet.myUrl.fr/rest/piecejointe/3579?login=mylogin&password=***&idSession=123456&canal=MOBILE: preparing request\n"
				+ "06-12 11:26:56.708  24770-24770/com.mpm.osis I/PHOToDownalod﹕ (0 ms) https://test.extranet.myUrl.fr/rest/piecejointe/3579?login=mylogin&password=***&idSession=123456&canal=MOBILE: Using loader: com.koushikdutta.ion.loader.HttpLoader@43a36e38\n"
				+ "06-12 11:26:56.708  24770-25439/com.mpm.osis D/PHOToDownalod﹕ (0 ms) https://test.extranet.myUrl.fr/rest/piecejointe/3579?login=mylogin&password=***&idSession=123456&canal=MOBILE: Executing request.\n"
				+ "06-12 11:26:56.718  24770-25439/com.mpm.osis V/PHOToDownalod﹕ (3 ms) https://test.extranet.myUrl.fr/rest/piecejointe/3579?login=mylogin&password=***&idSession=123456&canal=MOBILE: Resolving domain and connecting to all available addresses\n"
				+ "06-12 11:26:56.788  24770-25439/com.mpm.osis V/PHOToDownalod﹕ (75 ms) https://test.extranet.myUrl.fr/rest/piecejointe/3579?login=mylogin&password=***&idSession=123456&canal=MOBILE: socket connected\n"
				+ "06-12 11:26:56.788  24770-25439/com.mpm.osis V/PHOToDownalod﹕ (76 ms) https://test.extranet.myUrl.fr/rest/piecejointe/3579?login=mylogin&password=***&idSession=123456&canal=MOBILE:\n"
				+ "    GET /rest/piecejointe/3579?login=mylogin&password=***&idSession=123456&canal=MOBILE HTTP/1.1\n"
				+ "    Host: test.extranet.myUrl.fr\n"
				+ "    User-Agent: Dalvik/1.6.0 (Linux; U; Android 4.4.3; Nexus 5 Build/KTU84M)\n"
				+ "    Accept-Encoding: gzip, deflate\n"
				+ "    Connection: keep-alive\n"
				+ "    Accept: */*\n"
				+ "06-12 11:26:56.948  24770-25439/com.mpm.osis W/System.err﹕ javax.net.ssl.SSLProtocolException: Unexpected message type has been received: 22\n"
				+ "06-12 11:26:56.948  24770-25439/com.mpm.osis W/System.err﹕ at com.android.org.conscrypt.SSLRecordProtocol.unwrap(SSLRecordProtocol.java:360)\n"
				+ "06-12 11:26:56.948  24770-25439/com.mpm.osis W/System.err﹕ at com.android.org.conscrypt.SSLEngineImpl.unwrap(SSLEngineImpl.java:463)\n"
				+ "06-12 11:26:56.948  24770-25439/com.mpm.osis W/System.err﹕ at javax.net.ssl.SSLEngine.unwrap(SSLEngine.java:383)\n"
				+ "06-12 11:26:56.948  24770-25439/com.mpm.osis W/System.err﹕ at com.koushikdutta.async.AsyncSSLSocketWrapper$2.onDataAvailable(AsyncSSLSocketWrapper.java:97)\n"
				+ "06-12 11:26:56.948  24770-25439/com.mpm.osis W/System.err﹕ at com.koushikdutta.async.BufferedDataEmitter.onDataAvailable(BufferedDataEmitter.java:33)\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis W/System.err﹕ at com.koushikdutta.async.BufferedDataEmitter.onDataAvailable(BufferedDataEmitter.java:61)\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis W/System.err﹕ at com.koushikdutta.async.Util.emitAllData(Util.java:20)\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis W/System.err﹕ at com.koushikdutta.async.AsyncNetworkSocket.onReadable(AsyncNetworkSocket.java:171)\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis W/System.err﹕ at com.koushikdutta.async.AsyncServer.runLoop(AsyncServer.java:725)\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis W/System.err﹕ at com.koushikdutta.async.AsyncServer.run(AsyncServer.java:583)\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis W/System.err﹕ at com.koushikdutta.async.AsyncServer.access$700(AsyncServer.java:36)\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis W/System.err﹕ at com.koushikdutta.async.AsyncServer$13.run(AsyncServer.java:531)\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis E/PHOToDownalod﹕ (242 ms) https://test.extranet.myUrl.fr/rest/piecejointe/3579?login=mylogin&password=***&idSession=123456&canal=MOBILE: exception during response\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis E/PHOToDownalod﹕ connection closed before response completed.\n"
				+ "    com.koushikdutta.async.http.ConnectionClosedException: connection closed before response completed.\n"
				+ "            at com.koushikdutta.async.http.AsyncHttpResponseImpl$3.onCompleted(AsyncHttpResponseImpl.java:95)\n"
				+ "            at com.koushikdutta.async.AsyncSSLSocketWrapper.report(AsyncSSLSocketWrapper.java:382)\n"
				+ "            at com.koushikdutta.async.AsyncSSLSocketWrapper.access$100(AsyncSSLSocketWrapper.java:27)\n"
				+ "            at com.koushikdutta.async.AsyncSSLSocketWrapper$2.onDataAvailable(AsyncSSLSocketWrapper.java:126)\n"
				+ "            at com.koushikdutta.async.BufferedDataEmitter.onDataAvailable(BufferedDataEmitter.java:33)\n"
				+ "            at com.koushikdutta.async.BufferedDataEmitter.onDataAvailable(BufferedDataEmitter.java:61)\n"
				+ "            at com.koushikdutta.async.Util.emitAllData(Util.java:20)\n"
				+ "            at com.koushikdutta.async.AsyncNetworkSocket.onReadable(AsyncNetworkSocket.java:171)\n"
				+ "            at com.koushikdutta.async.AsyncServer.runLoop(AsyncServer.java:725)\n"
				+ "            at com.koushikdutta.async.AsyncServer.run(AsyncServer.java:583)\n"
				+ "            at com.koushikdutta.async.AsyncServer.access$700(AsyncServer.java:36)\n"
				+ "            at com.koushikdutta.async.AsyncServer$13.run(AsyncServer.java:531)\n"
				+ "     Caused by: javax.net.ssl.SSLProtocolException: Unexpected message type has been received: 22\n"
				+ "            at com.android.org.conscrypt.SSLRecordProtocol.unwrap(SSLRecordProtocol.java:360)\n"
				+ "            at com.android.org.conscrypt.SSLEngineImpl.unwrap(SSLEngineImpl.java:463)\n"
				+ "            at javax.net.ssl.SSLEngine.unwrap(SSLEngine.java:383)\n"
				+ "            at com.koushikdutta.async.AsyncSSLSocketWrapper$2.onDataAvailable(AsyncSSLSocketWrapper.java:97)\n"
				+ "            at com.koushikdutta.async.BufferedDataEmitter.onDataAvailable(BufferedDataEmitter.java:33)\n"
				+ "            at com.koushikdutta.async.BufferedDataEmitter.onDataAvailable(BufferedDataEmitter.java:61)\n"
				+ "            at com.koushikdutta.async.Util.emitAllData(Util.java:20)\n"
				+ "            at com.koushikdutta.async.AsyncNetworkSocket.onReadable(AsyncNetworkSocket.java:171)\n"
				+ "            at com.koushikdutta.async.AsyncServer.runLoop(AsyncServer.java:725)\n"
				+ "            at com.koushikdutta.async.AsyncServer.run(AsyncServer.java:583)\n"
				+ "            at com.koushikdutta.async.AsyncServer.access$700(AsyncServer.java:36)\n"
				+ "            at com.koushikdutta.async.AsyncServer$13.run(AsyncServer.java:531)\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis E/PHOToDownalod﹕ (244 ms) https://test.extranet.myUrl.fr/rest/piecejointe/3579?login=mylogin&password=***&idSession=123456&canal=MOBILE: Connection error\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis E/PHOToDownalod﹕ connection closed before response completed.\n"
				+ "    com.koushikdutta.async.http.ConnectionClosedException: connection closed before response completed.\n"
				+ "            at com.koushikdutta.async.http.AsyncHttpResponseImpl$3.onCompleted(AsyncHttpResponseImpl.java:95)\n"
				+ "            at com.koushikdutta.async.AsyncSSLSocketWrapper.report(AsyncSSLSocketWrapper.java:382)\n"
				+ "            at com.koushikdutta.async.AsyncSSLSocketWrapper.access$100(AsyncSSLSocketWrapper.java:27)\n"
				+ "            at com.koushikdutta.async.AsyncSSLSocketWrapper$2.onDataAvailable(AsyncSSLSocketWrapper.java:126)\n"
				+ "            at com.koushikdutta.async.BufferedDataEmitter.onDataAvailable(BufferedDataEmitter.java:33)\n"
				+ "            at com.koushikdutta.async.BufferedDataEmitter.onDataAvailable(BufferedDataEmitter.java:61)\n"
				+ "            at com.koushikdutta.async.Util.emitAllData(Util.java:20)\n"
				+ "            at com.koushikdutta.async.AsyncNetworkSocket.onReadable(AsyncNetworkSocket.java:171)\n"
				+ "            at com.koushikdutta.async.AsyncServer.runLoop(AsyncServer.java:725)\n"
				+ "            at com.koushikdutta.async.AsyncServer.run(AsyncServer.java:583)\n"
				+ "            at com.koushikdutta.async.AsyncServer.access$700(AsyncServer.java:36)\n"
				+ "            at com.koushikdutta.async.AsyncServer$13.run(AsyncServer.java:531)\n"
				+ "     Caused by: javax.net.ssl.SSLProtocolException: Unexpected message type has been received: 22\n"
				+ "            at com.android.org.conscrypt.SSLRecordProtocol.unwrap(SSLRecordProtocol.java:360)\n"
				+ "            at com.android.org.conscrypt.SSLEngineImpl.unwrap(SSLEngineImpl.java:463)\n"
				+ "            at javax.net.ssl.SSLEngine.unwrap(SSLEngine.java:383)\n"
				+ "            at com.koushikdutta.async.AsyncSSLSocketWrapper$2.onDataAvailable(AsyncSSLSocketWrapper.java:97)\n"
				+ "            at com.koushikdutta.async.BufferedDataEmitter.onDataAvailable(BufferedDataEmitter.java:33)\n"
				+ "            at com.koushikdutta.async.BufferedDataEmitter.onDataAvailable(BufferedDataEmitter.java:61)\n"
				+ "            at com.koushikdutta.async.Util.emitAllData(Util.java:20)\n"
				+ "            at com.koushikdutta.async.AsyncNetworkSocket.onReadable(AsyncNetworkSocket.java:171)\n"
				+ "            at com.koushikdutta.async.AsyncServer.runLoop(AsyncServer.java:725)\n"
				+ "            at com.koushikdutta.async.AsyncServer.run(AsyncServer.java:583)\n"
				+ "            at com.koushikdutta.async.AsyncServer.access$700(AsyncServer.java:36)\n"
				+ "            at com.koushikdutta.async.AsyncServer$13.run(AsyncServer.java:531)\n"
				+ "06-12 11:26:56.958  24770-25439/com.mpm.osis V/PHOToDownalod﹕ (246 ms) https://test.extranet.myUrl.fr/rest/piecejointe/3579?login=mylogin&password=***&idSession=123456&canal=MOBILE: closing out socket (exception)\n"
				+ "\n"
				+ "Same url is working with Android-Universal-Image-Loader but I already use Ion in my application and i would like to keep it for all request.\n"
				+ "have you got a specific workaround for this case ?\n"
				+ "thanks, \n"
				+ "and by the way, thanks for this great library .\n";
		String example3 = "/s3auth>mvn clean install\n"
				+ "				\n"
				+ "				\n"
				+ "exception: com.amazonaws.AmazonServiceException: Status Code: 400, AWS Service: AmazonDynamoDBv2, AWS Request ID: null, AWS Error Code: ValidationException, AWS Error Message: Invalid table/index name.  Table/index names must be between 3 and 255 characters long, and may contain only the characters a-z, A-Z, 0-9, '_', '-', and '.'\n"
				+ "        at com.amazonaws.http.AmazonHttpClient.handleErrorResponse(AmazonHttpClient.java:798)\n"
				+ "        at com.amazonaws.http.AmazonHttpClient.executeHelper(AmazonHttpClient.java:421)\n"
				+ "        at com.amazonaws.http.AmazonHttpClient.execute(AmazonHttpClient.java:232)\n"
				+ "        at com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient.invoke(AmazonDynamoDBClient.java:2996)\n"
				+ "        at com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient.scan(AmazonDynamoDBClient.java:430)\n"
				+ "        at com.s3auth.hosts.DefaultDynamo.load_aroundBody2(DefaultDynamo.java:172)\n"
				+ "        at com.s3auth.hosts.DefaultDynamo$AjcClosure3.run(DefaultDynamo.java:1)\n"
				+ "        at org.aspectj.runtime.reflect.JoinPointImpl.proceed(JoinPointImpl.java:149)\n"
				+ "        at com.jcabi.aspects.aj.MethodCacher$Tunnel.through(MethodCacher.java:277)\n"
				+ "        at com.jcabi.aspects.aj.MethodCacher.cache(MethodCacher.java:126)\n"
				+ "        at com.s3auth.hosts.DefaultDynamo.load(DefaultDynamo.java:169)\n"
				+ "        at com.s3auth.hosts.DefaultDynamo.load(DefaultDynamo.java:65)\n"
				+ "        at com.s3auth.hosts.DynamoHosts.domains_aroundBody4(DynamoHosts.java:108)\n"
				+ "        at com.s3auth.hosts.DynamoHosts$AjcClosure5.run(DynamoHosts.java:1)\n"
				+ "        at org.aspectj.runtime.reflect.JoinPointImpl.proceed(JoinPointImpl.java:149)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.wrap(MethodLogger.java:200)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.ajc$inlineAccessMethod$com_jcabi_aspects_aj_MethodLogger$com_jcabi_aspects_aj_MethodLogger$wrap(MethodLogger.java:1)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.wrapClass(MethodLogger.java:128)\n"
				+ "        at com.s3auth.hosts.DynamoHosts.domains(DynamoHosts.java:108)\n"
				+ "        at com.s3auth.hosts.SyslogHosts.domains_aroundBody4(SyslogHosts.java:88)\n"
				+ "        at com.s3auth.hosts.SyslogHosts$AjcClosure5.run(SyslogHosts.java:1)\n"
				+ "        at org.aspectj.runtime.reflect.JoinPointImpl.proceed(JoinPointImpl.java:149)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.wrap(MethodLogger.java:200)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.ajc$inlineAccessMethod$com_jcabi_aspects_aj_MethodLogger$com_jcabi_aspects_aj_MethodLogger$wrap(MethodLogger.java:1)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.wrapClass(MethodLogger.java:128)\n"
				+ "        at com.s3auth.hosts.SyslogHosts.domains(SyslogHosts.java:88)\n"
				+ "        at com.s3auth.rest.IndexRs.domains(IndexRs.java:186)\n"
				+ "        at com.s3auth.rest.IndexRs.index_aroundBody0(IndexRs.java:77)\n"
				+ "        at com.s3auth.rest.IndexRs$AjcClosure1.run(IndexRs.java:1)\n"
				+ "        at org.aspectj.runtime.reflect.JoinPointImpl.proceed(JoinPointImpl.java:149)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.wrap(MethodLogger.java:200)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.ajc$inlineAccessMethod$com_jcabi_aspects_aj_MethodLogger$com_jcabi_aspects_aj_MethodLogger$wrap(MethodLogger.java:1)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.wrapClass(MethodLogger.java:128)\n"
				+ "        at com.s3auth.rest.IndexRs.index(IndexRs.java:77)\n"
				+ "        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
				+ "        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n"
				+ "        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n"
				+ "        at java.lang.reflect.Method.invoke(Method.java:606)\n"
				+ "        at com.sun.jersey.spi.container.JavaMethodInvokerFactory$1.invoke(JavaMethodInvokerFactory.java:60)\n"
				+ "        at com.sun.jersey.server.impl.model.method.dispatch.AbstractResourceMethodDispatchProvider$ResponseOutInvoker._dispatch(AbstractResourceMethodDispatchProvider.java:205)\n"
				+ "        at com.sun.jersey.server.impl.model.method.dispatch.ResourceJavaMethodDispatcher.dispatch(ResourceJavaMethodDispatcher.java:75)\n"
				+ "        at com.sun.jersey.server.impl.uri.rules.HttpMethodRule.accept(HttpMethodRule.java:302)\n"
				+ "        at com.sun.jersey.server.impl.uri.rules.ResourceClassRule.accept(ResourceClassRule.java:108)\n"
				+ "        at com.sun.jersey.server.impl.uri.rules.RightHandPathRule.accept(RightHandPathRule.java:147)\n"
				+ "        at com.sun.jersey.server.impl.uri.rules.RootResourceClassesRule.accept(RootResourceClassesRule.java:84)\n"
				+ "        at com.sun.jersey.server.impl.application.WebApplicationImpl._handleRequest(WebApplicationImpl.java:1511)\n"
				+ "        at com.sun.jersey.server.impl.application.WebApplicationImpl._handleRequest(WebApplicationImpl.java:1442)\n"
				+ "        at com.sun.jersey.server.impl.application.WebApplicationImpl.handleRequest(WebApplicationImpl.java:1391)\n"
				+ "        at com.sun.jersey.server.impl.application.WebApplicationImpl.handleRequest(WebApplicationImpl.java:1381)\n"
				+ "        at com.sun.jersey.spi.container.servlet.WebComponent.service(WebComponent.java:416)\n"
				+ "        at com.sun.jersey.spi.container.servlet.ServletContainer.service(ServletContainer.java:538)\n"
				+ "        at com.sun.jersey.spi.container.servlet.ServletContainer.service(ServletContainer.java:716)\n"
				+ "        at com.rexsl.core.RestfulServlet.service_aroundBody2(RestfulServlet.java:183)\n"
				+ "        at com.rexsl.core.RestfulServlet$AjcClosure3.run(RestfulServlet.java:1)\n"
				+ "        at org.aspectj.runtime.reflect.JoinPointImpl.proceed(JoinPointImpl.java:149)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.wrap(MethodLogger.java:200)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.ajc$inlineAccessMethod$com_jcabi_aspects_aj_MethodLogger$com_jcabi_aspects_aj_MethodLogger$wrap(MethodLogger.java:1)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.wrapClass(MethodLogger.java:128)\n"
				+ "        at com.rexsl.core.RestfulServlet.service(RestfulServlet.java:182)\n"
				+ "        at javax.servlet.http.HttpServlet.service(HttpServlet.java:848)\n"
				+ "        at org.eclipse.jetty.servlet.ServletHolder.handle(ServletHolder.java:558)\n"
				+ "        at org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1365)\n"
				+ "        at com.rexsl.core.XsltFilter.filter(XsltFilter.java:168)\n"
				+ "        at com.rexsl.core.XsltFilter.doFilter_aroundBody2(XsltFilter.java:137)\n"
				+ "        at com.rexsl.core.XsltFilter$AjcClosure3.run(XsltFilter.java:1)\n"
				+ "        at org.aspectj.runtime.reflect.JoinPointImpl.proceed(JoinPointImpl.java:149)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.wrap(MethodLogger.java:200)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.ajc$inlineAccessMethod$com_jcabi_aspects_aj_MethodLogger$com_jcabi_aspects_aj_MethodLogger$wrap(MethodLogger.java:1)\n"
				+ "        at com.jcabi.aspects.aj.MethodLogger.wrapClass(MethodLogger.java:128)\n"
				+ "        at com.rexsl.core.XsltFilter.doFilter(XsltFilter.java:135)\n"
				+ "        at org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1336)\n"
				+ "        at org.eclipse.jetty.servlet.ServletHandler.doHandle(ServletHandler.java:486)\n"
				+ "        at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:119)\n"
				+ "        at org.eclipse.jetty.security.SecurityHandler.handle(SecurityHandler.java:520)\n"
				+ "        at org.eclipse.jetty.server.session.SessionHandler.doHandle(SessionHandler.java:233)\n"
				+ "        at org.eclipse.jetty.server.handler.ContextHandler.doHandle(ContextHandler.java:972)\n"
				+ "        at org.eclipse.jetty.servlet.ServletHandler.doScope(ServletHandler.java:417)\n"
				+ "        at org.eclipse.jetty.server.session.SessionHandler.doScope(SessionHandler.java:192)\n"
				+ "        at org.eclipse.jetty.server.handler.ContextHandler.doScope(ContextHandler.java:906)\n"
				+ "        at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:117)\n"
				+ "        at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:110)\n"
				+ "        at org.eclipse.jetty.server.Server.handle(Server.java:350)\n"
				+ "        at org.eclipse.jetty.server.HttpConnection.handleRequest(HttpConnection.java:442)\n"
				+ "        at org.eclipse.jetty.server.HttpConnection$RequestHandler.headerComplete(HttpConnection.java:924)\n"
				+ "        at org.eclipse.jetty.http.HttpParser.parseNext(HttpParser.java:582)\n"
				+ "        at org.eclipse.jetty.http.HttpParser.parseAvailable(HttpParser.java:218)\n"
				+ "        at org.eclipse.jetty.server.AsyncHttpConnection.handle(AsyncHttpConnection.java:52)\n"
				+ "        at org.eclipse.jetty.io.nio.SelectChannelEndPoint.handle(SelectChannelEndPoint.java:586)\n"
				+ "        at org.eclipse.jetty.io.nio.SelectChannelEndPoint$1.run(SelectChannelEndPoint.java:44)\n"
				+ "        at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:598)\n"
				+ "        at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:533)\n"
				+ "        at java.lang.Thread.run(Thread.java:744)\n"
				+ "        \n" + "</pre>\n" + "        </div>\n"
				+ "    </body>\n" + "</html>\n" + ">\n";

		String quotedExample = "We have two kinds of nodes: those with ssds (used for indexing and search recent data), those with large spinning disks (used for archiving old indices).\r\n\r\nI'd like to setup a mechanism to move old indices from ssds to spinning disks.\r\n\r\nThe first solution uses reroute command in cluster api. However it feels unnatural since you have to do it shard by shard and decide the target node.\r\n\r\nWhat I want to achieve is the following:\r\n1. stick recent indices (the current one being written) to ssds. They have 2 copies.\r\n2. at some point (disk on ssds is above 65%), one copy is moved to larger boxes (1 copy is still on ssd to help search, 1 copy on large box)\r\n3. when disk is scarce on ssd boxes (90%), we simply drop the copy present on ssd. Since we don't care that much of old data having only one copy is not an issue.\r\n\r\nI have tried to implement this with shard awareness allocation and allocation filtering but it does not seem to work as expected.\r\n\r\nNodes have ```flavor``` attribute depending on their hardware (```ssd``` or ```iodisk```).\r\nCluster is using shard awareness based on ```flavor``` attribute (```cluster.routing.allocation.awareness.attributes: flavor```).\r\n\r\n1. My index template has ```routing.allocation.require: ssd``` to impose two have all copies on ssds first. \r\n2. At some point, I drop the requirement (effectively ``routing.allocation.require: *```). I expect flavor awareness to move one copy to large (iodisk) boxes.\r\n3. At a later point, I'll set ```number_of_replicas``` to 0 and change ```routing.allocation.require``` to ```iodisk``` to drop the shard copy on ssds\r\n\r\nSadly allocation filtering and shard awareness do not seem to cooperate well :\r\nwhen an new index is created, one copy goes to ssds and the other is not allocated anywhere (index stays in yellow state).\r\n\r\nUsing ```curl -XPUT localhost:9200/_cluster/settings -d '{\"transient\":{\"logger.cluster.routing.allocation\":\"trace\"}}'```,\r\nI have observed what happen when a new index is created.\r\n\r\n```\r\n[2014-10-16 06:53:19,462][TRACE][cluster.routing.allocation.decider] [bungeearchive01-par.storage.criteo.preprod] Can not allocate [[2014-10-16.01][3], node[null], [R], s[UNASSIGNED]] on node [qK34VLdhTferCQs2oNJOyg] due to [SameShardAllocationDecider]\r\n[2014-10-16 06:53:19,463][TRACE][cluster.routing.allocation.decider] [bungeearchive01-par.storage.criteo.preprod] Can not allocate [[2014-10-16.01][3], node[null], [R], s[UNASSIGNED]] on node [gE7OTgevSUuoj44RozxK0Q] due to [AwarenessAllocationDecider]\r\n[2014-10-16 06:53:19,463][TRACE][cluster.routing.allocation.decider] [bungeearchive01-par.storage.criteo.preprod] Can not allocate [[2014-10-16.01][3], node[null], [R], s[UNASSIGNED]] on node [Y2k9qXfsTx6X2iQTxg9RBQ] due to [AwarenessAllocationDecider]\r\n[2014-10-16 06:53:19,463][TRACE][cluster.routing.allocation.decider] [bungeearchive01-par.storage.criteo.preprod] Can not allocate [[2014-10-16.01][3], node[null], [R], s[UNASSIGNED]] on node [FwWc2XPPRWuje2KH6AlDEQ] due to [FilterAllocationDecider]\r\n[2014-10-16 06:53:19,492][TRACE][cluster.routing.allocation.allocator] [bungeearchive01-par.storage.criteo.preprod] No Node found to assign shard [[2014-10-16.01][3], node[null], [R], s[UNASSIGNED]]\r\n```\r\n\r\nThis transcript shows that \r\n- shard 3 primary replica is on node qK34VLdhTferCQs2oNJOyg (flavor:ssd) which prevent its copy to placed there\r\n- it cannot be placed on gE7OTgevSUuoj44RozxK0Q (ssd as well) because it tries to maximizes dispersion accross flavors\r\n- it cannot be placed on Y2k9qXfsTx6X2iQTxg9RBQ for the same reason\r\n- it cannot be placed on FwWc2XPPRWuje2KH6AlDEQ (flavor: iodisk) because of the filter\r\n\r\nQuestions:\r\n- am I doing it wrong?\r\n- should I stick with a set of reroute command?\r\n- are awareness and filtering supposed to cooperate?\r\n\r\n,";

		 fetchingProcedure();

		// normalizeXML1();
		// StackTraceStatistic();
		// testSplitStactTrace();
		// long l = Math.round(2.0/ (1.0/1 + 1.0/100.0));
		// System.out.println(l);
		// List<StackTrace> stacktraces = Util.splitStackTrace(example);
		// stacktraces.get(0).findSimilarPairs(stacktraces.get(0).buildSimilarityMatrix(stacktraces.get(1)));;
		// long startTime = System.nanoTime();

		// System.out.println("PDM Similarity = "
		// + stacktraces.get(0).compareTo(stacktraces.get(1)));

		// long endTime = System.nanoTime();
		// long duration = (endTime - startTime); // divide by 1000000 to get //
		// milliseconds.
		// System.out.println("The whole process ate up: "
		// + (duration / 1000000 / 1000 ) + " seconds");
		// for (int i = 0; i < stacktraces.length-1; i++){
		// Util.splitException(stacktraces[i]);
		// }
		// Util.splitQuotedText(quotedExample);

		// String[] issuesUnderObservation = { "4209", "3995", "4801", "1527",
		// "6617", "2522", "7168" };
		// long startTime = System.nanoTime(); //
		// IssueManager.getInstance().loadRepositoriesToLocal(repoDetails);
		// IssueManager.getInstance().processLocalRepositories(repoDetails);
		// IssueManager.getInstance().buildVector();
		// LSHasher hasher = new LSHasher(0.5);
		// hasher.generateHashVectors(Issue.df);
		// hasher.categorizeLSH(IssueManager.getInstance().issueData);
		// int count = 0;
		// CSVWriter writer = null;
		// CSVWriter tfidfReportWriter = null;
		// try {
		// writer = new CSVWriter(new FileWriter("\\data\\" + "Similar_issue_"
		// + System.currentTimeMillis() + ".csv"));
		//
		// tfidfReportWriter = new CSVWriter(new FileWriter("\\data\\"
		// + "tfidf_report_" + System.currentTimeMillis() + ".csv"));
		//
		// for (Map.Entry<Integer, List<Issue>> entry : hasher.mCategories
		// .entrySet()) {
		// int key = entry.getKey();
		// List<Issue> issues = entry.getValue();
		// System.out
		// .println("Category[" + key + "] has " + issues.size());
		// for (int i = 0; i < (issues.size() - 1); i++) {
		// for (int j = i + 1; j < issues.size(); j++) {
		// double sim = Issue.CosineSimilarity(issues.get(i),
		// issues.get(j));
		// if (sim >= 0.8) {
		// writePair(writer, issues.get(i), issues.get(j),
		// sim, tfidfReportWriter,
		// issuesUnderObservation);
		// count++;
		// if (count % 1000 == 0) {
		// System.out.println("..found " + count
		// + " similar pairs..");
		// writer.flush();
		// tfidfReportWriter.flush();
		// return;
		// }
		// }
		// }
		// }
		// }
		// } catch (IOException e) { // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally {
		// tfidfReportWriter.close();
		// writer.close();
		// long endTime = System.nanoTime();
		// long duration = (endTime - startTime); // divide by 1000000 to get
		// // // milliseconds.
		// System.out.println("..found " + count + " similar pairs..");
		//
		// System.out.println("The whole process ate up: "
		// + (duration / 1000000 / 1000 / 60) + " minutes");
		//
		// }

	}

	private static void normalizeXML1() {

		 System.out.println(">> Start normalizing xml");
		final Path src = Paths.get("E:/","IssueData/", "gnome_bugzilla.xml/",
				"gnome_bugzilla.xml");
		final Path dst = Paths.get("E:/","IssueData/", "gnome_bugzilla.xml/",
				"gnome_bugzilla_norm.xml");
		final BufferedReader reader;
		final BufferedWriter writer;
		String line;

		try {
			reader = Files.newBufferedReader(src, StandardCharsets.UTF_8);
			writer = Files.newBufferedWriter(dst, StandardCharsets.UTF_8);
			while ((line = reader.readLine()) != null) {
				while ((line = reader.readLine()) != null) {
					writer.write(Util.stripNonValidXMLCharacters(line));
					// must do this: .readLine() will have stripped line endings
					writer.newLine();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		 System.out.println(">> done normalizing xml");
	}

	private static void importBugs_Eclipse() {
		MySQLConnector db = null;
		MySQLConnector dbEclipse = null;
		int count = 0;
		System.out.println(">>Starting to import new data..");
		try {
			String prjID = "377";
			db = new MySQLConnector("phong1990", "phdcs2014",
					IssueManager.DATABASE);
			dbEclipse = new MySQLConnector("root", "phdcs2014", "eclipsebugs");
			String[] fields = { "b.bug_id", "t.short_desc", "t.comments",
					"t.comments_noprivate" };
			ResultSet resultSet = dbEclipse.select(
					"eclipsebugs.bugs_fulltext t, eclipsebugs.bugs b", fields,
					"b.bug_id = t.bug_id AND b.bug_status='CLOSED'");
			while (resultSet.next()) {
				String summary = resultSet.getString("t.short_desc");
				String issueID = String.valueOf(resultSet.getInt("b.bug_id"));
				String body = resultSet.getString("t.comments");
				String comments = resultSet.getString("t.comments_noprivate");
				if (body == null)
					continue;
				if (summary == null)
					continue;
				if (summary.length() < 5 || body.length() < 5)
					continue;
				String values[] = { prjID, issueID, summary, body, comments };
				db.insert(IssueManager.ISSUES_TABLE_DB, values);
				count++;
				// System.out.println(issueID + "-" + summary + "-" +
				// body.substring(0, 10));

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
			if (dbEclipse != null)
				dbEclipse.close();
		}
		System.out.println(">>Done: " + count + " issues.");
	}

	private static void testSplitStactTrace() {

		MySQLConnector db = null;
		try {
			db = new MySQLConnector("phong1990", "phdcs2014",
					IssueManager.DATABASE);
			String prjID = "2";

			String fields[] = { "prj.name", "iss.issue_id",
					"iss.issue_summary", "iss.issue_body" };
			ResultSet resultSet = db.select(IssueManager.PROJECTS_TABLE_DB
					+ " prj, " + IssueManager.ISSUES_TABLE_DB + " iss", fields,
					"prj.project_id=iss.project_id AND prj.project_id = '"
							+ prjID + "'");
			while (resultSet.next()) {
				String summary = resultSet.getString("iss.issue_summary");
				String body = resultSet.getString("iss.issue_body");
				String issueID = String.valueOf(resultSet
						.getInt("iss.issue_id"));
				String projectName = resultSet.getString("prj.name");
				if (summary == null)
					continue;
				if (summary.length() < 5 || body.length() < 5)
					continue;
				List<StackTrace> mStackTraces = Util.splitStackTrace_v2(body);
				// List<StackTrace> mStackTraces = Util
				// .splitStackTrace_v2(body);
				// only take issue with stacktrace
				if (mStackTraces != null) {
					System.out.println(projectName + "-" + issueID);
				}
			}
			resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
	}

	private static void crawlingProcedure() {
		long startTime = System.nanoTime();
		GitHubCrawler crawler = new GitHubCrawler("phong1990", "phdcs2014");

		System.out.println(">>Crawling for a list of java repositories.");
		int choosenProjects = crawler.crawlRepos(crawler.crawlUser());
		System.out.println("Choosen Projects = " + choosenProjects);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); // divide by 1000000 to get
												// milliseconds.
		System.out.println("The whole process ate up: "
				+ (duration / 1000000 / 1000 / 60) + " minutes");
	}

	private static void fetchingProcedure() {
		long startTime = System.nanoTime();
		GitHubCrawler crawler = new GitHubCrawler("phong1990", "phdcs2014");

		System.out.println(">>Fetching issues to local storage.");
		crawler.loadRepositoriesToLocal();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); // divide by 1000000 to get
												// milliseconds.
		System.out.println("The whole process ate up: "
				+ (duration / 1000000 / 1000 / 60) + " minutes");

	}

	// only does the statistic on issues which have StackTrace inside them
	private static void StackTraceStatistic() {
		long startTime = System.nanoTime();
		int issueCount = IssueManager.getInstance()
				.processLocalRepositories_ST();
		// IssueManager.getInstance().buildVector();
		// LSHasher hasher = new LSHasher(0.5);
		// hasher.generateHashVectors(Issue.df);
		// hasher.categorizeLSH(IssueManager.getInstance().issueData);
		int count = 0;
		CSVWriter writer = null;
		CSVWriter analistWriter = null;
		try {
			writer = new CSVWriter(new FileWriter(
					"\\IssueData\\StackTraceStat\\" + "Similar_StackTrace_"
							+ System.currentTimeMillis() + ".csv"));
			analistWriter = new CSVWriter(new FileWriter(
					"\\IssueData\\StackTraceStat\\" + "StackTrace_Analysis_"
							+ System.currentTimeMillis() + ".csv"));

			// for (Map.Entry<Integer, List<Issue>> entry : hasher.mCategories
			// .entrySet()) {
			// int key = entry.getKey();
			List<Issue> issues = IssueManager.getInstance().issueData;
			System.out.println("Number of issue = " + issues.size());
			HashMap<Integer, Integer> STAnalist = new HashMap<>();

			String[] title1 = new String[3];
			title1[0] = "Project 1";
			title1[1] = "Project 2";
			title1[2] = "Similarity";
			writer.writeNext(title1);
			
			String[] title2 = new String[4];
			title2[0] = "Project-issue";
			title2[1] = "No. Of ST";
			title2[2] = "ST type";
			title2[3] = "Number";
			analistWriter.writeNext(title2);
			for (int i = 0; i < (issues.size() - 1); i++) {
				for (int j = i + 1; j < issues.size(); j++) {
					if (issues.get(i).mStackTraces.size() >= 1
							&& issues.get(j).mStackTraces.size() >= 1) {
						double sim = issues.get(i).mStackTraces.get(0)
								.compareTo(issues.get(j).mStackTraces.get(0));
						Integer number = STAnalist.get(1);
						if (sim >= 0.9 && sim < 1) {
							// /////////////////////////////////////////////
							String[] entries = new String[3];
							entries[0] = Util.buildHyperLink(issues.get(i));
							entries[1] = Util.buildHyperLink(issues.get(j));
							entries[2] = String.valueOf(sim);
							writer.writeNext(entries);
							// /////////////////////////////////////////////
							count++;
							if (count % 1000 == 0) {
								System.out.println("..found " + count
										+ " similar pairs..");
							}
						}
					}
					Issue.CosineSimilarity(issues.get(i), issues.get(j));
				}
				// StackTrace number analysis
				Integer number = STAnalist.get(issues.get(i).mStackTraces
						.size());
				if (number != null)
					STAnalist
							.put(issues.get(i).mStackTraces.size(), number + 1);
				else
					STAnalist.put(issues.get(i).mStackTraces.size(), 1);
				// /////////////////////////////////////////////
				String[] entries = new String[2];
				entries[0] = Util.buildHyperLink(issues.get(i));
				entries[1] = String.valueOf(issues.get(i).mStackTraces.size());
				analistWriter.writeNext(entries);
				// /////////////////////////////////////////////
			}

			// StackTrace number analysis
			Integer number = STAnalist
					.get(issues.get(issues.size() - 1).mStackTraces.size());
			if (number != null)
				STAnalist.put(
						issues.get(issues.size() - 1).mStackTraces.size(),
						number + 1);
			else
				STAnalist.put(
						issues.get(issues.size() - 1).mStackTraces.size(), 1);
			// /////////////////////////////////////////////
			for (Integer i : STAnalist.keySet()) {
				// /////////////////////////////////////////////
				String[] entries = new String[4];
				entries[0] = "-1";
				entries[1] = "-1";
				entries[2] = i + " Stack Trace(s):";
				entries[3] = String.valueOf(STAnalist.get(i));
				analistWriter.writeNext(entries);
				// /////////////////////////////////////////////
			}
			String[] entries = new String[4];
			entries[0] = "-1";
			entries[1] = "-1";
			entries[2] = "Total issues(ST):";
			entries[3] = String.valueOf(issues.size());
			analistWriter.writeNext(entries);
			entries[0] = "-1";
			entries[1] = "-1";
			entries[2] = "Total issues(All):";
			entries[3] = String.valueOf(issueCount);
			analistWriter.writeNext(entries);
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				analistWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long endTime = System.nanoTime();
			long duration = (endTime - startTime); // divide by 1000000 to get
													// // milliseconds.
			System.out.println("..found " + count + " similar pairs..");

			System.out.println("The whole process ate up: "
					+ (duration / 1000000 / 1000) + " seconds");

		}
	}

	private static void writePair(CSVWriter writer, Issue issue1, Issue issue2,
			double sim, CSVWriter tfidfReportWriter,
			String[] issuesUnderObservation) {
		// TODO Auto-generated method stub

		String[] entries = new String[3];
		entries[0] = Util.buildHyperLink(issue1);
		entries[1] = Util.buildHyperLink(issue2);
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
