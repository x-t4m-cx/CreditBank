package com.creditbank.calculator.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(public * com.creditbank.calculator.service..*.*(..))")
    public void allServicePublicMethods() {}

    @Around("allServicePublicMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info(">> {}() - {}", methodName, Arrays.toString(args));

        Object result = joinPoint.proceed();

        log.info("<< {}() - {}", methodName, result);

        return result;
    }
}
