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
public class PaginatedUsersResponse {
    private long count;
    private String next;
    private String previous;
    private List<UserSummaryResponse> results;
}
