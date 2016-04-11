package com.platform.cubism.jdbc;

import static com.platform.cubism.util.StringUtils.hasText;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.struc.PageHead;
import com.platform.cubism.util.CubismHelper;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.util.StringUtils;

public abstract class AbstractSqlExecutor extends SqlProperties implements SqlExecutor {
	private static boolean hascall = false;
	private static final Pattern hasLike = Pattern.compile("\\s*like\\s*\'", Pattern.CASE_INSENSITIVE);

	private static final String inClobParamPrefix = "@{";//clob blob
	private static final String inClobParamSuffix = "}";
	
	private static final String inBlobParamPrefix = "^{";//blob
	private static final String inBlobParamSuffix = "}";

	private static final String inStaticParamPrefix = "#{";//STR_TO_DATE('#{in.start_time.start}','%Y-%c-%d')没送则去掉函数
	private static final String inStaticParamSuffix = "}";
	
	private static final String inStaticParamPrefix2 = "#[";//STR_TO_DATE('#{in.start_time.start}','%Y-%c-%d')没送则保留函数
	private static final String inStaticParamSuffix2 = "]";

	private static final String inDynamicParamPrefix = "?{";//STR_TO_DATE('#{in.start_time.start}','%Y-%c-%d')没送则去掉函数
	private static final String inDynamicParamSuffix = "}";

	private static final String inDynamicParamPrefix2 = "?[";//STR_TO_DATE('#{in.start_time.start}','%Y-%c-%d')没送则保留函数
	private static final String inDynamicParamSuffix2 = "]";

	private static final String outParamPrefix = "${";
	private static final String outParamSuffix = "}";

	private static final String inoutStaticParamPrefix = "#${";
	private static final String inoutStaticParamSuffix = "}";

	private static final String inoutDynamicParamPrefix = "?${";
	private static final String inoutDynamicParamSuffix = "}";

	public Json execute(String sql, Json in) throws SQLException {
		parseSql(sql, in);

		switch (getType()) {
		case select:
			getPagination().setPagination(HeadHelper.isPagination(getSql(), in));
			if (getPagination().isPagination()) {
				getPagination().setPaginationCountSql(HeadHelper.getPaginationCount(getSql(), in));
				getPagination().setCurrentpage(HeadHelper.getPageHead(PageHead.CURRENTPAGE, in));
				getPagination().setPagecount(HeadHelper.getPageHead(PageHead.PAGECOUNT, in));
				getPagination().setTotalpage(HeadHelper.getPageHead(PageHead.TOTALPAGE, in));
				getPagination().setTotalrecord(HeadHelper.getPageHead(PageHead.TOTALRECORD, in));
			}
			return executeQuery();
		case insert:
		case update:
		case delete:
			return executeUpdate();
		case call:
			return call();
		default:
			return executeUpdate();
		}
	}
	
	public Json nextPage() throws SQLException{
		if(getType() != SqlType.select || !hasNextPage() || !hasText(getOriginal())){
			return JsonFactory.create();
		}
		getPagination().nextPage();
		setSql(getOriginal());
		return executeQuery();
	}
	protected abstract Json executeQuery() throws SQLException;

	protected abstract Json executeUpdate() throws SQLException;

	protected abstract Json call() throws SQLException;

	private void parseSql(String sql, Json in) {
		if (logger.isDebugEnabled()) {
			logger.debug("解析时的数据：" + in);
			logger.debug("需要解析的SQL：" + sql);
		}
		if (!hasText(sql)) {
			return;
		}
		setSql(sql);
		preProcess();//sql语句预处理
		hascall = getType() == SqlType.call;
		String newsql = new TokenParser(new inHandler(in), 
				new String[] { inClobParamPrefix, inClobParamSuffix },
				new String[] { inBlobParamPrefix, inBlobParamSuffix },
				new String[] { inoutStaticParamPrefix, inoutStaticParamSuffix }, 
				new String[] { inoutDynamicParamPrefix, inoutDynamicParamSuffix }, 
				new String[] { inStaticParamPrefix, inStaticParamSuffix }, 
				new String[] { inDynamicParamPrefix, inDynamicParamSuffix }, 
				new String[] { inStaticParamPrefix2, inStaticParamSuffix2 }, 
				new String[] { inDynamicParamPrefix2, inDynamicParamSuffix2 }, 
				new String[] { outParamPrefix, outParamSuffix }).parse2(getSql());
		//setSql(SqlTrim.getInstance().trim(newsql));
		setSql(newsql);
		parseStartParam(in);//处理带*号的语句，根据输入字段将*号替代
		hascall = getType() == SqlType.call;
		if (logger.isDebugEnabled()) {
			logger.debug("解析后的SQL：");
			logger.debug(getSql());
		}
		flatParameter();
	}

