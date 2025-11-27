package com.example.nagoyameshi.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;

// Publisherクラスは、イベントを発行するクラス
@Component // ListenerクラスのインスタンスがDIコンテナに登録される
public class SignupEventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;
	
	public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	// publishSignupEventメソッドでイベントを発行する
	public void publishSignupEvent(User user, String requestUrl) {
		applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
	}
}
