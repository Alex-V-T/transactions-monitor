package com.alext.monitor.storage.mongodb;

import com.alext.monitor.Transaction;
import com.alext.monitor.TransactionType;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class MongoStorageTest {
    public static final int MONGO_PORT = 27017;
    public static final String LOCALHOST = "localhost";
    public static final String MONGODB_LOCALHOST_URL = "mongodb://localhost";
    private static final MongodStarter starter = MongodStarter.getDefaultInstance();
    private MongodExecutable _mongodExe;
    private MongodProcess _mongod;
    private MongoClient _mongo;

    @Before
    public void setUp() throws Exception {

        _mongodExe = starter.prepare(new MongodConfigBuilder()
                .version(Version.Main.V3_2)
                .net(new Net(LOCALHOST, MONGO_PORT, Network.localhostIsIPv6()))
                .build());
        _mongod = _mongodExe.start();

        _mongo = new MongoClient(LOCALHOST, MONGO_PORT);
    }

    @After
    public void tearDown() throws Exception {
        _mongod.stop();
        _mongodExe.stop();
    }

    public Mongo getMongo() {
        return _mongo;
    }

    @Test
    public void testgetIdOfLastPublished_emptyDatabase_returnsNoRecords() {
        MongoStorage dao = new MongoStorage(MONGODB_LOCALHOST_URL, "testgetIdOfLastPublished_emptyDatabase_returnsNoRecords");

        long id = dao.getIdOfLastPublished();

        assertEquals(-1, id);

        Collection<Transaction> transactions = dao.getTransactionsNewerThan(id);

        assertTrue(transactions.isEmpty());
    }

    @Test
    public void testgetIdOfLastPublished_markPublished_returnsNoRecords() {
        MongoStorage dao = new MongoStorage(MONGODB_LOCALHOST_URL, "testgetIdOfLastPublished_markPublished_returnsNoRecords");

        long id = dao.getIdOfLastPublished();

        assertEquals(-1, id);

        Collection<Transaction> transactions = dao.getTransactionsNewerThan(id);

        assertTrue(transactions.isEmpty());

        Transaction transaction = new Transaction.Builder().
                id(++id).
                type(TransactionType.DEPOSIT).
                timestamp("7654321").
                field("field1", "value1").
                field("field2", "value2").
                build();

        dao.markPublished(transaction);

        assertEquals(id, dao.getIdOfLastPublished());
    }

}