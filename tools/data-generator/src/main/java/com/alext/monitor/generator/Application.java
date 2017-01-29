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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.alext.monitor.storage.mongodb.TransactionsGenerator.generateNewTransaction;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        final int toGenerate = args.length < 1 ? Integer.MAX_VALUE - 1 : Integer.valueOf(args[0]);
        final int pause = args.length < 2 ? 5000 : Integer.valueOf(args[1]);

        final Properties properties = new Properties();
        properties.load(Application.class.getClassLoader().getResourceAsStream("mongo.properties"));
        final String url = args.length < 3 ? properties.getProperty("mongo.url") : args[2];
        final String database = args.length < 4 ? properties.getProperty("mongo.database") : args[3];

        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(url))) {

            final MongoCollection<Document> transactions = mongoClient.getDatabase(database).getCollection("transactions");

            long id = getLatestId(transactions);

            while (id <= toGenerate) {
                id++;
                final Document newDocument = generateNewTransaction(id);
                logger.info("Inserting new document: {}", newDocument);
                transactions.insertOne(newDocument);
                Thread.sleep(pause);
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
