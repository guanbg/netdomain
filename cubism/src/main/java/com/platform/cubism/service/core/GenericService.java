package com.platform.cubism.service.core;

import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.service.convert.ConvertManager.IN;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.service.config.ServiceConfigManager;
import com.platform.cubism.service.convert.ConvertManager;
import com.platform.cubism.struc.PageHead;
import com.platform.cubism.util.CubismHelper;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.jdbc.tran.Transaction;
import com.platform.cubism.jdbc.conn.DataSourceManager;
import com.platform.cubism.jdbc.tran.TransactionStatus;
import com.platform.cubism.jdbc.tran.DataSourceTransaction;

public class GenericService extends AbstractService {
	public GenericService(String serviceId) {
		super(serviceId);
	}
	
	protected void commit(Transaction tran,TransactionStatus status){
		if (logger.isDebugEnabled()) {
			logger.debug("GenericService========>>提交数据库事务");
		}
		if (tran != null) {
			tran.commit(status);
			tran = null;
			status = null;
		}
	}
	private void rollback(Transaction tran,TransactionStatus status){
		if (logger.isDebugEnabled()) {
			logger.debug("GenericService========>>回滚数据库事务");
		}
		if (tran != null) {
			tran.rollback(status);
			tran = null;
			status = null;
		}
	}
	
	@Override
	public Json perform(Json in) throws Exception {
		ServiceEntity[] entity = getServiceEntity();
		if (entity == null || entity.length <= 0) {
			return null;
		}
		Transaction tran = null;
		TransactionStatus status = null;
		Json ret = null;
		EntityExecutor entityExecutor = null;
		Json result = JsonFactory.create();
		result.mergeOf(in);
		for (ServiceEntity se : entity) {
			if (se == null) {
				continue;
			}
			if (isEachEntity(se)) {
				continue;
			}
			try {
				if (hasText(se.getCondition())) {
					if (!ConditionFactory.isTrue(se.getCondition(), result)) {
						continue;
					}
				}
				entityExecutor = EntityFactory.getExecutor(se);
				if (entityExecutor == null) {
					continue;
				}
				setCurrentServiceEntity(se);// 当前正在执行的服务实体
				if (hasSqlElement() && tran == null) {
					logger.debug("GenericService/DataSourceManager/getDataSource========>>"+se.getDatasource());
					tran = new DataSourceTransaction().setDataSource(DataSourceManager.getDataSource(se.getDatasource()));
					status = tran.getTransaction(null);
				}
				if (hasSqlElement() && status != null) {
					entityExecutor.setTransactionStatus(status);
				}
				ret = entityExecutor.execute(result);
				if(se.isExecEachInGs()){
					forEach(result, ret, status);
				}
			} catch (Throwable e) {
				rollback(tran,status);
				if (logger.isInfoEnabled()) {
					logger.info(se.getId() + "执行失败：" + e.getMessage());
				}
				if (ret != null) {
					result.mergeOf(ret);
				}
				in.mergeOf(result);
				entityExecutor.failure(in);
				throw new Exception(e);
			}
			if (ret != null) {
				if(se.getType() == EntityType.service){
					ret.setName(se.getId());
					result.addStruc(ret.getObject());
				}else if(se.getType() == EntityType.clazz){
					ret.setName(se.getId());
					result.addStruc(ret.getObject());
				}
				else{
					result.mergeOf(ret);
				}
			}

			if (HeadHelper.getRetStatus(ret) != null && !HeadHelper.isSuccess(ret)) {// 服务执行失败，后继服务终止执行
				rollback(tran,status);
				entityExecutor.failure(in);
				return result.mergeOf(ret);
			}
		}
		commit(tran, status);
		if(entityExecutor != null){
			entityExecutor.success(in);
		}
		return result;
	}

	@Override
	protected Json validate(Json in) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isEachEntity(ServiceEntity se) {
		for (ServiceEntity s : getServiceEntity()) {
			if(s == null){
				continue;
			}
			String each = s.getEach();
			if (!hasText(each)) {
				continue;
			}
			String[] ids = each.split("\\s*,\\s*|\\s*;\\s*|\\s");
			for (String id : ids) {
				if (se.getId().equalsIgnoreCase(id)) {
					return true;
				}
			}
		}
		return false;
	}

