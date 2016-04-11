<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="updateFormsfilesDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title text-center">修改案卷表格</h4>
      </div>
      <div class="modal-body">
      	<div class="widget-main padding-8 no-padding-left no-padding-right" style="padding-bottom:0px;">
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="document_number">文件编号：</label>
				<input type="text" id="document_number" class="form-control" placeholder="文件编号必须输入" data-validation-required-message="文件编号必输">
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="file_title">文件题名：</label>
				<input type="text" id="file_title" class="form-control" placeholder="请输入文件题名" data-validation-required-message="文件题名必输">
			</span>
			<div class="space-4"></div>
			<form action="netdomain.sys_dbfile_upload" method="post" enctype="multipart/form-data">
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label">模板文件：</label>
					<a href="#" class="form-control" id="tmpl_file_name">&nbsp;</a>
					<input type="hidden" id="tmpl_file_id">
					<span class="file-upload input-icon">
						<i class="glyphicon glyphicon-open blue"></i>
						<input type="file" name="fileUpload" onchange="javascript:tmpl_file_upload.upload(this);"/>
					</span>
				</span>
			</form>
			<div class="space-4"></div>
			<form action="netdomain.sys_dbfile_upload" method="post" enctype="multipart/form-data">
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label">样板文件：</label>
					<a href="#" class="form-control" id="example_file_name">&nbsp;</a>
					<input type="hidden" id="example_file_id">
					<span class="file-upload input-icon">
						<i class="glyphicon glyphicon-open purple"></i>
						<input type="file" name="fileUpload" onchange="javascript:example_file_upload.upload(this);"/>
					</span>
				</span>
			</form>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="tb_version">版&nbsp;&nbsp;本&nbsp;&nbsp;号：</label>
				<input type="text" id="tb_version" class="form-control" placeholder="请输入版本号">
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="paper_size">纸张大小：</label>
				<input type="text" id="paper_size" class="form-control" placeholder="请输入纸张大小">
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="fill_in_rules">填写规则：</label>
				<textarea id="fill_in_rules" class="form-control" placeholder="请输入填写规则"></textarea>
			</span>
			<input type="hidden" id="tb_id"><input type="hidden" id="dir_id">
		</div>
     </div>
     <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" id="addNewFormsfilesButton">保存为新版本</button>
        <button type="button" class="btn btn-primary" id="updateFormsfilesButton">保存</button>
     </div>
   </div>
</div>
</div>