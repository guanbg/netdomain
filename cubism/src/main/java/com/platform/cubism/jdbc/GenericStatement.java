package com.platform.cubism.jdbc;

import static com.platform.cubism.util.StringUtils.hasText;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.ServiceCache;
import com.platform.cubism.struc.PageHead;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.util.StringUtils;

public class GenericStatement extends AbstractStatement {
	@Override
	protected Json executeQuery() throws SQLException {
		long startTime = System.currentTimeMillis();
		if (isAlwayarray() && getPagination().isPagination()) {//当只查询 单条时，不需要分页统计
			if (getPagination().calcTotalrecord()) {
				boolean alwayarray = isAlwayarray();
				setAlwayarray(false);
				Json page = null;
				if (isStaticSql()) {
					page = queryForStatement(getPagination().getPaginationCountSql());
				} else {
					page = queryForPreparedStatement(getPagination().getPaginationCountSql());
				}
				setAlwayarray(alwayarray);
				if (page != null) {
					getPagination().setTotalrecord(page.getField(getId() + "." + PageHead.TOTALRECORD.value()).getValue());
				}
			}
			if (!getPagination().hasMore()) {// 无数据
				return JsonFactory.create();
			}
			if (getPagination().isOverPage()) {// 是否需要分页
				setOriginal(getSql());
				setSql(getPageSql());
				if (logger.isDebugEnabled()) {
					logger.debug("分页后的查询语句：" + getSql());
				}
			}
		}
		Json result;
		if (isStaticSql()) {
			result = queryForStatement();
		} else {
			result = queryForPreparedStatement();
		}
		if (result == null) {
			result = JsonFactory.create();
		}
		if (getPagination().isPagination()) {
			result.addStruc(HeadHelper.createPageHead(getPagination()));
		}
		if(logger.isInfoEnabled()){
			long processingTime = System.currentTimeMillis() - startTime;
			logger.info("===>"+getId()+":executeQuery total spend time:" + processingTime + "ms(毫秒)");
		}
			
		return result;
	}

	@Override
	protected Json executeUpdate() throws SQLException {
		long startTime = System.currentTimeMillis();
		int[] ret = null;
		if (isStaticSql()) {
			ret = new int[1];
			if (getGeneratedKeys() == null) {
				ret[0] = updateForStatement();
			} else {
				ret[0] = updateForStatementWithKeys();
			}
		} else {
			ret = updateForPreparedStatement();
		}

		if (ret == null) {
			if(logger.isInfoEnabled()){
				long processingTime = System.currentTimeMillis() - startTime;
				logger.info("===>"+getId()+":executeUpdate total spend time:" + processingTime + "ms(毫秒)");
			}
			if(hasText(getUpdatecache())) ServiceCache.clearServiceCache(getUpdatecache());
			return getGeneratekeysvalue();
		}
		int successfully = 0;
		int failed = 0;
		for (int i : ret) {
			if (i >= 0) {
				successfully += i;
			} else {
				failed += i;
			}
		}
		Json json = JsonFactory.create();
		Json keys = getGeneratekeysvalue();
		if (keys == null) {
			if (failed < 0) {
				json.addField(getId(), String.valueOf(failed));
			} else {
				json.addField(getId(), String.valueOf(successfully));
			}
		} else {
			CStruc cs = keys.getStruc(getId());
			CArray ca = keys.getArray(getId());
			if (cs == null && ca == null) {
				if (failed < 0) {
					keys.addField(getId(), String.valueOf(failed));
				} else {
					keys.addField(getId(), String.valueOf(successfully));
				}
			}
			else {
				if (failed < 0) {
					keys.addField(getId() + "_f", String.valueOf(failed));
				} else {
					keys.addField(getId() + "_s", String.valueOf(successfully));
				}
			}
			
			json = keys;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("executeUpdate返回结果：" + json);
		}
		if(logger.isInfoEnabled()){
			long processingTime = System.currentTimeMillis() - startTime;
			logger.info("===>"+getId()+":executeUpdate total spend time:" + processingTime + "ms(毫秒)");
		}
		if(HeadHelper.isSuccess(json)){
			if(hasText(getUpdatecache())) ServiceCache.clearServiceCache(getUpdatecache());
		}
		return json;
	}

	@Override
	protected Json call() throws SQLException {
		Json json =  executeForCallableStatement();
		if(HeadHelper.isSuccess(json)){
			if(hasText(getUpdatecache())) ServiceCache.clearServiceCache(getUpdatecache());
		}
		
		return json;
	}

	private Json queryForStatement() throws SQLException {
		return queryForStatement(getSql());
	}

