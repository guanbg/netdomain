<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.jsp.*,java.io.*"%> 
<%@ page import="com.platform.cubism.front.login.Login"%> 
<%
	String ctx = request.getContextPath();
	String skin = Login.getUser(request,"skin");
	if(skin == null || skin.length() <= 0){
		skin = "default";
	}
%>
	<meta charset="utf-8" />
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	
	<title>轨道建设领域企业综合管理平台</title>
	<link rel="shortcut icon" href="<%=ctx%>/skin/favicon.ico">		
	
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/css/bootstrap.min.css" />
	
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/css/font-awesome.min.css" />
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/css/bootstrap-dialog.min.css" />
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/css/bootstrap-datetimepicker.min.css" />
	
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/css/ui.jqgrid-bootstrap.css" />
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/css/multi-select.css" /><!-- 多选框样式文件 -->
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/css/bootstrap-editable.css" /><!-- 子段编辑器样式文件 -->
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/css/ace.tree.css" /><!-- 树样式文件 -->
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/tree/proton/style.min.css" /><!-- 树样式文件 -->
	
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/css/ace.min.css" /><!-- 必须放在jqgrid.css之后 -->
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/css/component.css" />
	<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/admin/css/user-defined.css" />
	<link rel="stylesheet" href="<%=ctx%>/skin/common.css" />
		
	<!--[if lt IE 9]>
		<script src="<%=ctx%>/js/html5shiv.js"></script>
		<script src="<%=ctx%>/js/respond.min.js"></script>
	<![endif]-->