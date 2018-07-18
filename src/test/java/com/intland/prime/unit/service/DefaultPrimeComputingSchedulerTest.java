package com.intland.prime.unit.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.intland.prime.service.check.PrimeCheckService;
import com.intland.prime.service.computing.PrimeComputingScheduler;
import com.intland.prime.service.computing.PrimeComputingService;
import com.intland.prime.service.computing.impl.DefaultPrimeComputingScheduler;
import com.intland.prime.service.computing.impl.PrimeComputingTask;
import com.intland.prime.service.computing.impl.PrimeComputingTaskFactory;
import com.intland.prime.service.queue.QueueService;
import com.intland.prime.service.store.PrimeNumberStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultPrimeComputingSchedulerTest {

    @MockBean
    private PrimeComputingScheduler primeComputingSchuler;

    @MockBean
    private PrimeComputingService primeComputingService;

    @MockBean
    private QueueService primeQueueService;

    @MockBean
    private PrimeNumberStore primeNumberStore;

    @MockBean
    private PrimeCheckService primeCheckService;

    private DefaultPrimeComputingScheduler service;

    private PrimeComputingTaskFactory factory;

    @Before
    public void init() {
        this.factory = new PrimeComputingTaskFactory(this.primeCheckService, this.primeNumberStore, this.primeQueueService);
        this.service = new DefaultPrimeComputingScheduler(this.factory, this.primeComputingService, this.primeQueueService);
    }

    @Test
    public void testWhenQueueIsEmpty() {

        // GIVEN
        when(this.primeQueueService.hasNextScheduled()).thenReturn(false);

        // WHEN
        this.service.scheduleNext();

        // THEN
        verify(this.primeQueueService, times(1)).hasNextScheduled();
        verifyNoMoreInteractions(this.primeQueueService);
        verifyNoMoreInteractions(this.primeComputingService);
    }

    @Test
    public void testWhenQueueIsNotEmptyButThereServerIsBusy() {

        // GIVEN
        when(this.primeQueueService.hasNextScheduled()).thenReturn(true);
        when(this.primeComputingService.isAvailable()).thenReturn(false);

        // WHEN
        this.service.scheduleNext();

        // THEN
        verify(this.primeQueueService, times(1)).hasNextScheduled();
        verify(this.primeComputingService, times(1)).isAvailable();
        verifyNoMoreInteractions(this.primeQueueService);
        verifyNoMoreInteractions(this.primeComputingService);
    }

    @Test
    public void testWhenQueueIsNotEmptyButSomeOtherServerStoleIt() {

        // GIVEN
        when(this.primeQueueService.hasNextScheduled()).thenReturn(true);
        when(this.primeQueueService.getNextIndex()).thenReturn(Optional.empty());
        when(this.primeComputingService.isAvailable()).thenReturn(true);

        // WHEN
        this.service.scheduleNext();

        // THEN
        verify(this.primeQueueService, times(1)).hasNextScheduled();
        verify(this.primeQueueService, times(1)).getNextIndex();
        verify(this.primeComputingService, times(1)).isAvailable();
        verifyNoMoreInteractions(this.primeQueueService);
        verifyNoMoreInteractions(this.primeComputingService);
    }

    @Test
    public void testWhenQueueIsNotEmptyButOtherServerIsWorkingOnIt() {

        // GIVEN
        final long index = 1L;
        when(this.primeQueueService.hasNextScheduled()).thenReturn(true);
        when(this.primeQueueService.getNextIndex()).thenReturn(Optional.of(index));
        when(this.primeComputingService.isAvailable()).thenReturn(true);

        // WHEN
        this.service.scheduleNext();

        // THEN
        verify(this.primeQueueService, times(1)).hasNextScheduled();
        verify(this.primeQueueService, times(1)).getNextIndex();
        verify(this.primeComputingService, times(1)).isAvailable();
        verify(this.primeComputingService, times(1)).startComputingPrime(ArgumentMatchers.any(PrimeComputingTask.class));

        verifyNoMoreInteractions(this.primeQueueService);
        verifyNoMoreInteractions(this.primeComputingService);
    }

    @Test
    public void testWhenQueueIsNotEmptyAndCanBeScheduled() {

        // GIVEN
        final long index = 1L;
        when(this.primeQueueService.hasNextScheduled()).thenReturn(true);
        when(this.primeQueueService.getNextIndex()).thenReturn(Optional.of(index));
        when(this.primeComputingService.isAvailable()).thenReturn(true);

        // WHEN
        this.service.scheduleNext();

        // THEN
        verify(this.primeQueueService, times(1)).hasNextScheduled();
        verify(this.primeQueueService, times(1)).getNextIndex();
        verify(this.primeComputingService, times(1)).isAvailable();
        verify(this.primeComputingService, times(1)).startComputingPrime(ArgumentMatchers.any(PrimeComputingTask.class));

        // verifyNoMoreInteractions(this.processingPrimeQueueService);
        verifyNoMoreInteractions(this.primeQueueService);
        verifyNoMoreInteractions(this.primeComputingService);
    }

}
