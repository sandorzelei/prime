package com.intland.prime.controller;

import com.intland.prime.controller.model.PrimeForm;
import com.intland.prime.controller.model.PrimeResultForm;
import com.intland.prime.service.queue.QueueService;
import com.intland.prime.service.store.PrimeNumberStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigInteger;
import java.util.Optional;

import javax.validation.Valid;

@Controller
public class PrimeFormController {

    private static final Logger logger = LoggerFactory.getLogger(PrimeFormController.class);

    @Autowired
    private QueueService primeQueueService;

    @Autowired
    private PrimeNumberStore primeNumberStore;

    @GetMapping("/")
    public String showForm(final PrimeForm personForm) {
        return "form";
    }

    @GetMapping("/prime/{index}")
    public String showForm(@PathVariable final Long index, final PrimeResultForm form) {

        if (index <= 0) {
            return "error/zeroOrNegativeIndex";
        }

        form.setIndex(index);

        final Optional<BigInteger> primeNumber = this.primeNumberStore.getPrime(index);
        if (primeNumber.isPresent()) {
            logger.debug("Prime is found in the store");
            form.setPrimeNumber(primeNumber.get());
            return "result";
        }

        if (this.primeQueueService.isProcessing(index)) {
            logger.debug("Prime is in the processing queue");
            return "resultFetching";
        }

        if (this.primeQueueService.isScheduled(index)) {
            logger.debug("Prime is in the scheduled queue");
            return "resultFetching";
        }

        return "error/404";
    }

    @PostMapping("/find")
    public String startComputing(@Valid final PrimeForm personForm, final BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "form";
        }

        this.primeQueueService.schedule(personForm.getNumber());

        return String.format("redirect:/prime/%s", personForm.getNumber());
    }

}
