package com.intland.prime.controller;

import com.intland.prime.controller.model.PrimeForm;
import com.intland.prime.controller.model.PrimeResultForm;
import com.intland.prime.service.queue.QueueService;
import com.intland.prime.service.store.PrimeNumberStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("scheduled")
    private QueueService scheduledPrimeQueueService;

    @Autowired
    @Qualifier("processing")
    private QueueService processingPrimeQueueService;

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
            form.setPrimeNumber(primeNumber.get());
            return "result";
        }

        if (this.scheduledPrimeQueueService.contains(index) || this.processingPrimeQueueService.contains(index)) {
            return "resultFetching";
        }

        return "error/404";
    }

    @PostMapping("/find")
    public String startComputing(@Valid final PrimeForm personForm, final BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "form";
        }

        this.scheduledPrimeQueueService.put(personForm.getNumber());

        return String.format("redirect:/prime/%s", personForm.getNumber());
    }

}
