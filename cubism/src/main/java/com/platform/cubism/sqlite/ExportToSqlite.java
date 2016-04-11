package com.platform.cubism.sqlite;

import static com.platform.cubism.util.FileUtils.copyFile;
import static com.platform.cubism.SystemConfig.getUploadPath;
import static com.platform.cubism.util.CubismHelper.getAppRootDir;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

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

public class ExportToSqlite implements CustomService{
	private static Logger logger = LoggerFactory.getLogger(ExportToSqlite.class);
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
		List<String> sql = new ArrayList<String>(6000);
		StringBuilder navType4 = new StringBuilder();
		StringBuilder navType5 = new StringBuilder();
		
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
			
			
			
			try {
				dist_conn.setAutoCommit(false);
				Statement stmt = dist_conn.createStatement();
				for(String s : sql){
					stmt.execute(s);
				}
				dist_conn.commit();
				//dist_conn.setAutoCommit(true);
			} catch (SQLException e) {
				logger.error("提交数据库事物错误，出错信息为:" + e.getMessage());
				if (logger.isDebugEnabled()) {
					e.printStackTrace();
				}
				closeConn(true);
				return ;
			}
			logger.debug("9:数据写入sqlite库完毕");
			
			String fileName = updateNdfile();
			closeConn(false);
			if (fileName == null || fileName.length() <= 0) {
				return ;
			}
			logger.debug("10:下载路径更新成功");
			
			ExportStatus.setStatus(lib_id, "1");
			new File(dist_path + lib_id + "/" + ExportToSqlite.DB_NAME).renameTo(new File(dist_path + lib_id + "/" + fileName.substring(0,fileName.lastIndexOf('.')) + ExportToSqlite.DB_NAME.substring(ExportToSqlite.DB_NAME.lastIndexOf('.'))));
			
