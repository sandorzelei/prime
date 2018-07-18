package com.intland.prime.service.queue;

import org.springframework.lang.NonNull;

import java.util.Optional;

public interface QueueService {

    /**
     * Store a value in the queue
     */
    void schedule(@NonNull Long value);

    /**
     * Return a value from the queue
     *
     * @return The next index that should be processed
     *
     */
    @NonNull
    Optional<Long> getNextIndex();

    /**
     * Return true if queue is not empty, otherwise false
     *
     * @return return true if queue is not empty, otherwise false
     *
     */
    @NonNull
    Boolean hasNextScheduled();

    /**
     * Clear the queue
     *
     */
    void clear();

    /**
     * Return true if value is stored in processing queue, otherwise false
     *
     * @param value
     *            It cannot be null
     *
     * @return return true if value is stored in queue, otherwise false
     *
     */
    @NonNull
    boolean isProcessing(@NonNull Long index);

    /**
     * Return true if value is stored in scheduled queue, otherwise false
     *
     * @param value
     *            It cannot be null
     *
     * @return return true if value is stored in queue, otherwise false
     *
     */
    @NonNull
    boolean isScheduled(@NonNull Long index);

    /**
     * Remove value from the processing queue
     *
     * @param value
     *            It cannot be null
     *
     */
    void processed(Long index);

}
