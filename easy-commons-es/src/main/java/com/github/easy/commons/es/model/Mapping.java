package com.github.easy.commons.es.model;

public class Mapping implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	/** 类型 (string, integer/long, float/double, boolean, and null) */
	private String type="string";
	/**not_analyzed or analyzed  */
	private String index="not_analyzed";
	/** yes or no*/
	private String store="yes";
	
	public Mapping() {
		
	}
	
	public Mapping(String type, String index, String store) {
		this.type = type;
		this.index = index;
		this.store = store;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}

}
