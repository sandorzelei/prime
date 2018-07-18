package com.intland.prime.unit.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.intland.prime.service.computing.PrimeComputingScheduler;
import com.intland.prime.service.computing.PrimeComputingService;
import com.intland.prime.service.computing.impl.DefaultPrimeComputingScheduler;
import com.intland.prime.service.queue.QueueService;

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

    @MockBean(name = "scheduled")
    private QueueService scheduledPrimeQueueService;

    @MockBean(name = "processing")
    private QueueService processingPrimeQueueService;

    private DefaultPrimeComputingScheduler service;

    @Before
    public void init() {
        this.service = new DefaultPrimeComputingScheduler(this.primeComputingService, this.scheduledPrimeQueueService, this.processingPrimeQueueService);
    }

    @Test
    public void testWhenQueueIsEmpty() {

        // GIVEN
        when(this.scheduledPrimeQueueService.hasNext()).thenReturn(false);

        // WHEN
        this.service.scheduleNext();

        // THEN
        verify(this.scheduledPrimeQueueService, times(1)).hasNext();
        verifyNoMoreInteractions(this.scheduledPrimeQueueService);
        verifyNoMoreInteractions(this.processingPrimeQueueService);
        verifyNoMoreInteractions(this.primeComputingService);
    }

    @Test
    public void testWhenQueueIsNotEmptyButThereServerIsBusy() {

        // GIVEN
        when(this.scheduledPrimeQueueService.hasNext()).thenReturn(true);
        when(this.primeComputingService.isAvailable()).thenReturn(false);

        // WHEN
        this.service.scheduleNext();

        // THEN
        verify(this.scheduledPrimeQueueService, times(1)).hasNext();
        verify(this.primeComputingService, times(1)).isAvailable();
        verifyNoMoreInteractions(this.processingPrimeQueueService);
        verifyNoMoreInteractions(this.scheduledPrimeQueueService);
        verifyNoMoreInteractions(this.primeComputingService);
    }

    @Test
    public void testWhenQueueIsNotEmptyButSomeOtherServerStoleIt() {

        // GIVEN
        when(this.scheduledPrimeQueueService.hasNext()).thenReturn(true);
        when(this.scheduledPrimeQueueService.pop()).thenReturn(Optional.empty());
        when(this.primeComputingService.isAvailable()).thenReturn(true);

        // WHEN
        this.service.scheduleNext();

        // THEN
        verify(this.scheduledPrimeQueueService, times(1)).hasNext();
        verify(this.scheduledPrimeQueueService, times(1)).pop();
        verify(this.primeComputingService, times(1)).isAvailable();
        verifyNoMoreInteractions(this.processingPrimeQueueService);
        verifyNoMoreInteractions(this.scheduledPrimeQueueService);
        verifyNoMoreInteractions(this.primeComputingService);
    }

    @Test
    public void testWhenQueueIsNotEmptyButSomeOtherServerIsWorkingOnIt() {

        // GIVEN
        final long index = 1L;
        when(this.scheduledPrimeQueueService.hasNext()).thenReturn(true);
        when(this.scheduledPrimeQueueService.pop()).thenReturn(Optional.of(index));
        when(this.primeComputingService.isAvailable()).thenReturn(true);
        when(this.processingPrimeQueueService.contains(ArgumentMatchers.eq(index))).thenReturn(true);

        // WHEN
        this.service.scheduleNext();

        // THEN
        verify(this.scheduledPrimeQueueService, times(1)).hasNext();
        verify(this.scheduledPrimeQueueService, times(1)).pop();
        verify(this.primeComputingService, times(1)).isAvailable();
        verify(this.processingPrimeQueueService, times(1)).contains(ArgumentMatchers.eq(index));
        verifyNoMoreInteractions(this.processingPrimeQueueService);
        verifyNoMoreInteractions(this.scheduledPrimeQueueService);
        verifyNoMoreInteractions(this.primeComputingService);
    }

    @Test
    public void testWhenQueueIsNotEmptyAndCanBeScheduled() {

        // GIVEN
        final long index = 1L;
        when(this.scheduledPrimeQueueService.hasNext()).thenReturn(true);
        when(this.scheduledPrimeQueueService.pop()).thenReturn(Optional.of(index));
        when(this.primeComputingService.isAvailable()).thenReturn(true);
        when(this.processingPrimeQueueService.contains(ArgumentMatchers.eq(index))).thenReturn(false);

        // WHEN
        this.service.scheduleNext();

        // THEN
        verify(this.scheduledPrimeQueueService, times(1)).hasNext();
        verify(this.scheduledPrimeQueueService, times(1)).pop();
        verify(this.primeComputingService, times(1)).isAvailable();
        verify(this.processingPrimeQueueService, times(1)).contains(ArgumentMatchers.eq(index));
        verify(this.primeComputingService, times(1)).startComputingPrime(ArgumentMatchers.eq(index));
        verifyNoMoreInteractions(this.processingPrimeQueueService);
        verifyNoMoreInteractions(this.scheduledPrimeQueueService);
        verifyNoMoreInteractions(this.primeComputingService);
    }
}
