package com.alext.monitor;

import org.junit.Test;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testTransactionBuilder_toString_returnsExpected() {
        long id = 123;
        TransactionType type = TransactionType.TRANSFER;
        String timestamp = "4321 T 56:78";

        Transaction transaction = new Transaction.Builder().
                id(id).
                type(type).
                timestamp(timestamp).
                field("field5", "value5").
                field("field6", "value6").
                build();

        String transactionAsString = transaction.toString();

        assertTrue(transactionAsString.contains("id=" + String.valueOf(id)));
        assertTrue(transactionAsString.contains("type=" + type.toString()));
    }

    @Test
    public void testTransaction_hashCodeAndEqualsOfSameTransaction_returnsExpected() {
        long id = 123;
        TransactionType type = TransactionType.TRANSFER;
        String timestamp = "4321 T 56:78";

        Transaction transaction1 = new Transaction.Builder().
                id(id).
                type(type).
                timestamp(timestamp).
                field("field5", "value5").
                field("field6", "value6").
                build();

        Transaction transaction2 = new Transaction.Builder().
                id(id).
                type(type).
                timestamp(timestamp).
                field("field5", "value5").
                field("field6", "value6").
                build();

        assertTrue(transaction1.equals(transaction1));
        assertTrue(transaction2.equals(transaction2));
        assertTrue(transaction1.equals(transaction2));
        assertTrue(transaction2.equals(transaction1));
        assertEquals(transaction1.hashCode(), transaction2.hashCode());

    }
}