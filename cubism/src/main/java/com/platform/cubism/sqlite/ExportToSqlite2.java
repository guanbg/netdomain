package com.platform.cubism.sqlite;

import static com.platform.cubism.util.FileUtils.copyFile;
import static com.platform.cubism.SystemConfig.getUploadPath;
import static com.platform.cubism.util.CubismHelper.getAppRootDir;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.CubismException;
import com.platform.cubism.SystemConfig;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.jdbc.conn.JndiDataSourceLookup;
import com.platform.cubism.service.CustomService;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.util.FileUtils;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.util.SecurityHelper;

public class ExportToSqlite2 implements CustomService{
	private static Logger logger = LoggerFactory.getLogger(ExportToSqlite2.class);
	private static String CUBISM = "cubism";// 数据源名称
	private static String DB_NAME = "offlineRCFMS.db";// 数据库名称
	private static String DB_TMPL_FILE_PATH_NAME = getAppRootDir(true) + "template/" + DB_NAME;// 初始数据库位置
	private static String DB_DIST_PATH = getUploadPath() + "ND/";// 数据库包下载位置
	private static DataSource ds = null;
	
	@Override
	public Json execute(Json in) throws CubismException {
		Json ret = JsonFactory.create();
		String lib_id = in.getFieldValue("lib_id");
		if(lib_id == null || lib_id.length() <= 0 || !export(lib_id)){
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.sqlite.ExportToSqlite", "90001", "数据导出服务打包错误！", MsgLevel.D,RetStatus.FAILED));
		}
		else{
			ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.sqlite.ExportToSqlite", "00000", "执行完毕", MsgLevel.B,RetStatus.SUCCESS));
		}
		return ret;
	}
	
	public boolean export(String lib_id) {
		Connection src_conn = null, dist_conn = null;
		Statement src_stmt = null;
		try {
			if(ds == null){
				ds = JndiDataSourceLookup.getDataSource(CUBISM);
			}
			src_conn = ds.getConnection();
			src_stmt = src_conn.createStatement();
		} catch (Throwable t) {
			logger.error("JNDI没有查找到数据源" + CUBISM + "，出错信息为:" + t.getMessage());
			if (logger.isDebugEnabled()) {
				t.printStackTrace();
			}
			closeConn(src_conn);
			return false;
		}

		logger.debug("1:业务数据库连接成功");
		
		
		File src = new File(DB_TMPL_FILE_PATH_NAME);
		if (!src.exists()) {
			closeConn(src_conn);
			return false;
		}
		
		String dbPath = DB_DIST_PATH + lib_id + "/";
		String dbPathName = dbPath + DB_NAME;
		
		logger.debug("DB_TMPL_FILE_PATH_NAME="+DB_TMPL_FILE_PATH_NAME);
		logger.debug("dbPathName="+dbPathName);
		
		File dist = new File(dbPathName);
		if (copyFile(src, dist) <= 0) {
			closeConn(src_conn);
			return false;
		}

		try {
			Class.forName("org.sqlite.JDBC");
			dist_conn = DriverManager.getConnection("jdbc:sqlite:" + dbPathName);
		} catch (Throwable t) {
			logger.error("没有查找到sqlite数据驱动，出错信息为:" + t.getMessage());
			if (logger.isDebugEnabled()) {
				t.printStackTrace();
			}
			closeConn(src_conn);
			closeConn(dist_conn);
			return false;
		}
		
		logger.debug("2:sqlite数据库连接成功");
		
		
		new Thread(new ExportToSqliteThread(src_conn, src_stmt, dist_conn, lib_id,SystemConfig.getUploadPath(),DB_DIST_PATH),"导出数据到sqlite数据库").start();
		
		return true;
	}

	private void closeConn(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (Throwable t) {
			logger.error("关闭数据库连接错误，出错信息为:" + t.getMessage());
			if (logger.isDebugEnabled()) {
				t.printStackTrace();
			}
		}
	}

	
	private static class ExportToSqliteThread implements Runnable {
		Connection src_conn = null, dist_conn = null;
		Statement src_stmt = null;
		String lib_id = null;
		String root_path = null, dist_path = null;
		
		public ExportToSqliteThread(Connection src_conn, Statement src_stmt, Connection dist_conn, String lib_id,String root_path,String dist_path){
			this.src_conn = src_conn;
			this.src_stmt = src_stmt;
			this.dist_conn = dist_conn;
			this.lib_id = lib_id;
			this.root_path = root_path;
			this.dist_path = dist_path;
			ExportStatus.setStatus(lib_id, "1");
		}
		
