package com.alext.monitor.storage.mongodb;

import com.alext.monitor.Transaction;
import com.alext.monitor.storage.StorageApi;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@PropertySource("mongo.properties")
public class MongoStorage implements StorageApi {
    private final MongoCollection<Document> transactions;
    private final MongoCollection<Document> published;
    private final MongoClient mongoClient;

    MongoStorage(@Value("${mongo.url}") final String url,
                 @Value("${mongo.database}") final String dbName) {
        mongoClient = new MongoClient(new MongoClientURI(url));
        transactions = mongoClient.getDatabase(dbName).getCollection("transactions");
        published = mongoClient.getDatabase(dbName).getCollection("published");
    }

    @Override
    public long getIdOfLastPublished() {
        // MongoDB query looks like this
        // db.published.aggregate({ $group: {_id: null, max: {$max: "$_id"}}});
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document(MongoConstants.$_GROUP,
                new Document(MongoConstants._ID, null).append(MongoConstants.MAX, new Document(MongoConstants.$_MAX, MongoConstants.$_ID))
        ));

        AggregateIterable<Document> queryResults = published.aggregate(pipeline);

        if (queryResults.iterator().hasNext())
            return queryResults.iterator().next().getLong(MongoConstants.MAX);

        return -1;
    }

    @Override
    public void markPublished(Transaction transaction) {
        published.insertOne(new Document(MongoConstants._ID, transaction.getId()));
    }

    @Override
    public Collection<Transaction> getTransactionsNewerThan(long id) {
        // db.transactions.find({_id : {$gt : 3}});
        FindIterable<Document> queryResults = transactions.find(
                new Document(MongoConstants._ID,
                        new Document("$gt", id)
                )
        ).sort(new Document(MongoConstants._ID, 1));

        List<Transaction> result = new ArrayList<>();

        for (Document document : queryResults) {
            result.add(BsonConvertor.convert(document));
        }

        return result;
    }

    MongoCollection<Document> getTransactions() {
        return transactions;
    }
}
