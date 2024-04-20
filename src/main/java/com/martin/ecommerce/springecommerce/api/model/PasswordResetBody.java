package com.martin.ecommerce.springecommerce.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordResetBody {

    @NotBlank
    @NotNull
    private String token;

    @NotNull
    @NotBlank
//    @Pattern(regexp = )
    @Size(min = 5, max = 32)
    private String password;



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
