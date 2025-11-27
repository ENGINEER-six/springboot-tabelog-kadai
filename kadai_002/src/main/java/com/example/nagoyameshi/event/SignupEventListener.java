package com.example.nagoyameshi.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.VerificationTokenService;

// Listenerクラスは、対応するEventクラスから知らせを受けると、自動的にそのメソッドを実行するクラス
@Component	// ListenerクラスのインスタンスがDIコンテナに登録される
public class SignupEventListener {
	private final VerificationTokenService verificationTokenService;
	private final JavaMailSender javaMailSender;
	
	public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
		this.verificationTokenService = verificationTokenService;
		this.javaMailSender = mailSender;
	}
	
	// Eventクラスからの通知を受け取り、メール認証用のメールを送信する
	@EventListener
	private void onSignEvent(SignupEvent signupEvent) {
		User user = signupEvent.getUser();
		String token = UUID.randomUUID().toString();
		verificationTokenService.createVerificationToken(user, token);
		
		String senderAddress = "springboot.samurainagoyameshi@example.com";				// 送信元のメールアドレス
		String recipientAddress = user.getEmail();																// 送信先のメールアドレス
		String subject = "新規会員登録用のメール認証";															// 件名
		String message = "以下のリンクをクリックして会員登録を完了してください。";			// 本文
		String confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;	// 本文（URL）
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();	
		mailMessage.setFrom(senderAddress);
		mailMessage.setTo(recipientAddress);
		mailMessage.setSubject(subject);
		mailMessage.setText(message + "\n" + confirmationUrl);
		javaMailSender.send(mailMessage);
	}
}
