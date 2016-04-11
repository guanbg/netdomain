package com.platform.cubism.service.core;

import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.service.convert.ConvertManager.IN;
import static com.platform.cubism.service.convert.ConvertManager.OUT;

import java.util.List;

import org.slf4j.LoggerFactory;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.config.ArrElement;
import com.platform.cubism.service.config.ExpElement;
import com.platform.cubism.service.config.FldElement;
import com.platform.cubism.service.config.ImpElement;
import com.platform.cubism.service.config.InElement;
import com.platform.cubism.service.config.LogElement;
import com.platform.cubism.service.config.OutElement;
import com.platform.cubism.service.config.QuitElement;
import com.platform.cubism.service.config.RefElement;
import com.platform.cubism.service.config.SqlElement;
import com.platform.cubism.service.config.StcElement;
import com.platform.cubism.service.config.ServiceConfig;
import com.platform.cubism.service.config.ServiceConfigManager;
import com.platform.cubism.service.convert.ConvertManager;
import com.platform.cubism.util.Assert;
import com.platform.cubism.util.CubismHelper;

public class ConfigParser {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	private String serviceId;
	private ServiceEntity currentServiceEntity;// 当前正在执行的服务实体
	private ServiceEntity[] serviceEntity;// 当前需要执行的所有服务实体

