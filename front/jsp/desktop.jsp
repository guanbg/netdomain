<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.jsp.*,java.io.*"%> 
<%@ page import="com.platform.cubism.service.ServiceFactory, com.platform.cubism.front.login.Login, com.platform.cubism.util.HeadHelper, com.platform.cubism.base.*"%> 
<%
	String ctx = request.getContextPath();
	Json in = null, ret = null;
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../inc/headerMain.jsp"/>
	</head>
	<body class="no-skin" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
		<fieldset>
    		<legend>平台信息</legend>
		    <div class='col-sm-offset-1 col-sm-5'>
		    	<span class="input-group">
					<label class="input-group-addon input-group-addon-label">登录时间</label>
					<span class="form-control noborder" id="datetime">登录时间</span>
				</span>
				<span class="input-group">
					<label class="input-group-addon input-group-addon-label">登&nbsp;&nbsp;录&nbsp;&nbsp;IP</label>
					<span class="form-control noborder" id="ip"></span>
				</span>
				<span class="input-group">
					<label class="input-group-addon input-group-addon-label">浏&nbsp;&nbsp;览&nbsp;&nbsp;器</label>
					<span class="form-control noborder" id="ie" style="height:auto;"></span>
				</span>
		    </div>
		    <div class='col-sm-5'>
		    	<span class="input-group">
					<label class="input-group-addon input-group-addon-label">系统版本</label>
					<span class="form-control noborder" id="version"></span>
				</span>
				<span class="input-group">
					<label class="input-group-addon input-group-addon-label">系统名称</label>
					<span class="form-control noborder" id="name"></span>
				</span>
				<span class="input-group">
					<label class="input-group-addon input-group-addon-label">关于我们</label>
					<span class="form-control noborder" id="href"></span>
				</span>
		    </div>
  		</fieldset><% 
  		in = JsonFactory.create();
		in.addField("url", "company/companyAudit.jsp");
		HeadHelper.checkSysHead(in,request);
		ret = ServiceFactory.executeService("sys.menu.auth.query", in);
		if(HeadHelper.isSuccess(ret) && ret.getField("auth").getIntValue()>0){%>
		<fieldset>
    		<legend>待审企业　　　<a href="company/companyAudit.jsp" >[更多]</a></legend>
				<table class="dmb_table table table-striped table-hover margin-bottom-0" id="crop_table">
	      		<thead>
   					<tr class="thin">
       					<th class="noborder">序号</th>
       					<th class="noborder">单位名称</th>
       					<th class="noborder">营业执照注册号</th>
       					<th class="noborder">电子邮箱</th>
       					<th class="noborder">类型</th>
       					<th class="noborder">法定代表人信息</th>
       					<th class="noborder">提交时间</th>
   					</tr>
				</thead>
				<tbody>
				</tbody>
	      	</table>
  		</fieldset>
  		<% }
  		in = JsonFactory.create();
		in.addField("url", "documentor/documentorAudit.jsp");
		HeadHelper.checkSysHead(in,request);
		ret = ServiceFactory.executeService("sys.menu.auth.query", in);
		if(HeadHelper.isSuccess(ret) && ret.getField("auth").getIntValue()>0){%>
  		<fieldset>
    		<legend>待审资料员　　<a href="documentor/documentorAudit.jsp" >[更多]</a></legend>
		     <table class="dmb_table table table-striped table-hover margin-bottom-0" id="documentor_table">
	      		<thead>
   					<tr class="thin">
       					<th class="noborder">序号</th>
       					<th class="noborder">姓名</th>
       					<th class="noborder">合同名称</th>
       					<th class="noborder">所属单位</th>
       					<th class="noborder">职称</th>
       					<th class="noborder">标段代号</th>
       					<th class="noborder">身份证号</th>
       					<th class="noborder">提交时间</th>
   					</tr>
				</thead>
				<tbody>
				</tbody>
	      	</table>
  		</fieldset>
  		<%} 
  		in = JsonFactory.create();
		in.addField("url", "sys/registerLog.jsp");
		HeadHelper.checkSysHead(in,request);
		ret = ServiceFactory.executeService("sys.menu.auth.query", in);
		if(HeadHelper.isSuccess(ret) && ret.getField("auth").getIntValue()>0){%>
		<fieldset>
    		<legend>登录日志　　　<a href="sys/registerLog.jsp" >[更多]</a></legend>
		    <table class="dmb_table table table-striped table-hover margin-bottom-0" id="logs_table">
	      		<thead>
   					<tr class="thin">
       					<th class="noborder">序号</th>
       					<th class="noborder">登录账号</th>
       					<th class="noborder">服务说明</th>
       					<th class="noborder">登录时间</th>
       					<th class="noborder">登录IP</th>
       					<th class="noborder">消息描述</th>
   					</tr>
				</thead>
				<tbody>
   					
				</tbody>
	      	</table>
  		</fieldset>
  		<%}%>
  		
		<jsp:include page="../inc/script.jsp"/>		
		<script src="desktop.js"></script>	
	</body>
</html>		    