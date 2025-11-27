package com.example.nagoyameshi.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name="terms")
@Data
public class Term {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	// 利用規約の本文
	@Column(name="content")
	private String content;
	
	// 作成日時
	@Column(name="created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	// 更新日時
	@Column(name="updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
}
