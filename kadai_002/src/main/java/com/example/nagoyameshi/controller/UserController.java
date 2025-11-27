package com.example.nagoyameshi.controller;

import java.time.format.DateTimeFormatter;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.form.WithdrawalForm;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userDetailsImpl.getUser();
		
		model.addAttribute("user", user);
		
		return "user/index";
	}
	
	@GetMapping("/edit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userDetailsImpl.getUser();
		String birthday = null;
		
		if(user.getBirthday() != null) {
			birthday = user.getBirthday().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		}
		
		UserEditForm userEditForm = new UserEditForm(user.getName(), user.getFurigana(), user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), birthday, user.getOccupation(), user.getEmail());
		
		model.addAttribute("userEditForm", userEditForm);
		
		return "user/edit";
	}
	
	@PostMapping("/update")
	public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes, Model model) {
		User user = userDetailsImpl.getUser();
		
		// メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
		if(userService.isEmailChanged(userEditForm, user) && userService.isEmailRegistered(userEditForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);
		}
		if(bindingResult.hasErrors()) {
			model.addAttribute("userEditForm", userEditForm);
			return "user/edit";
		}
		
		userService.updateUser(userEditForm, user);
		redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
		
		return "redirect:/user";
	}
	
	   // 【会員退会】
	   @GetMapping("/withdrawal")
	   public String showwithdrawal(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redictAttributes, Model model) {
		  
		   if(userDetailsImpl == null) {
			   return "redirect:/login?error=notLoggedIn";
		   }
		   
		   User user=userDetailsImpl.getUser();
		   
		   // 有料会員であれば、有料プラン解約する
		   if(user.getStripeCustomerId() != null) {
			   redictAttributes.addFlashAttribute("subscriptionMessage", "退会前に有料プラン解約お願いいたします。");
			   return "redirect:/subscription/cancel";
		   }
		   
		   model.addAttribute("withdrawalForm", new WithdrawalForm());
		   
		   return "user/withdrawal";
	   }
	   
	   @PostMapping("/withdrawal")
	   public String withdrawal(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @ModelAttribute @Validated WithdrawalForm withdrawalForm, BindingResult bindingResult, RedirectAttributes redirectAttributes,  HttpServletRequest httpServletRequest, Model model) {
		   // 再設定用のパスワードと再設定用のパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
		   if(!userService.isSamePassword(withdrawalForm.getPassword(), withdrawalForm.getPasswordConfirmation())) {
			   FieldError fieldError=new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
			   bindingResult.addError(fieldError);
		   }
		   
		   if(bindingResult.hasErrors()) {
				model.addAttribute("withdrawalForm", withdrawalForm);
			   
			   return "user/withdrawal";
		   }
		   
		   httpServletRequest.getSession().invalidate();
		   SecurityContextHolder.clearContext();
		   
		   User user=userDetailsImpl.getUser();
		   userService.deleteUser(user.getEmail());
		   redirectAttributes.addFlashAttribute("successMessage", "退会が完了いたしました。");
		   
		   return "redirect:/";
	   }
}
