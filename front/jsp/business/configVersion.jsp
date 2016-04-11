<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="configVersionDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title text-center">创建新版本</h4>
      </div>
      <div class="modal-body">
      	<div class="widget-main padding-8 no-padding-left no-padding-right" style="padding-bottom:0px;">
	      	<div class="tab-content padding-0">
				<div class="row" style='margin:0px;'>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="parent_name_version">上&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;级：</label>
						<span id="parent_name_version" class="form-control"></span><input type="hidden" id="id_version">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="version_code">版&nbsp;&nbsp;本&nbsp;号：</label>
						<input type="text" id="version_code" class="form-control" placeholder="必须输入" data-validation-required-message="版本号必输">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="version_name">版本名称：</label>
						<input type="text" id="version_name" class="form-control" placeholder="必须输入" data-validation-required-message="版本名称必输">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="base_version">基础版本：</label>
						<select id="base_version" class="form-control selectpicker" data-placeholder="请选择..."></select>
					</span>
				</div>
			</div>
      	</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" id="configVersionCloseButton">关闭</button>
        <button type="button" class="btn btn-primary" id="configVersionButton">创建版本</button>
      </div>
    </div>
  </div>
</div>