	public ConfigParser(String serviceId) {
		Assert.hasText(serviceId);
		this.serviceId = CubismHelper.getServiceName(serviceId);
		Assert.hasText(serviceId);
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getServiceIdSafe() {
		// JSON字段名称中不能包含"."字符，如果包含点字符，则将点字符转换为下划线字符再作为字段名称
		if (hasText(serviceId) && serviceId.indexOf('.') != -1) {
			return serviceId.replaceAll("\\.", "_");
		}
		return serviceId;
	}

	public String getSafeId(String id) {
		// JSON字段名称中不能包含"."字符，如果包含点字符，则将点字符转换为下划线字符再作为字段名称
		if (hasText(id) && id.indexOf('.') != -1) {
			return id.replaceAll("\\.", "_");
		}
		return id;
	}

	public ServiceEntity getCurrentServiceEntity() {
		return currentServiceEntity;
	}

	public void setCurrentServiceEntity(ServiceEntity currentServiceEntity) {
		this.currentServiceEntity = currentServiceEntity;
	}

	public Json inMapping(Json in) {
		if (in == null) {
			return null;
		}
		ServiceConfig sc = getServiceConfig();
		if (sc == null || sc.getIn() == null) {
			Json ret = in.deepCopy();
			ret.setName(IN);
			return ret;
		}

		Json ret = JsonFactory.create(IN);
		InElement inElem = sc.getIn();
		if (inElem != null && inElem.size() > 0) {
			CStruc struc = JsonFactory.createStruc(IN);
			if (in != null) {
				if (in.getObject().containsName(IN)) {
					struc.mergeOf(in.getObject());
				} else {
					struc.addStruc(JsonFactory.createStruc(IN).mergeOf(in.getObject()));// 复制上送报文
				}
			}

			if (inElem.getFld() != null){
				for (FldElement fld : inElem.getFld()) {
					CField cf = ConvertManager.fldConvert(fld, struc);
					if (cf != null) {
						ret.addField(cf);
					}
				}
			}
			if (inElem.getStc() != null){
				for (StcElement stc : inElem.getStc()) {
					if (IN.equalsIgnoreCase(stc.getValue()) && !hasText(stc.getName())) {
						ret.addOf(in);
						continue;
					}
					CStruc cs = ConvertManager.stcConvert(stc, struc);
					if (cs != null) {
						ret.addStruc(cs);
					}
				}
			}
			if (inElem.getArr() != null){
				for (ArrElement arr : inElem.getArr()) {
					CArray ca = ConvertManager.arrConvert(arr, struc);
					if (ca != null) {
						ret.addArray(ca);
					}
				}
			}
		}
		if (inElem != null && hasText(inElem.getClazz())) {
			if (logger.isDebugEnabled()) {
				logger.debug("该接口暂未实现，类没有被调用" + inElem.getClazz());// 以后实现
			}
		}
		if (inElem != null && hasText(inElem.getJson())) {
			if (sc.getJson(inElem.getJson()) != null) {
				ret.toJson(sc.getJson(inElem.getJson()).getValue());
			} else {
				ret.toJson(inElem.getJson());
			}
		}

		if (ret.isEmpty()) {
			Json rt = in.deepCopy();
			if (in.getStruc(IN) != null) {
				ret.getObject().mergeOf(rt.getStruc(IN));
			} else {
				ret.getObject().mergeOf(rt.getObject());
			}

			if (logger.isDebugEnabled()) {
				logger.debug("没有配置数据映射，直接返回上送报文：" + ret.toString());
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("数据映射后的报文：" + ret.toString());
			}
		}
		return ret;
	}

	public Json outMapping(Json result) {
		Json out = JsonFactory.create(getServiceIdSafe());
		ServiceConfig sc = getServiceConfig();
		if (sc == null || sc.getOut() == null) {
			return out.mergeOf(result);
		}

		OutElement outElem = sc.getOut();
		if (outElem.size() > 0) {
			if (result == null || result.isEmpty()) {
				result = JsonFactory.create(OUT);
			}
			CStruc struc = result.getObject();
			if (outElem.getFld() != null) {
				for (FldElement fld : outElem.getFld()) {
					CField cf = ConvertManager.fldConvert(fld, struc);
					if (cf != null) {
						out.addField(cf);
					}/*
					else{
						CField[] cfs = ConvertManager.fldConvertToArray(fld, struc);
						if (cf != null) {
							
						}
					}*/
				}
			}
			if (outElem.getStc() != null) {
				for (StcElement stc : outElem.getStc()) {
					CStruc cs = ConvertManager.stcConvert(stc, struc);
					if (cs != null) {
						out.addStruc(cs);
					}
				}
			}

			if (outElem.getArr() != null) {
				for (ArrElement arr : outElem.getArr()) {
					CArray ca = ConvertManager.arrConvert(arr, struc);
					if (ca != null) {
						out.addArray(ca);
					}
				}
			}
		}
		if (hasText(outElem.getClazz())) {
			;// 以后实现
		}
		if (hasText(outElem.getJson())) {
			if (sc.getJson(outElem.getJson()) != null) {
				out.toJson(sc.getJson(outElem.getJson()).getValue());
			} else {
				out.toJson(outElem.getJson());
			}
		}
		if (out.isEmpty()) {
			out = result.deepCopy();
			out.remove(IN);
			out.getObject().setName(getServiceIdSafe());
			if (logger.isDebugEnabled()) {
				logger.debug("没有配置数据映射，直接返回服务下送报文：" + out.toString());
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("数据映射后的下送报文：" + out.toString());
			}
		}
		return out;
	}

	public boolean hasLog() {
		List<LogElement> log = getServiceConfig().getLog();
		// if (log == null || log.isEmpty()) {
		if (log == null) {
			return false;
		}
		return true;
	}

	public List<LogElement> getLogElement() {
		ServiceConfig sc = getServiceConfig();
		List<LogElement> logs = sc.getLog();
		if(logs == null || logs.isEmpty()){
			return null;
		}

		for (LogElement le : logs) {
			if (!hasText(le.getBefore())) {
				le.setBefore(sc.getDefaultlogbefore());
			}
			if (!hasText(le.getAfter())) {
				le.setAfter(sc.getDefaultlogafter());
			}
		}

		return logs;
	}

	public boolean hasSqlElement() {
		Assert.hasText(serviceId);
		return hasSqlElement(serviceId);
	}

	private boolean hasSqlElement(String serviceId) {
		if (!hasText(serviceId)) {
			return false;
		}
		ServiceConfig sc = ServiceConfigManager.getService(serviceId);
		if (sc == null) {
			return false;
		}

		List<SqlElement> sqlElem = sc.getSql();
		if (sqlElem != null) {
			for (SqlElement sql : sqlElem) {
				if (hasText(sql.getValue())) {
					return true;
				}
			}
		}
		List<ExpElement> expElem = sc.getExp();
		if (expElem != null) {
			for (ExpElement exp : expElem) {
				if (hasText(exp.getValue())) {
					return true;
				}
			}
		}
		List<ImpElement> impElem = sc.getImp();
		if (impElem != null) {
			for (ImpElement imp : impElem) {
				if (hasText(imp.getValue())) {
					return true;
				}
			}
		}
		List<RefElement> refElem = sc.getRef();
		if (refElem != null) {
			for (RefElement ref : refElem) {
				if (hasText(ref.getSrv())) {
					if (hasSqlElement(ref.getSrv())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public ServiceEntity getServiceEntity(String id) {
		if (!hasText(id)) {
			return null;
		}
		if (serviceEntity == null) {
			serviceEntity = getServiceEntity();
		}
		if (serviceEntity == null) {
			return null;
		}
		for (ServiceEntity se : serviceEntity) {
			if (id.equalsIgnoreCase(se.getId())) {
				return se;
			}
		}
		return null;
	}

	public ServiceEntity[] getServiceEntity() {
		if (serviceEntity != null) {
			return serviceEntity;
		}
		ServiceConfig sc = getServiceConfig();

		int maxseq = sc.getMaxSeq();
		if (maxseq <= 0) {
			serviceEntity = new ServiceEntity[0];
			return serviceEntity;
		}
		RefElement ref;
		SqlElement sql;
		ExpElement exp;
		ImpElement imp;
		QuitElement quit;
		serviceEntity = new ServiceEntity[maxseq];
		for (int seq = 1; seq <= maxseq; seq++) {
			sql = sc.getSqlAt(seq);
			if (sql != null) {
				serviceEntity[seq - 1] = getEntityBySql(sql);
				if(serviceEntity[seq - 1] != null){
					serviceEntity[seq - 1].setServiceId(getServiceId());//结果集缓存时用到的键值
					serviceEntity[seq - 1].setDatasource(sc.getDatasource());
				}
				continue;
			}

			ref = sc.getRefAt(seq);
			if (ref != null) {
				serviceEntity[seq - 1] = getEntityByRef(ref);
				if(serviceEntity[seq - 1] != null){
					serviceEntity[seq - 1].setDatasource(sc.getDatasource());
				}
				continue;
			}
			exp = sc.getExpAt(seq);
			if (exp != null) {
				serviceEntity[seq - 1] = getEntityByExp(exp);
				if(serviceEntity[seq - 1] != null){
					serviceEntity[seq - 1].setDatasource(sc.getDatasource());
				}
				continue;
			}
			imp = sc.getImpAt(seq);
			if (imp != null) {
				serviceEntity[seq - 1] = getEntityByImp(imp);
				if(serviceEntity[seq - 1] != null){
					serviceEntity[seq - 1].setDatasource(sc.getDatasource());
				}
				continue;
			}

			quit = sc.getQuitAt(seq);
			if (quit != null) {
				serviceEntity[seq - 1] = getEntityByQuit(quit);
				continue;
			}
		}

		return serviceEntity;
	}

	private ServiceEntity getEntityBySql(SqlElement sql) {
		Assert.notNull(sql);
		return new ServiceEntity(sql.getId(), sql.getValue(), sql.getCondition(), sql.getErrorcode(), sql.getErrormsg(), sql.getEach(),
				EntityType.sql).setAlwayarray(sql.isAlwayarray()).setLowercase(sql.isLowercase()).setGeneratedkeys(sql.getGeneratedkeys()).setCache(sql.getCache()).setUpdatecache(sql.getUpdatecache());
	}
	private ServiceEntity getEntityByExp(ExpElement exp) {
		Assert.notNull(exp);
		return new ServiceEntity(exp.getId(), exp.getValue(), exp.getCondition(), exp.getErrorcode(), exp.getErrormsg(), exp.getEach(),
				EntityType.exp).setProcessor(exp.getProcessor()).setFetch(exp.getFetch()).setAsync(exp.isAsync()).setExecEachInGs(false);
	}
	private ServiceEntity getEntityByImp(ImpElement imp) {
		Assert.notNull(imp);
		return new ServiceEntity(imp.getId(), imp.getValue(), imp.getCondition(), imp.getErrorcode(), imp.getErrormsg(), imp.getEach(),
				EntityType.imp).setProcessor(imp.getProcessor()).setFetch(imp.getFetch()).setAsync(imp.isAsync()).setExecEachInGs(false);
	}

	private ServiceEntity getEntityByRef(RefElement ref) {
		Assert.notNull(ref);
		if (!hasText(ref.getId())) {
			ref.setId("ref" + ref.getSequence());
			// continue;
		}
		ServiceEntity se = new ServiceEntity(ref.getId()).setCondition(ref.getCondition()).setErrorcode(ref.getErrorcode())
				.setErrormsg(ref.getErrormsg());
		if (hasText(ref.getSrv())) {
			se.setValue(ref.getSrv());
			se.setType(EntityType.service);
		} else if (hasText(ref.getClazz())) {
			se.setValue(ref.getClazz());
			se.setType(EntityType.clazz);
		}
		return se;
	}

	private ServiceEntity getEntityByQuit(QuitElement quit) {
		Assert.notNull(quit);
		return new ServiceEntity("quit" + quit.getSequence(), quit.getType(), quit.getCondition(), quit.getErrorcode(), quit.getErrormsg(), null,
				EntityType.quit);
	}
	
	protected ServiceConfig getServiceConfig() {
		Assert.hasText(serviceId);
		return ServiceConfigManager.getService(serviceId);
	}
}