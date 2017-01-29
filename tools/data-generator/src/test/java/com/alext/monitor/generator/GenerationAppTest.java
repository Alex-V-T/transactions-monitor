package com.alext.monitor.generator;

import com.alext.monitor.Transaction;
import com.alext.monitor.storage.mongodb.MongoStorage;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static com.alext.monitor.storage.mongodb.TransactionsGenerator.generateNewTransaction;
import static junit.framework.TestCase.assertEquals;

public class GenerationAppTest {
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
    public void testMain_run_10recordsInserted() throws IOException, InterruptedException {
        MongoStorage dao = new MongoStorage(MONGODB_LOCALHOST_URL, "generation");

        assertEquals(0, dao.getTransactions().count());

        Application.main(new String[]{"10", "0", MONGODB_LOCALHOST_URL, "generation"});

        assertEquals(10, dao.getTransactions().count());
    }
}