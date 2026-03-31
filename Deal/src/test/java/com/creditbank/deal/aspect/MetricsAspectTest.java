package com.creditbank.deal.aspect;

import com.creditbank.deal.exception.DeniedException;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MetricsAspectTest {

    @Mock
    private Counter deniedCounter;

    @InjectMocks
    private MetricsAspect metricsAspect;

    @Test
    void shouldIncrementDeniedCounterWhenDeniedExceptionHandled() {
        metricsAspect.handleDeniedException(new DeniedException("Denied"));

        verify(deniedCounter).increment();
    }
}

