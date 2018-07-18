package com.intland.prime.service.queue.impl;

import com.intland.prime.service.queue.QueueService;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.util.Assert;

import java.util.Optional;

public class RedisQueueService implements QueueService {

    private final RedisTemplate<String, Long> template;

    private final String cacheName;

    public RedisQueueService(final String cacheName, final RedisTemplate<String, Long> template) {
        super();
        Assert.notNull(cacheName, "value cannot be null");
        this.cacheName = cacheName;
        this.template = template;
    }

    @Override
    public void put(final Long value) {
        Assert.notNull(value, "value cannot be null");
        this.opsForSet().add(this.cacheName, value);
    }

    @Override
    public Optional<Long> pop() {
        return Optional.ofNullable(this.opsForSet().pop(this.cacheName));
    }

    @Override
    public Boolean hasNext() {
        return this.opsForSet().size(this.cacheName) > 0;
    }

    @Override
    public void clear() {
        this.template.delete(this.cacheName);
    }

    @Override
    public Boolean contains(final Long value) {
        Assert.notNull(value, "value cannot be null");
        return this.opsForSet().isMember(this.cacheName, value);
    }

    @Override
    public void remove(final Long value) {
        Assert.notNull(value, "value cannot be null");
        this.opsForSet().remove(this.cacheName, value);
    }

    private SetOperations<String, Long> opsForSet() {
        return this.template.opsForSet();
    }
}
