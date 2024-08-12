package kz.ozon.javaozonhub.dto;

import lombok.Data;

@Data
public class RefreshJwtRequest {
    private String refreshToken;
}
