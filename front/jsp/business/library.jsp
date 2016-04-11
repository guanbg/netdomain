<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
		<style type="text/css">
		.modal-open{overflow:auto;}
		body{overflow-x:auto;}
		
		.block-tree .jstree-wholerow-ul{display:block;}
		.block-tree .jstree .jstree-open > .jstree-children{display:block;}
		</style>
	</head>

	<body class="no-skin">
		<div class="main-container" >
			<div class='row-fluid margin-lr-0'>
	  				<div id="leftContentMain">
	  					<div class="widget-box no-border">
							<div class="widget-header">
								<h4 class="widget-title lighter smaller">档案库</h4>
								<div class="widget-toolbar  no-border no-float">
									<!-- a href="#" tabindex="-1" data-toggle="tooltip" data-placement="bottom" data-original-title="锁定档案库" id='lockLibraryButton'><i tabindex="-1" class="fa fa-unlock red hand"></i></a> -->
									<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="bottom" data-original-title="新增档案库" id='addLibraryButton'><i tabindex="-1" class="fa fa-plus purple hand"></i></a>
									
									<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="bottom" data-original-title="收起全部" id='closeAllButton'><i tabindex="-1" class="fa fa-chevron-up blue"></i></a>
									<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="bottom" data-original-title="刷新档案库" id='refreshLibraryButton'><i tabindex="-1" class="fa fa-refresh green hand padding-4"></i></a>
								</div>
							</div>
							<div class="widget-body">
								<div class="widget-main" style='padding:0px;'>
									<div id="library_tree"></div>
									
									<div id="libraryDialog" class="popover">
										<div class="arrow"></div>
										<h3 class="popover-title">新建</h3>
										<div class="popover-content padding-bottom-0">
											<span class="input-group">
												<label class="input-group-addon no-padding-left no-padding-right input-group-addon-label" for="fs_code">编号：</label>
												<input type="text" id="fs_code" class="form-control" data-validation-required-message="编号必须输入"/>
												<span class="input-group-btn"><input type="text" id="fs_name_code" class="form-control" style="width:50px;border-left:0px;"/></span>
											</span>
											<div class="space-4"></div>
							          		<span class="input-group">
												<label class="input-group-addon no-padding-left no-padding-right input-group-addon-label" for="fs_name">名称：</label>
												<textarea id="fs_name" class="form-control" rows="3" data-validation-required-message="名称必须输入"></textarea>
											</span>
											<div class="space-4"></div>
											<span class="input-group">
												<label class="input-group-addon no-padding-left no-padding-right input-group-addon-label" for="disp_order">顺序：</label>
												<input type="number" id="disp_order" value="100" class="form-control input-mini noclear"/>
											</span>
											<div class="space-4"></div>
											<span class="input-group">
												<label class="input-group-addon no-padding-left no-padding-right input-group-addon-label" for="fs_memo">备注：</label>
												<textarea id="fs_memo" class="form-control" rows="3"></textarea>
											</span>
											<div class="padding-4 text-center noclear">
										        <button type="button" class="btn btn-default" id="close_library_button">关闭</button>
										        <button type="button" class="btn btn-primary" id="library_button">新增</button>
										    </div>
										</div>
									</div>
									
									<div id="importFilesDialog" class="popover">
										<div class="arrow"></div>
										<h3 class="popover-title">导入卷内文件</h3>
										<div class="popover-content padding-bottom-0">
											<span class="input-group">
												<label class="input-group-addon no-padding-left no-padding-right input-group-addon-label" for="tb_version">导入文件类型：</label>
												<span class="form-control">EXCEL</span>
											</span>
											<div class="space-4"></div>
											<form action="netdomain.sys_diskfile_upload" method="post" enctype="multipart/form-data">
												<span class="input-group">
													<label class="input-group-addon no-padding-right input-group-addon-label">选择导入文件：</label>
													<span class="form-control" id="import_file_name">&nbsp;</span>
													<input type="hidden" id="import_file_id">
													<span class="file-upload input-icon">
														<i class="glyphicon glyphicon-open blue"></i>
														<input type="file" name="fileUpload" onchange="javascript:import_file_upload.upload(this);"/>
													</span>
												</span>
											</form>
											<div class="space-4"></div>
											<div class="padding-4 text-center noclear">
										        <button type="button" class="btn btn-default" id="close_importFiles_button">取消</button>
										        <button type="button" class="btn btn-primary" id="importFiles_button">导入</button>
										    </div>
										</div>
									</div>
								</div>
							</div>
						</div>
	  				</div>
			</div>
			
			<div class="achive_file_container hide2">
  				<div id="rightContentMain_related" class="nooverflowx">
  					<table id='relatedJqGrid' style="width:100%"></table> 
					<div id='relatedJqGridPager'></div>
  				</div>
 			</div>
 			<div class="achive_file_container hide2">
  				<div id="rightContentMain_waiting" class="nooverflowx">
		        	<table id='waitingJqGrid' style="width:100%"></table> 
					<div id='waitingJqGridPager'></div>
  				</div>
 			</div>
 			<div class="achive_file_container hide2">
  				<div id="rightContentMain_detail" class="nooverflowx">
  				</div>
 			</div>
 			
			<div id="viewFormsfilesDialogContainer" class="hide2"></div>
			<div id="versionPackDialogContainer" class="hide2"></div>
		</div><!-- /.main-container -->
		
		<jsp:include page="../../inc/script.jsp"/>
		<script src="library.js"></script>
		<script type="text/javascript">
			var select_node_name,select_node_id;
		</script>
	</body>
</html>