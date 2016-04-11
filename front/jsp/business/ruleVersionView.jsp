<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="ruleVersionViewDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-lg modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title">业务参数规则</h4>
      </div>
      <div class="modal-body">
        <div class="row config_version_info" style='margin:0px;'>
			<div class="col-sm-6" style='padding:0px;padding-right:5px;'>
	        	<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label">规则编号：</label>
					<span id="version_num" class="form-control"></span>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label">规则名称：</label>
					<span id="version_name" class="form-control"></span>
				</span>
				<div class="space-4"></div>
        	</div>
			<div class="col-sm-6" style='padding:0px;padding-left:5px;'>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label">规则说明：</label>
					<span id="version_memo" class="form-control"></span>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label">显示顺序：</label>
					<span id="disp_order" class="form-control"></span>
				</span>
				<div class="space-4"></div>
			</div>
		</div>
		<div class="row" style='margin:0px;'>
			<div class="col-sm-6" style='padding:0px;padding-right:5px;'>
				<div class="space-4"></div>
				<div class="panel panel-default">
				   <div class="panel-heading">
				      <h3 class="panel-title">业务规则版本信息</h3>
				   </div>
				   <div class="panel-body" style="padding:0px;">
				   		<table class="table table-bordered table-striped noclear">
							<thead class="thin-border-bottom">
								<tr>
									<th><i class="ace-icon fa fa-caret-right blue"></i>名称</th>
									<th><i class="ace-icon fa fa-caret-right blue"></i>版本</th>
								</tr>
							</thead>
							<tbody id="businessrule_view">
							</tbody>
						</table>
				   </div>
				</div>
			</div>
			<div class="col-sm-6" style='padding:0px;padding-left:5px;'>
				<div class="space-4"></div>
				<div class="panel panel-success">
				   <div class="panel-heading">
				      <h3 class="panel-title">业务参数版本信息</h3>
				   </div>
				   <div class="panel-body" style="padding:0px;padding-left:2px;">
			      		<div id="configTree" class="tree"></div>
				   </div>
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