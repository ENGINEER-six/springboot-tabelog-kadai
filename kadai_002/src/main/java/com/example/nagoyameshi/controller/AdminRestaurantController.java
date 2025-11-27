package com.example.nagoyameshi.controller;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.RegularHoliday;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.service.CategoryRestaurantService;
import com.example.nagoyameshi.service.CategoryService;
import com.example.nagoyameshi.service.RegularHolidayRestaurantService;
import com.example.nagoyameshi.service.RegularHolidayService;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/admin/restaurants")
public class AdminRestaurantController {
	private final RestaurantService restaurantService;												// 【フィールド_再代入不可】店舗_サービス
	private final CategoryService categoryService;													// 【フィールド_再代入不可】カテゴリ_サービス
private final CategoryRestaurantService categoryRestaurantService;	 					//【フィールド_再代入不可】店舗ｰカテゴリ（中間テーブル）_サービス
	private final RegularHolidayService regularHolidayService;									//【フィールド_再代入不可】定休日_サービス
	private final RegularHolidayRestaurantService regularHolidayRestaurantService;	//【フィールド_再代入不可】店舗ｰ定休日（中間テーブル）_サービス
	
	public AdminRestaurantController(RestaurantService restaurantService, CategoryService categoryService, CategoryRestaurantService categoryRestaurantService, RegularHolidayService regularHolidayService, RegularHolidayRestaurantService regularHolidayRestaurantService) {
		this.restaurantService = restaurantService;	// 【インスタンス】店舗_サービスをフィールドにセットする
		this.categoryService = categoryService;			// 【インスタンス】カテゴリ_サービスをフィールドにセットする
		this.categoryRestaurantService = categoryRestaurantService;	// 【インスタンス】店舗ｰカテゴリ（中間テーブル）_サービスをフィールドにセットする
		this.regularHolidayService = regularHolidayService;									// 【インスタンス】定休日_サービスをフィールドにセットする
		this.regularHolidayRestaurantService = regularHolidayRestaurantService;	// 【インスタンス】店舗ｰ定休日（中間テーブル）をフィールドにセットする
	}
	
	// 【管理者側_店舗一覧ページ】
	@GetMapping
	public String index(@RequestParam(name="keyword", required=false)String keyword, @PageableDefault(page=0, size=15, sort="id", direction=Direction.ASC)Pageable pageable, Model model) {
								// 【@RequestParam】リクエストパラメータから検索キーワードを取得する
								// 【@PageableDefaullt】ページング情報を自動生成する
								// 【Model】ビューにデータを渡すためのビュー
		Page<Restaurant> restaurantPage;	// 【ローカル変数】店舗ページング
		
		// 【条件分岐】検索キーワードは入力されているか判定
		if(keyword != null && !keyword.isEmpty()) {
			restaurantPage=restaurantService.findRestaurantsByNameLike(keyword, pageable);	// 【true】キーワードで絞り込んだ店舗データを取得
		} else {
			restaurantPage=restaurantService.findAllRestaurants(pageable);									// 【flase】すべての店舗をページングされた状態で取得する
		}
		
		model.addAttribute("restaurantPage", restaurantPage);	// モデルにページングデータを追加
		model.addAttribute("keyword", keyword);						// モデルに検索キーワードを保持させる
		
		return "admin/restaurants/index";	// 店舗一覧画面のビューテンプレートを返す
	}
	
	// 【管理者側_店舗登録ページ】
	@GetMapping("/register")
	public String register(Model model) {
		List<Category> categories = categoryService.findAllCategories();
		List<RegularHoliday> regularHolidays = regularHolidayService.findAllRegularHolidays();
		model.addAttribute("restaurantRegisterForm", new RestaurantRegisterForm());
		model.addAttribute("categories", categories);
		model.addAttribute("regularHolidays", regularHolidays);
		
		return "admin/restaurants/register";
	}
	
