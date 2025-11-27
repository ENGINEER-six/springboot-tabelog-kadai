package com.example.nagoyameshi.entity;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name="restaurants")
@Data
@ToString(exclude={"categoriesRestaurants", "regularHolidaysRestaurants", "reviews", "reservations", "favorites"})
public class Restaurant {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	// 店舗店
	@Column(name="name")
	private String name;
	
	// 店舗画像
	@Column(name="image")
	private String image;
	
	// 店舗説明
	@Column(name="description")
	private String description;
	
	// 最低価格
	@Column(name="lowest_price")
	private Integer lowestPrice;
	
	// 最高価格
	@Column(name="highest_price")
	private Integer highestPrice;
	
	// 郵便番号
	@Column(name="postal_code")
	private String postalCode;

	// 住所
	@Column(name="address")
	private String address;
	
	// 開店時間
	@Column(name="opening_time")
	private LocalTime openingTime;
	
	// 閉店時間
	@Column(name="closing_time")
	private LocalTime closingTime;
	
	// 座席数
	@Column(name="seating_capacity")
	private Integer seatingCapacity;
	
	// 作成日時
	@Column(name="created_at", insertable=false, updatable=false)
	private Timestamp createdAt;
	
	// 更新日時
	@Column(name="updated_at", insertable=false, updatable=false)
	private Timestamp updatedAt;
	
	@OneToMany(mappedBy="restaurant", fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
	@OrderBy("id ASC")
	private List<CategoryRestaurant> categoriesRestaurants;
	
	@OneToMany(mappedBy="restaurant", fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
	@OrderBy("id ASC")
	private List<RegularHolidayRestaurant> regularHolidaysRestaurants;
	
	@OneToMany(mappedBy="restaurant", fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
	private List<Review> reviews;

	@OneToMany(mappedBy="restaurant", fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
	private List<Reservation> reservations;

	@OneToMany(mappedBy="restaurant", fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
	private List<Favorite> favorites;
	
	// 平均評価を取得する
	@Transient
	public Double getAverageScore() {
		Double averageScore=reviews.stream().mapToInt(Review::getScore).average().orElse(0.0);
		
		return averageScore;
	}
}
