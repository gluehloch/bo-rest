package de.betoffice.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import de.betoffice.validation.ServiceResult;
import de.betoffice.validation.ValidationMessage;
import de.betoffice.validation.ValidationMessage.MessageType;
import de.betoffice.validation.ValidationMessages;

public class ResponseEntityBuilder {

    public static Map<MessageType, List<String>> toMap(ValidationMessages messages) {
        Map<MessageType, List<String>> map = new HashMap<>();
        for (ValidationMessage message : messages.getMessages()) {
            map.computeIfAbsent(message.getMessageType(), _ -> new ArrayList<String>()).add(message.getMessage());
        }
        return map;
    }

    public static Map<String, Object> toProblemDetails(Map<MessageType, List<String>> messages) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<MessageType, List<String>> entry : messages.entrySet()) {
            map.put(entry.getKey().name(), String.join(", ", entry.getValue()));
        }
        return map;
    }

    public static ProblemDetail toProblemDetail(ValidationMessages messages) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("One or more validation errors occurred.");
        pd.setProperties(toProblemDetails(toMap(messages)));
        return pd;
    }

    public static <T> ResponseEntity<T> toResponseEntity(ServiceResult<T> serviceResult) {
        final ValidationMessages messages = serviceResult.messages();
        if (messages != null && messages.containsAnError()) {
            return ResponseEntity.of(ResponseEntityBuilder.toProblemDetail(messages)).build();
        }

        return ResponseEntity.of(serviceResult.result());
    }

}
