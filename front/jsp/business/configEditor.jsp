<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.jsp.*,java.io.*"%> 
<%@ page import="com.platform.cubism.front.login.Login"%> 
<%
	String configId = request.getParameter("id");
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
			
			.label-info-0{background-color:#3a87ad !important;}
			.label-info-0.arrowed-in:before{border-color:#3a87ad;-moz-border-right-colors:#3a87ad;}
			.label-info-0.arrowed-right:after{border-left-color:#3a87ad;-moz-border-left-colors:#3a87ad;}
			
			.label-info-1{background-color:#478EB3 !important;}
			.label-info-1.arrowed-in:before{border-color:#478EB3;-moz-border-right-colors:#478EB3;}
			.label-info-1.arrowed-right:after{border-left-color:#478EB3;-moz-border-left-colors:#478EB3;}
			
			.label-info-2{background-color:#4C92B6 !important;}
			.label-info-2.arrowed-in:before{border-color:#4C92B6;-moz-border-right-colors:#4C92B6;}
			.label-info-2.arrowed-right:after{border-left-color:#4C92B6;-moz-border-left-colors:#4C92B6;}
			
			.label-info-3{background-color:#5DA0C1 !important;}
			.label-info-3.arrowed-in:before{border-color:#5DA0C1;-moz-border-right-colors:#5DA0C1;}
			.label-info-3.arrowed-right:after{border-left-color:#5DA0C1;-moz-border-left-colors:#5DA0C1;}
			
			.label-info-4{background-color:#6DABC9 !important;}
			.label-info-4.arrowed-in:before{border-color:#6DABC9;-moz-border-right-colors:#6DABC9;}
			.label-info-4.arrowed-right:after{border-left-color:#6DABC9;-moz-border-left-colors:#6DABC9;}
			
			.label-info-5{background-color:#7EB6D2 !important;}
			.label-info-5.arrowed-in:before{border-color:#7EB6D2;-moz-border-right-colors:#7EB6D2;}
			.label-info-5.arrowed-right:after{border-left-color:#7EB6D2;-moz-border-left-colors:#7EB6D2;}
			
			.label-info-6{background-color:#88BED8 !important;}
			.label-info-6.arrowed-in:before{border-color:#88BED8;-moz-border-right-colors:#88BED8;}
			.label-info-6.arrowed-right:after{border-left-color:#88BED8;-moz-border-left-colors:#88BED8;}
			
			.label-info-7{background-color:#94C6DF !important;}
			.label-info-7.arrowed-in:before{border-color:#94C6DF;-moz-border-right-colors:#94C6DF;}
			.label-info-7.arrowed-right:after{border-left-color:#94C6DF;-moz-border-left-colors:#94C6DF;}
			
			.label-info-8{background-color:#9CCCE3 !important;}
			.label-info-8.arrowed-in:before{border-color:#9CCCE3;-moz-border-right-colors:#9CCCE3;}
			.label-info-8.arrowed-right:after{border-left-color:#9CCCE3;-moz-border-left-colors:#9CCCE3;}
			
			.label-info-9{background-color:#A8D4EA !important;}
			.label-info-9.arrowed-in:before{border-color:#A8D4EA;-moz-border-right-colors:#A8D4EA;}
			.label-info-9.arrowed-right:after{border-left-color:#A8D4EA;-moz-border-left-colors:#A8D4EA;}
			
			.label-info-10{background-color:#B0D9EE !important;}
			.label-info-10.arrowed-in:before{border-color:#B0D9EE;-moz-border-right-colors:#B0D9EE;}
			.label-info-10.arrowed-right:after{border-left-color:#B0D9EE;-moz-border-left-colors:#B0D9EE;}
		</style>
	</head>

	<body class="no-skin" style="padding:0px;margin:0px; font-family:'微软雅黑','宋体', Microsoft YaHei, Verdana, Arial, sans-serif !important;">
		<div class="main-container" >
			<div id="configPathName" style="margin-top: -3px;"></div>
			<div id="configEditor"></div>
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
			var getConfigId = function(){return '<%=configId%>';};
			document.title = opener.select_node_name || "轨道建设领域企业综合管理平台";
		</script>
		<script src="<%=ctx%>/js/json3.min.js"></script>
		<script type="text/javascript" src="<%=ctx%>/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="<%=ctx%>/js/bootstrap-dialog.min.js"></script>
		<script src="<%=ctx%>/js/serviceUtil.js"></script>
		<script type="text/javascript" src="<%=ctx%>/js/handsontable.full.min.js"></script>
		<script src="configEditor.js"></script>
	</body>
</html>