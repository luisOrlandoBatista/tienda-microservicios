package com.tienda.auth.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CredencialesRequest(
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Size(max = 72) String password) {
}