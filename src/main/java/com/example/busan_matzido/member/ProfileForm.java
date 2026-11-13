package com.example.busan_matzido.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileForm {

    @NotBlank(message = "닉네임을 입력해 주세요.")
    @Size(min = 2, max = 12, message = "닉네임은 2~12자로 입력해 주세요.")
    private String nickname;

    /** 비밀번호를 바꿀 때만 입력 */
    private String currentPassword;

    @Pattern(regexp = "^$|^.{8,50}$", message = "새 비밀번호는 8~50자로 입력해 주세요.")
    private String newPassword;

    private String newPasswordConfirm;
}
