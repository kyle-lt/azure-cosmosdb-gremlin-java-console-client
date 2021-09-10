# Background

I used [this](https://github.com/Azure-Samples/azure-cosmos-db-graph-java-getting-started) project as the base for this project.

The purpose of this project is to illustrate how to configure AppDynamics to detect CosmosDB Gremlin Synchronous API queries.  This would be equally doable for Async queries, and done in a similar fashion, just not done in this repo.  The section [AppD](#appd) walks through the steps taken and results.

The original README (for notes on how to config and run) starts with section [Developing a Java app using Azure Cosmos DB](#developing-a-java-app-using-azure-cosmos-db).

I also added a section at the very end to walk through how to [Run with Docker Compose](#run-with-docker-compose).

## AppD

### Business Transaction
Because this is a simple console app, the first step is to create a Business Transaction that will instrument this code.

Here are the configs, and some screenshots to show them in the UI.

| Step Number | Config | Value | Description |
| ----------- | ------ | ----- | ----------- |
| 1 | Agent Type | Java | The type of agent being used, in this case Java for a Java app |
| 1 | Entry Point Type | POJO | Tells the agent how to apply an interceptor, in this case for a POJO (plain old java object) |
| 2 | Name | CosmosDB-Gremlin-Call | The name of the transaction in the UI, this can be whatever is preferred |
| 2 | Priority | 10 | This prioritizes this rule above others, 10 just makes it prioritized higher than any conflicting OOB rules |
| 2 | Scope | Default Scope | This allows separation of rules across different services, for this case, it doesn't matter |
| 3 | Match Classes | with a Class Name that, Equals, org.kjt.azure.java.cosmosdb.gremlin.Program | We want to instrument a Class (versus, perhaps an interface), and we want to Exact match the fully-qualifed Class name |
| 3 | Method Name | Equals, executeQueries | We want to instrument via Exact match of the given method name |

#### Step Number 1
![Step Number 1](/images/gremlin_business_transaction_config_1.png)

#### Step Number 2
![Step Number 2](/images/gremlin_business_transaction_config_2.png)

#### Step Number 3
![Step Number 3](/images/gremlin_business_transaction_config_3.png)

# Developing a Java app using Azure Cosmos DB
Azure Cosmos DB is a globally distributed multi-model database. One of the supported APIs is the Graph (Gremlin) API, which provides a graph data model with [Gremlin query/traversals](https://tinkerpop.apache.org/gremlin.html). This sample shows you how to use the Azure Cosmos DB with the Graph API to store and access data from a Java application.

## Running this sample

* Before you can run this sample, you must have the following prerequisites:

   * An active Azure account. If you don't have one, you can sign up for a [free account](https://azure.microsoft.com/free/). Alternatively, you can use the [Azure Cosmos DB Emulator](https://azure.microsoft.com/documentation/articles/documentdb-nosql-local-emulator) for this tutorial.
   * JDK 1.7+ (Run `apt-get install default-jdk` if you don't have JDK)
   * Maven (Run `apt-get install maven` if you don't have Maven)

* Then, clone this repository

* Next, substitute the endpoint and authorization key in the `remote.yaml` with your Cosmos DB account's values. 

| Setting | Suggested Value | Description |
| ------- | --------------- | ----------- |
| hosts   | [***.gremlin.cosmosdb.azure.com] | This is the Gremlin URI value on the Overview page of the Azure portal, in square brackets, with the trailing :443/ removed.  This value can also be retrieved from the Keys tab, using the URI value by removing https://, changing documents to graphs, and removing the trailing :443/. |
| port | 443 | Set the port to 443 |
| username | `/dbs/<db>/colls/<coll>` | The resource of the form `/dbs/<db>/colls/<coll>` where `<db>` is your database name and `<coll>` is your collection name. |
| password | Your primary key | This is your primary key, which you can retrieve from the Keys page of the Azure portal, in the Primary Key box. Use the copy button on the left side of the box to copy the value. |
| connectionPool | `{enableSsl: true}` | Your connection pool setting for SSL. |
| serializer | { className: org.apache.tinkerpop.gremlin. driver.ser.GraphSONMessageSerializerV1d0, config: { serializeResultToString: true }} | Set to this value and delete any \n line breaks and spaces when pasting in the value. |

* From a command prompt or shell, run `mvn package` to compile and resolve dependencies.

* From a command prompt or shell, run `mvn exec:java -D exec.mainClass=org.kjt.azure.java.cosmosdb.gremlin.Program` to run the application.
* Or, to run as a far jar, `java -jar target/gremlindriverclient-1.0-SNAPSHOT.jar` to run the application.

## About the code
The code included in this sample is intended to get you quickly started with a Java application that connects to Azure Cosmos DB with the Graph (Gremlin) API.

## More information

- [Azure Cosmos DB](https://docs.microsoft.com/azure/cosmos-db/introduction)
- [Azure Cosmos DB : Graph API](https://docs.microsoft.com/en-us/azure/cosmos-db/graph-introduction)
- [Gremlin Java SDK](http://tinkerpop.apache.org/docs/current/reference/#gremlin-java)
- [Gremlin Java Reference Documentation](http://tinkerpop.apache.org/javadocs/current/full/)

## Run with Docker Compose

//TODO
