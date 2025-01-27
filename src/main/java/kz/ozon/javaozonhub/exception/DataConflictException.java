package kz.ozon.javaozonhub.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DataConflictException extends RuntimeException {
    private final String message;
}
