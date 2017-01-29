package com.alext.monitor.storage.mongodb;

import com.alext.monitor.Transaction;
import org.bson.Document;
import org.junit.Test;

import static com.alext.monitor.storage.mongodb.MongoConstants._ID;
import static com.alext.monitor.storage.mongodb.TransactionsGenerator.generateNewTransaction;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransactionsGeneratorTest {
    @Test
    public void generateNewTransactionTest_generateOne_ReturnsNewTransaction() {
        long id = 987L;
        Document newMongoDoc = generateNewTransaction(id);

        Transaction transaction = BsonConvertor.convert(newMongoDoc);
        assertEquals(id, transaction.getId());
        assertNotNull(transaction.getType());
        assertNotNull(transaction.getTimestamp());
        assertEquals(50, transaction.getFields().size());
    }
}