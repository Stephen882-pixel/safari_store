package com.safari_store.ecommerce.users.Enum;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("User - can browse and purchase products"),
    ADMIN("Administrator - can manage products and orders");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

}
