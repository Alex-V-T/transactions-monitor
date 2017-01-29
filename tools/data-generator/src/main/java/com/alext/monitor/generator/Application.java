package com.alext.monitor.generator;

import com.alext.monitor.storage.mongodb.MongoConstants;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.alext.monitor.storage.mongodb.TransactionsGenerator.generateNewTransaction;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        final Properties properties = new Properties();
        properties.load(Application.class.getClassLoader().getResourceAsStream("mongo.properties"));
        final String url = properties.getProperty("mongo.url");
        final String database = properties.getProperty("mongo.database");

        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(url))) {

            final MongoCollection<Document> transactions = mongoClient.getDatabase(database).getCollection("transactions");

            long id = getLatestId(transactions);

            while (id < Integer.MAX_VALUE) {
                id++;
                final Document newDocument = generateNewTransaction(id);
                logger.info("Inserting new document: {}", newDocument);
                transactions.insertOne(newDocument);
                Thread.sleep(5000);
            }
        }
    }

    private static long getLatestId(MongoCollection<Document> collection) {
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document(MongoConstants.$_GROUP,
                new Document(MongoConstants._ID, null).append(MongoConstants.MAX, new Document(MongoConstants.$_MAX, MongoConstants.$_ID))
        ));

        AggregateIterable<Document> queryResults = collection.aggregate(pipeline);

        if (queryResults.iterator().hasNext())
            return queryResults.iterator().next().getLong(MongoConstants.MAX);

        return 1;
    }
}
