<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.jsp.*,java.io.*"%> 
<%@ page import="com.platform.cubism.service.ServiceFactory, com.platform.cubism.front.login.Login, com.platform.cubism.util.HeadHelper, com.platform.cubism.base.*"%> 
<%
	String ctx = request.getContextPath();
	String pageRoot = request.getServletPath();
	pageRoot = pageRoot.substring(0, pageRoot.lastIndexOf("/"));
	if(pageRoot != null && pageRoot.length() > 0){
		pageRoot = ctx + pageRoot;
	}
	else{
		pageRoot = ctx;
	}
	String skin = Login.getUser(request,"skin");
	if(skin == null || skin.length() <= 0){
		skin = "default";
	}
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../inc/headerMain.jsp"/>
		<link href="<%=ctx%>/favicon.ico" rel="icon" type="image/x-icon" />
	</head>
	
	<body class="no-skin" scroll=no style="background:#EFEFEF;overflow:hidden;" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
		<div class="navbar navbar-default margin-bottom-0 noborder main-top-nav" id="topNav"><!--  navbar-fixed-top -->
		  	<div class="container-fluid">
		    	<div class="navbar-header">
		      		<button id="navbar-button" type="button" class="navbar-toggle" aria-expanded="false">
		        		<span class="sr-only">展开菜单</span><!-- 屏幕阅读器(screen reader)需要找到能辨识的文本说明然后“读”出来给用户听  -->
		        		<span class="icon-bar"></span>
		        		<span class="icon-bar"></span>
		        		<span class="icon-bar"></span>
		      		</button>
		      		<a class="navbar-brand padding-0 margin-left-0" href="#"><img src="<%=ctx%>/skin/<%=skin%>/admin/img/logo.png" alt="轨道建设领域企业综合管理平台"/></a>
		    	</div>
		
		    	<div id="topNavMenu">
		      		<ul class="nav navbar-nav navbar-right">
		        		<li class="dropdown main-top-nav-spliter">
		          			<a href="#" class="dropdown-toggle main-top-nav-user" style="border:0px !important;" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><i class="fa fa-user fa-lg"></i> <%=Login.getUser(request,"username")%>，你好！ <span class="caret"></span></a>
		          			<ul class="dropdown-menu">
			        			<li><a href="#" id="changePswd"><i class="glyphicon glyphicon-lock margin-right-4"></i>修改密码</a></li>
			        			<li role="separator" class="divider"></li>
			        			<li><a href="http://help.ndsmart.cn/HelpCenter/qiyeguanlipingtai/" target='_blank'><i class="glyphicon glyphicon-question-sign margin-right-4"></i>帮助文档</a></li>
		            			<li><a href="#" id="aboutUs"><i class="glyphicon glyphicon-info-sign margin-right-4"></i>关于我们</a></li>
		            			<!--
		            			<li><a href="#">个人信息</a></li>
		            			<li><a href="#">皮肤一</a></li>
		            			<li><a href="#">皮肤二</a></li>
		            			<li><a href="#">皮肤三</a></li>
		            			-->
		          			</ul>
		        		</li>
		        		<li><a href="logout?/login.html" class="main-top-nav-user" style="border:0px !important;"><i class="fa fa-power-off fa-lg"></i> 退出</a></li>
		      		</ul>
		      		<input type="hidden" value="<%=Login.getUser(request, "id")%>" id="login_user_id">
		      		<ul class="nav nav-tabs navbar-right noborder"><%
						Json in = JsonFactory.create();
						in.addField("userid", Login.getUser(request, "id"));
						HeadHelper.checkSysHead(in,request);
						Json ret = ServiceFactory.executeService("sys.main.nav.query", in);
						if(HeadHelper.isSuccess(ret)){
							CArray mainnav = ret.getArray("mainnav");
							if(mainnav != null && !mainnav.isEmpty()){
								String active = "active";
								for(CStruc cs : mainnav.getRecords()){%>
									<li class="<%=active%> margin-0"><a href="#mainNav_<%=cs.getFieldValue("id")%>" class="text-center padding-tb-0" data-toggle="tab"><span class="img <%=cs.getFieldValue("iconclass")%>"></span><span><%=cs.getFieldValue("menuname")%></span></a></li><%
									active = "";//第一个为活动的
								}
							}
						}%>
		      		</ul>
		    	</div>
		  	</div><!-- /.container-fluid -->
		</div>
		<div id="changePswdModalDialogContainer" class="hide2"></div>
		
		<div class='row-fluid margin-lr-0' id="leftNavRow">
  			<div class='padding-lr-0 float-right' id="contentMainRow" style="width:100%;height:100%;margin-left:-220px !important">
    			<div id="contentMain" class="margin-right-0 border-left-7" style="margin-left:220px">
				    <iframe id="contentIframe" name="contentIframe" class="embed-responsive-item" src='<%=pageRoot%>/desktop.jsp' style='border:0px;width:100%;height:100%;'></iframe>
    			</div>
  			</div>
  			<div id="leftNav" class='float-left padding-lr-0' style="width:220px;">
  				<div class="tab-content padding-0 noborder"><%
 						if(HeadHelper.isSuccess(ret)){
 							CArray mainnav = ret.getArray("mainnav");
							if(mainnav != null && !mainnav.isEmpty()){
								String active = "active";
								CArray subnav = null;
								for(CStruc cs : mainnav.getRecords()){
									subnav = cs.getArray("subnav");;
	  								if(subnav != null && !subnav.isEmpty()){%>
	  									<div id="mainNav_<%=cs.getFieldValue("id")%>" class="tab-pane <%=active%> left"><ul><%
	  									for(CStruc cst : subnav.getRecords()){
	  										String cls = cst.getFieldValue("iconclass");
	  										if(cls != null && cls.startsWith("fa ")){//字体图标%>
							  					<li><a href="<%=pageRoot%>/<%=cst.getFieldValue("url")%>?_t=<%=System.nanoTime()%>" target="contentIframe"><%=cst.getFieldValue("menuname")%><i class="float-left <%=cls%>"></i></a></li><%
	  										}
	  										else{//图片图标%>
							  					<li><a href="<%=pageRoot%>/<%=cst.getFieldValue("url")%>?_t=<%=System.nanoTime()%>" class="<%=cls%>" target="contentIframe"><%=cst.getFieldValue("menuname")%></a></li><%
	  										}
	  									}%>
	  									</ul></div><%
	  									active = "";//第一个为活动的
	  								}
								}
							}
 						}%>
  				</div>
  			</div>
  			<div class="clear"></div>
  			<div id="aboutUsModalDialogContainer" class="hide2"></div> 
		</div>
  		<i id="leftSpliter" class="fa fa-caret-left"></i>
  		<div id="imageViewDialogContainer" class="hide2" style="position: fixed;left:0px;right:0px;top:0px:bottom:0px;background-color:black;text-align:center;"></div>
		<jsp:include page="../inc/script.jsp"/>
		<script type="text/javascript">
		jQuery(function($) {
			var hideLeftNav = function(isHide){
				if(isHide){
					$('#contentMainRow').css('margin-left',0);
		            $('#contentMain').css('margin-left',0);
    	            $("#leftSpliter").css('left',0);
		            $("#contentMain").removeClass("border-left-7");
    	            $("#leftNav").addClass("hide");
    	            $("#leftSpliter").addClass("hide");
    	            $('#leftNav').hide();
				}
				else{
					$('#contentMainRow').css('margin-left',0);
		            $('#contentMain').css('margin-left',0);
    	            $("#leftSpliter").css('left',0);
    	            $("#leftNav").addClass("hide");
    	            
    	            $("#contentMain").addClass("border-left-7");
    	            $("#leftSpliter").removeClass("hide");
    	            $('#leftSpliter').show();
    	            $('#leftSpliter').removeClass('fa-caret-left').addClass('fa-caret-right');
				}
			},
			showLeftNav = function(isHide){
				$("#leftNav").removeClass("hide");
   	            $("#leftSpliter").removeClass("hide");
   	            $('#leftNav').show();
   	            
				var leftNavWidth = $("#leftNav").width();		            
	            $('#contentMainRow').css('margin-left',leftNavWidth*-1);
	            $('#contentMain').css('margin-left',leftNavWidth);
	            $("#leftSpliter").css('left',leftNavWidth);
	            $("#contentMain").addClass("border-left-7");
	            $("#leftSpliter").removeClass('fa-caret-right').addClass('fa-caret-left');
			},
			resize = function(){
				var iframe=$("#contentIframe")[0],
            	winHeight = $(window).height(), 
            	winWidth = $(window).width(),
				bHeight = iframe.contentWindow.document.body ? iframe.contentWindow.document.body.scrollHeight : iframe.contentWindow.document.documentElement ? iframe.contentWindow.document.documentElement.scrollHeight : 0,
	            dHeight = iframe.contentWindow.document.documentElement?iframe.contentWindow.document.documentElement.scrollHeight:0;
	            height = Math.max(bHeight, dHeight);
            	
	            if (winWidth < 768) {
                	$("#contentMain").height(height);
                	$("#topNavMenu").addClass("hide");
                	hideLeftNav();//隐藏
                }
                else{
                	$("#contentMain").height(winHeight - $("#topNav").height());
                	$("#topNavMenu").removeClass("hide");
                	showLeftNav();//显示
                }
                $('#leftNavRow').css('margin-top',$('#topNav').height());
                
                $("#leftSpliter").css({//初始化
    				top: function() {
    			          return $(window).height()/2 - 10;
    				}/*, 
    				left: function(index, value) {
    			          return $("#leftNav").width();
    				}*/
    	       	});
			};			
			$("#leftSpliter").click(function(){
				if($(this).position().left < 1){
					showLeftNav();//显示
				}
				else{
					hideLeftNav();//隐藏
				}
			});
            $("#navbar-button").click(function(e) {
                e.preventDefault();

	            $("#topNavMenu").toggleClass("hide");
            });
            $("#contentIframe").on('load',function() {
	            var iframe=this;
	            if (!iframe.readyState || iframe.readyState == "complete") {
	            	resize();
            	}
            });
            $('#changePswd').click(function(){//修改密码
				if($('#changePswdModalDialogContainer').html()){
					$('#changePswdModalDialogContainer').show();
					$('#changePswdModalDialog').modal({backdrop:false,show:true});
				}
				else{
					var timestamp = (new Date()).valueOf();
					$.ajaxSetup({cache: false});//关闭AJAX相应的缓存
					$("#changePswdModalDialogContainer").load("jsp/changePswd.jsp?"+timestamp, null, function(){
						$.getScript('jsp/changePswd.js');

						$('#changePswdModalDialogContainer').show();
						$('#changePswdModalDialog').modal({backdrop:false,show:true});
					});
				}
			});
            $('#aboutUs').click(function(){//关于我们
				if($('#aboutUsModalDialogContainer').html()){
					$('#aboutUsModalDialogContainer').show();
					$('#aboutUsModalDialog').modal({backdrop:false,show:true});
				}
				else{
					var timestamp = (new Date()).valueOf();
					$.ajaxSetup({cache: false});//关闭AJAX相应的缓存
					$("#aboutUsModalDialogContainer").load("jsp/aboutUs.jsp?"+timestamp, null, function(){

						$('#aboutUsModalDialogContainer').show();
						$('#aboutUsModalDialog').modal({backdrop:false,show:true});
					});
				}
			});
            $(window).resize(resize);
            resize();
            $('[data-toggle="tooltip"]').tooltip({container:'body'});
        });
        window.onbeforeunload = function(){return '是否退出系统？';};
		</script>
	</body>
</html>		