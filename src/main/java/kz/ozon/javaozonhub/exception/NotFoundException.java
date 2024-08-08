package kz.ozon.javaozonhub.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotFoundException extends RuntimeException {
    private final String message;
}
