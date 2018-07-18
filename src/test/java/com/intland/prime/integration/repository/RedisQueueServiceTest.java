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
        final Boolean hasNext = this.service.hasNext();

        // THEN
        assertThat(hasNext).isFalse();
    }

    @Test
    public void testHasNextWhenQueueIsNotEmpty() {
        // GIVEN
        this.service.put(2L);

        // WHEN
        final Boolean hasNext = this.service.hasNext();

        // THEN
        assertThat(hasNext).isTrue();
    }

    @Test
    public void testQueueReturnTheRightValue() {
        // GIVEN
        final long index = 2L;
        this.service.put(index);

        // WHEN
        final Long nextIndex = this.service.pop().get();

        // THEN
        assertThat(nextIndex).isEqualTo(index);
    }

    @Test
    public void testQueueRemovesDuplcations() {
        // GIVEN
        final long index = 2L;
        this.service.put(index);
        this.service.put(index);

        // WHEN
        final Long nextIndex = this.service.pop().get();
        final Boolean hasNext = this.service.hasNext();

        // THEN
        assertThat(nextIndex).isEqualTo(index);
        assertThat(hasNext).isFalse();
    }

    @Test
    public void testQueueReturnTheRightValues() {
        // GIVEN
        LongStream.range(0, 10).forEach(this.service::put);

        // WHEN
        final List<Long> indexes = new ArrayList<>();
        while (this.service.hasNext()) {
            this.service.pop().ifPresent(indexes::add);
        }

        // THEN
        assertThat(indexes).size().isEqualTo(10);
        assertThat(indexes).containsOnly(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
    }

    @Test
    public void testQueueContainsElement() {
        // GIVEN
        LongStream.range(0, 10).forEach(this.service::put);

        // WHEN
        final Boolean isElement = this.service.contains(5L);

        // THEN
        assertThat(isElement).isTrue();
    }

    @Test
    public void testQueueReturnNullIfEmpty() {
        // GIVEN

        // WHEN
        final Long nextIndex = this.service.pop().get();

        // THEN
        assertThat(nextIndex).isNull();
    }

    @Test
    public void testQueueIsCleared() {
        // GIVEN
        LongStream.range(0, 10).forEach(this.service::put);

        // WHEN
        final Boolean hasNextBeforeClear = this.service.hasNext();
        this.service.clear();
        final Boolean hasNextAfterClear = this.service.hasNext();

        // THEN
        assertThat(hasNextBeforeClear).isTrue();
        assertThat(hasNextAfterClear).isFalse();
    }

}