		@Override
		public void run() {
			try {
				dist_conn.setAutoCommit(false);
			} catch (SQLException e) {
				logger.error("关闭数据库事物错误，出错信息为:" + e.getMessage());
				if (logger.isDebugEnabled()) {
					e.printStackTrace();
				}
				closeConn(true);
				return ;
			}
			
			if (!export_fms_version()) {
				closeConn(true);
				return ;
			}
			logger.debug("3:版本表导入成功");
			
			if (!export_fms_contractor_basicinfo()) {
				closeConn(true);
				return ;
			}
			logger.debug("4:企业基本信息表导入成功");
			
			if (!export_fms_contractor_documentor()) {
				closeConn(true);
				return ;
			}
			logger.debug("5:资料员基本信息表导入成功");
			
			if (!export_fms_config()) {
				closeConn(true);
				return ;
			}
			logger.debug("6:业务参数表导入成功");
			
			if (!export_fms_businessrule()) {
				closeConn(true);
				return ;
			}
			logger.debug("7:业务规则表导入成功");
			
			if (!export_fms_library()) {
				closeConn(true);
				return ;
			}
			logger.debug("8:档案库表导入成功");
			
			String fileName = updateNdfile();
			
			try {
				dist_conn.commit();
				dist_conn.setAutoCommit(true);
			} catch (SQLException e) {
				logger.error("提交数据库事物错误，出错信息为:" + e.getMessage());
				if (logger.isDebugEnabled()) {
					e.printStackTrace();
				}
			}
			closeConn(false);
			if (fileName == null || fileName.length() <= 0) {
				return ;
			}
			logger.debug("9:下载路径更新成功");
			
			ExportStatus.setStatus(lib_id, "1");
			new File(dist_path + lib_id + "/" + ExportToSqlite2.DB_NAME).renameTo(new File(dist_path + lib_id + "/" + fileName.substring(0,fileName.lastIndexOf('.')) + ExportToSqlite2.DB_NAME.substring(ExportToSqlite2.DB_NAME.lastIndexOf('.'))));
			
			String zipFileName = SecurityHelper.bytes2HexString(fileName.getBytes());
			FileUtils.zip(dist_path + lib_id + "/", dist_path, zipFileName,false);
			FileUtils.delFile(dist_path + lib_id);
			logger.debug("10:ND压缩包创建成功");
			ExportStatus.removeStatus(lib_id);
		}
		
