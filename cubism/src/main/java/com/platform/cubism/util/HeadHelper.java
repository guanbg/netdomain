package com.platform.cubism.util;

import static com.platform.cubism.SystemConfig.getAdminRights;
import static com.platform.cubism.util.CubismHelper.getServiceName;
import static com.platform.cubism.util.StringUtils.hasText;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;

import com.platform.cubism.CubismException;
import com.platform.cubism.SystemConfig;
import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.front.login.Login;
import com.platform.cubism.front.login.Logon;
import com.platform.cubism.jdbc.SqlProperties.Pagination;
import com.platform.cubism.jdbc.adapter.PageAdapter;
import com.platform.cubism.jdbc.adapter.PageAdapterManager;
import com.platform.cubism.service.convert.ConvertManager;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.PageHead;
import com.platform.cubism.struc.RetHead;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.struc.SysHead;

public class HeadHelper {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(HeadHelper.class);

	public static boolean isSuccess(Json data) {
		if (data == null) {
			return false;
		}
		CStruc ret = data.getStruc(RetHead.RETHEAD.value());
		if (ret == null) {
			return false;
		}
		CField status = ret.getField(RetHead.STATUS.value());
		if (status == null) {
			return false;
		}
		return RetStatus.SUCCESS.value().equals(status.getValue());
	}

	public static boolean isFailed(Json data) {
		if (data == null) {
			return false;
		}
		CStruc ret = data.getStruc(RetHead.RETHEAD.value());
		if (ret == null) {
			return false;
		}
		CField status = ret.getField(RetHead.STATUS.value());
		if (status == null) {
			return false;
		}
		return RetStatus.FAILED.value().equals(status.getValue());
	}

	public static String getRetHeadFirstMsg(Json data) {
		if (data == null) {
			return "";
		}
		CStruc rethead = data.getStruc(RetHead.RETHEAD.value());
		if (rethead == null) {
			return "";
		}
		CArray msgarr = rethead.getArray(RetHead.MSGARR.value());
		if (msgarr == null) {
			return "";
		}
		CStruc msg = msgarr.getRecord(0);
		if (msg == null) {
			return "";
		}
		return msg.getField(RetHead.DESC.value()).getValue();
	}

