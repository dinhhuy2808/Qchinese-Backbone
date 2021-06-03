package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.elearning.entity.Dictionary;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Integer> {
	List<Dictionary> findByHantu(String hantu);

	@Query(value = "ALTER TABLE dictionary AUTO_INCREMENT = 1", nativeQuery = true)
	void alterSetAutoIncrementToOne();

	@Query(value = " select * from dictionary order by id LIMIT 30 OFFSET :page ", nativeQuery = true)
	List<Dictionary> getWordsWithPaging(int page);

	@Query(value = " select * from dictionary where hantu like '%:keyword%' or pinyin like '%:keyword%' order by id", nativeQuery = true)
	List<Dictionary> findByKeyWord(String keyWord);

	List<Dictionary> findByHantuLikeOrPinyinLikeOrderById(String hantu, String pinyin);
}
