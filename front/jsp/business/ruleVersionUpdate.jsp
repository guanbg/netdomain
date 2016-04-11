<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="ruleVersionUpdateDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title">修改业务参数规则</h4>
      </div>
      <div class="modal-body">
	        	<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="version_num">规则编号：</label>
					<input type="text" id="version_num_upd" class="form-control" data-validation-required-message="规则编号必须输入"/>
					<input type="hidden" id="param_version_id_upd">
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="version_name">规则名称：</label>
					<input type="text" id="version_name_upd" class="form-control" data-validation-required-message="规则名称必须输入"/>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="version_memo">规则说明：</label>
					<input type="text" id="version_memo_upd" class="form-control"/>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="disp_order">显示顺序：</label>
					<input type="number" id="disp_order_upd" class="form-control input-mini"/>
				</span>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" id="update_version_button">保存</button>
      </div>
    </div>
  </div>
</div>