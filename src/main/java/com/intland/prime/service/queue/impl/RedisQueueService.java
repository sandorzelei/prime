package com.intland.prime.service.queue.impl;

import com.intland.prime.service.queue.QueueService;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.util.Assert;

import java.util.Optional;

public class RedisQueueService implements QueueService {

    private final RedisTemplate<String, Long> template;

    private final String scheduledCacheName;

    private final String processingCacheName;

    public RedisQueueService(final String cacheNamePrefix, final RedisTemplate<String, Long> template) {
        super();
        Assert.notNull(cacheNamePrefix, "value cannot be null");
        this.scheduledCacheName = cacheNamePrefix + "-scheduled";
        this.processingCacheName = cacheNamePrefix + "-processing";
        this.template = template;
    }

    @Override
    public void schedule(final Long value) {
        Assert.notNull(value, "value cannot be null");
        this.opsForSet().add(this.scheduledCacheName, value);
    }

    @Override
    public Optional<Long> getNextIndex() {

        final Long scheduledIndex = this.opsForSet().pop(this.scheduledCacheName);
        if (scheduledIndex == null) {
            return Optional.empty();
        }

        final boolean isAdded = this.addToProcessing(scheduledIndex);
        if (isAdded) {
            return Optional.ofNullable(scheduledIndex);
        }

        return Optional.empty();
    }

    @Override
    public Boolean hasNextScheduled() {
        return this.opsForSet().size(this.scheduledCacheName) > 0;
    }

    @Override
    public void clear() {
        this.template.delete(this.scheduledCacheName);
        this.template.delete(this.processingCacheName);
    }

    @Override
    public boolean isProcessing(final Long index) {
        Assert.notNull(index, "value cannot be null");
        return this.opsForSet().isMember(this.processingCacheName, index);
    }

    @Override
    public boolean isScheduled(final Long index) {
        Assert.notNull(index, "value cannot be null");
        return this.opsForSet().isMember(this.scheduledCacheName, index);
    }

    @Override
    public void processed(final Long index) {
        this.opsForSet().remove(this.processingCacheName, index);
    }

    private boolean addToProcessing(final Long scheduledIndex) {
        return this.opsForSet().add(this.processingCacheName, scheduledIndex) > 0;
    }

    private SetOperations<String, Long> opsForSet() {
        return this.template.opsForSet();
    }

}