	private Json queryForStatement(String sql) throws SQLException {
		if (!hasText(sql)) {
			return null;
		}
		if(hasText(getCache())){
			Json cache = ServiceCache.getSqlCache(getCache(), getServiceId()+"==>"+getId()+"==>"+sql);
			if(cache != null && !cache.isEmpty()){
				return cache;
			}
		}
		Connection conn = getConn();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if (supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
				stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			} else {
				stmt = conn.createStatement();
			}
			if (logger.isDebugEnabled()) {
				logger.debug("执行语句：" + sql);
			}
			rs = stmt.executeQuery(sql);
			Json ret = JsonFactory.create().toJson(rs, getId(), isAlwayarray(),isLowercase());
			if (logger.isDebugEnabled()) {
				logger.debug("返回结果：" + ret);
			}
			if(ret != null && !ret.isEmpty()){
				if(hasText(getCache())) ServiceCache.setSqlCache(getCache(), getServiceId()+"==>"+getId()+"==>"+sql, ret);
				if(hasText(getUpdatecache())) ServiceCache.clearServiceCache(getUpdatecache());
			}
			return ret;
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error(getId() + " 查询执行错误：" + e.getMessage());
				logger.error("SQL：" + sql);
			}
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {
					;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					;
				}
			}
		}
	}
	private Json queryForPreparedStatement() throws SQLException {
		return queryForPreparedStatement(getSql());
	}
	private Json queryForPreparedStatement(String sql) throws SQLException {
		if (!hasIn()) {
			return null;
		}
		if(hasText(getCache())){
			Json cache = ServiceCache.getSqlCache(getCache(), getServiceId()+"==>"+getId()+"==>"+sql+"==>"+inParamToString());
			if(cache != null && !cache.isEmpty()){
				return cache;
			}
		}
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = getConn();
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("执行语句：" + sql);
			}
			if (supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
				ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			} else {
				ps = conn.prepareStatement(sql);
			}
			Json json = null;
			for (int i = 0; i < getCount(); i++) {
				try {
					ps.clearParameters();
					for (SqlParameter sp : getInParam()) {
						if (logger.isDebugEnabled()) {
							logger.debug("==>" + sp.getIndex() + ":" + sp.getValue(i));
						}
						ps.setObject(sp.getIndex(), sp.getValue(i));
					}
					rs = ps.executeQuery();
					//Json jsn = JsonFactory.create().toJson(rs, getId());
					Json jsn = JsonFactory.create().toJson(rs, getId(), isAlwayarray(), isLowercase());
					if (logger.isDebugEnabled()) {
						logger.debug("返回结果：" + jsn);
					}
					if (json == null) {
						json = jsn;
					} else {
						CStruc cs = json.getObject().removeStruc(getId());
						if (cs != null) {
							json.addArray(getId(), cs);
						}
						CArray ca = json.getArray(getId());
						if (ca != null) {
							ca.add(jsn.getArray(getId()));// 这两个里面总只会执行其中的一个
							ca.add(jsn.getStruc(getId()));// 这两个里面总只会执行其中的一个
						} else {// cs == null && ca ==null
							json = jsn;
						}
					}
				} catch (SQLException e) {
					if (logger.isErrorEnabled()) {
						logger.error(getId() + " 查询执行错误：" + e.getMessage());
						logger.error("SQL：" + sql);
					}
					throw e;

				} finally {
					if (rs != null) {
						try {
							rs.close();
							rs = null;
						} catch (Exception e) {
							;
						}
					}
				}
			}
			if(json != null && !json.isEmpty()){
				if(hasText(getCache())) ServiceCache.setSqlCache(getCache(), getServiceId()+"==>"+getId()+"==>"+sql+"==>"+inParamToString(), json);
				if(hasText(getUpdatecache())) ServiceCache.clearServiceCache(getUpdatecache());
			}
			
			return json;
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error(getId() + " 查询执行错误：" + e.getMessage());
				logger.error("SQL：" + sql);
			}
			throw e;
		} finally {
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (Exception e) {
					;
				}
			}
		}
	}

	private int updateForStatement() throws SQLException {
		Connection conn = getConn();
		Statement stmt = null;
		try {
			if (supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
				stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			} else {
				stmt = conn.createStatement();
			}
			if (logger.isDebugEnabled()) {
				logger.debug("执行语句：" + getSql());
			}
			int ret = stmt.executeUpdate(getSql());
			if (logger.isDebugEnabled()) {
				logger.debug("返回结果：" + ret);
			}
			return ret;
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error(getId() + " 更新执行错误：" + e.getMessage());
				logger.error("SQL：" + getSql());
			}
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					;
				}
			}
		}
	}

	private int updateForStatementWithKeys() throws SQLException {
		String[] keys = getGeneratedKeys();
		if (keys == null) {
			return updateForStatement();
		}
		Connection conn = getConn();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(getSql(), keys);
			if (logger.isDebugEnabled()) {
				logger.debug("执行语句：" + getSql());
			}
			int ret = ps.executeUpdate();

			ResultSet rs = null;
			try {
				rs = ps.getGeneratedKeys();
				Json retjson = JsonFactory.create().toJson(rs, getId(), keys, isAlwayarray(), isLowercase());
				setGeneratekeysvalue(retjson);
				if (logger.isDebugEnabled()) {
					logger.debug("返回键值：" + retjson);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("读取返回主键错误：" + e.getMessage());
				}
			} finally {
				if (rs != null) {
					try {
						rs.close();
						rs = null;
					} catch (Exception e) {
						;
					}
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("返回结果：" + ret);
			}
			return ret;
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error(getId() + " 更新执行错误：" + e.getMessage());
				logger.error("SQL：" + getSql());
			}
			throw e;
		} finally {
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (Exception e) {
					;
				}
			}
		}
	}

	private int[] updateForPreparedStatement() throws SQLException {
		if (!hasIn()) {
			return null;
		}
		String charset = "UTF-8";//GBK
		Connection conn = getConn();
		PreparedStatement ps = null;
		try {
			int[] ret = null;
			if (logger.isDebugEnabled()) {
				logger.debug("执行语句：" + getSql());
			}
			String[] keys = getGeneratedKeys();
			if (keys != null) {
				ps = conn.prepareStatement(getSql(), keys);
			} else {
//				if (supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
//					ps = conn.prepareStatement(getSql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
//				} else {
//					ps = conn.prepareStatement(getSql());
//				}
				ps = conn.prepareStatement(getSql());
			}
			if (isBatch() && supportsBatchUpdates(conn)) {
				for (int i = 0; i < getCount(); i++) {
					if (logger.isDebugEnabled()) {
						logger.debug(String.valueOf((i + 1)));
					}
					for (SqlParameter sp : getInParam()) {
						if (sp.getSize() > i) {
							if(sp.getType() == 9 && sp.getValue(i).toString().length()>2048){//clob
								try {
									logger.debug("==>参数:" + sp.getIndex() + "==>取值:    clob");
									ps.setAsciiStream(sp.getIndex(), new ByteArrayInputStream(sp.getValue(i).toString().getBytes(charset)));
								} catch (Throwable t) {
									logger.error("CLOB字段读取错误:"+t.getMessage());
									if (logger.isDebugEnabled()) {
										t.printStackTrace();
									}
								}
							}
							else if(sp.getType() == 10){//blob
								logger.debug("==>参数:" + sp.getIndex() + "==>取值:    blob");
								InputStream in = null;
								try {
									in = new BufferedInputStream(new FileInputStream(new File(sp.getValue(i).toString())));
								} catch (Throwable t) {
									logger.error("读取文件流错误："+t.getMessage());
									in = null;
								}
								ps.setBinaryStream(sp.getIndex(), in);
							}
							else {
								if (logger.isDebugEnabled()) {
									logger.debug("==>参数:" + sp.getIndex() + "==>取值:" + sp.getValue(i));
								}
								ps.setObject(sp.getIndex(), sp.getValue(i));
							}
						} else {
							if(sp.getType() == 9 && sp.getValue(sp.getSize() - 1).toString().length()>2048){//clob
								try {
									logger.debug("==>参数:" + sp.getIndex() + "==>取值:    clob");
									ps.setAsciiStream(sp.getIndex(), new ByteArrayInputStream(sp.getValue(sp.getSize() - 1).toString().getBytes(charset)));
								} catch (Throwable t) {
									logger.error("CLOB字段读取错误:"+t.getMessage());
									if (logger.isDebugEnabled()) {
										t.printStackTrace();
									}
								}
							}
							else if(sp.getType() == 10){//blob
								logger.debug("==>参数:" + sp.getIndex() + "==>取值:    blob");
								InputStream in = null;
								try {
									in = new BufferedInputStream(new FileInputStream(new File(sp.getValue(sp.getSize() - 1).toString())));
								} catch (Throwable t) {
									logger.error("读取文件流错误："+t.getMessage());
									in = null;
								}
								ps.setBinaryStream(sp.getIndex(), in);
							}
							else {
								if (logger.isDebugEnabled()) {
									logger.debug("==>参数:" + sp.getIndex() + "==>取值:" + sp.getValue(sp.getSize() - 1));
								}
								ps.setObject(sp.getIndex(), sp.getValue(sp.getSize() - 1));
							}
						}
					}
					ps.addBatch();// 此处暂不处理巨大数量的批量更新问题
				}
				ret = ps.executeBatch();
				if (keys != null) {
					ResultSet rs = null;
					try {
						rs = ps.getGeneratedKeys();
						Json retjson = JsonFactory.create().toJson(rs, getId(),keys, isAlwayarray(),isLowercase());
						setGeneratekeysvalue(retjson);
						if (logger.isDebugEnabled()) {
							logger.debug("返回键值：" + retjson);
						}
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("读取返回主键错误：" + e.getMessage());
						}
					} finally {
						if (rs != null) {
							try {
								rs.close();
								rs = null;
							} catch (Exception e) {
								;
							}
						}
					}
					if (logger.isDebugEnabled()) {
						logger.debug("返回主键1：" + getGeneratekeysvalue());
					}
				}
			} else {
				ret = new int[getCount()];
				CArray gkArr = new CArray();
				for (int i = 0; i < getCount(); i++) {
					ps.clearParameters();
					if (logger.isDebugEnabled()) {
						logger.debug(String.valueOf(i + 1));
					}
					for (SqlParameter sp : getInParam()) {
						long len = 0;
						if (sp.getSize() > i) {
							if(sp.getType() == 9 && (len=sp.getValue(i).toString().length())>2048){//clob
								try {
									logger.debug("==>参数:" + sp.getIndex() + "==>取值:    clob");
									//ps.setAsciiStream(sp.getIndex(), new ByteArrayInputStream(sp.getValue(i).toString().getBytes(charset)));
									ps.setCharacterStream(sp.getIndex(), new StringReader(sp.getValue(i).toString()), len);
								} catch (Throwable t) {
									logger.error("CLOB字段读取错误:"+t.getMessage());
									if (logger.isDebugEnabled()) {
										t.printStackTrace();
									}
								}
							}
							else if(sp.getType() == 10){//blob
								logger.debug("==>参数:" + sp.getIndex() + "==>取值:    blob");
								InputStream in = null;
								try {
									in = new BufferedInputStream(new FileInputStream(new File(sp.getValue(i).toString())));
								} catch (Throwable t) {
									logger.error("读取文件流错误："+t.getMessage());
									in = null;
								}
								ps.setBinaryStream(sp.getIndex(), in);
							}
							else {
								if (logger.isDebugEnabled()) {
									logger.debug("==>参数:" + sp.getIndex() + "==>取值:" + sp.getValue(i));
								}
								ps.setObject(sp.getIndex(), sp.getValue(i));
							}
						} else {
							if(sp.getType() == 9 && (len=sp.getValue(i).toString().length())>2048){//clob
								try {
									logger.debug("==>参数:" + sp.getIndex() + "==>取值:    clob");
									//ps.setAsciiStream(sp.getIndex(), new ByteArrayInputStream(sp.getValue(sp.getSize() - 1).toString().getBytes(charset)));
									ps.setCharacterStream(sp.getIndex(), new StringReader(sp.getValue(i).toString()), len);
								} catch (Throwable t) {
									logger.error("CLOB字段读取错误:"+t.getMessage());
									if (logger.isDebugEnabled()) {
										t.printStackTrace();
									}
								}
							}
							else if(sp.getType() == 10){//blob
								logger.debug("==>参数:" + sp.getIndex() + "==>取值:    blob");
								InputStream in = null;
								try {
									in = new BufferedInputStream(new FileInputStream(new File(sp.getValue(i).toString())));
								} catch (Throwable t) {
									logger.error("读取文件流错误："+t.getMessage());
									in = null;
								}
								ps.setBinaryStream(sp.getIndex(), in);
							}
							else {
								if (logger.isDebugEnabled()) {
									logger.debug("==>参数:" + sp.getIndex() + "==>取值:" + sp.getValue(sp.getSize() - 1));
								}
								ps.setObject(sp.getIndex(), sp.getValue(sp.getSize() - 1));
							}
						}
					}
					ret[i] = ps.executeUpdate();
					if (keys != null) {
						ResultSet rs = null;
						try {
							rs = ps.getGeneratedKeys();
							Json retjson = JsonFactory.create().toJson(rs, getId(), keys, false,isLowercase());
							gkArr.add(retjson.getObject());
							if (logger.isDebugEnabled()) {
								logger.debug("返回键值：" + retjson);
							}
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("读取返回主键错误：" + e.getMessage());
							}
						} finally {
							if (rs != null) {
								try {
									rs.close();
									rs = null;
								} catch (Exception e) {
									;
								}
							}
						}
					}
				}
				
				if (keys != null  && !gkArr.isEmpty()) {
					if(isAlwayarray() || gkArr.size() > 1){
						setGeneratekeysvalue(JsonFactory.create(getId()).addArray(gkArr));
					}
					else{
						Json gkv = JsonFactory.create(getId());
						gkv.getObject().mergeOf(gkArr.getRecord(0));
						setGeneratekeysvalue(gkv);
					}
					if (logger.isDebugEnabled()) {
						logger.debug("返回主键2：" + getGeneratekeysvalue());
					}
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("返回结果：" + StringUtils.intArrayToString(ret));
			}
			return ret;
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error(getId() + " 更新执行错误：" + e.getMessage());
				logger.error("SQL：" + getSql());
			}
			throw e;
		} finally {
			if (ps != null) {
				try {
					ps.close();
					ps = null;
				} catch (Exception e) {
					;
				}
			}
		}
	}

	private Json executeForCallableStatement() throws SQLException {
		long startTime = System.currentTimeMillis();
		CallableStatement cs = null;
		Connection conn = getConn();
		try {
			List<Integer> updateCounts = null;
			List<Json> results = null;
			List<CStruc> outParameters = null;
			if (logger.isDebugEnabled()) {
				logger.debug("执行语句：" + getSql());
			}
			cs = conn.prepareCall(getSql());/*
			if (false && isBatch() && supportsBatchUpdates(conn)) {
				for (int i = 0; i < getCount(); i++) {
					if (hasIn()) {
						for (SqlParameter sp : getInParam()) {
							if (logger.isDebugEnabled()) {
								logger.debug("==>" + sp.getIndex() + ":" + sp.getValue(i));
							}
							cs.setObject(sp.getIndex(), sp.getValue(i));
						}
					}
					if (hasOut()) {
						for (SqlParameter sp : getOutParam()) {
							if (logger.isDebugEnabled()) {
								logger.debug("<==" + sp.getIndex() + ":" + sp.getValue(i));
							}
							cs.registerOutParameter(sp.getIndex(), java.sql.Types.VARCHAR);
						}
					}
					cs.addBatch();
				}
				int[] ret = cs.executeBatch();
				for (int i : ret) {
					if (updateCounts == null) {
						updateCounts = new ArrayList<Integer>();
					}
					updateCounts.add(i);
					if (logger.isDebugEnabled()) {
						logger.debug("==>返回更新计数：" + i);
					}
				}
				int updateCount = cs.getUpdateCount();
				if (logger.isDebugEnabled()) {
					logger.debug("getUpdateCount方法的返回：" + updateCount);
				}
				boolean moreResults;

				do {
					if (updateCount != -1) {
						if (updateCounts == null) {
							updateCounts = new ArrayList<Integer>();
						}
						updateCounts.add(updateCount);
						if (logger.isDebugEnabled()) {
							logger.debug("==>返回更新计数：" + updateCount);
						}
					} else {
						if (cs.getResultSet() != null) {
							if (results == null) {
								results = new ArrayList<Json>();
							}
							results.add(JsonFactory.create().toJson(cs.getResultSet(), getId(), isAlwayarray()));
							if (logger.isDebugEnabled()) {
								logger.debug("==>返回结果集：" + results.get(results.size() - 1));
							}
						}
					}
					moreResults = cs.getMoreResults();
					updateCount = cs.getUpdateCount();
					if (logger.isDebugEnabled()) {
						logger.debug("返回结果集标记：" + moreResults);
						logger.debug("返回更新计数标记：" + updateCount);
					}
				} while (moreResults || updateCount != -1);

				if (hasOut()) {
					CStruc out = null;
					for (SqlParameter sp : getOutParam()) {
						Object obj = cs.getObject(sp.getIndex());

						if (out == null) {
							out = JsonFactory.createStruc(getId());
						}
						out.addField(new CField(sp.getName(), obj));
					}
					if (out != null) {
						if (outParameters == null) {
							outParameters = new ArrayList<CStruc>();
						}
						outParameters.add(out);
						if (logger.isDebugEnabled()) {
							logger.debug("==>输出参数返回：" + out);
						}
					}
				}
			} else */if (getCount() > 0) {
				for (int i = 0; i < getCount(); i++) {
					cs.clearParameters();
					if (hasIn()) {
						for (SqlParameter sp : getInParam()) {
							if (logger.isDebugEnabled()) {
								logger.debug("==>" + sp.getIndex() + ":" + sp.getValue(i));
							}
							cs.setObject(sp.getIndex(), sp.getValue(i));
						}
					}
					if (hasOut()) {
						for (SqlParameter sp : getOutParam()) {
							if (logger.isDebugEnabled()) {
								logger.debug("<==" + sp.getIndex() + ":" + sp.getValue(i));
							}
							cs.registerOutParameter(sp.getIndex(), java.sql.Types.VARCHAR);
						}
					}

					/* 如果flag为true,那么说明返回了一个结果集(ResultSet)类型 */
					boolean retVal = cs.execute();
					if (logger.isDebugEnabled()) {
						logger.debug("execute方法的返回：" + retVal);
					}
					/*
					 * 如果返回-1,既不是结果集,又不是更新计数了.说明没的返回了.
					 * 如果返回0或大于0,则说明当前指针是更新计数(0的时候有可能是DDL指令).
					 * 无论是返回结果集或是更新计数,那么则可能还继续有其它返回.
					 * 只有在当前指指针getResultSet()==null && getUpdateCount() ==
					 * -1才说明没有再多的返回.
					 */
					int updateCount = cs.getUpdateCount();
					if (logger.isDebugEnabled()) {
						logger.debug("getUpdateCount方法的返回：" + updateCount);
					}
					boolean moreResults;
					List<Json> rets = null;
					Json rsJson;
					do {
						if (updateCount != -1) {
							if (updateCounts == null) {
								updateCounts = new ArrayList<Integer>();
							}
							updateCounts.add(updateCount);
							if (logger.isDebugEnabled()) {
								logger.debug("==>返回更新计数：" + updateCount);
							}
						} else {
							if (cs.getResultSet() != null) {
								if (rets == null) {
									rets = new ArrayList<Json>();
								}
								rsJson = JsonFactory.create().toJson(cs.getResultSet(), getId(), isAlwayarray(),isLowercase());
								if(rsJson.isEmpty()){
									if(isAlwayarray()){
										rsJson.addArray(JsonFactory.createArray(getId()));
									}
									else{
										rsJson.addStruc(JsonFactory.createStruc(getId()));
									}
								}
								rets.add(rsJson);
								if (logger.isDebugEnabled()) {
									logger.debug("==>返回结果集：" + rets.get(rets.size() - 1));
								}
							}
						}
						moreResults = cs.getMoreResults();
						updateCount = cs.getUpdateCount();
						if (logger.isDebugEnabled()) {
							logger.debug("返回结果集标记：" + moreResults);
							logger.debug("返回更新计数标记：" + updateCount);
						}
					} while (moreResults || updateCount != -1);
					
					if(rets != null){
						if (results == null) {
							results = new ArrayList<Json>();
						}
						for(int k=0; k<rets.size(); k++){
							if (k+1 > results.size()) {
								CArray ca = new CArray(getId());
								ca.add(rets.get(k).getStruc(getId()));
								ca.add(rets.get(k).getArray(getId()));
								results.add(JsonFactory.create(getId()).addArray(ca));
								continue;
							}
							
							Json js = results.get(k);
							if(js == null){
								js = JsonFactory.create(getId());
							}
							js.getArray(getId()).add(rets.get(k).getStruc(getId()));
							js.getArray(getId()).add(rets.get(k).getArray(getId()));
						}
						rets = null;
					}
					
					if (hasOut()) {
						CStruc out = null;
						for (SqlParameter sp : getOutParam()) {
							Object obj = cs.getObject(sp.getIndex());

							if (out == null) {
								out = JsonFactory.createStruc(getId());
							}
							out.addField(new CField(sp.getName(), obj));
						}
						if (out != null) {
							if (outParameters == null) {
								outParameters = new ArrayList<CStruc>();
							}
							outParameters.add(out);
							if (logger.isDebugEnabled()) {
								logger.debug("==>输出参数返回：" + out);
							}
						}
					}
				}// end for
			} else {
				cs.clearParameters();
				if (hasIn()) {
					for (SqlParameter sp : getInParam()) {
						cs.setObject(sp.getIndex(), sp.getValue());
					}
				}
				if (hasOut()) {
					for (SqlParameter sp : getOutParam()) {
						cs.registerOutParameter(sp.getIndex(), java.sql.Types.VARCHAR);
					}
				}
				boolean retVal = cs.execute();
				if (logger.isDebugEnabled()) {
					logger.debug("execute方法的返回：" + retVal);
				}
				/*
				 * 如果返回-1,既不是结果集,又不是更新计数了.说明没的返回了.
				 * 如果返回0或大于0,则说明当前指针是更新计数(0的时候有可能是DDL指令).
				 * 无论是返回结果集或是更新计数,那么则可能还继续有其它返回. 只有在当前指指针getResultSet()==null &&
				 * getUpdateCount() == -1才说明没有再多的返回.
				 */
				int updateCount = cs.getUpdateCount();
				if (logger.isDebugEnabled()) {
					logger.debug("getUpdateCount方法的返回：" + updateCount);
				}
				boolean moreResults;
				Json rsJson;
				do {
					if (updateCount != -1) {
						if (updateCounts == null) {
							updateCounts = new ArrayList<Integer>();
						}
						updateCounts.add(updateCount);
						if (logger.isDebugEnabled()) {
							logger.debug("==>返回更新计数：" + updateCount);
						}
					} else {
						if (cs.getResultSet() != null) {
							if (results == null) {
								results = new ArrayList<Json>();
							}
							rsJson = JsonFactory.create().toJson(cs.getResultSet(), getId(), isAlwayarray(),isLowercase());
							if(rsJson.isEmpty()){
								if(isAlwayarray()){
									rsJson.addArray(JsonFactory.createArray(getId()));
								}
								else{
									rsJson.addStruc(JsonFactory.createStruc(getId()));
								}
							}
							results.add(rsJson);
							if (logger.isDebugEnabled()) {
								logger.debug("==>返回结果集：" + results.get(results.size() - 1));
							}
						}
					}
					moreResults = cs.getMoreResults();
					updateCount = cs.getUpdateCount();
					if (logger.isDebugEnabled()) {
						logger.debug("返回结果集标记：" + moreResults);
						logger.debug("返回更新计数标记：" + updateCount);
					}
				} while (moreResults || updateCount != -1);

				if (hasOut()) {
					CStruc out = null;
					for (SqlParameter sp : getOutParam()) {
						Object obj = cs.getObject(sp.getIndex());

						if (out == null) {
							out = JsonFactory.createStruc(getId());
						}
						out.addField(new CField(sp.getName(), obj));
					}
					if (out != null) {
						if (outParameters == null) {
							outParameters = new ArrayList<CStruc>();
						}
						outParameters.add(out);
						if (logger.isDebugEnabled()) {
							logger.debug("==>输出参数返回：" + out);
						}
					}
				}
			}

			String countName, resultName, parameterName;
			countName = resultName = parameterName = getId();
			if (updateCounts != null && results == null && outParameters == null) {
				countName = getId();
				resultName = parameterName = null;
			} else if (results != null && updateCounts == null && outParameters == null) {
				resultName = getId();
				countName = parameterName = null;
			} else if (outParameters != null && updateCounts == null && results == null) {
				parameterName = getId();
				countName = resultName = null;
			} else if (results != null && updateCounts != null && outParameters == null) {
				resultName = getId();
				countName = "uc";
				parameterName = null;
			} else if (outParameters != null && updateCounts != null && results == null) {
				parameterName = getId();
				countName = "uc";
				resultName = null;
			} else {
				parameterName = getId();
				countName = "uc";
				resultName = "rs";
			}
			Json json = JsonFactory.create();//最终返回的数据,外层
			CStruc struc = JsonFactory.createStruc(getId());//里层返回结果的结果体
			if (updateCounts != null) {
				int successfully = 0;
				int failed = 0;
				for (int i : updateCounts) {
					if (i >= 0) {
						successfully += i;
					} else {
						failed += i;
					}
				}
				if (countName.equalsIgnoreCase(getId())) {// 名称相同则放到最外层
					if (failed < 0) {
						json.addField(countName, String.valueOf(failed));
					} else {
						json.addField(countName, String.valueOf(successfully));
					}
				} else {//名称不同则放到里层
					if (failed < 0) {
						struc.addField(JsonFactory.createField(countName, String.valueOf(failed)));
					} else {
						struc.addField(JsonFactory.createField(countName, String.valueOf(successfully)));
					}
				}
			}
			boolean xflag = false;
			if (results != null) {
				if (results.size() == 1) {//只有一个结果集
					Json js = results.get(0);
					CArray arr = js.getArray(getId());
					if (arr != null) {
						if(arr.size() == 1 && !isAlwayarray()){
							struc.mergeOf(arr.getRecord(0));
							xflag = true;
						}
						else{
							arr.setName(resultName);
							if (resultName.equalsIgnoreCase(getId())) {
								json.addArray(arr);
							} else {
								struc.addArray(arr);
							}
						}
					} else {
						CStruc stc = js.getStruc(getId());
						if (stc != null) {
							stc.setName(resultName);
							if (resultName.equalsIgnoreCase(getId())) {
								json.addStruc(stc);
							} else {
								struc.mergeOf(stc);
							}
						}
					}
				} else {
					int idx = 0;
					CStruc stc = null;
					CArray arr = null;
					String rname = resultName + idx;
					for (Json jn : results) {
						arr = jn.getArray(getId());
						if (arr != null) {
							arr.setName(rname);
							if (resultName.equalsIgnoreCase(getId())) {
								json.addArray(arr);
							} else {
								struc.addArray(arr);
							}
						} else {
							stc = jn.getStruc(getId());
							if (stc != null) {
								stc.setName(rname);
								if (resultName.equalsIgnoreCase(getId())) {
									json.addStruc(stc);
								} else {
									struc.mergeOf(stc);
								}
							}
						}
						idx++;
						if (idx == results.size() - 1) {
							rname = resultName;
						} else {
							rname = resultName + idx;
						}
					}
				}
			}
			if (outParameters != null) {
				if (outParameters.size() == 1) {
					CStruc stc = outParameters.get(0);
					stc.setName(parameterName);
					if(xflag){
						struc.mergeOf(stc);
					}
					else if(struc != null && !struc.isEmpty()){
						struc.mergeOf(stc);
					}
					else if (parameterName.equalsIgnoreCase(getId())) {
						json.getObject().mergeOf(stc);
					} else {
						struc.mergeOf(stc);
					}
				} else {
					if (parameterName.equalsIgnoreCase(getId())) {
						json.addArray(parameterName, outParameters.toArray(new CStruc[0]));
					} else {
						struc.addArray(JsonFactory.createArray(parameterName, outParameters.toArray(new CStruc[0])));
					}
				}
			}

			if (struc != null && !struc.isEmpty()) {
				CStruc stc = json.getStruc(getId());
				if (stc != null) {
					stc.mergeOf(struc);
				} else {
					CArray arr = json.getArray(getId());
					if (arr != null) {
						struc.addArray(arr);
						json.remove(getId());
					} else {
						CField fld = json.getField(getId());
						if (fld != null) {
							struc.addField(fld);
							json.remove(getId());
						}
					}
					json.addStruc(struc);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("==>存储过程返回：" + json);
			}
			return json;

		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error(getId() + " 存储过程执行错误：" + e.getMessage());
				logger.error("SQL：" + getSql());
			}
			throw e;
		} finally {
			if (cs != null) {
				try {
					cs.close();
					cs = null;
				} catch (Exception e) {
					;
				}
			}
			if(logger.isInfoEnabled()){
				long processingTime = System.currentTimeMillis() - startTime;
				logger.info("===>"+getId()+":executeForCallableStatement total spend time:" + processingTime + "ms(毫秒)");
			}
		}
	}

	public boolean supportsBatchUpdates(Connection con) {
		try {
			DatabaseMetaData dbmd = con.getMetaData();
			if (dbmd != null) {
				if (dbmd.supportsBatchUpdates()) {
					if (logger.isDebugEnabled()) {
						logger.debug("JDBC driver supports batch updates");
					}
					return true;
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("JDBC driver does not support batch updates");
					}
				}
			}
		} catch (SQLException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("JDBC driver 'supportsBatchUpdates' method threw exception", ex);
			}
		} catch (AbstractMethodError err) {
			if (logger.isDebugEnabled()) {
				logger.debug("JDBC driver does not support JDBC 2.0 'supportsBatchUpdates' method", err);
			}
		}
		return false;
	}

	public boolean supportsResultSetConcurrency(int type, int concurrency) {
		try {
			DatabaseMetaData dbmd = getConn().getMetaData();
			if (dbmd != null) {
				if (dbmd.supportsResultSetConcurrency(type, concurrency)) {
					if (logger.isDebugEnabled()) {
						logger.debug("JDBC driver supports ResultSet Concurrency");
					}
					return true;
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("JDBC driver does not support ResultSet Concurrency");
					}
				}
			}
		} catch (SQLException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("JDBC driver 'supportsResultSetConcurrency' method threw exception", ex);
			}
		} catch (AbstractMethodError err) {
			if (logger.isDebugEnabled()) {
				logger.debug("JDBC driver does not support JDBC 2.0 'supportsResultSetConcurrency' method", err);
			}
		}
		return false;
	}
}