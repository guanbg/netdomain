package com.platform.cubism.service.core;

public class ServiceEntity {
	private String id;
	private String value;
	private String condition;
	private String errorcode;
	private String errormsg;
	private boolean alwayarray;
	private boolean lowercase;
	private boolean async;
	private String generatedkeys;
	private int fetch;
	private String processor;
	private String cache;//缓存名称
	private String updatecache;//需要更新的键值，多个以空白或者逗号分号隔开
	private String serviceId;//所属服务编号
	private String each;
	private String datasource;
	private boolean execEachInGs;
	private EntityType type;

	public ServiceEntity() {
		execEachInGs = true;
	}

	public ServiceEntity(String id) {
		execEachInGs = true;
		setId(id);
	}

	public ServiceEntity(String id, String value, String condition, String errorcode, String errormsg, String each, EntityType type) {
		execEachInGs = true;
		setId(id);
		setValue(value);
		setCondition(condition);
		setErrorcode(errorcode);
		setErrormsg(errormsg);
		setType(type);
		setEach(each);
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getCache() {
		return cache;
	}

	public ServiceEntity setCache(String cache) {
		this.cache = cache;
		return this;
	}

	public String getUpdatecache() {
		return updatecache;
	}

	public ServiceEntity setUpdatecache(String updatecache) {
		this.updatecache = updatecache;
		return this;
	}

	public String getServiceId() {
		return serviceId;
	}

	public ServiceEntity setServiceId(String serviceId) {
		this.serviceId = serviceId;
		return this;
	}

	public int getFetch() {
		return fetch;
	}

	public ServiceEntity setFetch(int fetch) {
		this.fetch = fetch;
		return this;
	}

	public String getProcessor() {
		return processor;
	}

	public ServiceEntity setProcessor(String processor) {
		this.processor = processor;
		return this;
	}

	public String getId() {
		return id;
	}

	public boolean isAlwayarray() {
		return alwayarray;
	}

	public ServiceEntity setAlwayarray(boolean alwayarray) {
		this.alwayarray = alwayarray;
		return this;
	}

	public boolean isLowercase() {
		return lowercase;
	}

	public ServiceEntity setLowercase(boolean lowercase) {
		this.lowercase = lowercase;
		return this;
	}

	public boolean isAsync() {
		return async;
	}

	public ServiceEntity setAsync(boolean async) {
		this.async = async;
		return this;		
	}

	public String getValue() {
		return value;
	}

	public EntityType getType() {
		return type;
	}

	public String getCondition() {
		return condition;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public ServiceEntity setErrorcode(String errorcode) {
		this.errorcode = errorcode;
		return this;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public ServiceEntity setErrormsg(String errormsg) {
		this.errormsg = errormsg;
		return this;
	}

	public String getGeneratedkeys() {
		return generatedkeys;
	}

	public ServiceEntity setGeneratedkeys(String generatedkeys) {
		this.generatedkeys = generatedkeys;
		return this;
	}

	public String getEach() {
		return each;
	}

	public ServiceEntity setEach(String each) {
		this.each = each;
		return this;
	}

	public boolean isExecEachInGs() {
		return execEachInGs;
	}

	public ServiceEntity setExecEachInGs(boolean execEachInGs) {
		this.execEachInGs = execEachInGs;
		return this;
	}

	public ServiceEntity setId(String id) {
		this.id = id;
		return this;
	}

	public ServiceEntity setValue(String value) {
		if (value != null) {
			this.value = value.trim();
		} else {
			this.value = value;
		}
		return this;
	}

	public ServiceEntity setType(EntityType type) {
		this.type = type;
		return this;
	}

	public ServiceEntity setCondition(String condition) {
		this.condition = condition;
		return this;
	}
}