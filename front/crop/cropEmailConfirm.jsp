<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title>参建单位企业综合管理平台</title>
		<link rel="shortcut icon" href="<%=ctx%>/skin/favicon.ico">
		<link rel="stylesheet" href="<%=ctx%>/skin/default/crop/css/bootstrap.min.css" />
		<link rel="stylesheet" href="<%=ctx%>/skin/login/crop/logon.css" />
		<link rel="stylesheet" href="<%=ctx%>/skin/common.css" />
		<!--[if lt IE 9]>
			<script src="<%=ctx%>/js/html5shiv.js"></script>
			<script src="<%=ctx%>/js/respond.min.js"></script>
		<![endif]-->
	</head>

	<body class="no-skin" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
    	<div class="well padding-0 header">
    		<span class="pull-right margin-right-25 logo-height"><a href="#" class="help">帮助</a> | 版本：V2016</span>
    		<span class="margin-left-25"><img alt="网域科技" src="<%=ctx%>/skin/login/crop/images/website_logo.png"></span>
    	</div>
    	
    	<div class="row margin-lr-0 margin-top-auto">
    		<div class="col-sm-offset-2 col-md-offset-3 col-sm-8 col-md-6"><%
				String param = request.getQueryString();
				if("error".equalsIgnoreCase(param)){%>
					<div class="well margin-bottom-0 login-main2 text-center"><h3 class="margin-top-10">用户信息激活错误，返回</h3>【<a href='<%=ctx%>/'>登录页面</a>】</div><%
				}
				else if("success".equalsIgnoreCase(param)){%>
					<div class="well margin-bottom-0 login-main2 text-center"><h3 class="margin-top-10">用户成功激活，请进入</h3>【<a href='<%=ctx%>/'>登录页面</a>】</div><%
				}
				else{%>
					<div class="well margin-bottom-0 login-main2 text-center"><h3 class="margin-top-10">用户信息已激活或过期，返回</h3>【<a href='<%=ctx%>/'>登录页面</a>】</div><%
				}%>
    		</div>
    	</div>
    	
    	<div class="row footer">
	    	<div class=" padding-lr-0 text-center">
				<span class="footer-margin-auto">Copyright © 2015&nbsp;轨道建设领域综合服务平台 All Rights Reserved</span>
				<span >客服电话：400-86686666&nbsp;陕ICP备10011613号</span>
	    	</div>
    	</div>
     	<div id="background_pic" style="z-index:-1;position:absolute;left:0px;top:0px;"></div>
    	
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
		<script src="<%=ctx%>/js/bootstrap.min.js"></script>
		<script src="<%=ctx%>/js/bgstretcher.js"></script>
		
		<script type="text/javascript">
		if (self != top) {window.top.location.replace(top.location.pathname);}
		
		jQuery(function($){
		    document.oncontextmenu = function(e){
				if (e) {
					e.returnValue = false;
				}
				return false;
			}; 
			
			var h=$(window).height(),w=$(window).width();//屏幕分辨率的高度
			$("#background_pic img").css("height",h);
			$('#background_pic').height(h).bgStretcher({
				images: ['<%=ctx%>/skin/login/crop/images/1.jpg', '<%=ctx%>/skin/login/crop/images/2.jpg', '<%=ctx%>/skin/login/crop/images/3.jpg', '<%=ctx%>/skin/login/crop/images/4.jpg'],
				imageWidth: w, 
				imageHeight: h, 
				slideDirection: 'N',
				nextSlideDelay: 20*1000,
				slideShowSpeed: 2000,
				transitionEffect: 'fade',
				sequenceMode: 'normal',
				anchoring: 'center center',
				anchoringImg: 'center center',
				preloadImg: true
			});	
		});
		</script>
	</body>
</html>