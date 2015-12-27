package com.github.easy.commons.es;

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
	
}
