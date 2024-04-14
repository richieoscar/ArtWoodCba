package com.richieoscar.artwoodcba.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DefaultApiResponse<T> {
    private String status;
    private String message;

    private T data;
}
