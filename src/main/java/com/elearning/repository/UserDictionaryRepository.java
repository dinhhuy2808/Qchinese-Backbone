package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elearning.entity.UserDictionary;

@Repository
public interface UserDictionaryRepository extends JpaRepository<UserDictionary, Long> {
	Long countByTabAndUserIdAndPlace(String tabe, Long userId, String place);
	List<UserDictionary> findByTabAndUserIdAndPlace(String tab, Long userId, String place);
	List<UserDictionary> findByUserIdAndTab(Long userId, String tab);
	long deleteByUserIdAndTab(Long userId, String tab);
}
