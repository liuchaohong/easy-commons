package com.github.easy.commons.es;

import java.util.Set;

public interface EsService {

	/**
	 * 创建索引
	 * @param index
	 * @return
	 */
	public boolean createIndex(String index);
	
	/**
	 * 判断索引是否存在
	 * @param index
	 * @return
	 */
	public boolean isExistsIndex(String index);
	
	/**
	 * 获取所有索引
	 * @return
	 */
	public Set<String> getAllIndex();
	
	/**
	 * 删除索引
	 * @param index
	 * @return
	 */
	public boolean deleteIndex(String index);
}
