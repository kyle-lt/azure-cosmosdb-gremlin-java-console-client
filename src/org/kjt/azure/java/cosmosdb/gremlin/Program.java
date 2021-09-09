package org.kjt.azure.java.cosmosdb.gremlin;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.driver.exception.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Program {
	
	// Logger		
		private static final Logger logger = LoggerFactory.getLogger(Program.class);
		
	/*
	 * Example Gremlin queries to perform the following: - add vertices and edges -
	 * query with filters, projections, - traversals, including loops - update and
	 * delete vertices and edges
	 */
	static final String gremlinQueries[] = new String[] { "g.V().drop()",
			"g.addV('person').property('id', 'thomas').property('firstName', 'Thomas').property('age', 44).property('pk', 'pk')",
			"g.addV('person').property('id', 'mary').property('firstName', 'Mary').property('lastName', 'Andersen').property('age', 39).property('pk', 'pk')",
			"g.addV('person').property('id', 'ben').property('firstName', 'Ben').property('lastName', 'Miller').property('pk', 'pk')",
			"g.addV('person').property('id', 'robin').property('firstName', 'Robin').property('lastName', 'Wakefield').property('pk', 'pk')",
			"g.V('thomas').addE('knows').to(g.V('mary'))", "g.V('thomas').addE('knows').to(g.V('ben'))",
			"g.V('ben').addE('knows').to(g.V('robin'))", "g.V('thomas').property('age', 44)", "g.V().count()",
			"g.V().hasLabel('person').has('age', gt(40))", "g.V().hasLabel('person').order().by('firstName', decr)",
			"g.V('thomas').outE('knows').inV().hasLabel('person')",
			"g.V('thomas').outE('knows').inV().hasLabel('person').outE('knows').inV().hasLabel('person')",
			"g.V('thomas').repeat(out()).until(has('id', 'robin')).path()",
			"g.V('thomas').outE('knows').where(inV().has('id', 'mary')).drop()", "g.V('thomas').drop()" };

	/**
	 * There typically needs to be only one Cluster instance in an application.
	 */
	private static Cluster cluster;

	/**
	 * Use the Cluster instance to construct different Client instances (e.g. one
	 * for sessionless communication and one or more sessions). A sessionless Client
	 * should be thread-safe and typically no more than one is needed unless there
	 * is some need to divide connection pools across multiple Client instances. In
	 * this case there is just a single sessionless Client instance used for the
	 * entire App.
	 */
	private static Client client;

	public static void main(String[] args) throws ExecutionException, InterruptedException {

		try {
			// Attempt to create the connection objects
			cluster = Cluster.build(new File("src/remote.yaml")).create();
			client = cluster.connect();
		} catch (FileNotFoundException e) {
			// Handle file errors.
			logger.error("Couldn't find the configuration file.");
			e.printStackTrace();
			return;
		}

		int successful = 0;
		while (successful == 0) {
			successful = executeQueries();
			logger.info("***** SLEEPING FOR 60 seconds *****");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

		// Properly close all opened clients and the cluster
		cluster.close();

		System.exit(0);
	}

	public static int executeQueries() {

		// After connection is successful, run all the queries against the server.
		for (String query : gremlinQueries) {
			logger.info("\nSubmitting this Gremlin query: " + query);

			// Submitting remote query to the server.
			ResultSet results = client.submit(query);

			CompletableFuture<List<Result>> completableFutureResults;
			CompletableFuture<Map<String, Object>> completableFutureStatusAttributes;
			List<Result> resultList;
			Map<String, Object> statusAttributes;

			try {
				completableFutureResults = results.all();
				completableFutureStatusAttributes = results.statusAttributes();
				resultList = completableFutureResults.get();
				statusAttributes = completableFutureStatusAttributes.get();
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
				break;
			} catch (Exception e) {
				ResponseException re = (ResponseException) e.getCause();

				// Response status codes. You can catch the 429 status code response and work on
				// retry logic.
				logger.error("Status code: " + re.getStatusAttributes().get().get("x-ms-status-code"));
				logger.error("Substatus code: " + re.getStatusAttributes().get().get("x-ms-substatus-code"));

				// If error code is 429, this value will inform how many milliseconds you need
				// to wait before retrying.
				logger.error("Retry after (ms): " + re.getStatusAttributes().get().get("x-ms-retry-after"));

				// Total Request Units (RUs) charged for the operation, upon failure.
				logger.error("Request charge: " + re.getStatusAttributes().get().get("x-ms-total-request-charge"));

				// ActivityId for server-side debugging
				logger.error("ActivityId: " + re.getStatusAttributes().get().get("x-ms-activity-id"));
				throw (e);
			}

			for (Result result : resultList) {
				logger.info("\nQuery result:");
				logger.info(result.toString());
			}

			// Status code for successful query. Usually HTTP 200.
			logger.info("Status: " + statusAttributes.get("x-ms-status-code").toString());

			// Total Request Units (RUs) charged for the operation, after a successful run.
			logger.info("Total charge: " + statusAttributes.get("x-ms-total-request-charge").toString());
		}

		/*
		 * System.out.println("Demo complete!\n Press Enter key to continue..."); try{
		 * System.in.read(); } catch (IOException e){ e.printStackTrace(); return -1; }
		 */

		return 0;
	}
}
