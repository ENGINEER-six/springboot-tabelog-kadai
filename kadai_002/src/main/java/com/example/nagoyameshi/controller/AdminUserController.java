package com.example.nagoyameshi.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
	private final UserService userService;
	
	public AdminUserController(UserService userService) {
		this.userService = userService;
	}
	
	// 管理者用の会員一覧ページを表示する
	@GetMapping
	public String index(@RequestParam(name="keyword", required=false)String keyword, @PageableDefault(page = 0, size = 15, sort = "id", direction = Direction.ASC)Pageable pageable, Model model) {
								// 【@RequestParam】リクエストパラメータから検索キーワードを取得する
								// 【@PageableDefaullt】ページング情報を自動生成する
								// 【Model】ビューにデータを渡すためのビュー
		Page<User> userPage; // 【ローカル変数】会員ページング
		
		// 【条件分岐】検索キーワードは入力されているか判定
		if(keyword != null && !keyword.isEmpty()) {
			userPage = userService.findUsersByNameLikeOrFuriganaLike(keyword, keyword, pageable);	// 【true】キーワードで絞り込んだ会員データを取得
		} else {
			userPage = userService.findAllUsers(pageable);																	// 【flase】すべての会員をページングされた状態で取得する
		}
		
		model.addAttribute("userPage", userPage);	// モデルにページングデータを追加
		model.addAttribute("keyword", keyword);	// モデルに検索キーワードを保持させる
		
		return "admin/users/index";	// 会員一覧画面のビューテンプレートを返す
	}
	
	// 管理者用の会員詳細ページを表示する
	@GetMapping("/{id}")
	public String show(@PathVariable(name="id") Integer id, RedirectAttributes redirectAttributes, Model model) {
							// 【@PathVariable】URLのパス部分({id})に渡された値を変数として取り出して利用する機能
							// 【RedirectAttributes】リダイレクト先にメッセージなどの属性を一時的に引き継ぐ機能（フラッシュ属性）
							// 【Model】ビューにデータを渡すためのビュー
		Optional<User> optionalUser = userService.findUserById(id);	// 【ローカル変数】取得した会員データ本体
		
		// 【条件分岐】取得した店舗データが存在しない
		if(optionalUser.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが存在しません。");
			
			return "redirect:/admin/users";	// 会員一覧画面のビューテンプレートを返す
		}
		
		User user = optionalUser.get();		// 【ローカル変数】取得した会員データ本体
		model.addAttribute("user", user);	// モデルに会員データを追加
		return "admin/users/show";			// 会員詳細画面のビューテンプレートを返す
	}
}