		private void closeConn(boolean isRollback) {
			ExportStatus.removeStatus(lib_id);
			try {
				if (src_conn != null){
					src_conn.close();
				}
					
			} catch (Throwable t) {
				logger.error("关闭数据库连接错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
			}
			try {
				if (dist_conn != null){
					if(isRollback){
						dist_conn.rollback();
					}
					
					dist_conn.close();
				}
			} catch (Throwable t) {
				logger.error("关闭数据库连接错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
			}
		}
		private String updateNdfile() {
			String lib_aliase = null, fileName = null;
			try {
				ResultSet rs = src_stmt.executeQuery("select lib_aliase from fms_contractor_database where lib_id=" + lib_id);
				if(rs.next()){
					lib_aliase = rs.getString(1);
					fileName = lib_aliase+lib_id+".ND";
					logger.debug("下载路径："+dist_path+fileName);
					String fileid = SecurityHelper.bytes2HexString((fileName).getBytes());
					src_stmt.executeUpdate("update fms_contractor_database set ndfile='"+fileid+"' where lib_id=" + lib_id);
				}
				rs.close();
			} catch (Throwable t) {
				lib_aliase = null;
				fileName = null;
				logger.error("查询错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
			}
			return fileName;
		}
		private boolean export_fms_version() {
			try {
				ResultSet rs = src_stmt.executeQuery("select * from fms_version where version_id in (select version_id from fms_contractor_database where lib_id=" + lib_id + ")");
				if(rs.next()){
					PreparedStatement dist_stmt = dist_conn.prepareStatement("insert into offline_version(version_id,version_num,version_name,version_memo,create_date,last_date) values(?,?,?,?,?,?)");
					dist_stmt.setLong(1, rs.getLong("version_id"));
					dist_stmt.setString(2, rs.getString("version_num"));
					dist_stmt.setString(3, rs.getString("version_name"));
					dist_stmt.setString(4, rs.getString("version_memo"));
					dist_stmt.setString(5, rs.getString("create_date"));
					dist_stmt.setString(6, rs.getString("last_date"));
					dist_stmt.executeUpdate();
					dist_stmt.close();
					rs.close();
				}
				else{
					rs.close();
					return false;
				}
			} catch (Throwable t) {
				logger.error("查询错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		private boolean export_fms_contractor_basicinfo() {
			String sql = "SELECT * FROM fms_contractor_basicinfo WHERE contractor_id IN (SELECT contractor_id FROM fms_contractor_user WHERE user_id IN (SELECT user_id FROM fms_contractor_database WHERE lib_id=" + lib_id + ") UNION ALL SELECT contractor_id FROM fms_contractor_documentor WHERE documentor_id IN (SELECT documentor_id FROM fms_contractor_user WHERE user_id IN (SELECT user_id FROM fms_contractor_database WHERE lib_id=" + lib_id + ")))";
			try {
				ResultSet rs = src_stmt.executeQuery(sql);
				if(rs.next()){
					PreparedStatement dist_stmt = dist_conn.prepareStatement("insert into offline_contractor_basicinfo(contractor_id,participation_units,company_name,organizing_code,registration_authority,business_license_reg_num,business_license_reg_auth,enterprise_type,registered_capital,paid_registered_capital,establishment_date,domicile,operating_period,legal_representative,contact_phone,business_scope,change_description,attachment,create_date,create_user,last_date,last_user,approval_status,approval_date,approval_memo,approval_user,mobile_no,email,isdel,account_code_pic,business_license_pic,submit_approval_date,database_status) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					dist_stmt.setString(1, rs.getString("contractor_id"));
					dist_stmt.setString(2, rs.getString("participation_units"));
					dist_stmt.setString(3, rs.getString("company_name"));
					dist_stmt.setString(4, rs.getString("organizing_code"));
					dist_stmt.setString(5, rs.getString("registration_authority"));
					dist_stmt.setString(6, rs.getString("business_license_reg_num"));
					dist_stmt.setString(7, rs.getString("business_license_reg_auth"));
					dist_stmt.setString(8, rs.getString("enterprise_type"));
					dist_stmt.setString(9, rs.getString("registered_capital"));
					dist_stmt.setString(10, rs.getString("paid_registered_capital"));
					dist_stmt.setString(11, rs.getString("establishment_date"));
					dist_stmt.setString(12, rs.getString("domicile"));
					dist_stmt.setString(13, rs.getString("operating_period"));
					dist_stmt.setString(14, rs.getString("legal_representative"));
					dist_stmt.setString(15, rs.getString("contact_phone"));
					dist_stmt.setString(16, rs.getString("business_scope"));
					dist_stmt.setString(17, rs.getString("change_description"));
					dist_stmt.setString(18, rs.getString("attachment"));
					dist_stmt.setString(19, rs.getString("create_date"));
					dist_stmt.setString(20, rs.getString("create_user"));
					dist_stmt.setString(21, rs.getString("last_date"));
					dist_stmt.setString(22, rs.getString("last_user"));
					dist_stmt.setString(23, rs.getString("approval_status"));
					dist_stmt.setString(24, rs.getString("approval_date"));
					dist_stmt.setString(25, rs.getString("approval_memo"));
					dist_stmt.setInt(26, rs.getInt("approval_user"));
					dist_stmt.setString(27, rs.getString("mobile_no"));
					dist_stmt.setString(28, rs.getString("email"));
					dist_stmt.setInt(29, rs.getInt("isdel"));
					dist_stmt.setString(30, rs.getString("account_code_pic"));
					dist_stmt.setString(31, rs.getString("business_license_pic"));
					dist_stmt.setString(32, rs.getString("submit_approval_date"));
					dist_stmt.setInt(33, rs.getInt("database_status"));
					dist_stmt.executeUpdate();
					dist_stmt.close();
					rs.close();
				}
				else{
					rs.close();
					return false;
				}
			} catch (Throwable t) {
				logger.error("查询错误：" + sql + "，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		private boolean export_fms_contractor_documentor() {
			String sql = "select * from fms_contractor_documentor where documentor_id in (select documentor_id from fms_contractor_user where user_id in (select user_id from fms_contractor_database where lib_id=" + lib_id + "))";
			try {
				ResultSet rs = src_stmt.executeQuery(sql);
				if(rs.next()){
					PreparedStatement dist_stmt = dist_conn.prepareStatement("insert into offline_contractor_documentor(documentor_id,contractor_id,register_date,unit_mobile_no,documentor_name,documentor_sex,documentor_birthday,documentor_cardid,documentor_post,documentor_education,graduate_institutions,profession,working_date,mobile_no,email,tenders_code,contract_name,project_name,construction_unit_idea,supervision_unit_idea,construction_division_idea,information_centre_idea,construction_unit_date,supervision_unit_date,construction_division_date,information_centre_date) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					dist_stmt.setString(1, rs.getString("documentor_id"));
					dist_stmt.setString(2, rs.getString("contractor_id"));
					dist_stmt.setString(3, rs.getString("register_date"));
					dist_stmt.setString(4, rs.getString("unit_mobile_no"));
					dist_stmt.setString(5, rs.getString("documentor_name"));
					dist_stmt.setString(6, rs.getString("documentor_sex"));
					dist_stmt.setString(7, rs.getString("documentor_birthday"));
					dist_stmt.setString(8, rs.getString("documentor_cardid"));
					dist_stmt.setString(9, rs.getString("documentor_post"));
					dist_stmt.setString(10, rs.getString("documentor_education"));
					dist_stmt.setString(11, rs.getString("graduate_institutions"));
					dist_stmt.setString(12, rs.getString("profession"));
					dist_stmt.setString(13, rs.getString("working_date"));
					dist_stmt.setString(14, rs.getString("mobile_no"));
					dist_stmt.setString(15, rs.getString("email"));
					dist_stmt.setString(16, rs.getString("tenders_code"));
					dist_stmt.setString(17, rs.getString("contract_name"));
					dist_stmt.setString(18, rs.getString("project_name"));
					
					dist_stmt.setString(19, rs.getString("construction_unit_idea"));
					dist_stmt.setString(20, rs.getString("supervision_unit_idea"));
					dist_stmt.setString(21, rs.getString("construction_division_idea"));
					dist_stmt.setString(22, rs.getString("information_centre_idea"));
					dist_stmt.setString(23, rs.getString("construction_unit_date"));
					dist_stmt.setString(24, rs.getString("supervision_unit_date"));
					dist_stmt.setString(25, rs.getString("construction_division_date"));
					dist_stmt.setString(26, rs.getString("information_centre_date"));
					dist_stmt.executeUpdate();
					dist_stmt.close();
				}
				rs.close();
			} catch (Throwable t) {
				logger.error("查询错误：" + sql + "，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		
		private boolean export_fms_config() {
			String sql = "SELECT GROUP_CONCAT(config_id) ids FROM fms_config_version_items WHERE param_version_id IN (SELECT param_version_id FROM fms_version WHERE version_id IN (SELECT version_id FROM fms_contractor_database WHERE lib_id=" + lib_id + "))";
			try {
				ResultSet rs = src_stmt.executeQuery(sql);
				String ids = null;
				if(rs.next()){
					ids = rs.getString(1);
				}
				
				if(ids == null || ids.length() <= 0){//全部参数
					PreparedStatement dist_stmt = null;
					rs.close();
					rs = src_stmt.executeQuery("SELECT * FROM fms_config");
					while(rs.next()){
						if(dist_stmt == null){
							//dist_conn.setAutoCommit(false);
							dist_stmt = dist_conn.prepareStatement("insert into offline_config(ID,parentID,identity,nodetype,disptype,disporder,status,code,name,memo,value1,value2,value3,value4,value5,extend) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						}
						dist_stmt.setLong(1, rs.getLong("id"));
						dist_stmt.setLong(2, rs.getLong("parentid"));
						dist_stmt.setString(3, rs.getString("identity"));
						dist_stmt.setInt(4, rs.getInt("nodetype"));
						dist_stmt.setInt(5, rs.getInt("disptype"));
						dist_stmt.setInt(6, rs.getInt("disporder"));
						dist_stmt.setInt(7, rs.getInt("status"));
						dist_stmt.setString(8, rs.getString("code"));
						dist_stmt.setString(9, rs.getString("name"));
						dist_stmt.setString(10, rs.getString("memo"));
						dist_stmt.setString(11, rs.getString("value1"));
						dist_stmt.setString(12, rs.getString("value2"));
						dist_stmt.setString(13, rs.getString("value3"));
						dist_stmt.setString(14, rs.getString("value4"));
						dist_stmt.setString(15, rs.getString("value5"));
						dist_stmt.setString(16, rs.getString("extend"));
						
						dist_stmt.addBatch();
					}
					if(dist_stmt != null){
						dist_stmt.executeBatch();
						//dist_conn.setAutoCommit(true); 
						dist_stmt.close();
					}
					rs.close();
				}
				else{//无版本参数和指定版本的参数
					PreparedStatement dist_stmt = null;
					long rsid = 0l;
					rs.close();
					rs = src_stmt.executeQuery("SELECT * FROM fms_config WHERE parentid IS NULL AND (IFNULL(nodetype,0)<>1 OR (IFNULL(nodetype,0)=1 AND id IN ("+ids+")))");
					RowSetFactory rowSetFactory = RowSetProvider.newFactory();
					CachedRowSet crs = rowSetFactory.createCachedRowSet();
				    crs.populate(rs);
					while(crs.next()){
						if(dist_stmt == null){
							//dist_conn.setAutoCommit(false);
							dist_stmt = dist_conn.prepareStatement("insert into offline_config(ID,parentID,identity,nodetype,disptype,disporder,status,code,name,memo,value1,value2,value3,value4,value5,extend) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						}
						rsid = crs.getLong("id");
						dist_stmt.setLong(1, rsid);
						dist_stmt.setLong(2, crs.getLong("parentid"));
						dist_stmt.setString(3, crs.getString("identity"));
						dist_stmt.setInt(4, crs.getInt("nodetype"));
						dist_stmt.setInt(5, crs.getInt("disptype"));
						dist_stmt.setInt(6, crs.getInt("disporder"));
						dist_stmt.setInt(7, crs.getInt("status"));
						dist_stmt.setString(8, crs.getString("code"));
						dist_stmt.setString(9, crs.getString("name"));
						dist_stmt.setString(10, crs.getString("memo"));
						dist_stmt.setString(11, crs.getString("value1"));
						dist_stmt.setString(12, crs.getString("value2"));
						dist_stmt.setString(13, crs.getString("value3"));
						dist_stmt.setString(14, crs.getString("value4"));
						dist_stmt.setString(15, crs.getString("value5"));
						dist_stmt.setString(16, crs.getString("extend"));
						
						dist_stmt.addBatch();
						
						insertConfigChild(dist_stmt,ids,rsid);
					}
					if(dist_stmt != null){
						dist_stmt.executeBatch();
						//dist_conn.setAutoCommit(true); 
						dist_stmt.close();
					}
					crs.close();	
				}
			} catch (Throwable t) {
				logger.error("查询错误：" + sql + "，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		private boolean insertConfigChild(PreparedStatement dist_stmt, String ids, long id){
			long rsid = 0l;
			try {
				ResultSet rs = src_stmt.executeQuery("SELECT * FROM fms_config WHERE parentid ="+id+" AND (IFNULL(nodetype,0)<>1 OR (IFNULL(nodetype,0)=1 AND id IN ("+ids+")))");
				RowSetFactory rowSetFactory = RowSetProvider.newFactory();
				CachedRowSet crs = rowSetFactory.createCachedRowSet();
			    crs.populate(rs);
				while(crs.next()){
					rsid = crs.getLong("id");
					dist_stmt.setLong(1, rsid);
					dist_stmt.setLong(2, crs.getLong("parentid"));
					dist_stmt.setString(3, crs.getString("identity"));
					dist_stmt.setInt(4, crs.getInt("nodetype"));
					dist_stmt.setInt(5, crs.getInt("disptype"));
					dist_stmt.setInt(6, crs.getInt("disporder"));
					dist_stmt.setInt(7, crs.getInt("status"));
					dist_stmt.setString(8, crs.getString("code"));
					dist_stmt.setString(9, crs.getString("name"));
					dist_stmt.setString(10, crs.getString("memo"));
					dist_stmt.setString(11, crs.getString("value1"));
					dist_stmt.setString(12, crs.getString("value2"));
					dist_stmt.setString(13, crs.getString("value3"));
					dist_stmt.setString(14, crs.getString("value4"));
					dist_stmt.setString(15, crs.getString("value5"));
					dist_stmt.setString(16, crs.getString("extend"));
					
					dist_stmt.addBatch();
					
					insertConfigChild(dist_stmt,ids,rsid);
				}
				crs.close();
			} catch (Throwable t) {
				logger.error("查询错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		
		private boolean export_fms_businessrule() {
			try {
				ResultSet rs = src_stmt.executeQuery("SELECT GROUP_CONCAT(ruleid) ids FROM fms_businessrule_version_items WHERE param_version_id IN (SELECT param_version_id FROM fms_version WHERE version_id IN (SELECT version_id FROM fms_contractor_database WHERE lib_id=" + lib_id + "))");
				String ids = null;
				if(rs.next()){
					ids = rs.getString(1);
				}
				if(ids == null || ids.length() <= 0){//全部参数
					PreparedStatement dist_stmt = null;
					rs.close();
					rs = src_stmt.executeQuery("SELECT * FROM fms_businessrule");
					while(rs.next()){
						if(dist_stmt == null){
							//dist_conn.setAutoCommit(false); 
							dist_stmt = dist_conn.prepareStatement("insert into offline_businessrule(ruleid,ruleversion,rulecode,rulename,ruleexpress,instanse,sort,remark,status) values(?,?,?,?,?,?,?,?,?)");
						}
						dist_stmt.setLong(1, rs.getLong("ruleid"));
						dist_stmt.setString(2, rs.getString("ruleversion"));
						dist_stmt.setString(3, rs.getString("rulecode"));
						dist_stmt.setString(4, rs.getString("rulename"));
						dist_stmt.setString(5, rs.getString("ruleexpress"));
						dist_stmt.setString(6, rs.getString("instanse"));
						dist_stmt.setString(7, rs.getString("sort"));
						dist_stmt.setString(8, rs.getString("remark"));
						dist_stmt.setString(9, rs.getString("status"));
						
						dist_stmt.addBatch();
					}
					if(dist_stmt != null){
						dist_stmt.executeBatch();
						//dist_conn.setAutoCommit(true);
						dist_stmt.close();
						dist_stmt = null;
					}
					rs.close();
					rs = src_stmt.executeQuery("SELECT * FROM fms_businessrule_clause");
					while(rs.next()){
						if(dist_stmt == null){
							//dist_conn.setAutoCommit(false);
							dist_stmt = dist_conn.prepareStatement("insert into offline_businessrule_clause(clauseid,businessruleid,clausename,clausecode,clauseexample,isedit,sort,remark,status) values(?,?,?,?,?,?,?,?,?)");
						}
						dist_stmt.setLong(1, rs.getLong("clauseid"));
						dist_stmt.setLong(2, rs.getLong("businessruleid"));
						dist_stmt.setString(3, rs.getString("clausename"));
						dist_stmt.setString(4, rs.getString("clausecode"));
						dist_stmt.setString(5, rs.getString("clauseexample"));
						dist_stmt.setString(6, rs.getString("isedit"));
						dist_stmt.setInt(7, rs.getInt("sort"));
						dist_stmt.setString(8, rs.getString("remark"));
						dist_stmt.setString(9, rs.getString("status"));
						
						dist_stmt.addBatch();
					}
					if(dist_stmt != null){
						dist_stmt.executeBatch();
						//dist_conn.setAutoCommit(true);
						dist_stmt.close();
						dist_stmt = null;
					}
					rs.close();
				}
				else{//指定版本的参数
					PreparedStatement dist_stmt = null;
					rs.close();
					rs = src_stmt.executeQuery("SELECT * FROM fms_businessrule WHERE ruleid IN ("+ids+")");
					while(rs.next()){
						if(dist_stmt == null){
							//dist_conn.setAutoCommit(false); 
							dist_stmt = dist_conn.prepareStatement("insert into offline_businessrule(ruleid,ruleversion,rulecode,rulename,ruleexpress,instanse,sort,remark,status) values(?,?,?,?,?,?,?,?,?)");
						}
						dist_stmt.setLong(1, rs.getLong("ruleid"));
						dist_stmt.setString(2, rs.getString("ruleversion"));
						dist_stmt.setString(3, rs.getString("rulecode"));
						dist_stmt.setString(4, rs.getString("rulename"));
						dist_stmt.setString(5, rs.getString("ruleexpress"));
						dist_stmt.setString(6, rs.getString("instanse"));
						dist_stmt.setString(7, rs.getString("sort"));
						dist_stmt.setString(8, rs.getString("remark"));
						dist_stmt.setString(9, rs.getString("status"));
						
						dist_stmt.addBatch();
					}
					if(dist_stmt != null){
						dist_stmt.executeBatch();
						//dist_conn.setAutoCommit(true);
						dist_stmt.close();
						dist_stmt = null;
					}
					rs.close();
					rs = src_stmt.executeQuery("SELECT * FROM fms_businessrule_clause WHERE businessruleid IN ("+ids+")");
					while(rs.next()){
						if(dist_stmt == null){
							//dist_conn.setAutoCommit(false);
							dist_stmt = dist_conn.prepareStatement("insert into offline_businessrule_clause(clauseid,businessruleid,clausename,clausecode,clauseexample,isedit,sort,remark,status) values(?,?,?,?,?,?,?,?,?)");
						}
						dist_stmt.setLong(1, rs.getLong("clauseid"));
						dist_stmt.setLong(2, rs.getLong("businessruleid"));
						dist_stmt.setString(3, rs.getString("clausename"));
						dist_stmt.setString(4, rs.getString("clausecode"));
						dist_stmt.setString(5, rs.getString("clauseexample"));
						dist_stmt.setString(6, rs.getString("isedit"));
						dist_stmt.setInt(7, rs.getInt("sort"));
						dist_stmt.setString(8, rs.getString("remark"));
						dist_stmt.setString(9, rs.getString("status"));
						
						dist_stmt.addBatch();
					}
					if(dist_stmt != null){
						dist_stmt.executeBatch();
						//dist_conn.setAutoCommit(true);
						dist_stmt.close();
						dist_stmt = null;
					}
					rs.close();
				}
			} catch (Throwable t) {
				logger.error("查询错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		
		private boolean export_fms_library() {
			try {
				ResultSet rs = src_stmt.executeQuery("select * from fms_version where version_id in (select version_id from fms_contractor_database where lib_id=" + lib_id + ")");
				String versionId = null;
				if(rs.next()){
					versionId = rs.getString("version_id");
				}
				rs.close();
				if(versionId == null || versionId.length() <= 0){
					logger.error("查询版本信息错误，导出失败！");
					return false;
				}
				
				rs = src_stmt.executeQuery("select * from fms_contractor_database where lib_id=" + lib_id);
				String fs_ids = null;
				if(rs.next()){
					fs_ids = rs.getString("fs_ids");
				}
				rs.close();
				if(fs_ids == null || fs_ids.length() <= 0){
					logger.error("查询专业信息错误，导出失败！");
					return false;
				}
				
				PreparedStatement dist_stmt = null;
				String rsid = null;
				rs = src_stmt.executeQuery("SELECT DISTINCT * FROM fms_library_"+versionId+" WHERE id_type=1 AND fs_id IN (SELECT parent_id FROM fms_library_"+versionId+" WHERE fs_id IN ("+fs_ids+"))");
				RowSetFactory rowSetFactory = RowSetProvider.newFactory();
				CachedRowSet crs = rowSetFactory.createCachedRowSet();
			    crs.populate(rs);
				while(crs.next()){
					if(dist_stmt == null){
						//dist_conn.setAutoCommit(false);
						dist_stmt = dist_conn.prepareStatement("insert into offline_nav(nav_id,parent_id,id_type,code,name,memo,id_status,disp_order) values(?,?,?,?,?,?,?,?)");
					}
					rsid = crs.getString("fs_id");
					dist_stmt.setString(1, rsid);
					dist_stmt.setString(2, crs.getString("parent_id"));
					dist_stmt.setString(3, crs.getString("id_type"));
					dist_stmt.setString(4, crs.getString("fs_code"));
					dist_stmt.setString(5, crs.getString("fs_name"));
					dist_stmt.setString(6, crs.getString("fs_memo"));
					dist_stmt.setInt(7, crs.getInt("id_status"));
					dist_stmt.setInt(8, crs.getInt("disp_order"));
					
					dist_stmt.addBatch();
					
					insertLibraryChild(dist_stmt,fs_ids,versionId,rsid);
				}
				if(dist_stmt != null){
					dist_stmt.executeBatch();
					//dist_conn.setAutoCommit(true); 
					dist_stmt.close();
				}
				crs.close();	
				
				export_fms_foldertemplate(versionId);
				export_fms_formsfiles(versionId);
			} catch (Throwable t) {
				logger.error("查询错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			
			return true;
		}
		private boolean insertLibraryChild(PreparedStatement dist_stmt, String fs_ids, String versionId, String id){
			String rsid = null;
			boolean isFind = false;
			try {
				ResultSet rs = src_stmt.executeQuery("SELECT * FROM fms_library_"+versionId+" WHERE parent_id='"+id+"' AND (id_type<>2 OR (id_type=2 AND fs_id IN ("+fs_ids+")))");
				RowSetFactory rowSetFactory = RowSetProvider.newFactory();
				CachedRowSet crs = rowSetFactory.createCachedRowSet();
			    crs.populate(rs);
				while(crs.next()){
					isFind = true;
					rsid = crs.getString("fs_id");
					dist_stmt.setString(1, rsid);
					dist_stmt.setString(2, crs.getString("parent_id"));
					dist_stmt.setString(3, crs.getString("id_type"));
					dist_stmt.setString(4, crs.getString("fs_code"));
					dist_stmt.setString(5, crs.getString("fs_name"));
					dist_stmt.setString(6, crs.getString("fs_memo"));
					dist_stmt.setInt(7, crs.getInt("id_status"));
					dist_stmt.setInt(8, crs.getInt("disp_order"));
					
					dist_stmt.addBatch();
					
					insertLibraryChild(dist_stmt,fs_ids,versionId,rsid);
				}
				crs.close();
				
				if(!isFind){
					ResultSet rs2 = src_stmt.executeQuery("SELECT * FROM fms_library_"+versionId+" WHERE parent_id='"+id+"'");
					CachedRowSet crs2 = rowSetFactory.createCachedRowSet();
				    crs2.populate(rs2);
					while(crs2.next()){
						rsid = crs2.getString("fs_id");
						dist_stmt.setString(1, rsid);
						dist_stmt.setString(2, crs2.getString("parent_id"));
						dist_stmt.setString(3, crs2.getString("id_type"));
						dist_stmt.setString(4, crs2.getString("fs_code"));
						dist_stmt.setString(5, crs2.getString("fs_name"));
						dist_stmt.setString(6, crs2.getString("fs_memo"));
						dist_stmt.setInt(7, crs2.getInt("id_status"));
						dist_stmt.setInt(8, crs2.getInt("disp_order"));
						
						dist_stmt.addBatch();
						
						insertLibraryChild(dist_stmt,fs_ids,versionId,rsid);
					}
					crs2.close();
				}
			} catch (Throwable t) {
				logger.error("查询错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		
		private boolean export_fms_formsfiles(String versionId) {
			try {
				Statement stmt = dist_conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT nav_id FROM offline_nav WHERE id_type=5");
				StringBuilder sb = new StringBuilder();
				while(rs.next()){
					if(sb.length() > 0){
						sb.append(",");
					}
					sb.append("'").append(rs.getString(1)).append("'");
				}
				stmt.close();
				rs.close();
				if(sb.length() <= 0){
					return false;
				}
				
				PreparedStatement dist_stmt = null;
				rs = src_stmt.executeQuery("SELECT A.*,B.fs_id,B.tb_sn,(SELECT file_identify FROM fms_files_"+versionId+" WHERE file_id=A.tmpl_file_id) AS tmpl_file_identify,(SELECT file_identify FROM fms_files_"+versionId+" WHERE file_id=A.example_file_id) AS example_file_identify FROM fms_formsfiles_"+versionId+" A INNER JOIN fms_foldertemplate_formsfiles_"+versionId+" B ON A.tb_id=B.tb_id WHERE B.fs_id IN ("+sb.toString()+")");
				while(rs.next()){
					if(dist_stmt == null){
						//dist_conn.setAutoCommit(false);
						dist_stmt = dist_conn.prepareStatement("insert into offline_formsfiles(tb_id,nav_id,tmpl_file_id,example_file_id,document_number,fnsort_table,document_no,file_title,retention_period,ssecrecy_level,archived_copies,paper_size,total_pages,fill_in_rules,create_date,last_date) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					}
					dist_stmt.setString(1, rs.getString("tb_id"));
					dist_stmt.setString(2, rs.getString("fs_id"));
					dist_stmt.setString(3, rs.getString("tmpl_file_identify"));
					dist_stmt.setString(4, rs.getString("example_file_identify"));
					dist_stmt.setString(5, rs.getString("document_number"));
					dist_stmt.setInt(6, rs.getInt("tb_sn"));
					dist_stmt.setString(7, rs.getString("document_no"));
					dist_stmt.setString(8, rs.getString("file_title"));
					dist_stmt.setString(9, rs.getString("retention_period"));
					dist_stmt.setString(10, rs.getString("ssecrecy_level"));
					dist_stmt.setInt(11, rs.getInt("archived_copies"));
					dist_stmt.setString(12, rs.getString("paper_size"));
					dist_stmt.setInt(13, rs.getInt("total_pages"));
					dist_stmt.setString(14, rs.getString("fill_in_rules"));
					dist_stmt.setString(15, rs.getString("create_date"));
					dist_stmt.setString(16, rs.getString("last_date"));
					
					dist_stmt.addBatch();
					
					copyFile(rs.getString("tmpl_file_identify"));
					copyFile(rs.getString("example_file_identify"));
				}
				if(dist_stmt != null){
					dist_stmt.executeBatch();
					//dist_conn.setAutoCommit(true);
					dist_stmt.close();
				}
				rs.close();				
			} catch (Throwable t) {
				logger.error("查询错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		private boolean copyFile(String fileId){
			if(fileId == null || fileId.length() <= 0){
				return false;
			}
			String fileid = fileId.toUpperCase();
			int a = fileid.indexOf('A');
			if(a <= 0){
				return false;
			}
			String dt = fileid.substring(0, a);
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(Long.parseLong(dt));
			int year = cal.get(Calendar.YEAR);//获取年份
	        int month = cal.get(Calendar.MONTH)+1;//获取月份 
	        int day = cal.get(Calendar.DATE);//获取日 
	        
	        String dir = year+"/"+month+"/"+day+"/";
			String filePath = a < 0 ?fileid:dir+fileid;
			String srcFilename = root_path + filePath;
			String distFilename = dist_path + filePath;
			
			logger.debug("复制文件："+srcFilename);
			if (FileUtils.copyFile(srcFilename, distFilename) <= 0) {
				return false;
			}
			return true;
		}
		
		private boolean export_fms_foldertemplate(String versionId) {
			try {
				Statement stmt = dist_conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT nav_id FROM offline_nav WHERE id_type=4");
				StringBuilder sb = new StringBuilder();
				while(rs.next()){
					if(sb.length() > 0){
						sb.append(",");
					}
					sb.append("'").append(rs.getString(1)).append("'");
				}
				stmt.close();
				rs.close();
				if(sb.length() <= 0){
					return false;
				}
				
				PreparedStatement dist_stmt = null;
				rs = src_stmt.executeQuery("SELECT * FROM fms_foldertemplate_"+versionId+" WHERE fs_id IN ("+sb.toString()+")");
				while(rs.next()){
					if(dist_stmt == null){
						//dist_conn.setAutoCommit(false);
						dist_stmt = dist_conn.prepareStatement("insert into offline_foldertemplate(ag_id,nav_id,retention_period,security_classification,document_number,catalog_code,start_date,end_date,organization,organize_date,piece_number,archived_copies,total_pages,drawing_number,written_number,photo_number,build_user,build_date,check_user,check_date,storage_location,vice_location,handed_status,city_construction,create_date,last_date) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					}
					dist_stmt.setString(1, rs.getString("ag_id"));
					dist_stmt.setString(2, rs.getString("fs_id"));
					dist_stmt.setString(3, rs.getString("retention_period"));
					dist_stmt.setString(4, rs.getString("security_classification"));
					dist_stmt.setString(5, rs.getString("document_number"));
					dist_stmt.setString(6, rs.getString("catalog_code"));
					dist_stmt.setString(7, rs.getString("start_date"));
					dist_stmt.setString(8, rs.getString("end_date"));
					dist_stmt.setString(9, rs.getString("organization"));
					dist_stmt.setString(10, rs.getString("organize_date"));
					dist_stmt.setInt(11, rs.getInt("piece_number"));
					dist_stmt.setInt(12, rs.getInt("archived_copies"));
					dist_stmt.setInt(13, rs.getInt("total_pages"));
					dist_stmt.setInt(14, rs.getInt("drawing_number"));
					dist_stmt.setInt(15, rs.getInt("written_number"));
					dist_stmt.setInt(16, rs.getInt("photo_number"));
					dist_stmt.setString(17, rs.getString("build_user"));
					dist_stmt.setString(18, rs.getString("build_date"));
					dist_stmt.setString(19, rs.getString("check_user"));
					dist_stmt.setString(20, rs.getString("check_date"));
					dist_stmt.setString(21, rs.getString("storage_location"));
					dist_stmt.setString(22, rs.getString("vice_location"));
					dist_stmt.setString(23, rs.getString("handed_status"));
					dist_stmt.setString(24, rs.getString("city_construction"));
					dist_stmt.setString(25, rs.getString("create_date"));
					dist_stmt.setString(26, rs.getString("last_date"));
					
					dist_stmt.addBatch();
				}
				if(dist_stmt != null){
					dist_stmt.executeBatch();
					//dist_conn.setAutoCommit(true);
					dist_stmt.close();
				}
				rs.close();				
			} catch (Throwable t) {
				logger.error("查询错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
	}
}