	// 【管理者側_店舗作成ページ】
	@PostMapping("/create")
	public String create(@ModelAttribute @Validated RestaurantRegisterForm restaurantRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
								// 【@ModelAttribute】【@Validated】フォームデータをバインドし、バリデーションを実行する
								// 【BindingResult】バリデーション結果を格納するオブジェクト
								// 【RedirectAttributes】リダイレクト先に属性を渡すためのオブジェクト
								// 【Model】ビューにデータを渡すためのモデル
		Integer lowestPrice = restaurantRegisterForm.getLowestPrice();			// 【変数】最低価格
		Integer highestPrice = restaurantRegisterForm.getHighestPrice();		// 【変数】最高価格
		LocalTime openingTime = restaurantRegisterForm.getOpeningTime();	// 【変数】開店時間
		LocalTime closingTime = restaurantRegisterForm.getClosingTime();	// 【変数】閉店時間
		
		// 【条件分岐】価格設定が有効でない場合
		if(lowestPrice != null && highestPrice != null && !restaurantService.isValidPrices(lowestPrice, highestPrice)) {
			FieldError lowestPriceError = new FieldError(bindingResult.getObjectName(), "lowestPrice", "最低価格は最高価格以下に設定してください。");		// 【インスタンス】最低価格のエラーメッセージ
			FieldError highestPriceError = new FieldError(bindingResult.getObjectName(), "highestPrice", "最高価格は最低価格以上に設定してください。");	// 【インスタンス】最高価格のエラーメッセージ
			
			bindingResult.addError(lowestPriceError);	// 最低価格のバリデーション結果にエラーを追加
			bindingResult.addError(highestPriceError);	// 最高価格のバリデーション結果にエラーを追加
		}
		
		// 【条件分岐】開閉時間が有効でない場合
		if(openingTime != null && closingTime != null && !restaurantService.isValidBusinessHours(openingTime, closingTime)) {
			FieldError openingTimeError = new FieldError(bindingResult.getObjectName(), "openingTime", "開店時間は閉店時間よりも前に設定してください。");	// 【インスタンス】開店時間のエラーメッセージ
			FieldError closingTimeError = new FieldError(bindingResult.getObjectName(), "closingTime", "閉店時間は開店時間よりも後に設定してください。");		// 【インスタンス】閉店時間のエラーメッセージ
			
			bindingResult.addError(openingTimeError);	// 開店時間のバリデーション結果にエラーを追加
			bindingResult.addError(closingTimeError);	// 閉店時間のバリデーション結果にエラーを追加
		}
		
		// 【条件分岐】バリエーションエラーがある場合
		if(bindingResult.hasErrors()) {
			List<Category> categories = categoryService.findAllCategories();
			List<RegularHoliday> regularHolidays = regularHolidayService.findAllRegularHolidays();
			model.addAttribute("restaurantRegisterForm", restaurantRegisterForm);	// エラー情報を保持したフォームオブジェクトをモデルに追加し直す
			model.addAttribute("categories", categories);
			model.addAttribute("regularHolidays", regularHolidays);
				
			return "admin/restaurants/register";	// フォーム入力画面に戻る
		}
		
		// 【処理実行】エラーがなければ店舗情報をデータベースに保存
		restaurantService.createRestaurant(restaurantRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました。");	// 成功メッセージをリダイレクト先に渡す
		
		return "redirect:/admin/restaurants";	// 店舗一覧ページにリダイレクト
	}

	// 【管理者側_店舗詳細ページ】
	@GetMapping("/{id}")
	public String show(@PathVariable(name="id") Integer id, RedirectAttributes redirectAttributes, Model model) {
								// 【@PathVariable】URLのパス部分({id})に渡された値を変数として取り出して利用する機能
								// 【RedirectAttributes】リダイレクト先にメッセージなどの属性を一時的に引き継ぐ機能（フラッシュ属性）
								// 【Model】ビューにデータを渡すためのビュー
		Optional<Restaurant> optionalRestaurant=restaurantService.findRestaurantById(id);	// 【ローカル変数】Optional型（任意）の店舗データ
		
		// 【条件分岐】取得した店舗データが存在しない
		if(optionalRestaurant.isEmpty()) {
		redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
		
		return "redirect:/admin/restaurants";	// 店舗詳細画面のビューテンプレートを返す
		}
		
		Restaurant restaurant = optionalRestaurant.get();	// 【ローカル変数】取得した店舗データ本体
		
		model.addAttribute("restaurant", restaurant);			// モデルに店舗データを追加
		
		return "admin/restaurants/show";	// 店舗詳細画面のビューテンプレートを返す
	}

	// 【管理者側_店舗編集ページ】
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name="id")Integer id, RedirectAttributes redirectAttributes, Model model) {
							// 【@PathVariable】URLのパス部分({id})に渡された値を変数として取り出して利用する機能
							// 【RedirectAttributes】リダイレクト先にメッセージなどの属性を一時的に引き継ぐ機能（フラッシュ属性）
							// 【Model】ビューにデータを渡すためのビュー
		Optional<Restaurant>optionalRestaurant = restaurantService.findRestaurantById(id);	// 【ローカル変数】Optional型（任意）の店舗データ
		
		// 【条件分岐】取得した店舗データが存在しない
		if(optionalRestaurant.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
			
			return "redirect:/admin/restaurants";	// 店舗一覧画面のビューテンプレートを返す
		}
		
		Restaurant restaurant = optionalRestaurant.get(); // 【ローカル変数】取得した店舗データ本体
		List<Integer> categoryIds = categoryRestaurantService.findCategoryIdsByRestaurantOrderByIdAsc(restaurant);
		List<Integer> regularHolidayIds = regularHolidayRestaurantService.findRegularHolidayIdsByRestaurant(restaurant);
		RestaurantEditForm restaurantEditForm = new RestaurantEditForm(restaurant.getName(), null, restaurant.getDescription(), restaurant.getLowestPrice(), restaurant.getHighestPrice(), restaurant.getPostalCode(), restaurant.getAddress(), restaurant.getOpeningTime(), restaurant.getClosingTime(), restaurant.getSeatingCapacity(), categoryIds, regularHolidayIds); // 【インスタンス】店舗エンエンティのデータを使って、編集フォームのインスタンスを生成する
		List<Category> categories = categoryService.findAllCategories();
		List<RegularHoliday> regularHolidays = regularHolidayService.findAllRegularHolidays();
		model.addAttribute("restaurant", restaurant);								// モデルに店舗データを追加
		model.addAttribute("restaurantEditForm", restaurantEditForm);	// モデルに店舗編集フォームデータを追加
		model.addAttribute("categories", categories);
		model.addAttribute("regularHolidays", regularHolidays);
		
		return "admin/restaurants/edit"; // 店舗編集画面のビューテンプレートを返す
	}
	
	// 【管理者側_店舗更新ページ】
	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated RestaurantEditForm restaurantEditForm, BindingResult bindingResult, @PathVariable(name="id") Integer id, RedirectAttributes redirectAttributes, Model model) {
								// 【@ModelAttribute】【@Validated】フォームデータをバインドし、バリデーションを実行する
								// 【BindingResult】バリデーション結果を格納するオブジェクト
								// 【RedirectAttributes】リダイレクト先に属性を渡すためのオブジェクト
								// 【Model】ビューにデータを渡すためのモデル
		Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(id); // 【ローカル変数】Optional型（任意）の店舗データ
		
		// 【条件分岐】取得した店舗データが存在しない
		if(optionalRestaurant.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
			
			return "redirect:/admin/restaurants";	// 店舗一覧画面のビューテンプレートを返す
		}
		
		Restaurant restaurant = optionalRestaurant.get();
		Integer lowestPrice = restaurantEditForm.getLowestPrice();			// 【変数】最低価格
		Integer highestPrice = restaurantEditForm.getHighestPrice();			// 【変数】最高価格
		LocalTime openingTime = restaurantEditForm.getOpeningTime();	// 【変数】開店時間
		LocalTime closingTime = restaurantEditForm.getClosingTime();		// 【変数】閉店時間
		
		// 【条件分岐】価格設定が有効でない場合
		if(lowestPrice != null && highestPrice != null && !restaurantService.isValidPrices(lowestPrice, highestPrice)) {
			FieldError lowestPriceError = new FieldError(bindingResult.getObjectName(), "lowestPrice", "最低価格は最高価格以下に設定してください。");		// 【インスタンス】最低価格のエラーメッセージ
			FieldError highestPriceError = new FieldError(bindingResult.getObjectName(), "highestPrice", "最高価格は最低価格以上に設定してください。");	// 【インスタンス】最高価格のエラーメッセージ
			
			bindingResult.addError(lowestPriceError);	// 最低価格のバリデーション結果にエラーを追加
			bindingResult.addError(highestPriceError);	// 最高価格のバリデーション結果にエラーを追加
		}
		
		// 【条件分岐】開閉時間が有効でない場合
		if(openingTime != null && closingTime != null && !restaurantService.isValidBusinessHours(openingTime, closingTime)) {
			FieldError openingTimeError = new FieldError(bindingResult.getObjectName(), "openingTime", "開店時間は閉店時間よりも前に設定してください。");	// 【インスタンス】開店時間のエラーメッセージ
			FieldError closingTimeError = new FieldError(bindingResult.getObjectName(), "closingTime", "閉店時間は開店時間よりも後に設定してください。");		// 【インスタンス】閉店時間のエラーメッセージ
			
			bindingResult.addError(openingTimeError);	// 開店時間のバリデーション結果にエラーを追加
			bindingResult.addError(closingTimeError);	// 閉店時間のバリデーション結果にエラーを追加
		}
		
		// 【条件分岐】バリエーションエラーがある場合
		if(bindingResult.hasErrors()) {
		    List<Category> categories = categoryService.findAllCategories();
		    List<RegularHoliday> regularHolidays = regularHolidayService.findAllRegularHolidays();
			model.addAttribute("restaurant", restaurant);
			model.addAttribute("restaurantEditForm", restaurantEditForm);	// エラー情報を保持したフォームオブジェクトをモデルに追加し直す
			model.addAttribute("categories", categories);
			model.addAttribute("regularHolidays", regularHolidays);
				
			return "admin/restaurants/edit";	// フォーム入力画面に戻る
		}
		
		// 【処理実行】エラーがなければ店舗情報をデータベースに保存
		restaurantService.updateRestaurant(restaurantEditForm, restaurant);
		redirectAttributes.addFlashAttribute("successMessage", "店舗を編集しました。");	// 成功メッセージをリダイレクト先に渡す
		
		return "redirect:/admin/restaurants";	// 店舗一覧ページにリダイレクト
	}
	
	// 【管理者側_店舗削除ページ】
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name="id") Integer id, RedirectAttributes redirectAttributes) {
							// 【@PathVariable】URLのパス部分({id})に渡された値を変数として取り出して利用する機能
							// 【RedirectAttributes】リダイレクト先にメッセージなどの属性を一時的に引き継ぐ機能（フラッシュ属性）
							// 【Model】ビューにデータを渡すためのビュー

		Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(id); // 【ローカル変数】Optional型（任意）の店舗データ
		
		// 【条件分岐】取得した店舗データが存在しない
		if(optionalRestaurant.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
			
			return "redirect:/admin/restaurants";	// 店舗一覧画面のビューテンプレートを返す
		}
		
		Restaurant restaurant = optionalRestaurant.get();
		restaurantService.deleteRestaurant(restaurant);
		redirectAttributes.addFlashAttribute("successMessage", "店舗を削除しました。");
		
		return "redirect:/admin/restaurants";
	}
}
