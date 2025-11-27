package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class ResetPasswordForm {
	private String token;
	
	@NotBlank(message="パスワードを入力してください。")
	@Length(min=8, message="パスワードは8文字以上で入力してください。")
	private String password;
	
	@NotBlank(message="パスワード（確認用）を入力してください。")
	private String passwordConfirmation;
}
