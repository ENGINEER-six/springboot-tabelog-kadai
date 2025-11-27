package com.example.nagoyameshi.entity;

import java.sql.Timestamp;

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
@Table(name="reviews")
@Data
public class Review {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	// レビュー内容
	@Column(name="content")
	private String content;
	
	// スコア（星の数）
	@Column(name="score")
	private Integer score;
	
	// 店舗のID
	@ManyToOne
	@JoinColumn(name="restaurant_id")
	private Restaurant restaurant;
	
	// ユーザーのID
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

   // 作成日時
   @Column(name = "created_at", insertable = false, updatable = false)
   private Timestamp createdAt;

   // 更新日時
   @Column(name = "updated_at", insertable = false, updatable = false)
   private Timestamp updatedAt;
}