	class inHandler extends AbstractTokenHandler {
		private int idx = 1;// 动态参数索引号从1开始
		private Json in;

		public inHandler(Json in) {
			this.in = in;
		}

		public String handleToken(String before, String content, String after, String openToken, String closeToken) {
			if (!hasText(content)) {
				return "";
			}
			if (!inClobParamPrefix.equals(openToken) && !inBlobParamPrefix.equals(openToken) && !inStaticParamPrefix.equals(openToken) && 
				!inStaticParamPrefix2.equals(openToken) && !inDynamicParamPrefix2.equals(openToken) && 
				!outParamPrefix.equals(openToken) && !inoutStaticParamPrefix.equals(openToken) && 
				!inoutDynamicParamPrefix.equals(openToken) && !inDynamicParamPrefix.equals(openToken)) {
				return content;
			}
			
			if (outParamPrefix.equals(openToken) || inoutStaticParamPrefix.equals(openToken) || inoutDynamicParamPrefix.equals(openToken)) {
				SqlParameter sp = new SqlParameter(idx);
				sp.setName(content);
				addOutParam(sp);
				if (hascall && outParamPrefix.equals(openToken)) {
					idx++;
					return "?";
				}
			}
			
			addParamNames(content);//保存参数名称，以便处理带*号的插入或修改字段
			
			Object obj = in.get(content);
			if (obj == null) {// 上送报文中没有此字段
				if (inClobParamPrefix.equals(openToken)) {
					return "";
				} else if (inBlobParamPrefix.equals(openToken)) {
					return "";
				} else if (inStaticParamPrefix.equals(openToken)) {
					return "";
				} else if (inDynamicParamPrefix.equals(openToken)) {
					return "";
				} else if (inStaticParamPrefix2.equals(openToken)) {//是否去掉外网函数
					return null;
				} else if (inDynamicParamPrefix2.equals(openToken)) {//是否去掉外网函数
					return null;
				} else {
					addInParam(new SqlParameter(idx));
					idx++;
					return "?";
				}
			}

			CField[] fld = null;
			if (obj.getClass().isArray()) {
				if (CubismHelper.isAssignable(CField[].class, obj.getClass())) {
					fld = (CField[]) obj;
				}
				if (CubismHelper.isAssignable(CStruc[].class, obj.getClass())) {
					List<CField> fList = new ArrayList<CField>();
					CStruc[] ss = (CStruc[]) obj;
					for (CStruc s : ss) {
						fList.addAll(Arrays.asList(s.flat()));
					}
					fld = fList.toArray(new CField[0]);
				}
				if (CubismHelper.isAssignable(CArray[].class, obj.getClass())) {
					List<CField> fList = new ArrayList<CField>();
					CArray[] aa = (CArray[]) obj;
					for (CArray a : aa) {
						fList.addAll(Arrays.asList(a.flat()));
					}
					fld = fList.toArray(new CField[0]);
				}
			} else {
				if (CubismHelper.isAssignable(CField.class, obj.getClass())) {
					fld = new CField[1];
					fld[0] = ((CField) obj);
				}
				if (CubismHelper.isAssignable(CStruc.class, obj.getClass())) {
					fld = ((CStruc) obj).flat();
				}
				if (CubismHelper.isAssignable(CArray.class, obj.getClass())) {
					fld = ((CArray) obj).flat();
				}
			}

			if (fld == null) {
				if (inClobParamPrefix.equals(openToken)) {
					return null;// 字段上送值为空
				} else if (inBlobParamPrefix.equals(openToken)) {
					return null;// 字段上送值为空
				} else if (inStaticParamPrefix.equals(openToken)) {
					return null;// 字段上送值为空
				} else if (inDynamicParamPrefix.equals(openToken)) {
					return "null";
				} else {
					addInParam(new SqlParameter(idx));
					idx++;
					return "?";
				}
			}
			if(isInKey(before)){//处理sql 语句中的 in
				boolean hasQuotes = before.endsWith("'");
				StringBuffer sb = new StringBuffer();
				String s = null;
				for(int i=0; i<fld.length; i++){
					s = fld[i].getValue();
					if(s == null || s.length() <= 0){
						sb.append("NULL");
					}
					else if(s.indexOf(",") != -1){//本身就已经用逗号分隔好了，则不加引号
						sb.append(s);
					}
					else{
						if(hasQuotes && fld.length == 1){
							sb.append(s);
						}
						else if(hasQuotes && i == 0){
							sb.append(s).append("'");
						}
						else if(hasQuotes && i == fld.length-1){
							sb.append("'").append(s);
						}
						else{
							sb.append("'").append(s).append("'");
						}
					}
					if(i != fld.length-1){
						sb.append(",");
					}
				}
				return sb.toString();
			}
			if (inStaticParamPrefix.equals(openToken) && fld.length == 1) {
				String s = fld[0].getValue();
				if (hasText(s)) {
					if (s.indexOf('\'') != -1) {
						s = s.replace('\'', OX1B);
					}
					if (isLike(before)) {
						return StringUtils.escapeSql(s);
					} else {
						return s;
					}
				} else {
					return null;// 字段上送值为空
				}
			}
			if (inStaticParamPrefix2.equals(openToken) && fld.length == 1) {
				String s = fld[0].getValue();
				if (hasText(s)) {
					if (s.indexOf('\'') != -1) {
						s = s.replace('\'', OX1B);
					}
					if (isLike(before)) {
						return StringUtils.escapeSql(s);
					} else {
						return s;
					}
				}
			}
			if (inClobParamPrefix.equals(openToken)){
				addInParam(new SqlParameter(idx).setType(9).setObjValue(obj));
			} 
			else if (inBlobParamPrefix.equals(openToken)){
				addInParam(new SqlParameter(idx).setType(10).setObjValue(obj));
			} 
			else{
				addInParam(new SqlParameter(idx).setObjValue(obj));
			}
			
			idx++;
			return "?";
		}

