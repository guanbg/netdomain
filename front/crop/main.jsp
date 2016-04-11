<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.platform.cubism.front.login.Logon"%> 
<%
	String ctx = request.getContextPath();
	String skin = Logon.getUser(request,"skin");
	if(skin == null || skin.length() <= 0){
		skin = "default";
	}
	String user_type = Logon.getUser(request,"user_type");
	if(user_type == null || user_type.length() <= 0){
		getServletContext().getRequestDispatcher("/").forward(request, response);
	}
	String contractor_id = null;
	String documentor_id = null;
	String user_id = Logon.getUser(request,"user_id");
	if("0".equals(user_type)){//建设单位
		contractor_id = Logon.getUser(request,"contractor.contractor_id");
	}
	if("1".equals(user_type)){//资料员
		documentor_id = Logon.getUser(request,"documentor.documentor_id");
	}
%>
<!DOCTYPE html>
<html lang="en" class="fuelux">
	<head>
		<meta charset="utf-8" />
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		
		<title>轨道建设领域企业综合服务平台</title>
		<link href="<%=ctx%>/favicon.ico" rel="icon" type="image/x-icon" />
		<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/crop/css/jquery.lightbox-0.5.css" media="screen" />		
		<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/crop/css/bootstrap.min.css" />
		<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/crop/css/bootstrap-dialog.min.css" />
		<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/crop/css/font-awesome.min.css" />
		<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/crop/css/bootstrap-table.css" />
		<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/crop/css/bootstrap-editable.css" />
		<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/crop/css/bootstrap-tagsinput.css" />
		<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/crop/css/ace.tree.css" /><!-- 树样式文件 -->
		<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/crop/css/bootstrap-datepicker.min.css" />		
		<link rel="stylesheet" href="<%=ctx%>/skin/common.css" />
		<link rel="stylesheet" href="<%=ctx%>/skin/<%=skin%>/crop/css/main.css" />
		<!--[if lt IE 9]>
			<script src="<%=ctx%>/js/html5shiv.js"></script>
			<script src="<%=ctx%>/js/respond.min.js"></script>
		<![endif]-->
	</head>

	<body class="no-skin" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
		<div class="navbar navbar-default margin-bottom-0 noborder main-top-nav" id="topNav" role = "navigation"><!--  navbar-fixed-top -->
		  	<div class="container-fluid">
		    	<div class="navbar-header">
		      		<button id="navbar-button" type="button" class="navbar-toggle margin-right-4" style="margin-top:14px;" data-toggle="collapse" data-target="#main-navbar-collapse" aria-expanded="false">
		        		<span class="sr-only">展开菜单</span><!-- 屏幕阅读器(screen reader)需要找到能辨识的文本说明然后“读”出来给用户听  -->
		        		<span class="icon-bar"></span>
		        		<span class="icon-bar"></span>
		        		<span class="icon-bar"></span>
		      		</button>
		      		<a class="navbar-brand padding-0 margin-left-0" href="#"><img src="<%=ctx%>/skin/<%=skin%>/crop/img/logo.png" alt="轨道建设领域企业综合管理平台"/></a>
		    	</div>
		
		      	<div class = "nav-collapse collapse navbar-collapse" style="overflow:visible !important;" id = "main-navbar-collapse">	
		      		<ul class="nav nav-tabs navbar-right noborder"><%
	      				if("0".equals(user_type)){//建设单位%>
	      					<li class="margin-0 active"><a href="#cropInfo" class="main-top-icon text-center" tabindex="-1" data-toggle="tab"><span class="img icon1"></span><span>企业信息</span></a></li><%
	      				}
	      				if("1".equals(user_type)){//资料员%>
	      					<li class="margin-0 active"><a href="#docuInfo" class="main-top-icon text-center" tabindex="-1" data-toggle="tab" style="width: auto;"><span class="img icon1"></span><span>资料员信息</span></a></li><%
	      				}%>
		        		
		        		<li class="margin-0"><a href="#cropDatabase" class="main-top-icon text-center" tabindex="-1" data-toggle="tab" id="datebase" ><span class="img icon2"></span><span>资料库</span></a></li>
		        		<li class="margin-0"><a href="#sysMsg" class="main-top-icon text-center" tabindex="-1" data-toggle="tab"><span class="img icon3"><span class="badge" style="float: right;"></span></span><span>系统消息</span></a></li>
		        		<li class="margin-0 dropdown main-top-nav-spliter">
							<a href="#" class="main-top-icon dropdown-toggle" style="line-height:40px;border:0px !important;" data-toggle="dropdown"><i class="glyphicon glyphicon-cog margin-right-4"></i>设置<span class="caret"></span></a>
		          			<ul class="dropdown-menu">
			        			<li><a href="#" id="changePswd"><i class="glyphicon glyphicon-lock margin-right-4"></i>修改密码</a></li>
		            			<!-- 
		            			<li role="separator" class="divider"></li>
		            			<li><a href="#"><i class="glyphicon glyphicon-sound-5-1 margin-right-4"></i>皮肤一</a></li>
		            			<li><a href="#"><i class="glyphicon glyphicon-sound-6-1 margin-right-4"></i>皮肤二</a></li>
		            			<li><a href="#"><i class="glyphicon glyphicon-sound-7-1 margin-right-4"></i>皮肤三</a></li>
		            			 -->
			        			<li role="separator" class="divider"></li>
		            			<li><a href="http://help.ndsmart.cn/HelpCenter/qiyeguanlipingtai/" target='_blank'><i class="glyphicon glyphicon-question-sign margin-right-4"></i>帮助文档</a></li>
		            			<li><a href="#" id="aboutUs"><i class="glyphicon glyphicon-info-sign margin-right-4"></i>关于我们</a></li>
		          			</ul>
		          		</li>
		        		<li class="margin-0"><a href="logout?/" class="main-top-icon text-center" style="line-height:40px;border:0px !important;"><i class="glyphicon glyphicon-off margin-right-4"></i>退出</a></li>
		      		</ul>
	      		</div>
		  	</div><!-- /.container-fluid -->
		</div>
		
		<div class='row-fluid margin-lr-0' id="contentMainRow">
  			<div class='col-sm-12 padding-lr-0'>
  				<div id="contentMain">
	  				<div class="tab-content"><%
				        if("0".equals(user_type)){//建设单位%>
				        	<div id="cropInfo" class="tab-pane active">
		  					<jsp:include page="cropInfo.jsp"/>
				        	</div><%
				        }
				    	if("1".equals(user_type)){//资料员%>
				        	<div id="docuInfo" class="tab-pane active">
		  					<jsp:include page="docuInfo.jsp"/>
				        	</div><%
				    	}%>		
				    	<div id="cropDatabase" class="tab-pane">
				        	<jsp:include page="cropDatabase.jsp"/>
			        	</div>		        
				        <div id="sysMsg" class="tab-pane">
				        	<jsp:include page="sysMsg.jsp"/>
				        </div>
	  				</div>
  				</div>
  			</div>
		</div>
		<div id="changePswdModalDialogContainer" class="hide2"></div>
		<div id="aboutUsModalDialogContainer" class="hide2"></div>
		
		<div class='row-fluid margin-lr-0 footer' id="main_footer">
			<span class="margin-right-15 float-right"><a href="http://help.ndsmart.cn/HelpCenter/qiyeguanlipingtai/" class="help" target='_blank' style="color:#acabab; ">帮助</a> | 版本:V2016</span>
			<span class="margin-left-15 float-left">参建单位：西安网域电子科技有限公司</span>
		</div>
		
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
		<script src="<%=ctx%>/js/jquery.lightbox-0.5.js"></script>
		<script src="<%=ctx%>/js/bootstrap.js"></script>
		<script src="<%=ctx%>/js/json3.min.js"></script>
		<script src="<%=ctx%>/js/moment.min.js"></script>
		<script src="<%=ctx%>/js/bootstrap-editable.min.js"></script>
		<script src="<%=ctx%>/js/bootstrap-table.min.js"></script>
		<script src="<%=ctx%>/js/i18n/bootstrap-table-zh-CN.min.js"></script>
		<script src="<%=ctx%>/js/i18n/bootstrap-datepicker.zh-CN.min.js"></script>
		<script src="<%=ctx%>/js/bootstrap-datepicker.min.js"></script>
		<script src="<%=ctx%>/js/i18n/bootstrap-datepicker.zh-CN.min.js"></script>
		<script src="<%=ctx%>/js/bootstrap-tagsinput.min.js"></script>
		<script src="<%=ctx%>/js/fuelux.tree.min.js"></script>
		<script src="<%=ctx%>/js/bootstrap-dialog.min.js"></script>
		<script src="<%=ctx%>/js/serviceUtil.js"></script>
		<script src="<%=ctx%>/js/md5.js"></script>	
		
		<script type="text/javascript">
		var getContractorId = function(){
			return '<%=contractor_id%>';
		};
		var getUserId = function(){
			return '<%=user_id%>';
		};
		var getDocumentorId = function(){
			return '<%=documentor_id%>';
		};
		var readMsg = function(id){
			invockeService('crop.msg.count.service', {contractor_id:id}, function(data, isSucess){
				if(!isSucess){
					return;
				}
				if(data.msg==0){
					$("#main-navbar-collapse span .badge").text("");
				}else{
					$("#main-navbar-collapse span .badge").text(data.msg);
				}
			});
		};
		jQuery(function($) {
			$('#contentMain').height($(window).height()-($('#topNav').height()+$('#main_footer').height()));
			$(window).bind('resize', function() {
				$('#contentMain').height($(window).height()-($('#topNav').height()+$('#main_footer').height()));
			});
			
			$('#changePswd').click(function(){//修改密码
				if($('#changePswdModalDialogContainer').html()){
					$('#changePswdModalDialogContainer').show();
					$('#changePswdModalDialog').modal({backdrop:false,show:true});
				}
				else{
					var timestamp = (new Date()).valueOf();
					$.ajaxSetup({cache: false});//关闭AJAX相应的缓存
					$("#changePswdModalDialogContainer").load("crop/changePswd.jsp?"+timestamp, null, function(){
						$.getScript('crop/changePswd.js');

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
					$("#aboutUsModalDialogContainer").load("crop/aboutUs.jsp?"+timestamp, null, function(){

						$('#aboutUsModalDialogContainer').show();
						$('#aboutUsModalDialog').modal({backdrop:false,show:true});
					});
					
				}
			});
			
			var a1=false, a2=false;
			$('#main-navbar-collapse .nav-tabs a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
				var target = $(e.target).attr("href");
				if (!a1 && target == '#cropDatabase') {
					callIfFun('cropDatabaseInit');//调用页面初始化函数;
					a1 = true;
				}
				else if (!a2 && target == '#sysMsg') {
					callIfFun('sysMsgInit');//调用页面初始化函数;
					a2 = true;
				}
			});
			
			var userType=<%=user_type%>;
			//统计信息条数
			if(userType == 0){
				readMsg(getContractorId());
			}else if(userType == 1){
				invockeService('crop.documentor.get.service', {documentor_id:getDocumentorId()}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					readMsg(data.documentor['contractor_id']);
				});
			}
			
			$("#datebase").bind('click',function(event){
				if(userType == 0){
					xMsg("当前账户没有操作资料库的权限，请创建资料员账号进行资料库操作。");
					return false;
				}else{
					invockeServiceSync('crop.documentor.statusList.service', {documentor_id:getDocumentorId()}, function(data, isSucess){
						if(!isSucess){
							return;
						}
					  	var docuStatus =data.documentor['datebase_status'];
					  	var cropstatus =data.documentor['approval_status'];
					  	if(cropstatus == '4'){
							xMsg("您的企业业务已被冻结，无法操作资料库");
						}
						else if(docuStatus == '-1'){
							xMsg("该资料员业务已被冻结，请重新提交审核");
						}
						else if(docuStatus != '1'){
							xMsg("该资料员未通过审核，无法操作资料库");
						}
					  	
					  	if(cropstatus == '4' || docuStatus != '1'){
					  		//event.stopImmediatePropagation();
			                event.stopPropagation();//该方法将停止事件的传播，阻止它被分派到其他 Document 节点
			                event.preventDefault();//该方法将通知 Web 浏览器不要执行与事件关联的默认动作（如果存在这样的动作）。
			                
			                $("#datebase").bind('click',false);
					  	}
					});
				}
			});
			
			$('[data-toggle="tooltip"]').tooltip({container:'body'});

        });
		window.onbeforeunload = function(){return '是否退出系统？';};
		</script><%
		if("0".equals(user_type)){//建设单位%>
			<script src="<%=ctx%>/crop/cropInfo.js"></script><%
		}
		if("1".equals(user_type)){//资料员%>
			<script src="<%=ctx%>/crop/docuInfo.js"></script><%
		}%>
		
		<script src="<%=ctx%>/crop/cropDatabase.js"></script>
		<script src="<%=ctx%>/crop/sysMsg.js"></script>
	</body>
</html>