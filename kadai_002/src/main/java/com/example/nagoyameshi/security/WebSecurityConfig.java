package com.example.nagoyameshi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// WebSecurityConfigは以下のようなSpring Securityの設定を行うためのクラス
@Configuration				// そのクラスが設定用のクラスとして機能するようになる。
@EnableWebSecurity		// Spring Securityによるセキュリティ機能を有効にし、認証・認可のルールやログイン・ログアウト処理など各種設定を行えるようになる。
@EnableMethodSecurity	// メソッドレベルでのセキュリティ機能を有効にする。
public class WebSecurityConfig {
   @Bean	// アプリの起動時にそのメソッドの戻り値（インスタンス）がDIコンテナに登録される
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
           .authorizeHttpRequests((requests) -> requests
               .requestMatchers("/css/**", "/images/**", "/js/**", "/storage/**", "/", "/signup/**", "/forgotPassword/**", "/resetPassword/**").permitAll() 																// すべてのユーザーにアクセスを許可するURL
               .requestMatchers("/restaurants/{restaurantId}/reviews/**", "/reservations/**", "/restaurants/{restaurantId}/reservations/**", "/favorites/**", "/restaurants/{restaurantId}/favorites/**", "/user/withdrawal/**").hasAnyRole("FREE_MEMBER", "PAID_MEMBER")	// 無料会員と有料会員にアクセスを許可するURL
               .requestMatchers("/restaurants/**", "/company", "/terms").hasAnyRole("ANONYMOUS", "FREE_MEMBER", "PAID_MEMBER")  					// 未ログインのユーザー、無料会員、有料会員にアクセスを許可するURL
               .requestMatchers("/subscription/register", "/subscription/create").hasRole("FREE_MEMBER")  																	// 無料会員にのみアクセスを許可するURL
               .requestMatchers("/subscription/edit", "/subscription/update", "/subscription/cancel", "/subscription/delete").hasRole("PAID_MEMBER") 	// 有料会員にのみアクセスを許可するURL
               .requestMatchers("/admin/**").hasRole("ADMIN")																																// 管理者にのみアクセスを許可するURL
               .anyRequest().authenticated() 																																							// 上記以外のURLはログインが必要（どのロールでもOK）
           )
           .formLogin((form) -> form
               .loginPage("/login")						 // ログインページのURL
               .loginProcessingUrl("/login")			// ログインフォームの送信先URL
               .defaultSuccessUrl("/?loggedIn")  // ログイン成功時のリダイレクト先URL
               .failureUrl("/login?error")       		// ログイン失敗時のリダイレクト先URL
               .permitAll()									// ログイン・ログアウト関連のURLが誰にでもアクセス可能
           )
           .logout((logout) -> logout
        		   .logoutUrl("/logout")
               .logoutSuccessUrl("/?loggedOut")	// ログアウト時のリダイレクト先URL
               .permitAll()									// ログイン・ログアウト関連のURLが誰にでもアクセス可能
           );

       return http.build();
   }

   @Bean
   public PasswordEncoder passwordEncoder() {	// パスワードのハッシュアルゴリズムを設定する。
       return new BCryptPasswordEncoder();
   }
}
