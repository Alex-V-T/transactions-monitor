package com.alext.monitor.storage.mongodb;

import com.alext.monitor.TransactionType;
import org.bson.Document;

import java.util.Date;
import java.util.Random;

public class TransactionsGenerator {
    private static final Random random = new Random();

    private static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static Document generateNewTransaction(long id) {
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
