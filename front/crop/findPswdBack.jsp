<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.platform.cubism.service.ServiceFactory, com.platform.cubism.util.HeadHelper, com.platform.cubism.util.CubismHelper, com.platform.cubism.util.SecurityHelper, com.platform.cubism.base.*"%> 
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
    			Json in = null;
				String param = request.getQueryString();
				if(param == null || param.length() <= 0){%>
					<div class="well margin-bottom-0 login-main2 text-center"><h3 class="margin-top-10">用户信息错误，不能重新设置密码</h3></div><%
				}
				else{
					in = CubismHelper.queryStr2Json(new String(SecurityHelper.hexStringToBytes(param)));
					
					String user_id = in.getFieldValue("user_id");
					String login_password = in.getFieldValue("login_password");
					if(user_id == null || user_id.length() <= 0 || login_password == null || login_password.length() <= 0){%>
						<div class="well margin-bottom-0 login-main2 text-center"><h3 class="margin-top-10">用户信息错误，不能重新设置密码</h3></div><%
					}
					else{
	                	Json ret = ServiceFactory.executeService("crop.userpswd.exist", in);
	                	if(!HeadHelper.isSuccess(ret) || ret.getFieldValue("is_exist").length() <= 0 || ret.getField("is_exist").getIntValue() <= 0){%>
	                		<div class="well margin-bottom-0 login-main2 text-center"><h3 class="margin-top-10">用户信息错误，不能重新设置密码</h3></div><%
	                	}
	                	else{%>
	                		<input type="hidden" id="user_id" value="<%=user_id%>"/>
	                		<input type="hidden" id="login_password" value="<%=login_password%>"/>
	                		
	            			<div class="login_title">重新设置密码</div>
	            			<div class="well margin-bottom-0 login-main" id="mainform">
	            				<div class="row margin-lr-0">
	        	    				<div class="col-sm-5 padding-left-0 margin-bottom-10 padding-right-auto">
	          							<input type="password" id="userpswd" class="form-control userpswd" placeholder="新设密码">
	        			    		</div>
	        			    		<div class="col-sm-5 padding-left-0 margin-bottom-10 padding-right-auto">
	          							<input type="password" id="userpswd2" class="form-control userpswd" placeholder="确认密码">
	        			    		</div>
	        			    		<div class="col-sm-2 padding-left-0 margin-bottom-10 padding-right-auto">
	        	    					<button type="button" class="btn btn-Primary btn-block loginbutton" id="savebutton"><i class="glyphicon glyphicon-ok" style="color:white;"></i>&nbsp;&nbsp;保&nbsp;&nbsp;存</button>
	        			    		</div>
	            				</div>
	            			</div><%
	                	}
					}
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
		<script src="<%=ctx%>/js/json3.min.js"></script>
		<script src="<%=ctx%>/js/invockeservice.js"></script>
		<script src="<%=ctx%>/js/md5.js"></script>
		
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
			
			var submitFlag = false;
			$("body").keydown(function(event){
			   if(!submitFlag && event.keyCode == 13){
				   $('#savebutton').trigger("click");
			   }
			});
			
			//系统登录
			$('#savebutton').click(function(){
				if(!$("#userpswd").val()){
				   	$("#userpswd").val("").focus();
					return false;
			   	}
				if($("#userpswd").val().length<6){
					alert("密码最少为6位");
					$("#userpswd").val("").focus();
					return false;
			   	}
			   	if(!$("#userpswd2").val()){
				   	$("#userpswd2").val("").focus();
					return false;
			   	}
			   	if($("#userpswd").val() != $("#userpswd2").val()){
			   		alert("两次输入的密码不一样，请重新输入");
			   		$("#userpswd2").focus();
					return false;
			   	}
			   	submitFlag = true;
			   	$('#savebutton').prop("disabled",true);
			   	
				var dt = {
					user_id:$("#user_id").val(),
					login_password:$("#login_password").val(),
					userpswd:hex_md5(hex_md5($("#userpswd").val()))
				};
				
				$.ajax({
			    	type : "post", 
			        url : "<%=ctx%>/CropEmailConfirm?"+(new Date().getTime()), 
			        data : JSON.stringify(dt), 
			        async : false, 
			        success : function(data, textStatus, jqXHR){
			        	var ret = data.retHead || data.rethead || data;
			        	
			            if(ret.status == "S"){
				    	    $("#mainform").removeClass("login-main").addClass("login-main2 text-center").html("<h3 class='margin-top-10'>密码设置成功,返回<a href='<%=ctx%>/'>登录页面</a></h3>");
			        	}
			        	else{
				        	var msg = ret.msgarr || ret.msgArr;
				            alert(msg[0].code+" "+msg[0].desc);
				            submitFlag = false;
				            $('#loginbutton').prop("disabled", false);
			        	}
			        },
			        error:function(data){
			        	alert('系统错误！\n'+data.responseText);
			        	submitFlag = false;
			        	$('#loginbutton').prop("disabled", false);
			        }
				});
			});
		});
		</script>
	</body>
</html>