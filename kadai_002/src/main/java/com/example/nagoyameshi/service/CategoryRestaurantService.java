package com.example.nagoyameshi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.CategoryRestaurant;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.CategoryRestaurantRepository;

@Service
public class CategoryRestaurantService {
	private final CategoryRestaurantRepository categoryRestaurantRepository;	// 【インスタンス変数】
	private final CategoryService categoryService;											// 【インスタンス変数】
	
	// 【コンストラクタ】
	public CategoryRestaurantService(CategoryRestaurantRepository categoryRestaurantRepository, CategoryService categoryService) {
		this.categoryRestaurantRepository=categoryRestaurantRepository;	// 【インスタンス初期化】
		this.categoryService=categoryService;											// 【インスタンス初期化】
	}
	
	// 指定した店舗のカテゴリのCategoryエンティティのidをCategoryRestaurantエンティティのidが小さい順に並べ替えられた状態のリスト形式で取得する
	public List<Integer> findCategoryIdsByRestaurantOrderByIdAsc(Restaurant restaurant){
		return categoryRestaurantRepository.findCategoryIdsByRestaurantOrderByIdAsc(restaurant);
	}
	
	// フォームから送信されたカテゴリのidリストをもとに、category_restaurantテーブルにデータを登録する
	@Transactional
	public void createCategoriesRestaurants(List<Integer> categoryIds, Restaurant restaurant) {
		for(Integer categoryId : categoryIds) {
			if(categoryId != null) {				
				Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);
				
				if(optionalCategory.isPresent()) {	// isPrsent()メソッドとは、値が存在する場合は「true」、存在しない場合は「false」を返す
					Category category = optionalCategory.get();
					
					Optional<CategoryRestaurant> optionalCurrentCategoryRestaurant = categoryRestaurantRepository.findByCategoryAndRestaurant(category, restaurant);
					
					// 重複するエンティティが存在しない場合は新たにエンティティを作成する
					if(optionalCurrentCategoryRestaurant.isEmpty()) {
						CategoryRestaurant categoryRestaurant = new CategoryRestaurant();
						categoryRestaurant.setRestaurant(restaurant);
						categoryRestaurant.setCategory(category);
						
						categoryRestaurantRepository.save(categoryRestaurant);
					}
				}
			}
		}
	}
	
	// フォームから送信されたカテゴリのidリストをもとに、category_restaurantテーブルのデータを同期する
	@Transactional
	public void syncCategoriesRestaurants(List<Integer> newCategoryIds, Restaurant restaurant) {
		List<CategoryRestaurant> currentCategoriesRestaurants = categoryRestaurantRepository.findByRestaurantOrderByIdAsc(restaurant);
		
		if(newCategoryIds == null) {
			// newCategoryidsがnullの場合はすべてのエンティティを削除する
			for (CategoryRestaurant currentCategoryRestaurant : currentCategoriesRestaurants) {
				categoryRestaurantRepository.delete(currentCategoryRestaurant);
			}} else {
				//既存のエンティティが新しいリストに存在しない場合は削除する
				for(CategoryRestaurant currentCategoryRestaurant : currentCategoriesRestaurants) {
					if(!newCategoryIds.contains(currentCategoryRestaurant.getCategory().getId())) {
						categoryRestaurantRepository.delete(currentCategoryRestaurant);
				}
			}
			
			for(Integer newCategoryId : newCategoryIds) {
				if(newCategoryId != null) {
					Optional<Category> optionalCategory = categoryService.findCategoryById(newCategoryId);
					
					if(optionalCategory.isPresent()) {	// isPrsent()メソッドとは、値が存在する場合は「true」、存在しない場合は「false」を返す
						Category category = optionalCategory.get();
						
						Optional<CategoryRestaurant> optionalCurrentCategoryRestaurant = categoryRestaurantRepository.findByCategoryAndRestaurant(category, restaurant);
						
						// 重複するエンティティが存在しない場合は新たにエンエンティを作成する
						if(optionalCurrentCategoryRestaurant.isEmpty()) {
							CategoryRestaurant categoryRestaurant = new CategoryRestaurant();
							categoryRestaurant.setRestaurant(restaurant);
							categoryRestaurant.setCategory(category);
							
							categoryRestaurantRepository.save(categoryRestaurant);
						}
					}
				}
			}
		}
	}
}
