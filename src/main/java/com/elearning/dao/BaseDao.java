/*
 * Copyright (c) 2016 Aberger Software GmbH. All Rights Reserved.
 *               http://www.aberger.at
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.elearning.dao;

import java.util.List;

public interface BaseDao<T>  {
	 public List<?> findByKey(T t);
	 public List<?> findByKeySortBy(T t, String sort);
	 public List<?> findByGivenKey(T t, String key);
	 public List<?> findByGivenKeySortBy(T t, String key, String sort);
	 public String addThenReturnId(T t);
	 public boolean add(T t);
	 public boolean addList(List<T> t);
	 public boolean deleteByKey(T t);
	 public boolean deleteByGivenValue(T t);
	 public boolean updateByKey(T t);
	 public Integer updateByInputKey(T t,List<String> keys);
	 public Integer updateByInputKeyAndNotNullFields(T t,List<String> keys);
}