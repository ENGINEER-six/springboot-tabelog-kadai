package com.example.nagoyameshi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer>{
	public VerificationToken findByToken(String token);
	public VerificationToken findByUserId(User userId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM VerificationToken v WHERE v.userId.id = :userId")
	public void deleteByUserId(@Param("userId") Integer userId);
}
