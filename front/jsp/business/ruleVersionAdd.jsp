<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="ruleVersionAddDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-lg modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title">新建业务参数规则</h4>
      </div>
      <div class="modal-body">
        <div class="row" style='margin:0px;'>
			<div class="col-sm-6" style='padding:0px;padding-right:5px;'>
	        	<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="version_num">规则编号：</label>
					<input type="text" id="version_num" class="form-control" data-validation-required-message="规则编号必须输入"/>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="version_name">规则名称：</label>
					<input type="text" id="version_name" class="form-control" data-validation-required-message="规则名称必须输入"/>
				</span>
				<div class="space-4"></div>
        	</div>
			<div class="col-sm-6" style='padding:0px;padding-left:5px;'>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="version_memo">规则说明：</label>
					<input type="text" id="version_memo" class="form-control"/>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="disp_order">显示顺序：</label>
					<input type="number" id="disp_order" value="100" class="form-control input-mini"/>
				</span>
				<div class="space-4"></div>
			</div>
		</div>
		<div class="row" style='margin:0px;'>
			<select id="businessrule" multiple="multiple"></select>
		</div>
		<div class="row" style='margin:0px;'>
			<div class="col-sm-6" style='padding:0px;padding-right:5px;'>
				<div class="space-4"></div>
				<div class="panel panel-default">
				   <div class="panel-heading">
				      <div class="widget-toolbar  no-border" style="margin-top: -10px; padding-right: 0px;">
						<a href="#" data-toggle="tooltip" data-placement="left" data-original-title="刷新" id='leftRefreshButton'><i class="fa fa-refresh green hand"></i></a>
					  </div>
				      <h3 class="panel-title">请选择业务参数版本</h3>
				   </div>
				   <div class="panel-body" style="padding:0px;padding-left:2px;">
				   		<div id="leftConfigTree" class="tree"></div>
				   </div>
				</div>
			</div>
			<div class="col-sm-6" style='padding:0px;padding-left:5px;'>
				<div class="space-4"></div>
				<div class="panel panel-success">
				   <div class="panel-heading">
				   	  <div class="widget-toolbar  no-border" style="margin-top: -10px; padding-right: 0px;">
						<a href="#" data-toggle="tooltip" data-placement="left" data-original-title="重置" id='rightRefreshButton'><i class="fa fa-refresh green hand"></i></a>
					  </div>
				      <h3 class="panel-title">已选择业务参数版本</h3>
				   </div>
				   <div class="panel-body" style="padding:0px;padding-left:2px;">
			      		<div id="rightConfigTree" class="tree"></div>
				   </div>
				</div>
			</div>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" id="add_version_button">新建</button>
      </div>
    </div>
  </div>
</div>