package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elearning.entity.PromoteSetting;
import com.elearning.entity.UserResult;

@Repository
public interface PromoteSettingRepository extends JpaRepository<PromoteSetting, Long> {
	List<PromoteSetting> findByHsk(int hsk);
}
