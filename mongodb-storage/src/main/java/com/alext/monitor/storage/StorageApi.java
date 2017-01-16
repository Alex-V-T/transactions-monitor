package com.alext.monitor.storage;

import com.alext.monitor.Transaction;

import java.util.Collection;

public interface StorageApi {
    long getIdOfLastPublished();
    void markPublished(final Transaction transaction);
    Collection<Transaction> getTransactionsNewerThan(final long id);
}
