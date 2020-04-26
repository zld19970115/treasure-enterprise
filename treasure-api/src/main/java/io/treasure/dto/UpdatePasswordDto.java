package io.treasure.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordDto {

    private String oldPassword;

    private String newPassword;

    private String confirmPassword;

    private Long id;

}
