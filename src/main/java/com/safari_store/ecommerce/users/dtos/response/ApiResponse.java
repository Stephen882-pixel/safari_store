package com.safari_store.ecommerce.users.dtos.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String message;
    private String status;
    private T data;
    private Object errors;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .status("success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Object errors) {
        return ApiResponse.<T>builder()
                .message(message)
                .status("error")
                .errors(errors)
                .build();
    }

    public static <T> ApiResponse<T> failed(String message, Object errors) {
        return ApiResponse.<T>builder()
                .message(message)
                .status("failed")
                .errors(errors)
                .build();
    }
}