		@Override
		public String processNullValue(String text, String after, int start, int end, StringBuilder sb) {
			SqlType sType = getType(after.substring(0, start));
			if (sType == SqlType.unknown) {
				sType = getType(text.substring(0, text.length() - after.length()),false);
			}
			switch (sType) {
			case where:
				return processEmptyValue(text, after, start, end, sb);
			case values:
			case insert:
			case update:
			case call:
			case set:
				return super.processNullValue(text, after, start, end, sb);
			default:
				break;
			}
			return processEmptyValue(text, after, start, end, sb);
		}
		private boolean isInKey(String sql){
			Pattern pattern = Pattern.compile("^.*\\s+in\\s*\\(\\s*[']*$", Pattern.CASE_INSENSITIVE);
			return pattern.matcher(sql).matches();
		}
		private boolean isLike(String sql) {
			if (!hasText(sql)) {
				return false;
			}
			if (hasLike.matcher(sql).find()) {
				return true;
			}
			return false;
		}

		private SqlType getType(String sql) {
			return getType(sql, false);
		}

		private SqlType getType(String sql, boolean isasc) {
			if (!hasText(sql)) {
				return SqlType.unknown;
			}
			String s = sql.toLowerCase();
			int[] idx = new int[8];
			if (isasc) {
				idx[0] = s.indexOf(SqlType.insert.toString());
				idx[1] = s.indexOf(SqlType.update.toString());
				idx[2] = s.indexOf(SqlType.delete.toString());
				idx[3] = s.indexOf(SqlType.call.toString());
				idx[4] = s.indexOf(SqlType.select.toString());
				idx[5] = s.indexOf(SqlType.where.toString());
				idx[6] = s.indexOf(SqlType.values.toString());
				idx[7] = s.indexOf(SqlType.set.toString());
			} else {
				idx[0] = s.lastIndexOf(SqlType.insert.toString());
				idx[1] = s.lastIndexOf(SqlType.update.toString());
				idx[2] = s.lastIndexOf(SqlType.delete.toString());
				idx[3] = s.lastIndexOf(SqlType.call.toString());
				idx[4] = s.lastIndexOf(SqlType.select.toString());
				idx[5] = s.lastIndexOf(SqlType.where.toString());
				idx[6] = s.lastIndexOf(SqlType.values.toString());
				idx[7] = s.lastIndexOf(SqlType.set.toString());
			}

			if (isMax(0, idx)) {
				return SqlType.insert;
			} else if (isMax(1, idx)) {
				return SqlType.update;
			} else if (isMax(2, idx)) {
				return SqlType.delete;
			} else if (isMax(3, idx)) {
				return SqlType.call;
			} else if (isMax(4, idx)) {
				return SqlType.select;
			} else if (isMax(5, idx)) {
				return SqlType.where;
			} else if (isMax(6, idx)) {
				return SqlType.values;
			} else if (isMax(7, idx)) {
				return SqlType.set;
			} else {
				return SqlType.unknown;
			}
		}

		private boolean isMax(int idx, int[] data) {
			if (data[idx] < 0) {
				return false;
			}
			for (int i = 0; i < data.length; i++) {
				if (i == idx) {
					continue;
				}
				if (data[idx] < data[i]) {
					return false;
				}
			}
			return true;
		}
	}
}