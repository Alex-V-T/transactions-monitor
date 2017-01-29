package com.alext.monitor.storage.mongodb;

import com.alext.monitor.Transaction;
import com.alext.monitor.TransactionType;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import static com.alext.monitor.storage.mongodb.MongoConstants._ID;

public class BsonConvertor {
    private static final String TYPE = "type";
    private static final String TIMESTAMP = "timestamp";
    private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    public static Transaction convert(final Document document) {
        Transaction.Builder transactionBilder = new Transaction.Builder().
                id(document.getLong(_ID)).
                type(TransactionType.valueOf(document.getString(TYPE).toUpperCase())).
                timestamp(formatter.format(document.getDate(TIMESTAMP)));

        for (Map.Entry<String, Object> entry : document.entrySet()) {
            String key = entry.getKey();
            if (!key.equals(_ID) && !key.equals(TYPE) && !key.equals(TIMESTAMP)) {
                transactionBilder.field(key, entry.getValue().toString());
            }
        }
        return transactionBilder.build();
    }
}
