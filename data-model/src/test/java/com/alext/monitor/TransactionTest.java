package com.alext.monitor;

import org.junit.Test;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TransactionTest {

    @Test
    public void testTransactionBuilder_buildTransaction_expectedObjectCreated() {
        long id = 987;
        TransactionType type = TransactionType.DEPOSIT;
        String timestamp = "1234 T 56:78";

        Transaction transaction = new Transaction.Builder().
                id(id).
                type(type).
                timestamp(timestamp).
                field("field1", "value1").
                field("field2", "value2").
                build();

        assertEquals(id, transaction.getId());
        assertEquals(type, transaction.getType());
        assertEquals(timestamp, transaction.getTimestamp());
        assertEquals(2, transaction.getFields().size());
        assertThat(transaction.getFields(), allOf(
                hasEntry("field1", "value1"),
                hasEntry("field2", "value2"))
            );
    }
}