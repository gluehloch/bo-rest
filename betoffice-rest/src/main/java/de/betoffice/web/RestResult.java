package de.betoffice.web;

import java.util.function.Function;

import org.springframework.http.ResponseEntity;

import de.betoffice.validation.ServiceResult;
import de.betoffice.validation.ValidationMessages;

public class RestResult<T> {
    private final boolean successful;
    private final T result;
    private final ValidationMessages messages;

    public static <T, R> RestResult<T> of(final ServiceResult<R> serviceResult, final Function<R, T> mapper) {
        if (serviceResult.isSuccessful()) {
            final R resultObject = serviceResult.result().get();
            final T mappedResultObject = mapper.apply(resultObject);
            return new RestResult<T>(mappedResultObject, serviceResult.messages(), true);
        } else {
            return new RestResult<T>(null, serviceResult.messages(), false);
        }
    }

    private RestResult(final T result, final ValidationMessages messages, final boolean successful) {
        this.result = result;
        this.messages = messages;
        this.successful = successful;
    }

    public T getResult() {
        return result;
    }

    public ValidationMessages getMessages() {
        return messages;
    }

    public ResponseEntity<RestResult<T>> toResponseEntity() {
        if (successful) {
            return ResponseEntity.ok(this);
        } else {
            return ResponseEntity.badRequest().body(this);
        }
    }
}
