package com.creditbank.gateway.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class ErrorParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ParsedError parse(String responseBody) {
        ParsedError parsedError = new ParsedError();

        try {
            JsonNode root = objectMapper.readTree(responseBody);

            if (root.has("message")) {
                parsedError.setMessage(root.get("message").asText());
            } else {
                parsedError.setMessage("Unknown error");
            }


            if (root.has("errors")) {
                JsonNode errorsNode = root.get("errors");
                Map<String, String> errorsMap = new HashMap<>();

                Iterator<String> fieldNames = errorsNode.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    String errorText = errorsNode.get(fieldName).asText();
                    errorsMap.put(fieldName, errorText);
                }

                parsedError.setErrors(errorsMap);
            }

        } catch (Exception e) {
            parsedError.setMessage(responseBody);
            parsedError.setErrors(new HashMap<>());
        }

        return parsedError;
    }

    @Setter
    @Getter
    public static class ParsedError {
        private String message;
        private Map<String, String> errors;

        public ParsedError() {
            this.errors = new HashMap<>();
        }
    }
}