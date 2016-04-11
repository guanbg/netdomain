<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="viewFormsfilesDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title text-center">表格详细信息</h4>
      </div>
      <div class="modal-body">
      	<div class="widget-main padding-8 no-padding-left no-padding-right" style="padding-bottom:0px;">
	      	<div class="tab-content padding-0">
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="document_number">文件编号：</label>
					<span id="document_number" class="form-control"></span>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="file_title">文件名称：</label>
					<span id="file_title" class="form-control"></span>
				</span>
				
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="tb_version">版&nbsp;&nbsp;本&nbsp;&nbsp;号：</label>
					<span id="tb_version" class="form-control"></span>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="total_pages">总&nbsp;&nbsp;页&nbsp;&nbsp;数：</label>
					<span id="total_pages" class="form-control"></span>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="paper_size">纸张大小：</label>
					<span id="paper_size" class="form-control"></span>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="dir_name">分类目录：</label>
					<span id="dir_name" class="form-control"></span>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label">模板文件：</label>
					<a href="#" class="form-control" id="tmpl_file_name">&nbsp;</a>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label">样板文件：</label>
					<a href="#" class="form-control" id="example_file_name">&nbsp;</a>
				</span>
				<div class="space-4"></div>
				<div class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="fill_in_rules">填写规则：</label>
					<div id="fill_in_rules" class="form-control" style="min-height:75px;height:auto;"></div>
				</div>
			</div>
      	</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>