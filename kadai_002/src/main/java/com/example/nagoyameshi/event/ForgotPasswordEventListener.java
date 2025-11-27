package com.example.nagoyameshi.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.VerificationTokenService;

@Component
public class ForgotPasswordEventListener {
	private final VerificationTokenService verificationTokenService;
	private final JavaMailSender javaMailSender;
	
	public ForgotPasswordEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
		this.verificationTokenService=verificationTokenService;
		this.javaMailSender=mailSender;
	}
	
	@EventListener
	public void onForgotPasswordEvent(ForgotPasswordEvent forgotpasswordEvent) {
		User user=forgotpasswordEvent.getUser();
		String token=UUID.randomUUID().toString();
		verificationTokenService.createVerificationToken(user, token);
		
		String senderAddress="springboot.samuraitravel@example.com";
		String recipientAddress=user.getEmail();
		String subject="パスワード再設定";
		String message="以下のリンクをクリックしてパスワードを再設定してください。";
		String confirmationUrl=forgotpasswordEvent.getRequestUrl().replace("/forgotPassword", "") + "/resetPassword?token=" + token;
		
		SimpleMailMessage mailMessage=new SimpleMailMessage();
		mailMessage.setFrom(senderAddress);
		mailMessage.setTo(recipientAddress);
		mailMessage.setSubject(subject);
		mailMessage.setText(message + "\n" + confirmationUrl);
		javaMailSender.send(mailMessage);
	}
}
