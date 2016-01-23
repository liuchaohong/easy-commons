package com.github.easy.commons.es;

import java.util.Set;

import org.elasticsearch.common.settings.Settings;

public interface EsService {

	/**
	 * 创建索引
	 * @param index
	 * @return
	 */
	public boolean createIndex(String index);
	
	/**
	 * 创建索引
	 * @param index
	 * @param settings
	 * @return
	 */
	public boolean createIndex(String index, Settings settings);
	
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
