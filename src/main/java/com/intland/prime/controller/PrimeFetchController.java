package com.intland.prime.controller;

import com.intland.prime.controller.model.PrimeResult;
import com.intland.prime.service.queue.QueueService;
import com.intland.prime.service.store.PrimeNumberStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Optional;

@RestController
public class PrimeFetchController {

    @Autowired
    private PrimeNumberStore primeNumberStore;

    @Autowired
    private QueueService primeQueueService;

    @RequestMapping(path = "/prime/fetch", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PrimeResult> fetch(@RequestParam(value = "index") final Long index) {

        if (this.primeQueueService.isProcessing(index) || this.primeQueueService.isScheduled(index)) {
            return ResponseEntity.status(HttpStatus.FOUND).build();
        }

        final Optional<BigInteger> prime = this.primeNumberStore.getPrime(index);
        if (prime.isPresent()) {
            return ResponseEntity.ok(new PrimeResult(prime.get()));
        }

        return ResponseEntity.notFound().build();
    }

}
