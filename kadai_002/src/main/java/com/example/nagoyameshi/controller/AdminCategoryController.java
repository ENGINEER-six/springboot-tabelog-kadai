package com.example.nagoyameshi.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {
	private final CategoryService categoryService;	// 【フィールド_再代入不可】カテゴリ_サービス
	
	public AdminCategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;	// 【インスタンス】カテゴリ_サービスをフィールドにセットする
	}
	
	// 【管理者側_カテゴリ一覧ページ】
	@GetMapping
	public String index(@RequestParam(name="keyword", required=false)String keyword, @PageableDefault(page=0, size=15, sort="id", direction=Direction.ASC)Pageable pageable, Model model) {
							// 【@RequestParam】リクエストパラメータから検索キーワードを取得する
							// 【@PageableDefaullt】ページング情報を自動生成する
							// 【Model】ビューにデータを渡すためのビュー
		Page<Category> categoryPage;	// 【ローカル変数】カテゴリページング
		
		// 【条件分岐】検索キーワードは入力されているか判定
		if(keyword != null && !keyword.isEmpty()) {
			categoryPage=categoryService.findCategoriesByNameLike(keyword, pageable);	// 【true】キーワードで絞り込んだカテゴリデータを取得する
		} else {
			categoryPage=categoryService.findAllCategories(pageable);	// 【false】すべてのカテゴリをページングされた状態で取得する
		}
		
		model.addAttribute("categoryPage", categoryPage);	// モデルにページングデータを追加する
		model.addAttribute("keyword", keyword);					// モデルに検索キーワードを保持させる
		model.addAttribute("categoryRegisterForm", new CategoryRegisterForm());
		model.addAttribute("categoryEditForm", new CategoryEditForm());
		
		
		return "admin/categories/index";	// カテゴリ一覧画面のビュテンプレートを返す
	}
	
	// 【管理者側_カテゴリ登録ページ】
	@PostMapping("/create")
	public String create(@ModelAttribute @Validated CategoryRegisterForm categoryRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
							// 【@ModelAttribute】【@Validated】フォームデータをバインドし、バリデーションを実行する
							// 【BindingResult】バリデーション結果を格納するオブジェクト
							// 【RedirectAttributes】リダイレクト先に属性を渡すためのオブジェクト
							// 【Model】ビューにデータを渡すためのモデル
		// 【条件分岐】バリエーションエラーがある場合
		if(bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errorMessage", "カテゴリ名を入力してください。");	// エラーメッセージをリダイレクト先に渡す
			
			return "redirect:/admin/categories";		// フォーム入力画面に戻る
		}
		categoryService.createCategory(categoryRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "カテゴリを登録しました。");	// 成功メッセージをリダイレクト先に渡す
		
		return "redirect:/admin/categories";
	}
	
	// 【管理者側_カテゴリ編集ページ】
	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated CategoryEditForm categoryEditForm, BindingResult bindingResult, @PathVariable(name="id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		Optional<Category> optionalCategory = categoryService.findCategoryById(id);
		
		// 【条件分岐】取得したカテゴリデータが存在しない
		if(optionalCategory.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "カテゴリが存在しません。");
			
			return "redirect:/admin/categories";	// カテゴリ一覧画面のビューテンプレートを返す
		}
		
		// 【条件分岐】バリエーションエラーがある場合
		if(bindingResult.hasErrors()){
			redirectAttributes.addFlashAttribute("errorMessage", "カテゴリ名を入力してください。");
			
			return "redirect:/admin/categories";	// カテゴリ一覧画面のビューテンプレートを返す
		}
		
		// 【処理実行】
		Category category = optionalCategory.get();
		categoryService.updateCategory(categoryEditForm, category);
		redirectAttributes.addFlashAttribute("successMessage", "カテゴリを編集しました。");
		
		return "redirect:/admin/categories";
	}	
	
	// 【管理者側_カテゴリ削除ページ】
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name="id") Integer id, RedirectAttributes redirectAttributes) {
		Optional<Category> optionalCategory = categoryService.findCategoryById(id);
		
		// 【条件分岐】取得したカテゴリデータが存在しない
		if(optionalCategory.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "カテゴリが存在しません。");
			
			return "redirect:/admin/categories";	// カテゴリ一覧画面のビューテンプレートを返す
		}
		
		Category category = optionalCategory.get();
		categoryService.deleteCategory(category);
		redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました。");
		
		return "redirect:/admin/categories";
	}
}
