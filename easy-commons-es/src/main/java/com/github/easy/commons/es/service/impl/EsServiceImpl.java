package com.github.easy.commons.es.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.easy.commons.es.client.EsClient;
import com.github.easy.commons.es.service.EsService;
import com.github.easy.commons.es.util.ESUtil;
import com.github.easy.commons.es.util.IdWorker;
import com.github.easy.commons.es.util.iThreadLocal;
import com.github.easy.commons.util.json.JsonUtil;

public class EsServiceImpl implements EsService {

	private final static Logger LOGGER = LoggerFactory.getLogger(EsServiceImpl.class);
	
	protected EsClient esClient;

	protected String idName="id";
	
	protected IdWorker idWorker;
	
	protected int workerId;
	
	public void setEsClient(EsClient esClient) {
		this.esClient = esClient;
	}
	
	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public IdWorker getIdWorker() {
		return idWorker;
	}
	
	public void setIdWorker(IdWorker idWorker) {
		this.idWorker = idWorker;
	}

	public int getWorkerId() {
		return workerId;
	}

	public void setWorkerId(int workerId) {
		this.workerId = workerId;
		if(idWorker == null){
			idWorker = new IdWorker(workerId);
		}
	}

	public String getNextId(){
		if(idWorker != null){
			return String.valueOf(idWorker.nextId());
		}else{
			return UUID.randomUUID().toString().replaceAll("-", "");
		}
	}

	
	public boolean createIndex(String index){
		return createIndex(index, null);
	}
			
	public boolean createIndex(String index, Settings settings) {
		if (isExistsIndex(index)) {
			return true;
		}
		if(settings == null){
			settings = Settings.EMPTY;
		}
		CreateIndexResponse createIndexResponse = esClient.getClient().admin().indices().prepareCreate(index).setSettings(settings).execute().actionGet();
		boolean result = createIndexResponse.isAcknowledged();
		LOGGER.info("create index["+index+"]:"+result);
		return result;
	}

	public boolean isExistsIndex(String index) {
		IndicesExistsResponse indicesExistsResponse = esClient.getClient().admin().indices().prepareExists(index).execute().actionGet();
		boolean result = indicesExistsResponse.isExists();
		LOGGER.info("index["+index+"] isExists:"+result);
		return result;
	}

	public Set<String> getAllIndex() {
		ClusterStateResponse clusterStateResponse = esClient.getClient().admin().cluster().prepareState().execute().actionGet();
		String[] indices = clusterStateResponse.getState().getMetaData().concreteAllIndices();
		Set<String> indiceSet=new HashSet<String>();
		indiceSet.addAll(Arrays.asList(indices));
		return indiceSet;
	}

	public boolean deleteIndex(String index) {
		if (!isExistsIndex(index)) {
			return true;
		}
		DeleteIndexResponse deleteIndexResponse = esClient.getClient().admin().indices().prepareDelete(index).execute().actionGet();
		boolean result = deleteIndexResponse.isAcknowledged();
		LOGGER.info("delete index["+index+"]:"+result);
		return result;
	}

	public boolean createTypeAndMapping(String index, String type,
			Map<String, Object> map) {
		PutMappingRequestBuilder bulider = esClient.getClient().admin().indices().preparePutMapping(index);
		bulider.setType(type);
		Map<String,Object> mapoxa = new HashMap<String,Object>();
		mapoxa.put("enabled", false);
		Map<String,Object> mapp = new HashMap<String,Object>();
		mapp.put("properties", map);
		//此项禁止将影响全文搜索
		//mapp.put("_all", mapoxa);
		//source影响查看数据
		/*mapp.put("_source", mapoxa);*/
		Map<String,Object> mapt = new HashMap<String,Object>();
		mapt.put(type, mapp);
		String json = JsonUtil.toJson(mapt);
		PutMappingResponse res=bulider.setSource(json).execute().actionGet();
		if(!res.isAcknowledged()){
			LOGGER.error("index :" + index + " create " + type + " failure");
		}
		return res.isAcknowledged();
	}

