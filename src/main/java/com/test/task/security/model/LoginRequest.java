package com.test.task.security.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotNull
    @Pattern(regexp = "^\\w[a-zA-Z0-9.!#$%&\u2019*+/=?^_`{|}~\"-]{0,34}@((\\[?[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}]?)|(([a-zA-Z0-9][a-zA-Z\\-0-9]*\\.)+[a-zA-Z]+))$")
    private String email;

    @NotNull
    @Size(min = 5, max = 30)
    private String password;

}
