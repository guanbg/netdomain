<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
 <div class="body padding-0">
    <h4 class="modal-title text-center">表格文件详细信息</h4>
	<div class="row" style='margin:0px;'>
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