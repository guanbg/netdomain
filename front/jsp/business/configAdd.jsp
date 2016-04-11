<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="configAddDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title text-center">新增业务参数</h4>
      </div>
      <div class="modal-body">
      	<div class="widget-main padding-8 no-padding-left no-padding-right" style="padding-bottom:0px;">
	      	<div class="tab-content padding-0">
				<div class="row" style='margin:0px;'>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="parent_name_add">上级：</label>
						<span id="parent_name_add" class="form-control"></span><input type="hidden" id="parentid_add">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="name_add">名称：</label>
						<input type="text" id="name_add" class="form-control" placeholder="名称必须输入" data-validation-required-message="名称必输">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="code_add">编号：</label>
						<input type="text" id="code_add" class="form-control" placeholder="编号必须输入" data-validation-required-message="编号必输">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="disporder_add">次序：</label>
						<input type="number" id="disporder_add" value="100" class="form-control noclear" placeholder="请输入显示顺序">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="identity_add">标识：</label>
						<input type="text" id="identity_add" class="form-control" placeholder="请输入查找标识">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="status_add0">状态：</label>
						<span class="form-control" style="padding:4px;">
							<label class="radio-inline"><input name="status_add" id="status_add0" value="0" type="radio" checked>使用中</label>
							<label class="radio-inline"><input name="status_add" id="status_add1" value="1" type="radio">未使用</label>
						</span>
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="value1_add">显示代码：</label>
						<input type="text" id="value1_add" class="form-control noclear" placeholder="请输入显示代码">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="value2_add">年限：</label>
						<input type="number" id="value2_add" value="10" class="form-control noclear" placeholder="请输入最大年限">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="value3_add">页数：</label>
						<input type="number" id="value3_add" value="200" class="form-control noclear" placeholder="请输入最大页数">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="memo_add">备注：</label>
						<textarea id="memo_add" class="form-control" placeholder="请输入备注"></textarea>
					</span>
				</div>
			</div>
      	</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" id="configAddCloseButton">关闭</button>
        <button type="button" class="btn btn-primary" id="configAddButton">新增</button>
      </div>
    </div>
  </div>
</div>