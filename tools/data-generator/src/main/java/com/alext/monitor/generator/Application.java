package com.alext.monitor.generator;

import com.alext.monitor.TransactionType;
import com.alext.monitor.storage.mongodb.MongoConstants;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;

class Application {
    private static final Random random = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties properties = new Properties();
        properties.load(Application.class.getClassLoader().getResourceAsStream("mongo.properties"));
        final String url = properties.getProperty("mongo.url");
        final String database = properties.getProperty("mongo.database");

        MongoClient mongoClient = new MongoClient(new MongoClientURI(url));

        MongoCollection<Document> transactions = mongoClient.getDatabase(database).getCollection("transactions");

        long id = getLatestId(transactions);

        while (true) {
            id++;
            Document newDocument = generateNewTransaction(id);
            System.out.println("Inserting new document: " + newDocument);
            transactions.insertOne(newDocument);
            Thread.sleep(5000);
        }
    }

    private static long getLatestId(MongoCollection<Document> collection) {
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document(MongoConstants.$_GROUP,
                new Document(MongoConstants._ID, null).append(MongoConstants.MAX, new Document(MongoConstants.$_MAX, MongoConstants.$_ID))
        ));

        AggregateIterable<Document> queryResults = collection.aggregate(pipeline);

        if(queryResults.iterator().hasNext())
           return  queryResults.iterator().next().getLong(MongoConstants.MAX);

        return 1;
    }

    private static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    private static Document generateNewTransaction(long id) {
        String type = randomEnum(TransactionType.class).name().toLowerCase();
        Document newDocument = new Document(MongoConstants._ID, id)
                .append("type", type)
                .append("timestamp", new Date());

        for (int i = 0; i < 50; i++) {
            newDocument.append(type + "Field" + i, "value" + i);
        }
        return newDocument;
    }
}
