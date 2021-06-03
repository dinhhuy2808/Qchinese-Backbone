package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elearning.entity.User;
import com.elearning.entity.UserResult;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByPhone(String phone);
	List<User> findByEmail(String email);
	List<User> findByEmailAndPassword(String email, String password);
	List<User> findByPhoneAndPassword(String email, String password);
	List<User> findByUserIdAndPassword(Long userId, String password);
}
