package com.alext.monitor;

import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class FieldsFilterTest {

    @Test
    public void parseString_twoFields_returnsExpected() {
        String fieldsAsString = "transferField2,transferField5";

        Collection<String> actual = FieldsFilter.parseString(fieldsAsString);

        assertEquals(2, actual.size());
        assertThat(actual, containsInAnyOrder("transferField2", "transferField5"));
    }

    @Test
    public void parseString_commasWithoutValue_returnsExpected() {
        String fieldsAsString = "transferField2,,,transferField5";

        Collection<String> actual = FieldsFilter.parseString(fieldsAsString);

        assertEquals(2, actual.size());
        assertThat(actual, containsInAnyOrder("transferField2", "transferField5"));
    }

    @Test
    public void parseString_multipleSpaces_returnsExpected() {
        String fieldsAsString = " transferField2, transferField3 , , transferField5 ";

        Collection<String> actual = FieldsFilter.parseString(fieldsAsString);

        assertEquals(3, actual.size());
        assertThat(actual, containsInAnyOrder("transferField2", "transferField3", "transferField5"));

    }

    @Test
    public void filter_withoutFields_returnsExpected() {
        Transaction transaction = new Transaction.Builder().
                id(123L).
                type(TransactionType.TRANSFER).
                timestamp("123").build();

        FieldsFilter filter = new FieldsFilter("", "", "");
        Transaction filtredTransaction = filter.filter(transaction);

        assertEquals(transaction.getId(), filtredTransaction.getId());
        assertEquals(transaction.getType(), filtredTransaction.getType());
        assertEquals(transaction.getTimestamp(), filtredTransaction.getTimestamp());
    }

    @Test
    public void filter_twoFields_returnsExpected() {
        String fieldsAsString = " field3,field5";

        Transaction.Builder transactionBuilder = new Transaction.Builder().
                id(123L).
                type(TransactionType.TRANSFER).
                timestamp("123");

        for (int i = 1; i < 10; i++)
            transactionBuilder.field("field" + i, "value" + i);

        Transaction transaction = transactionBuilder.build();

        FieldsFilter filter = new FieldsFilter(fieldsAsString, "", "");
        Transaction filtredTransaction = filter.filter(transaction);

        assertEquals(transaction.getId(), filtredTransaction.getId());
        assertEquals(transaction.getType(), filtredTransaction.getType());
        assertEquals(transaction.getTimestamp(), filtredTransaction.getTimestamp());
        assertEquals(2, filtredTransaction.getFields().size());
        assertThat(filtredTransaction.getFields(), allOf(
                hasEntry("field3", "value3"),
                hasEntry("field5", "value5")
        ));
    }
}