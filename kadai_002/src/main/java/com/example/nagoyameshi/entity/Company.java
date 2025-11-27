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
@Table(name="companies")
@Data
public class Company {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	// 会社名
	@Column(name="name")
	private String name;
	
	// 郵便番号
	@Column(name="postal_code")
	private String postalCode;
	
	// 所在地
	@Column(name="address")
	private String address;
	
	// 代表者
	@Column(name="representative")
	private String representative;
	
	// 設立年月日
	@Column(name="establishment_date")
	private String establishmentDate;
	
	// 資本金
	@Column(name="capital")
	private String capital;
	
	// 事業内容
	@Column(name="business")
	private String business;
	
	// 従業員
	@Column(name="number_of_employees")
	private String numberOfEmployees;
	
	// 作成日時
	@Column(name="created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	// 更新日時
	@Column(name="updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
}
