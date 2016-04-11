<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="packVersionViewDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title">增量版本信息</h4>
      </div>
      <div class="modal-body">
       	<span class="input-group">
			<label class="input-group-addon no-padding-right input-group-addon-label">版本编号：</label>
			<span id="version_num" class="form-control"></span>
		</span>
		<div class="space-4"></div>
		<span class="input-group">
			<label class="input-group-addon no-padding-right input-group-addon-label">版本名称：</label>
			<span id="version_name" class="form-control"></span>
		</span>
		<div class="space-4"></div>
		<span class="input-group">
			<label class="input-group-addon no-padding-right input-group-addon-label">版本备注：</label>
			<span id="version_memo" class="form-control"></span>
		</span>
		<div class="space-4"></div>
		<span class="input-group">
			<label class="input-group-addon no-padding-right input-group-addon-label">增量文件：</label>
			<a href="#" class="form-control" id="version_file_name">下载</a>
		</span>
		<div class="space-4"></div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>