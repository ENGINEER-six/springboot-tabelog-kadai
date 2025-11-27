package com.example.nagoyameshi.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ResetPasswordForm;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RegularHolidayRepository;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.repository.VerificationTokenRepository;

@Service
public class UserService {

    private final VerificationTokenRepository verificationTokenRepository;

	private final RegularHolidayRepository regularHolidayRepository;
   private final UserRepository userRepository;
   private final ReservationRepository reservationRepository;
   private final FavoriteRepository favoriteRepository;
   private final ReviewRepository reviewRepository;
   private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;

   public UserService(UserRepository userRepository, ReservationRepository reservationRepository, FavoriteRepository favoriteRepository, ReviewRepository reviewRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, RegularHolidayRepository regularHolidayRepository, VerificationTokenRepository verificationTokenRepository) {
       this.userRepository = userRepository;
       this.reservationRepository=reservationRepository;
       this.favoriteRepository=favoriteRepository;
       this.reviewRepository=reviewRepository;
       this.roleRepository = roleRepository;
       this.passwordEncoder = passwordEncoder;
       this.regularHolidayRepository = regularHolidayRepository;
       this.verificationTokenRepository = verificationTokenRepository;
   }

   // 【新規登録】
   // フォームから送信された会員情報をデータベースに登録する
   @Transactional
   public User createUser(SignupForm signupForm) {
       User user = new User();
       Role role = roleRepository.findByName("ROLE_FREE_MEMBER");

       user.setName(signupForm.getName());							// 会員の氏名
       user.setFurigana(signupForm.getFurigana());					// 会員のフリガナ
       user.setPostalCode(signupForm.getPostalCode());				// 会員の郵便番号
       user.setAddress(signupForm.getAddress());						// 会員の住所
       user.setPhoneNumber(signupForm.getPhoneNumber());	// 会員の電話番号

       // 会員の誕生日
       if (!signupForm.getBirthday().isEmpty()) {
           user.setBirthday(LocalDate.parse(signupForm.getBirthday(), DateTimeFormatter.ofPattern("yyyyMMdd")));
       } else {
           user.setBirthday(null);
       }

       // 会員の職業
       if (!signupForm.getOccupation().isEmpty()) {
           user.setOccupation(signupForm.getOccupation());
       } else {
           user.setOccupation(null);
       }

       user.setEmail(signupForm.getEmail());								// 会員のメールアドレス
       user.setPassword(passwordEncoder.encode(signupForm.getPassword()));	// 会員のパスワード
       user.setRole(role);															// ロール名
       user.setEnabled(false);													// メール認証済みかどうかの判定に利用する

       return userRepository.save(user);
   }
   
   // 【会員側_会員管理機能】
   // フォームから送信された会員情報でデータベースを更新する
   @Transactional
   public void updateUser(UserEditForm userEditForm, User user) {
	   user.setName(userEditForm.getName());
	   user.setFurigana(userEditForm.getFurigana());
	   user.setPostalCode(userEditForm.getPostalCode());
	   user.setAddress(userEditForm.getAddress());
	   user.setPhoneNumber(userEditForm.getPhoneNumber());
	   
	   if(!userEditForm.getBirthday().isEmpty()) {
		   user.setBirthday(LocalDate.parse(userEditForm.getBirthday(), DateTimeFormatter.ofPattern("yyyyMMdd")));
	   } else {
		   user.setBirthday(null);
	   }
	   
	   if(!userEditForm.getOccupation().isEmpty()) {
		   user.setOccupation(userEditForm.getOccupation());
	   } else {
		   user.setOccupation(null);
	   }
	   
	   user.setEmail(userEditForm.getEmail());
	   
	   userRepository.save(user);
   }
   
   // 【会員側_退会】
   @Transactional
   public void deleteUser(String email) {
	   User user=userRepository.findByEmail(email);
	   
	   user.setStripeCustomerId(null);
	   user.setEnabled(false);
	   userRepository.save(user);
	   
	   verificationTokenRepository.deleteByUserId(user.getId());
	   reservationRepository.deleteByUserId(user.getId());
	   favoriteRepository.deleteByUser(user);
	   reviewRepository.deleteByUser(user);
	   
	   userRepository.deleteByEmail(email);
   }
   
