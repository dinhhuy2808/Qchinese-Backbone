package com.elearning.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.elearning.dto.StringListDTO;
import com.elearning.dto.UserDictionaryDatatableResponse;
import com.elearning.entity.Dictionary;
import com.elearning.entity.LessonDictionary;
import com.elearning.entity.UserDictionary;
import com.elearning.request.DatatableParamHolder;

@Component
public interface DictionaryDao {
	public List<Dictionary> findWords(String keyWord);
	public boolean deleteWordsFromUserDic(String tab,String wordIds, Long userId);
	
	public List<UserDictionary> getUserDictionaryList(String tab, Long userId);
	public UserDictionaryDatatableResponse getUserDictionaryResponse(List<UserDictionary> userDictionaries, String tab, DatatableParamHolder param);
	public int countLessonDictionary(int type, DatatableParamHolder param);
	public StringListDTO getUserTabs(Long userId);
	public StringListDTO getUserUserDictionaryCreateDate(Long userId, String tab);
}