	public boolean indexMap(String index, String type, Map<String, Object> map) {
		if(null != map && !map.isEmpty()){
			String id = null;
			if(StringUtils.isNotBlank(getIdName()) && map.containsKey(getIdName())){
				id=String.valueOf(map.get(getIdName()));
			}
			if(StringUtils.isBlank(id)){
				id=getNextId();
			}
			String json = JsonUtil.toJson(map);
			IndexResponse ir=esClient.getClient().prepareIndex(index, type).setId(id).setSource(json).execute().actionGet();
			if(!ir.isCreated()){
				LOGGER.error("indexModel " + index + " failure");
			}
			return ir.isCreated();
		}
		return false;
	}

	public <T> boolean indexList(String index, String type, List<T> list) {
		if(CollectionUtils.isNotEmpty(list)){
			Client client=esClient.getClient();
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			for(T obj:list){
				String id = null;
				/*if(obj instanceof BaseModel){
					id=((BaseModel) obj).getId();
				}else */
				if((obj instanceof Map)&&(((Map)obj).containsKey(getIdName()))){
					id = String.valueOf(((Map)obj).get(getIdName()));
				}else{
					if(StringUtils.isNotBlank(getIdName())){
						try {
							id = BeanUtils.getProperty(obj, getIdName());
						} catch (Exception e) {
						}
					}
				}
				if(StringUtils.isBlank(id)){
					id = getNextId();
				}
				String json = JsonUtil.toJson(obj);
				IndexRequestBuilder request = client.prepareIndex(index, type).setId(id).setSource(json);
				bulkRequest.add(request);
			}
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures()) {  
				LOGGER.error(bulkResponse.buildFailureMessage());
	        }  
			return !bulkResponse.hasFailures();
		}
		return false;
	}

	public boolean deleteIndexById(String index, String type, String id) {
		DeleteResponse res = esClient.getClient().prepareDelete(index, type, id).execute().actionGet();
		return res.isFound();
	}

	public boolean deleteIndexByIds(String index, String type, List<String> ids) {
		if(CollectionUtils.isNotEmpty(ids)){
			Client client=esClient.getClient();
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			if(null!=ids&&!ids.isEmpty()){
				for(String id:ids){
					DeleteRequestBuilder request=client.prepareDelete(index, type, id);
					bulkRequest.add(request);
				}
			}
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures()) {  
				LOGGER.error(bulkResponse.buildFailureMessage());
	        }  
			return !bulkResponse.hasFailures();
		}
		return false;
	}

	public <T> T getIndexById(String index, String type, String id, Class<T> clazz) {
		GetResponse res = esClient.getClient().prepareGet(index, type, id).execute().actionGet();
		if(res.isExists()){
			return JsonUtil.toObject(res.getSourceAsString(), clazz);
		}
		return null;
	}

	public <T> List<T> getDataListByQuery(String index, String type, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders, Class<T> clazz, int from, int size) {
		List<T> list = new ArrayList<T>();
		SearchResponse sr = getDataListByQuery(index, type, queryBuilder, sortBuilders, from, size);
		for(SearchHit hit : sr.getHits()){
			list.add(JsonUtil.toObject(hit.getSourceAsString(), clazz));
		}
		return list;
	}

	public SearchResponse getDataListByQuery(String index, String type, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders, int from, int size) {
		return getDataListByQuery(index, type, queryBuilder, null, sortBuilders, from, size);
	}

	public SearchResponse getDataListByQuery(String index, String type, QueryBuilder queryBuilder, AggregationBuilder aggregationBuilder, List<SortBuilder> sortBuilders, int from, int size) {
		SearchRequestBuilder sb = null;
		if(aggregationBuilder == null){
			sb=esClient.getClient().prepareSearch(index).setTypes(type).setQuery(queryBuilder).setFrom(from).setSize(size);
		}else{
			sb=esClient.getClient().prepareSearch(index).setTypes(type).setQuery(queryBuilder).addAggregation(aggregationBuilder).setFrom(from).setSize(size);
		}
		
		if(CollectionUtils.isNotEmpty(sortBuilders)){
			for(SortBuilder sortBuilder:sortBuilders){
				sb.addSort(sortBuilder);
			}
		}
		SearchResponse sr=sb.execute().actionGet();
		//设置总数用于在分页时使用
		iThreadLocal.set(ESUtil.getTotalHitsThreadLocalKey(), sr.getHits().totalHits());
		LOGGER.info("Index:" + index + ",Type:" + type + ",Query" + sb.toString() + "用时：" + sr.getTook());
		return sr;
	}

	public List<Map<String, Object>> getMapDataListByQuery(String index, String type, String[] fieldArr, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders, int from, int size) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		SearchResponse sr = getFieldsDataListByQuery(index, type, fieldArr, queryBuilder, sortBuilders, from, size);
		for(SearchHit hit : sr.getHits()){
			Map<String,Object> map = new HashMap<String,Object>(); 
			Map<String, SearchHitField> esMap = hit.getFields();
			for(Map.Entry<String, SearchHitField> entry:esMap.entrySet()){
				map.put(entry.getKey(), entry.getValue().getValue());
			}
			list.add(map);
		}
		return list;
	}

	public SearchResponse getFieldsDataListByQuery(String index, String type,
			String[] fieldArr, QueryBuilder queryBuilder,
			List<SortBuilder> sortBuilders, int from, int size) {
		SearchRequestBuilder sb = esClient.getClient().prepareSearch(index).setTypes(type).addFields(fieldArr).setQuery(queryBuilder).setFrom(from).setSize(size);
		if(CollectionUtils.isNotEmpty(sortBuilders)){
			for(SortBuilder sortBuilder:sortBuilders){
				sb.addSort(sortBuilder);
			}
		}
		SearchResponse sr=sb.execute().actionGet();
		//设置总数用于在分页时使用
		iThreadLocal.set(ESUtil.getTotalHitsThreadLocalKey(), sr.getHits().totalHits());
		LOGGER.info("Index:" + index + ",Type:" + type + ",Query" + sb.toString() + "用时：" + sr.getTook());
		return sr;
	}

	public String getScrollIdByScrollQuery(String index, String type, String[] fieldArr, QueryBuilder queryBuilder, int size, String scrollTimeStr) {
		if(StringUtils.isBlank(scrollTimeStr)){
			scrollTimeStr = "1m";
		}
		SearchRequestBuilder sb=esClient.getClient().prepareSearch(index).setTypes(type).addFields(fieldArr).setQuery(queryBuilder).setSize(size).setScroll(scrollTimeStr).setSearchType(SearchType.SCAN);
		SearchResponse sr=sb.execute().actionGet();
		//设置总数用于在分页时使用
		iThreadLocal.set(ESUtil.getTotalHitsThreadLocalKey(), sr.getHits().totalHits());
		//设置scrollId
		iThreadLocal.set(ESUtil.getScrollIdThreadLocalKey(), sr.getScrollId());
		LOGGER.info("Index:" + index + ",Type:" + type + ",Query" + sb.toString() + "用时："+sr.getTook());
		LOGGER.info("FirstScrollId:" + sr.getScrollId());
		return sr.getScrollId();
	}

	public List<Map<String, Object>> getMapDataListByScrollId(String scrollId, String scrollTimeStr) {
		if(StringUtils.isBlank(scrollTimeStr)){
			scrollTimeStr = "1m";
		}
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		SearchScrollRequestBuilder sb = esClient.getClient().prepareSearchScroll(scrollId).setScroll(scrollTimeStr);
		SearchResponse sr = sb.execute().actionGet();
		for(SearchHit hit : sr.getHits()){
			Map<String,Object> map = new HashMap<String,Object>(); 
			Map<String, SearchHitField> esMap = hit.getFields();
			for(Map.Entry<String, SearchHitField> entry : esMap.entrySet()){
				map.put(entry.getKey(), entry.getValue().getValue());
			}
			list.add(map);
		}
		//设置总数用于在分页时使用
		iThreadLocal.set(ESUtil.getTotalHitsThreadLocalKey(), sr.getHits().totalHits());
		//设置scrollId
		iThreadLocal.set(ESUtil.getScrollIdThreadLocalKey(), sr.getScrollId());
		LOGGER.info("Scroll用时：" + sr.getTook() + ",size:" + list.size());
		return list;
	}

}
