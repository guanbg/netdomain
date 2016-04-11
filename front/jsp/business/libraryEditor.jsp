<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.jsp.*,java.io.*"%> 
<%@ page import="com.platform.cubism.front.login.Login"%> 
<%
	String libId = request.getParameter("id");
	String ctx = request.getContextPath();
	String skin = Login.getUser(request,"skin");
	if(skin == null || skin.length() <= 0){
		skin = "default";
	}
%>

<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8" />
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		
		<title>轨道建设领域企业综合管理平台</title>
		<link rel="shortcut icon" href="<%=ctx%>/skin/favicon.ico">
		<link rel="stylesheet" media="screen" href="<%=ctx%>/skin/<%=skin%>/admin/css/bootstrap.min.css" />
		<link rel="stylesheet" media="screen" href="<%=ctx%>/skin/<%=skin%>/admin/css/bootstrap-dialog.min.css" />
		<link rel="stylesheet" media="screen" href="<%=ctx%>/skin/<%=skin%>/admin/css/ace.min.css">
		<link rel="stylesheet" media="screen" href="<%=ctx%>/skin/common.css" />
		<link rel="stylesheet" media="screen" href="<%=ctx%>/skin/<%=skin%>/admin/css/handsontable.full.min.css">
		
		<!--[if lt IE 9]>
			<script src="<%=ctx%>/js/html5shiv.js"></script>
			<script src="<%=ctx%>/js/respond.min.js"></script>
		<![endif]-->
		<style type="text/css">
			#auto_close_msgbox{
				text-align:center;
				padding:5px;
				font-size:20px;
				font-weight:bold;
				color:#FFF;
				background-color:green;
				min-width:200px;
				border-radius:8px;
			}
			.handsontableInput{border:1px solid;}
		</style>
	</head>

	<body class="no-skin" style="padding:0px;margin:0px; font-family:'微软雅黑','宋体', Microsoft YaHei, Verdana, Arial, sans-serif !important;">
		<div class="main-container" >
			<div id="libraryPathName" style="margin-top: -3px;"></div>
			<div id="libraryEditor"></div>
		</div><!-- /.main-container -->
		
		<!--[if !IE]> -->
			<script src="<%=ctx%>/js/jquery-2.1.4.min.js"></script>
		<!-- <![endif]-->
		
		<!--[if IE]>
			<script src="<%=ctx%>/js/jquery-1.11.3.min.js"></script>
		<![endif]-->
		
		<!--[if !IE]> -->
		<script type="text/javascript">
			window.jQuery || document.write("<script src='<%=ctx%>/js/jquery-2.1.4.min.js'>"+"<"+"/script>");
		</script>
		<!-- <![endif]-->
		
		<!--[if IE]>
		<script type="text/javascript">
				window.jQuery || document.write("<script src='<%=ctx%>/js/jquery-1.11.3.min.js'>"+"<"+"/script>");
		</script>
		<![endif]-->
		
		<script type="text/javascript">
			if("ontouchend" in document) document.write("<script src='<%=ctx%>/js/jquery.mobile-1.4.5.min.js'>"+"<"+"/script>");
		</script>
		<script type="text/javascript">
			var getLibId = function(){return '<%=libId%>';};
			document.title = opener.select_node_name || "轨道建设领域企业综合管理平台";
		</script>
		<script type="text/javascript" src="<%=ctx%>/js/json3.min.js"></script>
		<script type="text/javascript" src="<%=ctx%>/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="<%=ctx%>/js/bootstrap-dialog.min.js"></script>
		<script type="text/javascript" src="<%=ctx%>/js/serviceUtil.js"></script>
		<script type="text/javascript" src="<%=ctx%>/js/handsontable.full.min.js"></script>
		<script type="text/javascript" src="libraryEditor.js"></script>
	</body>
</html>