			String zipFileName = SecurityHelper.bytes2HexString(fileName.getBytes());
			FileUtils.zip(dist_path + lib_id + "/", dist_path, zipFileName,false);
			FileUtils.delFile(dist_path + lib_id);
			logger.debug("11:ND压缩包创建成功");
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
		private String toNull(String s){
			if(s == null || s.length() <= 0){
				return "null";
			}
			else{
				//return "'"+s.replaceAll("\n", "\\\\n")+"'";
				return "'"+s.replaceAll("'", "''")+"'";
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
					StringBuilder sb = new StringBuilder();
					sb.append("insert into offline_version(version_id,lib_id,version_num,version_name,version_memo,create_date,last_date) values(");
					sb.append(rs.getLong("version_id")).append(",").append(lib_id).append(",");
					sb.append(toNull(rs.getString("version_num"))).append(",");
					sb.append(toNull(rs.getString("version_name"))).append(",");
					sb.append(toNull(rs.getString("version_memo"))).append(",");
					sb.append(toNull(rs.getString("create_date"))).append(",");
					sb.append(toNull(rs.getString("last_date"))).append(")");					
					sql.add(sb.toString());
				}
				else{
					return false;
				}
			} catch (Throwable t) {
				logger.error("查询版本信息错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			
			return true;
		}
		private boolean export_fms_contractor_basicinfo() {
			try {
				ResultSet rs = src_stmt.executeQuery("SELECT * FROM fms_contractor_basicinfo WHERE contractor_id IN (SELECT contractor_id FROM fms_contractor_user WHERE user_id IN (SELECT user_id FROM fms_contractor_database WHERE lib_id=" + lib_id + ") UNION ALL SELECT contractor_id FROM fms_contractor_documentor WHERE documentor_id IN (SELECT documentor_id FROM fms_contractor_user WHERE user_id IN (SELECT user_id FROM fms_contractor_database WHERE lib_id=" + lib_id + ")))");
				if(rs.next()){
					StringBuilder sb = new StringBuilder();
					sb.append("insert into offline_contractor_basicinfo(contractor_id,participation_units,company_name,organizing_code,registration_authority,business_license_reg_num,business_license_reg_auth,enterprise_type,registered_capital,paid_registered_capital,establishment_date,domicile,operating_period,legal_representative,contact_phone,business_scope,change_description,attachment,create_date,create_user,last_date,last_user,approval_status,approval_date,approval_memo,approval_user,mobile_no,email,isdel,account_code_pic,business_license_pic,submit_approval_date,database_status) values(");
					sb.append(toNull(rs.getString("contractor_id"))).append(",");
					sb.append(toNull(rs.getString("participation_units"))).append(",");
					sb.append(toNull(rs.getString("company_name"))).append(",");
					sb.append(toNull(rs.getString("organizing_code"))).append(",");
					sb.append(toNull(rs.getString("registration_authority"))).append(",");
					sb.append(toNull(rs.getString("business_license_reg_num"))).append(",");
					sb.append(toNull(rs.getString("business_license_reg_auth"))).append(",");
					sb.append(toNull(rs.getString("enterprise_type"))).append(",");
					sb.append(toNull(rs.getString("registered_capital"))).append(",");
					sb.append(toNull(rs.getString("paid_registered_capital"))).append(",");
					sb.append(toNull(rs.getString("establishment_date"))).append(",");
					sb.append(toNull(rs.getString("domicile"))).append(",");
					sb.append(toNull(rs.getString("operating_period"))).append(",");
					sb.append(toNull(rs.getString("legal_representative"))).append(",");
					sb.append(toNull(rs.getString("contact_phone"))).append(",");
					sb.append(toNull(rs.getString("business_scope"))).append(",");
					sb.append(toNull(rs.getString("change_description"))).append(",");
					sb.append(toNull(rs.getString("attachment"))).append(",");
					sb.append(toNull(rs.getString("create_date"))).append(",");
					sb.append(toNull(rs.getString("create_user"))).append(",");
					sb.append(toNull(rs.getString("last_date"))).append(",");
					sb.append(toNull(rs.getString("last_user"))).append(",");
					sb.append(toNull(rs.getString("approval_status"))).append(",");
					sb.append(toNull(rs.getString("approval_date"))).append(",");
					sb.append(toNull(rs.getString("approval_memo"))).append(",");
					sb.append(rs.getInt("approval_user")).append(",");
					sb.append(toNull(rs.getString("mobile_no"))).append(",");
					sb.append(toNull(rs.getString("email"))).append(",");
					sb.append(rs.getInt("isdel")).append(",");
					sb.append(toNull(rs.getString("account_code_pic"))).append(",");
					sb.append(toNull(rs.getString("business_license_pic"))).append(",");
					sb.append(toNull(rs.getString("submit_approval_date"))).append(",");
					sb.append(rs.getInt("database_status")).append(")");					
					sql.add(sb.toString());
				}
				else{
					return false;
				}
			} catch (Throwable t) {
				logger.error("查询建设单位信息错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		private boolean export_fms_contractor_documentor() {
			try {
				ResultSet rs = src_stmt.executeQuery("select * from fms_contractor_documentor where documentor_id in (select documentor_id from fms_contractor_user where user_id in (select user_id from fms_contractor_database where lib_id=" + lib_id + "))");
				if(rs.next()){
					StringBuilder sb = new StringBuilder();
					sb.append("insert into offline_contractor_documentor(documentor_id,contractor_id,register_date,unit_mobile_no,documentor_name,documentor_sex,documentor_birthday,documentor_cardid,documentor_post,documentor_education,graduate_institutions,profession,working_date,mobile_no,email,tenders_code,contract_name,project_name,construction_unit_idea,supervision_unit_idea,construction_division_idea,information_centre_idea,construction_unit_date,supervision_unit_date,construction_division_date,information_centre_date) values(");
					sb.append(toNull(rs.getString("documentor_id"))).append(",");
					sb.append(toNull(rs.getString("contractor_id"))).append(",");
					sb.append(toNull(rs.getString("register_date"))).append(",");
					sb.append(toNull(rs.getString("unit_mobile_no"))).append(",");
					sb.append(toNull(rs.getString("documentor_name"))).append(",");
					sb.append(toNull(rs.getString("documentor_sex"))).append(",");
					sb.append(toNull(rs.getString("documentor_birthday"))).append(",");
					sb.append(toNull(rs.getString("documentor_cardid"))).append(",");
					sb.append(toNull(rs.getString("documentor_post"))).append(",");
					sb.append(toNull(rs.getString("documentor_education"))).append(",");
					sb.append(toNull(rs.getString("graduate_institutions"))).append(",");
					sb.append(toNull(rs.getString("profession"))).append(",");
					sb.append(toNull(rs.getString("working_date"))).append(",");
					sb.append(toNull(rs.getString("mobile_no"))).append(",");
					sb.append(toNull(rs.getString("email"))).append(",");
					sb.append(toNull(rs.getString("tenders_code"))).append(",");
					sb.append(toNull(rs.getString("contract_name"))).append(",");
					sb.append(toNull(rs.getString("project_name"))).append(",");
					sb.append(toNull(rs.getString("construction_unit_idea"))).append(",");
					sb.append(toNull(rs.getString("supervision_unit_idea"))).append(",");
					sb.append(toNull(rs.getString("construction_division_idea"))).append(",");
					sb.append(toNull(rs.getString("information_centre_idea"))).append(",");
					sb.append(toNull(rs.getString("construction_unit_date"))).append(",");
					sb.append(toNull(rs.getString("supervision_unit_date"))).append(",");
					sb.append(toNull(rs.getString("construction_division_date"))).append(",");
					sb.append(toNull(rs.getString("information_centre_date"))).append(")");					
					sql.add(sb.toString());
				}
			} catch (Throwable t) {
				logger.error("查询资料员信息错误：，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		
		private boolean export_fms_config() {
			try {
				ResultSet rs = src_stmt.executeQuery("SELECT GROUP_CONCAT(config_id) ids FROM fms_config_version_items WHERE param_version_id IN (SELECT param_version_id FROM fms_version WHERE version_id IN (SELECT version_id FROM fms_contractor_database WHERE lib_id=" + lib_id + "))");
				String ids = null;
				if(rs.next()){
					ids = rs.getString(1);
				}
				
				if(ids == null || ids.length() <= 0){//全部参数
					rs.close();
					rs = src_stmt.executeQuery("SELECT * FROM fms_config");
					while(rs.next()){
						StringBuilder sb = new StringBuilder();
						sb.append("insert into offline_config(ID,parentID,identity,nodetype,disptype,disporder,status,code,name,memo,value1,value2,value3,value4,value5,extend) values(");
						sb.append(rs.getLong("id")).append(",");
						sb.append(rs.getLong("parentid")).append(",");
						sb.append(toNull(rs.getString("identity"))).append(",");
						sb.append(rs.getInt("nodetype")).append(",");
						sb.append(rs.getInt("disptype")).append(",");
						sb.append(rs.getInt("disporder")).append(",");
						sb.append(rs.getInt("status")).append(",");
						sb.append(toNull(rs.getString("code"))).append(",");
						sb.append(toNull(rs.getString("name"))).append(",");
						sb.append(toNull(rs.getString("memo"))).append(",");
						sb.append(toNull(rs.getString("value1"))).append(",");
						sb.append(toNull(rs.getString("value2"))).append(",");
						sb.append(toNull(rs.getString("value3"))).append(",");
						sb.append(toNull(rs.getString("value4"))).append(",");
						sb.append(toNull(rs.getString("value5"))).append(",");
						sb.append(toNull(rs.getString("extend"))).append(")");					
						sql.add(sb.toString());
					}
				}
				else{//无版本参数和指定版本的参数
					rs.close();
					List<Long> rsids = new ArrayList<Long>();
					rs = src_stmt.executeQuery("SELECT * FROM fms_config WHERE parentid IS NULL AND (IFNULL(nodetype,0)<>1 OR (IFNULL(nodetype,0)=1 AND id IN ("+ids+")))");
					while(rs.next()){
						rsids.add(rs.getLong("id"));
						StringBuilder sb = new StringBuilder();
						sb.append("insert into offline_config(ID,parentID,identity,nodetype,disptype,disporder,status,code,name,memo,value1,value2,value3,value4,value5,extend) values(");
						sb.append(rs.getLong("id")).append(",");
						sb.append(rs.getLong("parentid")).append(",");
						sb.append(toNull(rs.getString("identity"))).append(",");
						sb.append(rs.getInt("nodetype")).append(",");
						sb.append(rs.getInt("disptype")).append(",");
						sb.append(rs.getInt("disporder")).append(",");
						sb.append(rs.getInt("status")).append(",");
						sb.append(toNull(rs.getString("code"))).append(",");
						sb.append(toNull(rs.getString("name"))).append(",");
						sb.append(toNull(rs.getString("memo"))).append(",");
						sb.append(toNull(rs.getString("value1"))).append(",");
						sb.append(toNull(rs.getString("value2"))).append(",");
						sb.append(toNull(rs.getString("value3"))).append(",");
						sb.append(toNull(rs.getString("value4"))).append(",");
						sb.append(toNull(rs.getString("value5"))).append(",");
						sb.append(toNull(rs.getString("extend"))).append(")");					
						sql.add(sb.toString());
					}
					for(long rsid : rsids){
						insertConfigChild(ids,rsid);
					}
				}
			} catch (Throwable t) {
				logger.error("查询参数设置信息错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		private boolean insertConfigChild(String ids, long id){
			List<Long> rsids = new ArrayList<Long>();
			try {
				ResultSet rs = src_stmt.executeQuery("SELECT * FROM fms_config WHERE parentid ="+id+" AND (IFNULL(nodetype,0)<>1 OR (IFNULL(nodetype,0)=1 AND id IN ("+ids+")))");
				while(rs.next()){
					rsids.add(rs.getLong("id"));
					StringBuilder sb = new StringBuilder();
					sb.append("insert into offline_config(ID,parentID,identity,nodetype,disptype,disporder,status,code,name,memo,value1,value2,value3,value4,value5,extend) values(");
					sb.append(rs.getLong("id")).append(",");
					sb.append(rs.getLong("parentid")).append(",");
					sb.append(toNull(rs.getString("identity"))).append(",");
					sb.append(rs.getInt("nodetype")).append(",");
					sb.append(rs.getInt("disptype")).append(",");
					sb.append(rs.getInt("disporder")).append(",");
					sb.append(rs.getInt("status")).append(",");
					sb.append(toNull(rs.getString("code"))).append(",");
					sb.append(toNull(rs.getString("name"))).append(",");
					sb.append(toNull(rs.getString("memo"))).append(",");
					sb.append(toNull(rs.getString("value1"))).append(",");
					sb.append(toNull(rs.getString("value2"))).append(",");
					sb.append(toNull(rs.getString("value3"))).append(",");
					sb.append(toNull(rs.getString("value4"))).append(",");
					sb.append(toNull(rs.getString("value5"))).append(",");
					sb.append(toNull(rs.getString("extend"))).append(")");					
					sql.add(sb.toString());
				}
				for(long rsid : rsids){
					insertConfigChild(ids,rsid);
				}
			} catch (Throwable t) {
				logger.error("查询参数设置信息错误，出错信息为:" + t.getMessage());
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
					rs.close();
					rs = src_stmt.executeQuery("SELECT * FROM fms_businessrule");
					while(rs.next()){
						StringBuilder sb = new StringBuilder();
						sb.append("insert into offline_businessrule(ruleid,ruleversion,rulecode,rulename,ruleexpress,instanse,sort,remark,status) values(");
						sb.append(rs.getLong("ruleid")).append(",");
						sb.append(toNull(rs.getString("ruleversion"))).append(",");
						sb.append(toNull(rs.getString("rulecode"))).append(",");
						sb.append(toNull(rs.getString("rulename"))).append(",");
						sb.append(toNull(rs.getString("ruleexpress"))).append(",");
						sb.append(toNull(rs.getString("instanse"))).append(",");
						sb.append(toNull(rs.getString("sort"))).append(",");
						sb.append(toNull(rs.getString("remark"))).append(",");
						sb.append(toNull(rs.getString("status"))).append(")");					
						sql.add(sb.toString());
					}
					rs.close();
					rs = src_stmt.executeQuery("SELECT * FROM fms_businessrule_clause");
					while(rs.next()){
						StringBuilder sb = new StringBuilder();
						sb.append("insert into offline_businessrule_clause(clauseid,businessruleid,clausename,clausecode,clauseexample,isedit,sort,remark,status) values(");
						sb.append(rs.getLong("clauseid")).append(",");
						sb.append(rs.getLong("businessruleid")).append(",");
						sb.append(toNull(rs.getString("clausename"))).append(",");
						sb.append(toNull(rs.getString("clausecode"))).append(",");
						sb.append(toNull(rs.getString("clauseexample"))).append(",");
						sb.append(toNull(rs.getString("isedit"))).append(",");
						sb.append(rs.getInt("sort")).append(",");
						sb.append(toNull(rs.getString("remark"))).append(",");
						sb.append(toNull(rs.getString("status"))).append(")");					
						sql.add(sb.toString());
					}
					rs.close();
				}
				else{//指定版本的参数
					rs.close();
					rs = src_stmt.executeQuery("SELECT * FROM fms_businessrule WHERE ruleid IN ("+ids+")");
					while(rs.next()){
						StringBuilder sb = new StringBuilder();
						sb.append("insert into offline_businessrule(ruleid,ruleversion,rulecode,rulename,ruleexpress,instanse,sort,remark,status) values(");
						sb.append(rs.getLong("ruleid")).append(",");
						sb.append(toNull(rs.getString("ruleversion"))).append(",");
						sb.append(toNull(rs.getString("rulecode"))).append(",");
						sb.append(toNull(rs.getString("rulename"))).append(",");
						sb.append(toNull(rs.getString("ruleexpress"))).append(",");
						sb.append(toNull(rs.getString("instanse"))).append(",");
						sb.append(toNull(rs.getString("sort"))).append(",");
						sb.append(toNull(rs.getString("remark"))).append(",");
						sb.append(toNull(rs.getString("status"))).append(")");					
						sql.add(sb.toString());
					}
					rs.close();
					rs = src_stmt.executeQuery("SELECT * FROM fms_businessrule_clause WHERE businessruleid IN ("+ids+")");
					while(rs.next()){
						StringBuilder sb = new StringBuilder();
						sb.append("insert into offline_businessrule_clause(clauseid,businessruleid,clausename,clausecode,clauseexample,isedit,sort,remark,status) values(");
						sb.append(rs.getLong("clauseid")).append(",");
						sb.append(rs.getLong("businessruleid")).append(",");
						sb.append(toNull(rs.getString("clausename"))).append(",");
						sb.append(toNull(rs.getString("clausecode"))).append(",");
						sb.append(toNull(rs.getString("clauseexample"))).append(",");
						sb.append(toNull(rs.getString("isedit"))).append(",");
						sb.append(rs.getInt("sort")).append(",");
						sb.append(toNull(rs.getString("remark"))).append(",");
						sb.append(toNull(rs.getString("status"))).append(")");					
						sql.add(sb.toString());
					}
					rs.close();
				}
			} catch (Throwable t) {
				logger.error("查询业务规则信息错误，出错信息为:" + t.getMessage());
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
				if(versionId == null || versionId.length() <= 0){
					logger.error("查询版本信息错误，导出失败！");
					return false;
				}
				
				rs = src_stmt.executeQuery("select * from fms_contractor_database where lib_id=" + lib_id);
				String fs_ids = null;
				if(rs.next()){
					fs_ids = rs.getString("fs_ids");
				}
				if(fs_ids == null || fs_ids.length() <= 0){
					logger.error("查询专业信息错误，导出失败！");
					return false;
				}
				
				List<String> rsids = new ArrayList<String>();
				rs = src_stmt.executeQuery("SELECT DISTINCT * FROM fms_library_"+versionId+" WHERE id_type=1 AND fs_id IN (SELECT parent_id FROM fms_library_"+versionId+" WHERE fs_id IN ("+fs_ids+"))");
				while(rs.next()){
					rsids.add(rs.getString("fs_id"));
					
					StringBuilder sb = new StringBuilder();
					sb.append("insert into offline_nav(nav_id,parent_id,id_type,code,name,memo,id_status,disp_order) values(");
					sb.append(toNull(rs.getString("fs_id"))).append(",");
					sb.append(toNull(rs.getString("parent_id"))).append(",");
					sb.append(toNull(rs.getString("id_type"))).append(",");
					sb.append(toNull(rs.getString("fs_code"))).append(",");
					sb.append(toNull(rs.getString("fs_name"))).append(",");
					sb.append(toNull(rs.getString("fs_memo"))).append(",");
					sb.append(rs.getInt("id_status")).append(",");
					sb.append(rs.getInt("disp_order")).append(")");					
					sql.add(sb.toString());
				}
				for(String id : rsids){
					insertLibraryChild(fs_ids,versionId,id);
				}
				export_fms_foldertemplate(versionId);
				export_fms_formsfiles(versionId);
			} catch (Throwable t) {
				logger.error("查询资料库信息错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			
			return true;
		}
		private boolean insertLibraryChild(String fs_ids, String versionId, String id){
			boolean isFind = false;
			
			try {
				List<String> rsids = new ArrayList<String>();
				ResultSet rs = src_stmt.executeQuery("SELECT * FROM fms_library_"+versionId+" WHERE parent_id='"+id+"' AND (id_type<>2 OR (id_type=2 AND fs_id IN ("+fs_ids+")))");
				while(rs.next()){
					isFind = true;
					rsids.add(rs.getString("fs_id"));
					
					StringBuilder sb = new StringBuilder();
					sb.append("insert into offline_nav(nav_id,parent_id,id_type,code,name,memo,id_status,disp_order) values(");
					sb.append(toNull(rs.getString("fs_id"))).append(",");
					sb.append(toNull(rs.getString("parent_id"))).append(",");
					sb.append(toNull(rs.getString("id_type"))).append(",");
					sb.append(toNull(rs.getString("fs_code"))).append(",");
					sb.append(toNull(rs.getString("fs_name"))).append(",");
					sb.append(toNull(rs.getString("fs_memo"))).append(",");
					sb.append(rs.getInt("id_status")).append(",");
					sb.append(rs.getInt("disp_order")).append(")");					
					sql.add(sb.toString());
					
					if("4".equals(rs.getString("id_type"))){
						if(navType4.length() > 0){
							navType4.append(",");
						}
						navType4.append("'").append(rs.getString("fs_id")).append("'");
					}
					else if("5".equals(rs.getString("id_type"))){
						if(navType5.length() > 0){
							navType5.append(",");
						}
						navType5.append("'").append(rs.getString("fs_id")).append("'");
					}
				}
				for(String rsid : rsids){
					insertLibraryChild(fs_ids,versionId,rsid);
				}
				
				if(!isFind){
					List<String> rsids2 = new ArrayList<String>();
					ResultSet rs2 = src_stmt.executeQuery("SELECT * FROM fms_library_"+versionId+" WHERE parent_id='"+id+"'");
					while(rs2.next()){
						rsids2.add(rs2.getString("fs_id"));
						StringBuilder sb = new StringBuilder();
						sb.append("insert into offline_nav(nav_id,parent_id,id_type,code,name,memo,id_status,disp_order) values(");
						sb.append(toNull(rs2.getString("fs_id"))).append(",");
						sb.append(toNull(rs2.getString("parent_id"))).append(",");
						sb.append(toNull(rs2.getString("id_type"))).append(",");
						sb.append(toNull(rs2.getString("fs_code"))).append(",");
						sb.append(toNull(rs2.getString("fs_name"))).append(",");
						sb.append(toNull(rs2.getString("fs_memo"))).append(",");
						sb.append(rs2.getInt("id_status")).append(",");
						sb.append(rs2.getInt("disp_order")).append(")");					
						sql.add(sb.toString());
						
						if("4".equals(rs2.getString("id_type"))){
							if(navType4.length() > 0){
								navType4.append(",");
							}
							navType4.append("'").append(rs2.getString("fs_id")).append("'");
						}
						else if("5".equals(rs2.getString("id_type"))){
							if(navType5.length() > 0){
								navType5.append(",");
							}
							navType5.append("'").append(rs2.getString("fs_id")).append("'");
						}
					}
					for(String rsid : rsids2){
						insertLibraryChild(fs_ids,versionId,rsid);
					}
				}
			} catch (Throwable t) {
				logger.error("查询资料库信息错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
		
		private boolean export_fms_formsfiles(String versionId) {
			try {
				if(navType5.length() <= 0){
					return false;
				}
				
				ResultSet rs = src_stmt.executeQuery("SELECT A.*,B.fs_id,B.tb_sn,(SELECT file_identify FROM fms_files_"+versionId+" WHERE file_id=A.tmpl_file_id) AS tmpl_file_identify,(SELECT file_identify FROM fms_files_"+versionId+" WHERE file_id=A.example_file_id) AS example_file_identify FROM fms_formsfiles_"+versionId+" A INNER JOIN fms_foldertemplate_formsfiles_"+versionId+" B ON A.tb_id=B.tb_id WHERE B.fs_id IN ("+navType5.toString()+")");
				while(rs.next()){
					StringBuilder sb = new StringBuilder();
					sb.append("insert into offline_formsfiles(tb_id,nav_id,tmpl_file_id,example_file_id,document_number,fnsort_table,document_no,file_title,retention_period,ssecrecy_level,archived_copies,paper_size,total_pages,fill_in_rules,create_date,last_date) values(");
					sb.append(toNull(rs.getString("tb_id"))).append(",");
					sb.append(toNull(rs.getString("fs_id"))).append(",");
					sb.append(toNull(rs.getString("tmpl_file_identify"))).append(",");
					sb.append(toNull(rs.getString("example_file_identify"))).append(",");
					sb.append(toNull(rs.getString("document_number"))).append(",");
					sb.append(rs.getInt("tb_sn")).append(",");
					sb.append(toNull(rs.getString("document_no"))).append(",");
					sb.append(toNull(rs.getString("file_title"))).append(",");
					sb.append(toNull(rs.getString("retention_period"))).append(",");
					sb.append(toNull(rs.getString("ssecrecy_level"))).append(",");
					sb.append(rs.getInt("archived_copies")).append(",");
					sb.append(toNull(rs.getString("paper_size"))).append(",");
					sb.append(rs.getInt("total_pages")).append(",");
					sb.append(toNull(rs.getString("fill_in_rules"))).append(",");
					sb.append(toNull(rs.getString("create_date"))).append(",");
					sb.append(toNull(rs.getString("last_date"))).append(")");					
					sql.add(sb.toString());
					
					copyFile(rs.getString("tmpl_file_identify"));
					copyFile(rs.getString("example_file_identify"));
				}
			} catch (Throwable t) {
				logger.error("查询表格文件信息错误，出错信息为:" + t.getMessage());
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
				if(navType4.length() <= 0){
					return false;
				}
				
				ResultSet rs = src_stmt.executeQuery("SELECT * FROM fms_foldertemplate_"+versionId+" WHERE fs_id IN ("+navType4.toString()+")");
				while(rs.next()){
					StringBuilder sb = new StringBuilder();
					sb.append("insert into offline_foldertemplate(ag_id,nav_id,retention_period,security_classification,document_number,catalog_code,start_date,end_date,organization,organize_date,piece_number,archived_copies,total_pages,drawing_number,written_number,photo_number,build_user,build_date,check_user,check_date,storage_location,vice_location,handed_status,city_construction,create_date,last_date) values(");
					sb.append(toNull(rs.getString("ag_id"))).append(",");
					sb.append(toNull(rs.getString("fs_id"))).append(",");
					sb.append(toNull(rs.getString("retention_period"))).append(",");
					sb.append(toNull(rs.getString("security_classification"))).append(",");
					sb.append(toNull(rs.getString("document_number"))).append(",");
					sb.append(toNull(rs.getString("catalog_code"))).append(",");
					sb.append(toNull(rs.getString("start_date"))).append(",");
					sb.append(toNull(rs.getString("end_date"))).append(",");
					sb.append(toNull(rs.getString("organization"))).append(",");
					sb.append(toNull(rs.getString("organize_date"))).append(",");
					sb.append(rs.getInt("piece_number")).append(",");
					sb.append(rs.getInt("archived_copies")).append(",");
					sb.append(rs.getInt("total_pages")).append(",");
					sb.append(rs.getInt("drawing_number")).append(",");
					sb.append(rs.getInt("written_number")).append(",");
					sb.append(rs.getInt("photo_number")).append(",");
					sb.append(toNull(rs.getString("build_user"))).append(",");
					sb.append(toNull(rs.getString("build_date"))).append(",");
					sb.append(toNull(rs.getString("check_user"))).append(",");
					sb.append(toNull(rs.getString("check_date"))).append(",");
					sb.append(toNull(rs.getString("storage_location"))).append(",");
					sb.append(toNull(rs.getString("vice_location"))).append(",");
					sb.append(toNull(rs.getString("handed_status"))).append(",");
					sb.append(toNull(rs.getString("city_construction"))).append(",");
					sb.append(toNull(rs.getString("create_date"))).append(",");
					sb.append(toNull(rs.getString("last_date"))).append(")");
					sql.add(sb.toString());
				}
			} catch (Throwable t) {
				logger.error("查询案卷信息错误，出错信息为:" + t.getMessage());
				if (logger.isDebugEnabled()) {
					t.printStackTrace();
				}
				return false;
			}
			return true;
		}
	}
}
