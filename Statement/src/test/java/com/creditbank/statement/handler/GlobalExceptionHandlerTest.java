package com.creditbank.statement.handler;
import com.creditbank.statement.dto.response.ErrorResponse;
import com.creditbank.statement.exception.StatementNotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    static class DummyController {
        @SuppressWarnings("unused")
        void create(Object body) {
        }
    }

    static class SelfCauseRuntimeException extends RuntimeException {
        @Override
        public synchronized Throwable getCause() {
            return this;
        }
    }

    @Test
    void shouldHandleStatementNotFoundExceptionAndReturn404() {
        ResponseEntity<ErrorResponse> response = handler.handleStatementNotFoundException(
                new StatementNotFoundException("Statement not found - id: 1")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).contains("id: 1");
    }

    @Test
    void shouldHandleGenericExceptionAndReturn500() {
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal server error");
    }

    @Test
    void shouldBuildHelpfulMessageForInvalidFormat() {
        InvalidFormatException ife = InvalidFormatException.from(null, "bad", "abc", Integer.class);
        ife.prependPath(new Object(), "term");

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(
                new HttpMessageNotReadableException("msg", ife)
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Missing or invalid value for field 'term'");
    }

    @Test
    void shouldHandleMismatchedInputWithoutPathAsMissingBody() {
        MismatchedInputException mie = MismatchedInputException.from(null, Object.class, "bad");

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(
                new HttpMessageNotReadableException("msg", mie)
        );

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Request body is missing or malformed");
    }

    @Test
    void shouldUseDefaultMessageForUnknownDeserializationError() {
        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(
                new HttpMessageNotReadableException("msg", new IllegalArgumentException("x"))
        );

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid request format");
    }

    @Test
    void shouldBuildMismatchedInputMessageWithFieldName() {
        MismatchedInputException mie = MismatchedInputException.from(null, Object.class, "bad");
        mie.prependPath(new Object(), "amount");

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(
                new HttpMessageNotReadableException("msg", mie)
        );

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Missing or invalid value for field 'amount'");
    }

    @Test
    void shouldReturnMalformedJsonMessageForJsonParseException() {
        JsonParseException jpe = new JsonParseException(null, "malformed");

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(
                new HttpMessageNotReadableException("msg", jpe)
        );

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Malformed JSON request");
    }

    @Test
    void shouldResolveFieldPathFromArrayIndex() {
        MismatchedInputException mie = MismatchedInputException.from(null, Object.class, "bad");
        mie.prependPath(new Object(), 0);

        String field = invokeGetFieldPath(mie);

        assertThat(field).isEqualTo("[0]");
    }

    @Test
    void shouldBuildValidationErrorsForFieldErrorAndObjectError() throws Exception {
        Method method = DummyController.class.getDeclaredMethod("create", Object.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
        bindingResult.addError(new FieldError("request", "amount", "Amount must be at least 20000"));
        bindingResult.addError(new ObjectError("request", "Invalid request"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).containsKey("amount");
        assertThat(response.getBody().getErrors()).containsKey("request");
    }

    @Test
    void shouldResolveRootCauseForExceptionChain() {
        RuntimeException root = new RuntimeException("root");
        RuntimeException mid = new RuntimeException("mid", root);
        RuntimeException top = new RuntimeException("top", mid);

        Throwable resolved = invokeGetRootCause(top);

        assertThat(resolved).isSameAs(root);
    }

    @Test
    void shouldStopResolvingRootCauseWhenCauseIsSelf() {
        Throwable resolved = invokeGetRootCause(new SelfCauseRuntimeException());
        assertThat(resolved).isInstanceOf(SelfCauseRuntimeException.class);
    }

    private String invokeGetFieldPath(JsonMappingException ex) {
        try {
            Method method = GlobalExceptionHandler.class.getDeclaredMethod("getFieldPath", JsonMappingException.class);
            method.setAccessible(true);
            return (String) method.invoke(handler, ex);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke getFieldPath", e);
        }
    }

    private Throwable invokeGetRootCause(Throwable ex) {
        try {
            Method method = GlobalExceptionHandler.class.getDeclaredMethod("getRootCause", Throwable.class);
            method.setAccessible(true);
            return (Throwable) method.invoke(handler, ex);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke getRootCause", e);
        }
    }
}