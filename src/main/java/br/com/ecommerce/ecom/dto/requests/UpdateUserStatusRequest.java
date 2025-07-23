package br.com.ecommerce.ecom.dto.requests;

import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    private boolean active;
}