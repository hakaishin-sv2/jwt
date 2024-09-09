package com.example.we_hoc_tap_api.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Full name is required")
    @Size(min = 1, max = 100, message = "Full name must be between 1 and 100 characters")
    private String fullname;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private Integer status; // Optional, depending on your use case
}
