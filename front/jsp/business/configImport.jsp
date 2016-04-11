<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="configImportDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title text-center">导入业务参数</h4>
      </div>
      <div class="modal-body">
      	<div class="widget-main padding-8 no-padding-left no-padding-right" style="padding-bottom:0px;">
	      	<div class="tab-content padding-0">
				<div class="row" style='margin:0px;'>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="parent_name_import">业务参数上级：</label>
						<span id="parent_name_import" class="form-control"></span><input type="hidden" id="parentid_import">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="parent_name_Import">导入文件类型：</label>
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
				</div>
			</div>
      	</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary" id="configImportButton">导入</button>
      </div>
    </div>
  </div>
</div>