	public static String getRetHeadMsg(Json data) {
		if (data == null) {
			return "";
		}
		CStruc rethead = data.getStruc(RetHead.RETHEAD.value());
		if (rethead == null) {
			return "";
		}
		CArray msgarr = rethead.getArray(RetHead.MSGARR.value());
		if (msgarr == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (CStruc msg : msgarr.getRecords()) {
			sb.append(msg.getField(RetHead.DESC.value()).getValue()).append("\t");
			sb.append(msg.getField(RetHead.CODE.value()).getValue()).append("\t");
			sb.append(msg.getField(RetHead.LEVEL.value()).getValue()).append("\r\n");
		}
		return sb.toString();
	}

	public static RetStatus getRetStatus(Json data) {
		if (data == null) {
			return null;
		}
		CStruc ret = data.getStruc(RetHead.RETHEAD.value());
		if (ret == null) {
			return null;
		}
		CField status = ret.getField(RetHead.STATUS.value());
		if (status == null) {
			return null;
		}
		return RetStatus.valueOfStr(status.getValue().toUpperCase());
	}
	
	public static String getRetCode(Json data) {
		if (data == null) {
			return null;
		}
		CStruc rethead = data.getStruc(RetHead.RETHEAD.value());
		if (rethead == null) {
			return null;
		}
		CArray msgarr = rethead.getArray(RetHead.MSGARR.value());
		if (msgarr == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (CStruc msg : msgarr.getRecords()) {
			if(sb.length() > 0){
				sb.append(",");
			}
			sb.append(msg.getField(RetHead.CODE.value()).getValue());
		}
		
		return sb.toString();
	}
	/**
	   syshead : { 
	   		useragent:"",
	 		ip : "",
	 		datetime : "",
	  		userid : "",
	  		loginname : "",
	  		username : "",
	  		departid : "",
	  		employeeid : "",
	  		servicename : "" 
	   }
	 */
	public static void createSysHead(Json in, String useragent, String userid, String usertype, String loginname, String username, String departid, String ip, String servicename, String employeeid) {
		if (in == null) {
			return;
		}
		CStruc cs = new CStruc(SysHead.SYSHEAD.value());
		cs.addField(new CField(SysHead.USERAGENT.value(), useragent));
		cs.addField(new CField(SysHead.DATETIME.value(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
		cs.addField(new CField(SysHead.SEQNO.value(), String.valueOf(System.nanoTime())));
		cs.addField(new CField(SysHead.IP.value(), ip));
		cs.addField(new CField(SysHead.USERID.value(), userid));
		cs.addField(new CField(SysHead.USERTYPE.value(), usertype));
		cs.addField(new CField(SysHead.LOGINNAME.value(), loginname));
		cs.addField(new CField(SysHead.USERNAME.value(), username));
		cs.addField(new CField(SysHead.DEPARTID.value(), departid));
		cs.addField(new CField(SysHead.EMPLOYEEID.value(), employeeid));
		cs.addField(new CField(SysHead.SERVICENAME.value(), servicename));

		CStruc stc = in.getStruc(SysHead.SYSHEAD.value());
		if (stc != null && !stc.isEmpty()) {
			cs.mergeOf(stc);
		}
		if (stc != null) {
			in.remove(SysHead.SYSHEAD.value());
		}
		in.addStruc(cs);
	}

	/**
	 * rethead:{ service:"", status:"", msgarr:[ { code:"", desc:"", level:"" }
	 * ] }
	 */
	public static CStruc createRetHead(String service, String code, String desc, MsgLevel level) {
		CStruc cs = new CStruc(RetHead.MSGARR.value());
		cs.addField(new CField(RetHead.CODE.value(), code));
		cs.addField(new CField(RetHead.DESC.value(), desc));
		cs.addField(new CField(RetHead.LEVEL.value(), level.toString()));

		CStruc ret = new CStruc(RetHead.RETHEAD.value());
		ret.addField(new CField(RetHead.STATUS.value(), RetStatus.FAILED.value()));
		ret.addArray(new CArray(RetHead.MSGARR.value()).add(cs));

		return ret;
	}

	public static CStruc createRetHead(String service, String code, String desc, MsgLevel level, RetStatus status) {
		CStruc cs = new CStruc(RetHead.MSGARR.value());
		cs.addField(new CField(RetHead.CODE.value(), code));
		cs.addField(new CField(RetHead.DESC.value(), desc));
		cs.addField(new CField(RetHead.LEVEL.value(), level.toString()));

		CStruc ret = new CStruc(RetHead.RETHEAD.value());
		ret.addField(new CField(RetHead.SERVICE.value(), service));
		ret.addField(new CField(RetHead.STATUS.value(), status.value()));
		ret.addArray(new CArray(RetHead.MSGARR.value()).add(cs));

		return ret;
	}

	public static String createRetHead(String service, String code, String desc, String level, String status) {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append("\"rethead\":{\"status\":\"").append(status).append("\",\"service\":\"").append(service).append("\",\"msgarr\":[{\"code\":\"").append(code)
				.append("\",\"desc\":\"").append(desc).append("\",\"level\":\"").append(level).append("\"}]}");
		sb.append('}');
		return sb.toString();
	}

	/**
	 * pagehead:{ currentpage:"", totalpage:"", pagecount:"", totalrecord:""}
	 */
	public static CStruc createPageHead(String currentpage, String totalpage, String pagecount, String totalrecord) {
		CStruc ret = new CStruc(PageHead.PAGEHEAD.value());
		ret.addField(new CField(PageHead.CURRENTPAGE.value(), currentpage));
		ret.addField(new CField(PageHead.TOTALPAGE.value(), totalpage));
		ret.addField(new CField(PageHead.PAGECOUNT.value(), pagecount));
		ret.addField(new CField(PageHead.TOTALRECORD.value(), totalrecord));
		return ret;
	}

	/**
	 * pagehead:{ currentpage:"", totalpage:"", pagecount:"", totalrecord:""}
	 */
	public static CStruc createPageHead(Json in, String currentpage, String totalpage, String pagecount, String totalrecord) {
		if (in == null) {
			return null;
		}
		String pageheadname = ConvertManager.IN_PREFIX + PageHead.PAGEHEAD.value();
		CStruc pagehead = in.getStruc(pageheadname);
		if (pagehead == null || pagehead.isEmpty()) {
			pageheadname = PageHead.PAGEHEAD.value();
			pagehead = in.getStruc(pageheadname);
		}
		if (pagehead == null || pagehead.isEmpty()) {
			pagehead = new CStruc(PageHead.PAGEHEAD.value());
			pagehead.addField(new CField(PageHead.CURRENTPAGE.value(), currentpage));
			pagehead.addField(new CField(PageHead.TOTALPAGE.value(), totalpage));
			pagehead.addField(new CField(PageHead.PAGECOUNT.value(), pagecount));
			pagehead.addField(new CField(PageHead.TOTALRECORD.value(), totalrecord));
			if (in.getStruc(ConvertManager.IN) != null) {
				in.getStruc(ConvertManager.IN).addStruc(pagehead);
			} else {
				in.addStruc(pagehead);
			}
		} else {
			if (pagehead.getField(PageHead.CURRENTPAGE.value()) == null) {
				pagehead.addField(new CField(PageHead.CURRENTPAGE.value(), currentpage));
			} else {
				pagehead.getField(PageHead.CURRENTPAGE.value()).setValue(currentpage);
			}

			if (pagehead.getField(PageHead.TOTALPAGE.value()) == null) {
				pagehead.addField(new CField(PageHead.TOTALPAGE.value(), totalpage));
			} else {
				pagehead.getField(PageHead.TOTALPAGE.value()).setValue(totalpage);
			}

			if (pagehead.getField(PageHead.PAGECOUNT.value()) == null) {
				pagehead.addField(new CField(PageHead.PAGECOUNT.value(), pagecount));
			} else {
				pagehead.getField(PageHead.PAGECOUNT.value()).setValue(pagecount);
			}

			if (pagehead.getField(PageHead.TOTALRECORD.value()) == null) {
				pagehead.addField(new CField(PageHead.TOTALRECORD.value(), totalrecord));
			} else {
				pagehead.getField(PageHead.TOTALRECORD.value()).setValue(totalrecord);
			}
		}

		return pagehead;
	}

	public static CStruc createPageHead(Pagination pagination) {
		return createPageHead(pagination.getCurrentpage(), pagination.getTotalpage(), pagination.getPagecount(), pagination.getTotalrecord());
	}

	public static String getPageHead(PageHead ph, Json in) {
		if (in == null || in.isEmpty()) {
			return "";
		}
		CStruc pagehead = in.getStruc(ConvertManager.IN_PREFIX + PageHead.PAGEHEAD.value());
		if (pagehead == null || pagehead.isEmpty()) {
			return "0";
		}
		if (pagehead.getField(ph.value()) == null) {
			return "0";
		}
		if (!hasText(pagehead.getField(ph.value()).getValue())) {
			return "0";
		}
		return pagehead.getField(ph.value()).getValue();
	}

	public static String getSysHead(SysHead sh, Json in) {
		if (in == null || in.isEmpty() || sh == null) {
			return "";
		}
		CStruc syshead = in.getStruc(SysHead.SYSHEAD.value());
		if (syshead == null || syshead.isEmpty()) {
			syshead = in.getStruc(ConvertManager.IN_PREFIX + SysHead.SYSHEAD.value());
			if (syshead == null || syshead.isEmpty()) {
				return "";
			}
		}
		if (syshead.getField(sh.value()) == null) {
			return "";
		}
		String value = syshead.getField(sh.value()).getValue();
		if (sh == SysHead.SERVICENAME) {
			return getServiceName(value);
		} else {
			return value;
		}
	}

	public static String getSysHead(String name, Json in) {
		if (in == null || in.isEmpty() || !hasText(name)) {
			return "";
		}
		CStruc syshead = in.getStruc(SysHead.SYSHEAD.value());
		if (syshead == null || syshead.isEmpty()) {
			syshead = in.getStruc(ConvertManager.IN_PREFIX + SysHead.SYSHEAD.value());
			if (syshead == null || syshead.isEmpty()) {
				return "";
			}
		}
		if (syshead.getField(name) == null) {
			return "";
		}
		String value = syshead.getField(name).getValue();
		if (name.equalsIgnoreCase(SysHead.SERVICENAME.value())) {
			return getServiceName(value);
		} else {
			return value;
		}
	}

	public static void setSysHead(String name, String value, Json in) {
		if (in == null || in.isEmpty() || !hasText(name)) {
			return;
		}
		CStruc syshead = in.getStruc(SysHead.SYSHEAD.value());
		if (syshead == null || syshead.isEmpty()) {
			syshead = in.getStruc(ConvertManager.IN_PREFIX + SysHead.SYSHEAD.value());
			if (syshead == null || syshead.isEmpty()) {
				return;
			}
		}
		syshead.addField(name, value);
	}
	public static void addSysHead(String name, String value, Json in) {
		if (!hasText(name)) {
			return;
		}
		if (in == null) {
			in = JsonFactory.create();
		}
		if(!in.contains(SysHead.SYSHEAD.value())){
			CStruc cs = new CStruc(SysHead.SYSHEAD.value());
			cs.addField(new CField(name, value));
			in.addStruc(cs);
		}
		else{
			CStruc cs = in.getStruc(SysHead.SYSHEAD.value());
			cs.addField(new CField(name, value));
		}
	}
	public static void addPageHead(Json result, Json jsonSrv) {
		if (jsonSrv != null && !jsonSrv.isEmpty()) {
			CStruc page = jsonSrv.getStruc(PageHead.PAGEHEAD.value());
			if (page != null && result.getStruc(PageHead.PAGEHEAD.value()) == null) {
				result.addStruc(page);
				return;
			}
		}
	}

	public static void addRetHead(Json result, Json jsonSrv, String service) {
		if (jsonSrv != null && !jsonSrv.isEmpty()) {
			if (isSuccess(result)) {
				return;
			}
			if (result.getStruc(RetHead.RETHEAD.value()) != null) {
				return;
			}
			result.addStruc(HeadHelper.createRetHead(service, "00000", "执行成功", MsgLevel.B, RetStatus.SUCCESS));
		} else {
			if (!isSuccess(result)) {
				return;
			}
			if (result.getStruc(RetHead.RETHEAD.value()) != null) {
				result.remove(RetHead.RETHEAD.value());
			}
			result.addStruc(HeadHelper.createRetHead(service, "11111", "业务数据不正确", MsgLevel.D));
		}
	}

	public static void addAllHead(Json result, Json jsonSrv, String service) {
		addPageHead(result, jsonSrv);
		addRetHead(result, jsonSrv, service);
	}

	public static boolean isPagination(String sql, Json in) {
		if (!hasText(sql)) {
			return false;
		}
		if (in == null || in.isEmpty()) {
			return false;
		}
		CStruc page = in.getStruc(ConvertManager.IN_PREFIX + PageHead.PAGEHEAD.value());
		if (page == null || page.isEmpty()) {
			return false;
		}
		CField cf = page.getField(PageHead.CURRENTPAGE.value());
		int currentpage = cf == null ? -1 : cf.getIntValue();
		if (currentpage <= 0) {
			return false;
		}
		// cf = page.getField(PageHead.TOTALPAGE.value());
		// int totalpage = cf == null ? -1 : cf.getIntValue();
		// cf = page.getField(PageHead.PAGECOUNT.value());
		// int pagecount = cf == null ? -1 : cf.getIntValue();

		// if (currentpage > totalpage) {
		// return false;
		// }
		PageAdapter adapter = PageAdapterManager.getPageAdapter();
		if (adapter == null) {
			return false;
		}
		return true;
	}

	public static String getPaginationCount(String sql, Json in) {
		if (!isPagination(sql, in)) {
			return "";
		}

		PageAdapter adapter = PageAdapterManager.getPageAdapter();
		if (adapter == null) {
			return "";
		}
		return adapter.getTotalPage(sql);
	}
	
	public static boolean hasRights(String svrName, CStruc user){
		boolean ret = true;
		
		if(user == null || user.getField("employeeid") == null || !user.getField("employeeid").isEmpty()){
			return ret;
		}
		
		//系统用户权限验证，判断用户是否和员工做了关联，如果做了关联，则无限制，否则，只能具有系统管理的权限，而没有业务处理的权限
		String [] rights = getAdminRights();
		if(rights != null && rights.length > 0){
			ret = false;
			for(String right : rights){
				if(svrName.startsWith(right)){
					ret = true;
					break;
				}
			}
		}
		return ret;		
	}
	public static void checkSysHead(Json in, HttpServletRequest request) {
		String id = null,usertype = null,loginname = null,username = null,departmentid = null, employeeid = null;
		String uri = request.getServletPath();
		String serviceName = uri.substring(uri.lastIndexOf('/') + 1);
		CStruc user = (CStruc) request.getSession().getAttribute(Login.USER_STRUC_SESSION);//系统管理登录
		if(user == null){
			user = (CStruc) request.getSession().getAttribute(Logon.USER_STRUC_SESSION_LOGON);//参建单位登录
		}
		if (user == null && SystemConfig.isCheckUserSession()) {
			if (logger.isDebugEnabled()) {
				logger.debug("=========================>>>没有用户信息,退出");
			}
			in = null;
			throw new CubismException("没有用户信息或用户登录信息已过期,请重新登录");
		}
		else if(user != null){
			CField fld = user.getField("id");
			id = fld == null ? "" : fld.getValue();
	
			fld = user.getField("usertype");
			usertype = fld == null ? "" : fld.getValue();
			
			fld = user.getField("loginname");
			loginname = fld == null ? "" : fld.getValue();
	
			fld = user.getField("username");
			username = fld == null ? "" : fld.getValue();
	
			fld = user.getField("departmentid");
			departmentid = fld == null ? "" : fld.getValue();
			
			fld = user.getField("employeeid");
			employeeid = fld == null ? "" : fld.getValue();
		}
		String agent = request.getHeader("USER-AGENT");
		
		HeadHelper.createSysHead(in, agent, id, usertype, loginname, username, departmentid, CubismHelper.getIpAddr(request), serviceName, employeeid);
	}
}