	private void forEach(Json result, Json ret, TransactionStatus status) throws Exception {
		ServiceEntity cse = getCurrentServiceEntity();
		String each = cse.getEach();
		if (!hasText(each)) {
			return;
		}
		if(ret == null){
			ret = JsonFactory.create();
		}
		CField fld = ret.getField(getSafeId(cse.getId()));
		CStruc stc = ret.getStruc(getSafeId(cse.getId()));
		CArray arr = ret.getArray(getSafeId(cse.getId()));
		
		if(stc != null && !stc.isEmpty() && stc.containsName(cse.getId()) && arr == null){//存储过程返回的结果集会外包一层结构，内层才是数组
			arr = stc.getArray(getSafeId(cse.getId()));
		}
		
		Json in;
		if(fld == null && stc == null && arr == null){
			return;
		}
		Json resultCopy = CubismHelper.deepCopy(result);
		String[] ids = each.split("\\s*,\\s*|\\s*;\\s*|\\s");
		
		if (arr != null) {
			for (CStruc s : arr.getRecords()) {
				for (String id : ids) {
					if (!hasText(id)) {
						continue;
					}
					CStruc s1 = CubismHelper.deepCopy(s);
					in = JsonFactory.create();
					in.mergeOf(resultCopy);
					in.getObject().mergeOf(s1);
					
					CStruc cs = in.getStruc(IN);
					if(cs == null){
						in.addStruc(s1.setName(IN));
					}
					else{
						in.getObject().mergeOf(cs);
						cs.mergeOf(s1);
					}
					
					if(!in.getObject().containsName(arr.getName())){
						CStruc s2 = new CStruc().copyOf(s1).setName(arr.getName());
						in.addStruc(s2);
					}
					Json rt = executeEach(id, in, s, status);
					if(rt != null && !rt.isEmpty()){
						resultCopy.mergeOf(rt);
					}
				}
				resultCopy = CubismHelper.deepCopy(result);
			}
		} else {
			for (String id : ids) {
				if (!hasText(id)) {
					continue;
				}
				if (fld != null) {
					in = JsonFactory.create().addField(CubismHelper.deepCopy(fld));
					in.addOf(resultCopy);
					Json rt = executeEach(id, in, ret.getObject(), status);
					if(rt != null && !rt.isEmpty()){
						resultCopy.mergeOf(rt);
					}
				} else if (stc != null) {
					CStruc s1 = CubismHelper.deepCopy(stc);
					in = JsonFactory.create();
					in.mergeOf(resultCopy);
					in.getObject().mergeOf(s1);
					
					CStruc cs = in.getStruc(IN);
					if(cs == null){
						in.addStruc(s1.setName(IN));
					}
					else{
						in.getObject().mergeOf(cs);
						cs.mergeOf(s1);
					}
					
					if(!in.getObject().containsName(stc.getName())){
						CStruc s2 = new CStruc().copyOf(s1).setName(stc.getName());
						in.addStruc(s2);
					}
					Json rt = executeEach(id, in, stc, status);
					if(rt != null && !rt.isEmpty()){
						resultCopy.mergeOf(rt);
					}
				} else {
					in = CubismHelper.deepCopy(ret);
					Json rt = executeEach(id, in, ret.getObject(), status);
					if(rt != null && !rt.isEmpty()){
						resultCopy.mergeOf(rt);
					}
				}
			}
		}
	}

	private Json executeEach(String id, Json in, CStruc ret, TransactionStatus status) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("executeEach========>>"+in);
		}
		if(in.contains(PageHead.PAGEHEAD.value())){
			in.remove(PageHead.PAGEHEAD.value());
			if(in.contains(ConvertManager.IN_PREFIX + PageHead.PAGEHEAD.value())){
				in.remove(ConvertManager.IN_PREFIX + PageHead.PAGEHEAD.value());
			}
			logger.debug("executeEach no pagehead========>>"+in);
		}
		
		Json rt = null;
		ServiceEntity se = getServiceEntity(id);
		if (se != null) {
			if (hasText(se.getCondition())) {
				if (!ConditionFactory.isTrue(se.getCondition(), in)) {
					return rt;
				}
			}
			EntityExecutor entityExecutor = EntityFactory.getExecutor(se);
			if (entityExecutor == null) {
				return rt;
			}
			if (hasSqlElement() && status != null) {
				entityExecutor.setTransactionStatus(status);
			}
			rt = entityExecutor.execute(in);
			if (rt != null) {
				if (rt.getField(getSafeId(id)) != null) {
					ret.addField(rt.getField(getSafeId(id)));
				} else if (rt.getStruc(getSafeId(id)) != null) {
					ret.addStruc(rt.getStruc(getSafeId(id)));
				} else if (rt.getArray(getSafeId(id)) != null) {
					//ret.addArray(rt.getArray(getSafeId(id)));
					ret.mergeOf(rt.getObject());//返回多个结果集
				}
			}
		} else if (ServiceConfigManager.hasService(id)) {
			rt = ServiceFactory.executeService(id, in);
			if (HeadHelper.getRetStatus(rt) != null && HeadHelper.isSuccess(rt)) {
				ret.mergeOf(rt.getObject());
			}
		}
		
		return rt;
	}
}