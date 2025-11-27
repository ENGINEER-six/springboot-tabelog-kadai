package com.example.nagoyameshi.event;

import org.springframework.context.ApplicationEvent;

import com.example.nagoyameshi.entity.User;

import lombok.Getter;

// Eventクラスは、Listenerクラスにイベントが発生したことを知らせるクラス
@Getter
public class SignupEvent extends ApplicationEvent{
	private User user;				// 【フィールド】ユーザー情報を保持する
	private String requestUrl;	// 【フィールド】リクエストを受けたURLを保持する
	
	public SignupEvent(Object source, User user, String requestUrl) {
		super(source);
		
		// 【コンストラクタ】初期化する
		this.user=user;
		this.requestUrl=requestUrl;
	}
}
