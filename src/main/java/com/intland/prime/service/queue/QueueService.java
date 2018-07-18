package com.intland.prime.service.queue;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface QueueService {

    /**
     * Store a value in the queue
     */
    void put(@NonNull Long value);

    /**
     * Return a value from the queue
     *
     * @return Value can be null in case the queue is empty
     *
     */
    @Nullable
    Optional<Long> pop();

    /**
     * Return true if queue is not empty, otherwise false
     *
     * @return return true if queue is not empty, otherwise false
     *
     */
    @NonNull
    Boolean hasNext();

    /**
     * Return true if value is stored in queue, otherwise false
     *
     * @param value
     *            It cannot be null
     *
     * @return return true if value is stored in queue, otherwise false
     *
     */
    @NonNull
    Boolean contains(@NonNull Long value);

    /**
     * Clear the queue
     *
     */
    void clear();

    /**
     * Remove value from the queue
     *
     * @param value
     *            It cannot be null
     *
     */
    void remove(@NonNull Long value);

}
