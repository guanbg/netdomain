<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
	</head>

	<body class="no-skin" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
		<div class="main-container">
			<table id='myJqGrid' style="width:100%"></table> 
			<div id='myJqGridPager'></div>
			
			<div class="modal fade" id="updateVersionModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
			  <div class="modal-dialog"><!--  modal-lg -->
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">修改版本信息</h4>
			      </div>
			      <div class="modal-body">
			        <form role="form">
			        	<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="version_num">版&nbsp;&nbsp;本&nbsp;号：</label>
							<input type="text" id="version_num" class="form-control" data-validation-required-message="版本号必须输入"/>
						</span>
						<div class="space-4"></div>
						<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="version_name">版本名称：</label>
							<input type="text" id="version_name" class="form-control" data-validation-required-message="版本名称必须输入整数"/>
						</span>
						<div class="space-4"></div>
						<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="version_memo">版本说明：</label>
							<input type="text" id="version_memo" class="form-control"/>
						</span>
						<div class="space-4"></div>
						<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="disp_order">显示顺序：</label>
							<input type="number" id="disp_order" value="100" class="form-control input-mini"/>
						</span>
						<div class="space-4"></div>
						<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="disp_order">业务规则：</label>
							<select id="param_version_id" class="form-control"></select>
						</span>
						<input type="hidden" id="version_id"/>
			        </form>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			        <button type="button" class="btn btn-primary" id="update_version_button">保存</button>
			      </div>
			    </div>
			  </div>
			</div>
		</div><!-- /.main-container -->	
		
		<div id="ruleVersionViewDialogContainer" class="hide2"></div>
		<div id="packVersionViewDialogContainer" class="hide2"></div>
		
		<jsp:include page="../../inc/script.jsp"/>
		<script src="version.js"></script>
	</body>
</html>