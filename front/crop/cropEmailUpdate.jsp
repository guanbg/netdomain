<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,com.platform.cubism.service.ServiceFactory, com.platform.cubism.util.HeadHelper, com.platform.cubism.base.*"%> 
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title>参建单位企业综合管理平台</title>
		<link rel="shortcut icon" href="/skin/favicon.ico">
		<link rel="stylesheet" href="/skin/default/crop/css/bootstrap.min.css" />
		<link rel="stylesheet" href="/skin/login/crop/logon.css" />
		<link rel="stylesheet" href="/skin/common.css" />
		<!--[if lt IE 9]>
			<script src="/js/html5shiv.js"></script>
			<script src="/js/respond.min.js"></script>
		<![endif]-->
	</head>

	<body class="no-skin" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
    	<div class="well padding-0 header">
    		<span class="pull-right margin-right-25 logo-height"><a href="#" class="help">帮助</a>| 版本：V2016</span>
    		<span class="margin-left-25"><img alt="网域科技" src="/skin/login/crop/images/website_logo.png"></span>
    	</div>
    	
    	<div class="row margin-lr-0 margin-top-auto">
    		<div class="col-sm-offset-2 col-md-offset-3 col-sm-8 col-md-6"><%
				String contractor_id = request.getQueryString();
				String s=new String(com.platform.cubism.util.SecurityHelper.hexStringToBytes(contractor_id));
                String[] str = s.split("&");
                Json in = null, ret = null;
                in = JsonFactory.create();
                Calendar emailDate = Calendar.getInstance();
                emailDate.add(Calendar.DAY_OF_YEAR,-3);
                long nowDate= emailDate.getTimeInMillis();
                long email = Long.parseLong(str[0]); 
				    in.addField("contractor_id",str[1]);
	                in.addField("email",str[2]);
	            	ret = ServiceFactory.executeService("crop.useremail.find", in); 
	          		if(HeadHelper.isSuccess(ret)){
	          			if(email>=nowDate){%>
						<div class="well margin-bottom-0 login-main2 text-center"><h3 class="margin-top-10">邮箱变更信息确认成功,请激活新邮箱</h3>【<a href="#" onclick="activeEmail()">点击激活</a>】</div><%
						}
						else if(email<nowDate){%>
						<div class="well margin-bottom-0 login-main2 text-center"><h3 class="margin-top-10">邮箱变更信息已失效，请重新发送</h3></div><%
						}
	           		}
				%>
    		</div>
    	</div>
    	<div class="row footer">
	    	<div class=" padding-lr-0 text-center">
				<span class="footer-margin-auto">Copyright © 2015&nbsp;轨道建设领域综合服务平台 All Rights Reserved</span>
				<span >客服电话：400-86686666&nbsp;陕ICP备10011613号</span>
	    	</div>
	    	<span id="Id"><%=str[1]%></span>
	    	<span id="em"><%=str[2]%></span>
	    	<span id="name"></span>
    	</div>
     	<div id="background_pic" style="z-index:-1;position:absolute;left:0px;top:0px;"></div>
    	
		<!--[if !IE]> -->
			<script src="/js/jquery-2.1.4.min.js"></script>
		<!-- <![endif]-->
		
		<!--[if IE]>
			<script src="/js/jquery-1.11.3.min.js"></script>
		<![endif]-->
		
		<!--[if !IE]> -->
		<script type="text/javascript">
			window.jQuery || document.write("<script src='/js/jquery-2.1.4.min.js'>"+"<"+"/script>");
		</script>
		<!-- <![endif]-->
		
		<!--[if IE]>
		<script type="text/javascript">
 			window.jQuery || document.write("<script src='/js/jquery-1.11.3.min.js'>"+"<"+"/script>");
		</script>
		<![endif]-->
		
		<script type="text/javascript">
			if("ontouchend" in document) document.write("<script src='/js/jquery.mobile-1.4.5.min.js'>"+"<"+"/script>");
		</script>
		<script src="/js/bootstrap.min.js"></script>
		<script src="/js/bgstretcher.js"></script>
		<script src="/js/invockeservice.js"></script>
		
		<script type="text/javascript">
		if (self != top) {window.top.location.replace(top.location.pathname);}
			 invockeServiceSync('crop.registeuser.get.service', {contractor_id:$("#Id").text()}, function(data, isSucess){
				if(!isSucess){
					return;
				}
				$("#name").text(data.crop['company_name']);
			});
		function activeEmail(){
			var dt = {
					tmplname:'email.userchangemail.tmpl',
					title:'激活',
					email:$("#em").text(),
					email_new:$("#em").text(),
					company_name:$("#name").text(),
					contractor_id:$("#Id").text(),
					url:'/crop/cropEmail.jsp'
				}
			invockeServiceSync('com.platform.cubism.mail.SendMail.class', dt, function(data, isSucess){
				if(!isSucess){
					return;
				}
				xMsg("发送成功，请注意查收");
				setTimeout("location='/';",10000);//延时10秒 
			});
		};
		
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
				images: ['/skin/login/crop/images/1.jpg', '/skin/login/crop/images/2.jpg', '/skin/login/crop/images/3.jpg', '/skin/login/crop/images/4.jpg'],
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