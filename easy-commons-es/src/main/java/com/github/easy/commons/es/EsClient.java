package com.github.easy.commons.es;

import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class EsClient implements InitializingBean,DisposableBean{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(EsClient.class);
	
	/** 集群名称 */
	private String clusterName;
	/** 节点地址 */
	private String host = "localhost";
	/** 节点端口 */
	private Integer port = 9200;
	/** 客户端 */
	private Client client;
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPort(Integer port) {
		this.port = port;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public Client getClient() {
		return client;
	}

	public void afterPropertiesSet() throws Exception {
		try {
			LOGGER.info("esClient init...");
			Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName).build();
			client = TransportClient.builder().settings(settings).build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
		} catch (Exception e) {
			LOGGER.error("esClient init error", e);
		}
	}

	public void destroy() throws Exception {
		try {
			LOGGER.info("esClient close...");
			client.close();
		} catch (Exception e) {
			LOGGER.error("esClient close error", e);
		}
		
	}
	
}
