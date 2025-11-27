package com.example.nagoyameshi.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;

@Component
public class ForgotPasswordEventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;
	
	public ForgotPasswordEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher=applicationEventPublisher;
	}
	
	public void publishForgotEvent(User user, String requestUrl) {
		applicationEventPublisher.publishEvent(new ForgotPasswordEvent(this, user, requestUrl));
	}
}
