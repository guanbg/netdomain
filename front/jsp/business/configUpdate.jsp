<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="configUpdateDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title text-center">修改业务参数</h4>
      </div>
      <div class="modal-body">
      	<div class="widget-main padding-8 no-padding-left no-padding-right" style="padding-bottom:0px;">
	      	<div class="tab-content padding-0">
				<div class="row" style='margin:0px;'>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="parent_name_update">上级：</label>
						<span id="parent_name_update" class="form-control"></span><input type="hidden" id="id_update">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="name_update">名称：</label>
						<input type="text" id="name_update" class="form-control" placeholder="名称必须输入" data-validation-required-message="名称必输">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="code_update">编号：</label>
						<input type="text" id="code_update" class="form-control" placeholder="编号必须输入" data-validation-required-message="编号必输">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="disporder_update">次序：</label>
						<input type="number" id="disporder_update" value="100" class="form-control noclear" placeholder="请输入显示顺序">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="identity_update">标识：</label>
						<input type="text" id="identity_update" class="form-control" placeholder="请输入查找标识">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="status_update0">状态：</label>
						<span class="form-control" style="padding:4px;">
							<label class="radio-inline"><input name="status_update" value="0" type="radio">使用中</label>
							<label class="radio-inline"><input name="status_update" value="1" type="radio">未使用</label>
						</span>
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="value1_update">显示代码：</label>
						<input type="text" id="value1_update" class="form-control" placeholder="请输入显示代码">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="value2_update">年限：</label>
						<input type="number" id="value2_update" value="10" class="form-control noclear" placeholder="请输入最大年限">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="value3_update">页数：</label>
						<input type="number" id="value3_update" value="200" class="form-control noclear" placeholder="请输入最大页数">
					</span>
					<div class="space-4"></div>
					<span class="input-group">
						<label class="input-group-addon no-padding-right input-group-addon-label" for="memo_update">备注：</label>
						<textarea id="memo_update" class="form-control" placeholder="请输入备注"></textarea>
					</span>
				</div>
			</div>
      	</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" id="configCloseButton">关闭</button>
        <button type="button" class="btn btn-primary" id="configUpdateButton">保存</button>
      </div>
    </div>
  </div>
</div>