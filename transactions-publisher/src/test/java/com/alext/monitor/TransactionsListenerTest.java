package com.alext.monitor;

import com.alext.monitor.storage.StorageApi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MockedApiConfig.class)
public class TransactionsListenerTest {
    @Autowired

    @Mock
    private StorageApi mockStore;

    @InjectMocks
    @Autowired
    private TransactionsListener listener;

    @InjectMocks
    @Autowired
    private JmsPublisher publisher;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRun_mockedJmsAndNoSql_expectedActions() throws Exception {
        when(mockStore.getIdOfLastPublished())
                .thenReturn(10L);

        Transaction transaction = new Transaction.Builder().
                id(11).
                type(TransactionType.TRANSFER).
                build();

        when(mockStore.getTransactionsNewerThan(10))
                .thenReturn(Arrays.asList(transaction));

        when(mockStore.getTransactionsNewerThan(transaction.getId())).thenThrow(new RuntimeException());

        Thread.currentThread().interrupt();
        listener.run();

        verify(publisher).publish(transaction);
        verify(mockStore).markPublished(transaction);
    }

}