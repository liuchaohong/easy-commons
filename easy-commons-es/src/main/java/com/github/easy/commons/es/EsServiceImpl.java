package com.github.easy.commons.es;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsServiceImpl implements EsService {

	private final static Logger LOGGER = LoggerFactory.getLogger(EsServiceImpl.class);
	
	private EsClient esClient;
	
	public void setEsClient(EsClient esClient) {
		this.esClient = esClient;
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

}
