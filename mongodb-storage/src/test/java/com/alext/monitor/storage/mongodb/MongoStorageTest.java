package com.alext.monitor.storage.mongodb;

import com.alext.monitor.Transaction;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class MongoStorageTest {

    //@Test
    public void test(){
        //TODO: write integration test
        MongoStorage dao = new MongoStorage("mongodb://localhost", "bank-test");

        long id = dao.getIdOfLastPublished();
        System.out.println(id);

        System.out.println("Searching transaction");

        Collection<Transaction> transactions = dao.getTransactionsNewerThan(id);

        transactions.forEach(System.out::println);
    }
}