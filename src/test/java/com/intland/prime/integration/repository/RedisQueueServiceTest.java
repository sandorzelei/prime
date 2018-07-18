package com.intland.prime.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.intland.prime.service.queue.QueueService;
import com.intland.prime.service.queue.impl.RedisQueueService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisQueueServiceTest {

    @Autowired
    private RedisTemplate<String, Long> template;

    private QueueService service;

    @Before
    public void init() {
        this.service = new RedisQueueService("test", this.template);
        this.service.clear();
    }

    @Test
    public void testHasNextWhenQueueIsEmpty() {
        // GIVEN

        // WHEN
        final Boolean hasNext = this.service.hasNextScheduled();

        // THEN
        assertThat(hasNext).isFalse();
    }

    @Test
    public void testHasNextWhenQueueIsNotEmpty() {
        // GIVEN
        this.service.schedule(2L);

        // WHEN
        final Boolean hasNext = this.service.hasNextScheduled();

        // THEN
        assertThat(hasNext).isTrue();
    }

    @Test
    public void testQueueReturnTheRightValue() {
        // GIVEN
        final long index = 2L;
        this.service.schedule(index);

        // WHEN
        final Long nextIndex = this.service.getNextIndex().get();

        // THEN
        assertThat(nextIndex).isEqualTo(index);
        assertThat(this.service.isScheduled(index)).isFalse();
        assertThat(this.service.isProcessing(index)).isTrue();
    }

    @Test
    public void testQueueRemovesDuplcations() {
        // GIVEN
        final long index = 2L;
        this.service.schedule(index);
        this.service.schedule(index);

        // WHEN
        final Long nextIndex = this.service.getNextIndex().get();
        final Boolean hasNext = this.service.hasNextScheduled();

        // THEN
        assertThat(nextIndex).isEqualTo(index);
        assertThat(hasNext).isFalse();
    }

    @Test
    public void testQueueReturnTheRightValues() {
        // GIVEN
        LongStream.range(0, 10).forEach(this.service::schedule);

        // WHEN
        final List<Long> indexes = new ArrayList<>();
        while (this.service.hasNextScheduled()) {
            this.service.getNextIndex().ifPresent(indexes::add);
        }

        // THEN
        assertThat(indexes).size().isEqualTo(10);
        assertThat(indexes).containsOnly(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
    }

    @Test
    public void testScheduledQueueContainsElement() {
        // GIVEN
        LongStream.range(0, 10).forEach(this.service::schedule);

        // WHEN
        final Boolean isScheduled = this.service.isScheduled(5L);

        // THEN
        assertThat(isScheduled).isTrue();
    }

    @Test
    public void testQueueReturnNullIfEmpty() {
        // GIVEN

        // WHEN
        final Optional<Long> nextIndex = this.service.getNextIndex();

        // THEN
        assertThat(nextIndex).isNotNull();
        assertThat(nextIndex.isPresent()).isFalse();
    }

    @Test
    public void testQueueIsCleared() {
        // GIVEN
        LongStream.range(0, 10).forEach(this.service::schedule);

        // WHEN
        final Boolean hasNextBeforeClear = this.service.hasNextScheduled();
        this.service.clear();
        final Boolean hasNextAfterClear = this.service.hasNextScheduled();

        // THEN
        assertThat(hasNextBeforeClear).isTrue();
        assertThat(hasNextAfterClear).isFalse();
    }

}
