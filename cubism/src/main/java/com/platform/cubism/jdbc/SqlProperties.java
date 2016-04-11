package com.platform.cubism.jdbc;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.jdbc.adapter.PageAdapter;
import com.platform.cubism.jdbc.adapter.PageAdapterManager;
import com.platform.cubism.service.convert.ConvertManager;
import com.platform.cubism.util.CubismHelper;

public abstract class SqlProperties {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected final static char OX1B = 0x1b;
	protected final static char OX1A = 0x1a;//替换
	private String id;
	private String sql;
	private String original;
	private String cache;//缓存名称
	private String updatecache;//需要更新的键值，多个以空白或者逗号分号隔开
	private String serviceId;//所属服务编号
	private String generatedkeys;
	private Json generatekeysvalue;
	private SqlType type;
	private boolean alwayarray;// 是否始终按照数组结构返回查询结果
	private boolean lowercase;
	private int maxCount = -1;
	private List<String> paramNames;//所有参数名称的集合，用于处理语句中的*
	private List<SqlParameter> inParam;
	private List<SqlParameter> outParam;
	private Pagination pagination = new Pagination();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		if(!hasText(this.original)){
			this.original = original;
		}
	}

	public boolean isAlwayarray() {
		return alwayarray;
	}

	public void setAlwayarray(boolean alwayarray) {
		this.alwayarray = alwayarray;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getUpdatecache() {
		return updatecache;
	}

	public void setUpdatecache(String updatecache) {
		this.updatecache = updatecache;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public boolean isLowercase() {
		return lowercase;
	}

	public void setLowercase(boolean lowercase) {
		this.lowercase = lowercase;
	}

	public String[] getGeneratedKeys() {
		if (!hasText(generatedkeys)) {
			return null;
		}
		String[] keys = generatedkeys.split(",|;|\\s");
		List<String> keyList = null;
		for (String s : keys) {
			if (hasText(s)) {
				if (keyList == null) {
					keyList = new ArrayList<String>();
				}
				keyList.add(s);
			}
		}
		if (keyList == null || keyList.isEmpty()) {
			return null;
		}
		return keyList.toArray(new String[0]);
	}

	public void setGeneratedkeys(String generatedkeys) {
		this.generatedkeys = generatedkeys;
	}

	public Json getGeneratekeysvalue() {
		return generatekeysvalue;
	}

	public void setGeneratekeysvalue(Json generatekeysvalue) {
		this.generatekeysvalue = generatekeysvalue;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	public String getSql() {
		return sql;
	}

	public String getPageSql() {
		PageAdapter adapter = PageAdapterManager.getPageAdapter();
		if (adapter == null) {
			return sql;
		}
		int startPage = Integer.parseInt(pagination.getCurrentpage());
		int pageCount = Integer.parseInt(pagination.getPagecount());
		int totalrecord = Integer.parseInt(pagination.getTotalrecord());

		return adapter.getPageSql(sql, startPage, pageCount, totalrecord);
	}

	public SqlType getType() {
		return type;
	}
	
	public List<String> getParamNames() {
		if (paramNames == null) {
			return Collections.emptyList();
		}
		return paramNames;
	}
	
	public List<String> addParamNames(String paramName) {
		if (this.paramNames == null) {
			this.paramNames = new ArrayList<String>();
		}
		this.paramNames.add(paramName);
		
		return paramNames;
	}
	
	public void parseStartParam(Json input){//将带*号的语句替换为输入字段的集合
		Pattern hasStartInUpdate = Pattern.compile("\\s+set\\s+("+OX1A+")[,\\s]", Pattern.CASE_INSENSITIVE);
		Pattern hasStartInInsert = Pattern.compile("\\s*insert\\s+.+\\(\\s*("+OX1A+")[,\\s\\)].*values[\\s\\(]", Pattern.CASE_INSENSITIVE);
		Pattern hasStartInInsertValues = Pattern.compile("[\\s\\)]values\\s*\\(\\s*("+OX1A+")[,\\s\\)]", Pattern.CASE_INSENSITIVE);
		
		if(input == null || input.isEmpty() || input.getObject() == null || input.getObject().isEmpty()){
			return;
		}
		CStruc in = input.getObject().getStruc(ConvertManager.IN);
		if(in == null || in.isEmpty()){
			return;
		}
		if(logger.isDebugEnabled()){
			if(paramNames != null){
				logger.debug("=====>>>paramNames:"+paramNames.toString());
			}
		}
		Matcher mtch = null;
		StringBuffer buf = null;
		switch (type) {
			case update:
				mtch = hasStartInUpdate.matcher(sql);
				if(mtch.find()){
					StringBuilder sb = new StringBuilder();
					String name = null,value = null;
					String[] fields = sql.split("=|\\s"); 
					boolean flag = false;
					for(Map.Entry<String, CField> fld : in.getField().entrySet()){
						name = fld.getKey();
						if(logger.isDebugEnabled()){
							logger.debug("=====>>>parseStartParam:"+name);
						}
						if(paramNames != null && (paramNames.contains(name) || paramNames.contains(ConvertManager.IN_PREFIX+name))){
							continue;
						}
						flag = false;
						for(String f : fields){
							if(f != null && f.trim().equalsIgnoreCase(name)){
								flag = true;
								break;
							}
						}
						if(flag){
							continue;
						}
						
						value = fld.getValue().getValue();
						if(value == null || value.length() <= 0){
							sb.append(name).append("=NULL,");
						}
						else{
							sb.append(name).append("='").append(value).append("',");
						}
					}
					if(sb.length() <= 0){
						return;
					}
					sb.deleteCharAt(sb.length()-1);//去掉最后一个逗号
					
					buf = new StringBuffer();
					mtch.appendReplacement(buf, mtch.group(0).replace(mtch.group(1), sb.toString()));
					mtch.appendTail(buf);					
					sql = buf.toString();
				}
				break;
			case insert:
				mtch = hasStartInInsert.matcher(sql);
				if(mtch.find()){
					StringBuilder keys = new StringBuilder();
					StringBuilder values = new StringBuilder();
					String name = null,value = null;
					String[] fields = sql.split(",|\\(|\\)"); 
					boolean flag = false;
					for(Map.Entry<String, CField> fld : in.getField().entrySet()){
						name = fld.getKey();
						if(logger.isDebugEnabled()){
							logger.debug("=====>>>parseStartParam:"+name);
						}
						if(paramNames != null && (paramNames.contains(name) || paramNames.contains(ConvertManager.IN_PREFIX+name))){
							continue;
						}
						flag = false;
						for(String f : fields){
							if(f != null && f.trim().equalsIgnoreCase(name)){
								flag = true;
								break;
							}
						}
						if(flag){
							continue;
						}
						
						keys.append(name).append(",");
						value = fld.getValue().getValue();
						if(value == null || value.length() <= 0){
							values.append("NULL,");
						}
						else{
							values.append("'").append(value).append("',");
						}
					}
					
					if(keys.length() <= 0){
						return;
					}
					keys.deleteCharAt(keys.length()-1);//去掉最后一个逗号
					values.deleteCharAt(values.length()-1);//去掉最后一个逗号
					
					buf = new StringBuffer();
					mtch.appendReplacement(buf, mtch.group(0).replace(mtch.group(1), keys.toString()));
					mtch.appendTail(buf);


					mtch = hasStartInInsertValues.matcher(buf.toString());
					if(mtch.find()){
						buf = new StringBuffer();
						mtch.appendReplacement(buf, mtch.group(0).replace(mtch.group(1), values.toString()));
						mtch.appendTail(buf);
						
						sql = buf.toString();
					}
				}
				break;
			default:
				break;
		}
	}
	
	public List<SqlParameter> getInParam() {
		if (inParam == null) {
			return Collections.emptyList();
		}
		return inParam;
	}
	public String inParamToString(){
		if (inParam == null || inParam.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(SqlParameter sp : inParam){
			sb.append(sp.toString()).append(";");
		}
		return sb.toString();
	}
	public boolean hasIn() {
		return getInParam() != null && !getInParam().isEmpty();
	}

	public boolean hasOut() {
		return getOutParam() != null && !getOutParam().isEmpty();
	}

	public boolean isBatch() {
		if (maxCount <= 0) {
			count();
		}
		return maxCount > 1;
	}

	private int count() {
		int sz;
		maxCount = 0;
		if (inParam == null) {
			return maxCount;
		}
		for (SqlParameter sp : inParam) {
			sz = sp.getSize();
			if (sz > maxCount) {
				maxCount = sz;
			}
		}
		return maxCount;
	}

	public int getCount() {
		if (maxCount <= 0) {
			count();
		}
		return maxCount;
	}

	public void flatParameter() {
		if (inParam == null || inParam.isEmpty()) {
			return;
		}
		Object obj;
		SqlParameter sp;
		int n = inParam.size();
		List<CField[][]> fList = new ArrayList<CField[][]>();
		CField[][] cfs = null;
		CField[] f;
		for (int i = 0; i < n; i++) {
			sp = inParam.get(i);
			obj = sp.getObjValue();
			if (obj == null) {
				return;
			}
			if (obj.getClass().isArray()) {
				if (CubismHelper.isAssignable(CField[].class, obj.getClass())) {
					CField[] cf = (CField[]) obj;
					int len = cf.length;
					cfs = new CField[len][];
					for (int j = 0; j < len; j++) {
						f = new CField[1];
						f[0] = cf[j];
						cfs[j] = f;
					}
				}
				if (CubismHelper.isAssignable(CStruc[].class, obj.getClass())) {
					CStruc[] ss = (CStruc[]) obj;
					int len = ss.length;
					cfs = new CField[len][];
					for (int j = 0; j < len; j++) {
						f = ss[j].flat();
						cfs[j] = f;
					}
				}
				if (CubismHelper.isAssignable(CArray[].class, obj.getClass())) {
					CArray[] aa = (CArray[]) obj;
					int len = aa.length;
					cfs = new CField[len][];
					for (int j = 0; j < len; j++) {
						f = aa[j].flat();
						cfs[j] = f;
					}
				}
			} else {
				if (CubismHelper.isAssignable(CField.class, obj.getClass())) {
					f = new CField[1];
					f[0] = ((CField) obj);
					cfs = new CField[1][];
					cfs[0] = f;
				}
				if (CubismHelper.isAssignable(CStruc.class, obj.getClass())) {
					f = ((CStruc) obj).flat();
					cfs = new CField[1][];
					cfs[0] = f;
				}
				if (CubismHelper.isAssignable(CArray.class, obj.getClass())) {
					f = ((CArray) obj).flat();
					cfs = new CField[1][];
					cfs[0] = f;
				}
			}
			if (cfs != null) {
				fList.add(cfs);
			}
		}

		if (fList == null || fList.isEmpty()) {
			return;
		}
		int maxrow = 0;
		for (CField[][] cf : fList) {
			if (cf.length > maxrow) {
				maxrow = cf.length;
			}
		}/*
		for (CField[][] cf : fList) {
			int len = cf.length;
			if (len < maxrow) {
				CField[][] fld = new CField[maxrow][];
				for (int i = 0; i < maxrow; i++) {
					if (i < len) {
						fld[i] = cf[i];
					} else {
						fld[i] = cf[len - 1];
					}
				}
				cf = fld;
			}
		}*/
		for(int m=0; m<fList.size(); m++){
			CField[][] cf = fList.get(m);
			int len = cf.length;
			if (len < maxrow) {
				CField[][] fld = new CField[maxrow][];
				for (int i = 0; i < maxrow; i++) {
					if (i < len) {
						fld[i] = cf[i];
					} else {
						fld[i] = cf[len - 1];
					}
				}
				fList.set(m, fld);
			}
		}

		int[] maxcol = new int[maxrow];
		for (CField[][] cf : fList) {
			int len = cf.length;
			for (int i = 0; i < len; i++) {
				if (cf[i].length > maxcol[i]) {
					maxcol[i] = cf[i].length;
				}
			}
		}
		for (CField[][] cf : fList) {
			int len = cf.length;
			for (int i = 0; i < len; i++) {
				int sz = cf[i].length;
				if (sz >= maxcol[i]) {
					continue;
				}
				CField[] fld = new CField[maxcol[i]];
				for (int j = 0; j < maxcol[i]; j++) {
					if (sz <= 0) {
						fld[j] = new CField();
					} else if (j < sz) {
						fld[j] = cf[i][j];
					} else {
						fld[j] = cf[i][sz - 1];
					}
				}
				cf[i] = fld;
			}
		}
		for (int i = 0; i < n; i++) {
			sp = inParam.get(i);
			sp.setValue(fList.get(i));
		}
	}

	public List<SqlParameter> getOutParam() {
		if (outParam == null) {
			return Collections.emptyList();
		}
		return outParam;
	}
	
	public void preProcess() {//预处理，将特殊功能的标识符进行转换，以便能正确查找替换
		Pattern hasStartInUpdate = Pattern.compile("\\s+set\\s+(\\*)[,\\s]", Pattern.CASE_INSENSITIVE);
		Pattern hasStartInInsert = Pattern.compile("\\s*insert\\s+.+\\(\\s*(\\*)[,\\s\\)].*\\s*values[\\s\\(]", Pattern.CASE_INSENSITIVE);
		Pattern hasStartInInsertValues = Pattern.compile("[\\s\\)]values\\s*\\(\\s*(\\*)[,\\s\\)]", Pattern.CASE_INSENSITIVE);
		
		Matcher mtch = null;
		StringBuffer buf = null;
		switch (type) {
			case update:
				mtch = hasStartInUpdate.matcher(sql);
				if(mtch.find()){
					buf = new StringBuffer();
					mtch.appendReplacement(buf, mtch.group(0).replace(mtch.group(1), ""+OX1A));
					mtch.appendTail(buf);					
					sql = buf.toString();
				}
				break;
			case insert:
				mtch = hasStartInInsert.matcher(sql);
				if(mtch.find()){
					buf = new StringBuffer();
					mtch.appendReplacement(buf, mtch.group(0).replace(mtch.group(1), ""+OX1A));
					mtch.appendTail(buf);					
					sql = buf.toString();
					
					mtch = hasStartInInsertValues.matcher(sql);
					if(mtch.find()){
						buf = new StringBuffer();
						mtch.appendReplacement(buf, mtch.group(0).replace(mtch.group(1), ""+OX1A));
						mtch.appendTail(buf);					
						sql = buf.toString();
					}
				}
				break;
			default:
				break;
		}
	}
	
	public void setSql(String sql) {
		if (sql.indexOf(OX1B) != -1) {
			this.sql = sql.replaceAll(String.valueOf(OX1B), "''");
		} else {
			this.sql = sql;
		}

		setType();
	}

	private void setType() {
		if (!hasText(sql)) {
			this.type = SqlType.unknown;
			return;
		}
		String s = sql.toLowerCase();
		if (s.matches(".*\\b"+SqlType.insert.toString()+"\\b.*")) {
			this.type = SqlType.insert;
		} else if (s.matches(".*\\b"+SqlType.update.toString()+"\\b.*")) {
			this.type = SqlType.update;
		} else if (s.matches(".*\\b"+SqlType.delete.toString()+"\\b.*")) {
			this.type = SqlType.delete;
		} else if (s.matches("^\\s*begin.+end.*$") || s.matches("^\\s*\\{\\s*call.+\\s*\\}\\s*$")) {
			this.type = SqlType.call;
		} else if (s.matches(".*\\b"+SqlType.select.toString()+"\\b.*")) {
			this.type = SqlType.select;
		} else {
			this.type = SqlType.unknown;
		}
	}

	public void addInParam(SqlParameter inParam) {
		if (this.inParam == null) {
			this.inParam = new ArrayList<SqlParameter>();
		}
		this.inParam.add(inParam);
	}

	public void addOutParam(SqlParameter outParam) {
		if (this.outParam == null) {
			this.outParam = new ArrayList<SqlParameter>();
		}
		this.outParam.add(outParam);
	}

	public boolean isStaticSql() {
		if ((this.outParam == null || this.outParam.isEmpty()) && (this.inParam == null || this.inParam.isEmpty())) {
			return true;
		}

		return false;
	}

	public boolean hasGeneratedKeys() {
		return hasText(generatedkeys);
	}
	public boolean hasNextPage(){
		if(this.type != SqlType.select){
			return false;
		}
		return this.pagination.hasMore();
	}
	/**************************************************************************************************/
	public static class Pagination {
		private String currentpage;// 当前页码
		private String totalpage;// 总页数
		private String totalrecord;// 符合条件的总记录数
		private String pagecount;// 每页记录数
		private boolean isPagination;// 是否需要进行分页查询
		private String paginationCountSql;// 分页查询时统计总记录数的查询语句

		private void init() {
			if (!hasText(currentpage)) {
				currentpage = "0";
			}
			if (!hasText(totalpage)) {
				totalpage = "0";
			}
			if (!hasText(totalrecord)) {
				totalrecord = "0";
			}
			if (!hasText(pagecount)) {
				pagecount = "10";
			}
		}

		public boolean isPagination() {
			return isPagination;
		}

		public void setPagination(boolean isPagination) {
			this.isPagination = isPagination;
		}

		public String getPaginationCountSql() {
			return paginationCountSql;
		}

		public void setPaginationCountSql(String paginationCountSql) {
			this.paginationCountSql = paginationCountSql;
		}

		public String getCurrentpage() {
			return currentpage;
		}

		public void setCurrentpage(String currentpage) {
			this.currentpage = currentpage;
		}

		public String getTotalpage() {
			return totalpage;
		}

		public String getTotalrecord() {
			if (!hasText(totalrecord) || Integer.parseInt(totalrecord) < 1) {
				return String.valueOf(Integer.parseInt(totalpage) * Integer.parseInt(pagecount));
			}
			return totalrecord;
		}

		public void setTotalpage(String totalpage) {
			this.totalpage = totalpage;
		}

		public void setTotalrecord(String totalrecord) {
			this.totalrecord = totalrecord;
			this.totalpage = String.valueOf((Integer.parseInt(this.totalrecord)+Integer.parseInt(this.pagecount)-1)/Integer.parseInt(this.pagecount));
		}

		public String getPagecount() {
			return pagecount;
		}

		public void setPagecount(String pagecount) {
			this.pagecount = pagecount;
		}

		public boolean calcTotalrecord() {// 是否需要查询总记录数，当当前总记录数小于一时需要重新计算总记录数
			if(hasMore()){
				return false;
			}
			if (hasText(totalrecord) && Integer.parseInt(totalrecord) > 1
					&& Integer.parseInt(currentpage) * Integer.parseInt(pagecount) < Integer.parseInt(totalrecord)) {
				return false;
			}
			return true;
		}

		public boolean hasMore() {// 当前是否还有符合条件的记录，通过总记录数和当前请求的页码来计算
			init();
			int currp = Integer.parseInt(currentpage);
			int count = Integer.parseInt(pagecount);
			int ctotal = (currp - 1) * count;
			int rtotal = Integer.parseInt(totalrecord);
			
			if (rtotal < 1) {
				return false;
			}
			if (ctotal >= rtotal) {
				return false;
			}
			return true;
		}
		public int nextPage() {
			if(hasMore()){
				int currp = Integer.parseInt(currentpage);
				currp++;
				currentpage = String.valueOf(currp);
				return currp;
			}
			return -1;
		}
		public boolean isOverPage() {// 当前查询结果是否超出一页的数据
			init();
			int currp = Integer.parseInt(currentpage);
			int count = Integer.parseInt(pagecount);
			int ctotal = currp * count;
			int rtotal = Integer.parseInt(totalrecord);
			if (currp > 1) {
				return true;
			}
			if (rtotal >= ctotal) {
				return true;
			}
			return false;
		}
	}
}