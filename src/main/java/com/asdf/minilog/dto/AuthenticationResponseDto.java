package com.asdf.minilog.dto;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponseDto {
    @Nonnull private String jwt;
}
