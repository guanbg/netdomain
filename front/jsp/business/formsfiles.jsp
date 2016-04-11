<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
		<style type="text/css">
		.ui-jqgrid-hdiv .ui-jqgrid-htable{border:0px;}
		</style>
	</head>

	<body class="no-skin">
		<div class="main-container">
			<div class="row" style='margin:0px;'>
				<div class="col-sm-3" style='padding:0px;'>
					<div class="widget-box transparent">
						<div class="widget-header widget-header-flat">
							<h4 class="widget-title smaller">表格目录</h4>
								<div class="widget-toolbar  no-border no-float">
									<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="bottom" data-original-title="分类新建" id='addDirButton'><i tabindex="-1" class="fa fa-plus purple hand"></i></a>
									
									<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="bottom" data-original-title="收起全部" id='closeAllButton'><i tabindex="-1" class="fa fa-chevron-up blue"></i></a>
									<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="bottom" data-original-title="刷新分类" id='refreshDirButton'><i tabindex="-1" class="fa fa-refresh green hand padding-4"></i></a>
								</div>
						</div>
		
						<div class="widget-body" style="overflow-x: hidden;">
							<div id="formsfiles_tree"></div>
						</div>
					</div>
				</div>
				<div class="col-sm-9" style='padding:0px;'>
					<div class="widget-box transparent">
						<div class="widget-header widget-header-flat" style="margin-top: -1px;">
							<h4 class="widget-title smaller">案卷表格</h4>
		
							<div class="widget-toolbar">
								<label id="switch_button_label">
									<small class="grey">
										<b id="switch_title">全部表格</b>
									</small>
		
									<input id="switch_button" type="checkbox" class="ace ace-switch ace-switch-6">
									<span class="lbl middle"></span>
								</label>
							</div>
						</div>
		
						<div class="widget-body" style="overflow-x: hidden;">
							<table id='myJqGrid' style="width:100%"></table> 
							<div id='myJqGridPager'></div>
						</div>
					</div>
				</div>
			</div>
			<div id="addFormsfilesDialogContainer" class="hide2"></div>
			<div id="updateFormsfilesDialogContainer" class="hide2"></div>
			<div id="viewFormsfilesDialogContainer" class="hide2"></div>
			<div id="formsfilesLibraryDialogContainer" class="hide2"></div>
			<div id="formsfilesDirDialogContainer" class="hide2"></div>
		</div><!-- /.main-container -->
		
		<jsp:include page="../../inc/script.jsp"/>
		<script src="formsfiles.js"></script>
	</body>
</html>