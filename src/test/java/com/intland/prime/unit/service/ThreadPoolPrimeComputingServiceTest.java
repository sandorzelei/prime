package com.intland.prime.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.intland.prime.service.check.PrimeCheckService;
import com.intland.prime.service.computing.PrimeComputingScheduler;
import com.intland.prime.service.computing.impl.PrimeComputingTask;
import com.intland.prime.service.computing.impl.PrimeComputingTaskFactory;
import com.intland.prime.service.computing.impl.ThreadPoolPrimeComputingService;
import com.intland.prime.service.queue.QueueService;
import com.intland.prime.service.store.PrimeNumberStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ThreadPoolPrimeComputingServiceTest {

    @MockBean
    private PrimeComputingScheduler primeComputingSchuler;

    @MockBean
    private PrimeNumberStore primeNumberStore;

    @MockBean
    private QueueService primeQueueService;

    @MockBean
    private PrimeCheckService primeCheckService;

    @MockBean
    private ThreadPoolTaskExecutor executor;

    private ThreadPoolPrimeComputingService service;

    private PrimeComputingTaskFactory factory;

    @Before
    public void init() {
        this.factory = new PrimeComputingTaskFactory(this.primeCheckService, this.primeNumberStore, this.primeQueueService);
        this.service = spy(new ThreadPoolPrimeComputingService(this.executor));
    }

    @Test
    public void testTaskIsScheduledIfPrimeIsNotFound() {

        // GIVEN
        final long index = 1L;
        final PrimeComputingTask task = this.factory.create(index);

        when(this.primeNumberStore.getPrime(ArgumentMatchers.eq(index))).thenReturn(Optional.empty());

        // WHEN
        this.service.startComputingPrime(task);

        // THEN
        verify(this.executor, times(1)).submit(ArgumentMatchers.eq(task));
        verifyNoMoreInteractions(this.executor);
    }

    @Test
    public void testTaskIsNotScheduledIfPrimeIsFound() {

        // GIVEN
        final long index = 1L;
        final PrimeComputingTask task = this.factory.create(index);

        when(this.primeNumberStore.getPrime(ArgumentMatchers.eq(index))).thenReturn(Optional.of(BigInteger.valueOf(2L)));

        // WHEN
        this.service.startComputingPrime(task);

        // THEN
        verify(this.service, times(1)).startComputingPrime(ArgumentMatchers.eq(task));
        verify(this.executor, times(1)).submit(ArgumentMatchers.eq(task));
        verifyNoMoreInteractions(this.service);
    }

    @Test
    public void testServiceIsNotAvaliableIfExecutorIsBusy() {

        // GIVEN
        final long index = 1L;

        when(this.primeNumberStore.getPrime(ArgumentMatchers.eq(index))).thenReturn(Optional.empty());
        when(this.executor.getActiveCount()).thenReturn(1);
        when(this.executor.getMaxPoolSize()).thenReturn(1);

        // WHEN
        this.service.startComputingPrime(this.factory.create(index));
        final boolean isAvailable = this.service.isAvailable();

        // THEN
        assertThat(isAvailable).isFalse();
    }

    @Test
    public void testServiceIsAvaliableIfExecutorIsNotBusy() {

        // GIVEN
        final long index = 1L;

        when(this.primeNumberStore.getPrime(ArgumentMatchers.eq(index))).thenReturn(Optional.empty());
        when(this.executor.getActiveCount()).thenReturn(0);
        when(this.executor.getMaxPoolSize()).thenReturn(1);

        // WHEN
        this.service.startComputingPrime(this.factory.create(index));
        final boolean isAvailable = this.service.isAvailable();

        // THEN
        assertThat(isAvailable).isTrue();
    }
}
