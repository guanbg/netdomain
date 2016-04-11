<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
		<jsp:include page="../../inc/header.jsp"/>
</head>
<body class="no-skin" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
		<div class="main-container">
			<table id='myJqGrid' style="width:100%"></table> 
			<div id='myJqGridPager'></div>
		</div>
		<div class="modal fade" id="dataFeedDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
			  <div class="modal-lg modal-dialog">
			    <div class="modal-content" >
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">业务数据</h4>
			      </div>
			      <div class="modal-body">
			        <form role="form" id="msg_form">
						<div class="space-4"></div>
						<span class="input-group">
							&nbsp;&nbsp;&nbsp;资料员：&nbsp;<span id="documentor_name"   ></span>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							企业名称：&nbsp;<span id="company_name"   ></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						 	资料库名称：&nbsp;<span id="lib_aliase"   ></span>
						</span>
						<div class="space-4"></div>
						<div class="space-4"></div>
						<div class="space-4"></div>
						<div class="space-4"></div>
						<div class="input-group noclear">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="msg_title"></label>
							<table id='my_clause_JqGrid' width="530"></table> <div id='my_clause_JqGridPager'></div>
						</div>
			        </form>
			      </div>
			      <div class="modal-footer">
			      	<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			      </div>
			    </div>
			  </div>
			</div>
	   <jsp:include page="../../inc/script.jsp"/>
	   <script src='dataFeedback.js'></script>
</body>
</html>