   // 【会員側_パスワード再設定】
   // フォームから送信された会員情報でデータベースを更新する
   @Transactional
   public void resetUser(ResetPasswordForm resetPasswordForm, User user) {
	   user.setPassword(passwordEncoder.encode(resetPasswordForm.getPassword()));
	   user.setEnabled(true);
	   
	   userRepository.save(user);
	   
	   refreshAuthenticationByRole("ROLE_FREE_MEMBER");
   }

   // メールアドレスが登録済みかどうかをチェックする
   public boolean isEmailRegistered(String email) {
       User user = userRepository.findByEmail(email);
       return user != null;
   }

   // パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
   public boolean isSamePassword(String password, String passwordConfirmation) {
       return password.equals(passwordConfirmation);
   }
   
   // ユーザーを有効にする
   @Transactional
   public void enableUser(User user) {
	   user.setEnabled(true);			// メール認証済みかどうかの判定に利用する
	   userRepository.save(user);
   }
   
   // 【会員側_会員管理機能】
   // メールアドレスが変更されたかどうかをチェックする
   public boolean isEmailChanged(UserEditForm userEditForm, User user) {
	   return !userEditForm.getEmail().equals(user.getEmail());
   }
   
   // 【会員側_会員管理機能】
   // 指定したメールアドレスを持つユーザーを取得する
   public User findUserByEmail(String email) {
	   return userRepository.findByEmail(email);
   }
   
   // 【会員側_有料プラン登録機能】
   // 顧客IDをセットした後保存する
   @Transactional
   public void saveStripeCustomerId(User user, String stripeCustomerId) {
	   user.setStripeCustomerId(stripeCustomerId);
	   userRepository.save(user);
   }
   
   // 【会員側_有料プラン登録機能】
   // roleフィールドを指定したロールで更新する
   @Transactional
   public void updateRole(User user, String roleName) {
	   Role role=roleRepository.findByName(roleName);
	   user.setRole(role);
	   userRepository.save(user);
   }
   
   // 【会員側_有料プラン登録機能】
   // 認証情報のロールを更新する
   public void refreshAuthenticationByRole(String newRole) {
	   // 現在の認証情報を取得する
	   Authentication currentAuthentication=SecurityContextHolder.getContext().getAuthentication();
	   
	   // 新しい認証情報を作成する
	   List<SimpleGrantedAuthority> simpleGrantedAuthorities=new ArrayList<>();
	   simpleGrantedAuthorities.add(new SimpleGrantedAuthority(newRole));
	   Authentication newAuthentication=new UsernamePasswordAuthenticationToken(currentAuthentication.getPrincipal(), currentAuthentication.getCredentials(), simpleGrantedAuthorities);
	   
	   // 認証情報を更新する
	   SecurityContextHolder.getContext().setAuthentication(newAuthentication);
   }
   
   // 【管理者側_会員管理機能】
   // すべてのユーザーをページングされた状態で取得する
   public Page<User> findAllUsers(Pageable pageable){
	   return userRepository.findAll(pageable);
   }
   
   // 【管理者側_会員管理機能】
   // 指定されたキーワードを氏名またはフリガナに含むユーザーを、ページングされた状態で取得する
   public Page<User> findUsersByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable){
	   return userRepository.findByNameLikeOrFuriganaLike("%" + nameKeyword + "%", "%" + furiganaKeyword + "%", pageable);
   }
   
   // 【管理者側_会員管理機能】
   // 指定したidを持つユーザーを取得する
   public Optional<User> findUserById(Integer id){
	   return userRepository.findById(id);
   }

   // 【管理者側_会員管理機能】
   // 指定したロール名に紐づくユーザーのレコード数を取得する
   public long countUsersByRole_Name(String roleName) {
	   return userRepository.countByRole_Name(roleName);
   }
}
