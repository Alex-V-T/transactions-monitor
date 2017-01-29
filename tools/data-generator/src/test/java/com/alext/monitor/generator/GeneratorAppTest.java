package com.alext.monitor.generator;

import com.alext.monitor.Transaction;
import com.alext.monitor.storage.mongodb.BsonConvertor;
import org.bson.Document;
import org.junit.Test;

import static com.alext.monitor.generator.Application.generateNewTransaction;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GeneratorAppTest {
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