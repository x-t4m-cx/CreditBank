package com.creditbank.statement.aspect;

import org.junit.jupiter.api.Test;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingAspectTest {

    @Test
    void shouldFormatArgsAsNoArgsWhenEmpty() {
        LoggingAspect aspect = new LoggingAspect();
        Object result = ReflectionTestUtils.invokeMethod(aspect, "formatArgs", (Object) new Object[0]);
        assertThat(result).isEqualTo("no args");
    }

    @Test
    void shouldFormatArgsWithValues() {
        LoggingAspect aspect = new LoggingAspect();
        Object result = ReflectionTestUtils.invokeMethod(aspect, "formatArgs", (Object) new Object[]{"a", 1});
        assertThat((String) result).contains("a").contains("1");
    }

    @Test
    void shouldFormatResultNullAsLiteralNull() {
        LoggingAspect aspect = new LoggingAspect();
        Object result = ReflectionTestUtils.invokeMethod(aspect, "formatResult", new Object[]{null});
        assertThat(result).isEqualTo("null");
    }

    @Test
    void shouldTruncateLongResultString() {
        LoggingAspect aspect = new LoggingAspect();
        String longResult = "x".repeat(600);
        Object result = ReflectionTestUtils.invokeMethod(aspect, "formatResult", longResult);
        assertThat(result).isInstanceOf(String.class);
        assertThat((String) result).endsWith("...");
        assertThat(((String) result).length()).isEqualTo(503);
    }

    @Test
    void shouldReturnResultAsIsForShortString() {
        LoggingAspect aspect = new LoggingAspect();
        String value = "ok";
        Object result = ReflectionTestUtils.invokeMethod(aspect, "formatResult", value);
        assertThat(result).isEqualTo("ok");
    }

    @Test
    void shouldLogAroundVoidMethod() throws Throwable {
        LoggingAspect aspect = new LoggingAspect();
        ProceedingJoinPoint pjp = Mockito.mock(ProceedingJoinPoint.class);
        MethodSignature signature = Mockito.mock(MethodSignature.class);

        Mockito.when(pjp.getSignature()).thenReturn(signature);
        Mockito.when(signature.getReturnType()).thenReturn(void.class);
        Mockito.when(pjp.getArgs()).thenReturn(new Object[0]);

        Object result = aspect.logAround(pjp);

        assertThat(result).isNull();
    }

    @Test
    void shouldLogAroundNonVoidMethodAndReturnResult() throws Throwable {
        LoggingAspect aspect = new LoggingAspect();
        ProceedingJoinPoint pjp = Mockito.mock(ProceedingJoinPoint.class);
        MethodSignature signature = Mockito.mock(MethodSignature.class);

        Mockito.when(pjp.getSignature()).thenReturn(signature);
        Mockito.when(signature.getReturnType()).thenReturn(String.class);
        Mockito.when(pjp.getArgs()).thenReturn(new Object[]{"arg"});
        Mockito.when(pjp.proceed()).thenReturn("result");

        Object result = aspect.logAround(pjp);

        assertThat(result).isEqualTo("result");
    }
}

