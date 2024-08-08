package kz.ozon.javaozonhub.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoleEnum {
    ADMIN("ADMIN"),
    USER("USER");

    private final String vale;
}
