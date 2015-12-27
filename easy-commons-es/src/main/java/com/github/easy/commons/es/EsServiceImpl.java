package com.github.easy.commons.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsServiceImpl implements EsService {

	private final static Logger LOGGER = LoggerFactory.getLogger(EsServiceImpl.class);
	
	private EsClient esClient;
	
	public void setEsClient(EsClient esClient) {
		this.esClient = esClient;
	}

	public boolean createIndex(String index) {
		if (isExistsIndex(index)) {
			return true;
		}
		CreateIndexResponse createIndexResponse = esClient.getClient().admin().indices().prepareCreate(index).execute().actionGet();
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

}
