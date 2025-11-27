package com.example.nagoyameshi.entity;

import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	// ロールID
   @ManyToOne
   @JoinColumn(name = "role_id")
   private Role role;

   // 氏名
   @Column(name = "name")
   private String name;

   // フリガナ
   @Column(name = "furigana")
   private String furigana;

   // 郵便番号
   @Column(name = "postal_code")
   private String postalCode;

   // 住所
   @Column(name = "address")
   private String address;

   // 電話番号
   @Column(name = "phone_number")
   private String phoneNumber;

   // 誕生日
   @Column(name = "birthday")
   private LocalDate birthday;

   // 職業
   @Column(name = "occupation")
   private String occupation;

   // メールアドレス
   @Column(name = "email")
   private String email;

   // パスワード
   @Column(name = "password")
   private String password;

   // ユーザーが有効かどうか
   @Column(name = "enabled")
   private Boolean enabled;

   // 決済サービスのお客様ID
   @Column(name = "stripe_customer_id")
   private String stripeCustomerId;

   // 作成日時
   @Column(name = "created_at", insertable = false, updatable = false)
   private Timestamp createdAt;

   // 更新日時
   @Column(name = "updated_at", insertable = false, updatable = false)
   private Timestamp updatedAt;
}
