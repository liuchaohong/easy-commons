package com.github.easy.commons.es.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;

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
	
	/**
	 * 创建type和mapping
	 * @param index
	 * @param type
	 * @param map
	 * @return
	 */
	public boolean createTypeAndMapping(String index, String type, Map<String,Object> map); 
	
	/**
	 * 索引MAP
	 * @param index
	 * @param type
	 * @param map
	 * @return
	 */
	public boolean indexMap(String index, String type, Map<String, Object> map);

	/**
	 * 索引List
	 * @param <T>
	 * @param index
	 * @param type
	 * @param map
	 * @return
	 */
	public <T> boolean indexList(String index, String type, List<T> list);

	/**
	 * 根据id删除索引
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public boolean deleteIndexById(String index, String type, String id);

	/**
	 * 根据id删除批量索引
	 * @param index
	 * @param type
	 * @param ids
	 * @return
	 */
	public boolean deleteIndexByIds(String index, String type, List<String> ids);	

	/**
	 * 根据ID查询索引
	 * @param index
	 * @param type
	 * @param id
	 * @param clazz
	 * @return
	 */
	public <T> T getIndexById(String index, String type, String id, Class<T> clazz);

	/**
	 * 查询
	 * @param index 索引名称
	 * @param type 索引目标顾类型
	 * @param queryBuilder 查询语句
	 * @param sortBuilder 排序语句
	 * @param clazz 需要转化目标类
	 * @param from 分页开始
	 * @param size 每页数量
	 * @return
	 */
	public <T> List<T> getDataListByQuery(String index, String type,
			QueryBuilder queryBuilder, List<SortBuilder> sortBuilders, Class<T> clazz,
			int from, int size);

	/**
	 * 查询
	 * @param index 索引名称
	 * @param type 索引目标顾类型
	 * @param queryBuilder 查询语句
	 * @param sortBuilder 排序语句
	 * @param from 分页开始
	 * @param size 每页数量
	 * @return
	 */
	public SearchResponse getDataListByQuery(String index,
			String type, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders,
			int from, int size);
	
	/**
	 * 查询
	 * @param index 索引名称
	 * @param type 索引目标顾类型
	 * @param queryBuilder 查询语句
	 * @param aggregationBuilder
	 * @param sortBuilder 排序语句
	 * @param from 分页开始
	 * @param size 每页数量
	 * @return
	 */
	public SearchResponse getDataListByQuery(String index,String type,QueryBuilder queryBuilder,AggregationBuilder aggregationBuilder,List<SortBuilder> sortBuilders,int from,int size);
	
	/**
	 * 根据查询字段只查询想要的字体字段，对应的是MAP
	 * @param index
	 * @param type
	 * @param fieldArr
	 * @param queryBuilder
	 * @param sortBuilder
	 * @param from
	 * @param size
	 * @return
	 */
	public List<Map<String,Object>> getMapDataListByQuery(String index,String type,String[] fieldArr,QueryBuilder queryBuilder,List<SortBuilder> sortBuilders,int from,int size);
	
	/**
	 * 根据查询字段只查询想要的字体字段数据
	 * @param index
	 * @param type
	 * @param fieldArr
	 * @param queryBuilder
	 * @param sortBuilder
	 * @param from
	 * @param size
	 * @return
	 */
	public SearchResponse getFieldsDataListByQuery(String index,String type,String[] fieldArr,QueryBuilder queryBuilder,List<SortBuilder> sortBuilders,int from,int size);
	
	
	/**
	 * 返回滚动下载ID
	 * @param index
	 * @param type
	 * @param fieldArr
	 * @param queryBuilder
	 * @param scrollTimeStr
	 * @param size
	 * @return
	 */
	public String getScrollIdByScrollQuery(String index,String type,String[] fieldArr,QueryBuilder queryBuilder,int size,String scrollTimeStr);
	
	/**
	 * 根据滚动ID下载数据
	 * @param scrollId
	 * @param scrollTimeStr
	 * @return
	 */
	public List<Map<String,Object>> getMapDataListByScrollId(String scrollId,String scrollTimeStr);
	
}
