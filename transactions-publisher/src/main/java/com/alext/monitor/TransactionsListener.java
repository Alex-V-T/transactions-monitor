package com.alext.monitor;

import com.alext.monitor.storage.StorageApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
class TransactionsListener {
    public static final int WAIT_BEFORE_RETRY = 1000;
    private final Logger logger = LoggerFactory.getLogger(TransactionsListener.class);
    @Autowired
    private StorageApi storageApi;
    @Autowired
    private JmsPublisher publisher;
    @Autowired
    private FieldsFilter fieldsFilter;

    public void run() {
        long idOfLastPublished = storageApi.getIdOfLastPublished();

        while (true) {
            try {
                Collection<Transaction> newTransactions = storageApi.getTransactionsNewerThan(idOfLastPublished);

                if (newTransactions != null && newTransactions.size() > 0) {
                    logger.info("Received {} new transactions", newTransactions.size());
                    for (Transaction transaction : newTransactions) {
                        transaction = fieldsFilter.filter(transaction);

                        logger.info("Publishing transaction {}", transaction);
                        publisher.publish(transaction);

                        logger.info("Marking transaction 'published' in NoSQL data-storage");
                        storageApi.markPublished(transaction);

                        idOfLastPublished = transaction.getId();
                    }
                }
            } catch (Exception exception) {
                logger.error("Error in Tansaction listener", exception);
                logger.info("Waiting {}ms before re-trying", WAIT_BEFORE_RETRY);
                try {
                    Thread.sleep(WAIT_BEFORE_RETRY);
                } catch (InterruptedException e) {
                    logger.info("Interrupted signal received, exiting.");
                    return;
                }
            }
        }
    }
}
