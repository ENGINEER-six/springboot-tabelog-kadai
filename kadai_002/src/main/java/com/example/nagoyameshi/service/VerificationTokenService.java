package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.repository.VerificationTokenRepository;

@Service
public class VerificationTokenService {
	private final VerificationTokenRepository verificationTokenRepository;
	
	// トークンをユーザーIDとともにverification_tokenテーブルに保存する
	public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
		this.verificationTokenRepository = verificationTokenRepository;
	}
	@Transactional
	public void createVerificationToken(User userId, String token) {
		VerificationToken existionToken=verificationTokenRepository.findByUserId(userId);
		
		if(existionToken != null) {
			existionToken.setToken(token);
			verificationTokenRepository.save(existionToken);
		} else {
			VerificationToken verificationToken = new VerificationToken();
			
			verificationToken.setUserId(userId);
			verificationToken.setToken(token);
			
			verificationTokenRepository.save(verificationToken);
		}
	}
	
	// 指定したトークンに一致するVerificationTokenエンエンティを取得する
	// トークンの文字列で検索した結果を返す
	public VerificationToken getVerificationToken(String token) {
		return verificationTokenRepository.findByToken(token);
	}
	
	@Transactional
	public void delete(VerificationToken verificationToken) {
		verificationTokenRepository.delete(verificationToken);
	}
}
