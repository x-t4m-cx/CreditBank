package com.creditbank.deal.aspect;

import com.creditbank.deal.exception.DeniedException;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricsAspect {

    private final Counter deniedCounter;

    @AfterThrowing(pointcut = "execution(* com.creditbank.deal.service.CreditService.calculateCredit(..))", throwing = "ex")
    public void handleDeniedException(DeniedException ex) {
        deniedCounter.increment();
    }
}
