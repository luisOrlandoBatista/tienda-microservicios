package com.tienda.auth.application.port.out;

import java.time.Instant;

public record TokenGenerado(String token, Instant expiraEn) {
}