package com.example.nagoyameshi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.CategoryRestaurant;
import com.example.nagoyameshi.entity.Restaurant;

public interface CategoryRestaurantRepository extends JpaRepository<CategoryRestaurant, Integer>{
	// 指定した店舗のカテゴリのidを、CategoryRestaurantエンティティのidが小さい順に並べ替えられた状態のリスト形式で取得するメソッド
	@Query("SELECT cr.category.id FROM CategoryRestaurant cr WHERE cr.restaurant = :restaurant ORDER BY cr.id ASC")
   public List<Integer> findCategoryIdsByRestaurantOrderByIdAsc(@Param("restaurant") Restaurant restaurant);
	
	// 指定した店舗とカテゴリが紐づいたCategoryRestaurantエンティティを取得するエンティティ
   public Optional<CategoryRestaurant> findByCategoryAndRestaurant(Category category, Restaurant restaurant);
   
   // 指定した店舗に紐づくCategoryRestaurantエンティティをidが小さい順に並べ替えられた状態のリスト形式で取得するメソッド
   public List<CategoryRestaurant> findByRestaurantOrderByIdAsc(Restaurant restaurant);
	}
