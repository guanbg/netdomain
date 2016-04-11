<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="versionPackDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-lg-dialog "><!--  modal-lg modal-dialog-->
    <div class="modal-content">
      <div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title text-center">增量版本发布</h4>
      </div>
      <div class="modal-body block-tree">
      	<div class="row" style='margin:0px;'>
			<div class="col-sm-6" style='padding:0px;padding-right:5px;'>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="version_num">版本编号：</label>
					<input type="text" id="version_num" class="form-control"/>
				</span>
				<div class="space-4"></div>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" for="version_name">版本名称：</label>
					<input type="text" id="version_name" class="form-control"/>
				</span>
				<div class="space-4"></div>
			</div>
			<div class="col-sm-6" style='padding:0px;padding-left:5px;'>
				<span class="input-group">
					<label class="input-group-addon no-padding-right input-group-addon-label" style="vertical-align: top;" for="version_memo">版本备注：</label>
					<textarea id="version_memo" class="form-control" rows="2" style="min-height:75px;height:auto;"></textarea>
				</span>
			</div>
		</div>
		
		<div class="row" style='margin:0px;'>
			<div class="col-sm-6" style='padding:0px;'>
				<div class="widget-box widget-color-blue">
					<div class="widget-header">
						<h4 class="widget-title lighter smaller">增量版本：新增文件</h4>
						<div class="widget-toolbar  no-border">
							<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="top" data-original-title="清空文件" id='packAddRefreshButton'><i tabindex="-1" class="fa fa-undo green hand"></i></a>
						</div>
					</div>
					<div class="widget-body">
						<div class="widget-main" style='padding:0px;'>
							<div id="pack_add_tree"></div>
						</div>
					</div>
				</div>
				<div class="widget-box">
					<div class="widget-header">
						<h4 class="widget-title lighter smaller">基础版本：<span id="base_version_name"></span></h4>
						<div class="widget-toolbar  no-border">
							<div class="btn-group">
								<a href="#" tabindex="-1" class="dropdown-toggle" data-toggle="dropdown" data-placement="top" data-original-title="选择基础版本" id='baseVersionSelectButton'>
									<i tabindex="-1" class="fa fa-university purple hand"></i>
								</a>
								<ul class="dropdown-menu pull-right" role="menu" id="base_version_menu">
							      <li><a href="#" data-value="0">当前档案库</a></li>
							      <li class="divider"></li>
							   	</ul>
						   	</div>
							<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="top" data-original-title="刷新版本" id='baseVersionRefreshButton'><i tabindex="-1" class="fa fa-refresh green hand"></i></a>
						</div>
					</div>
					<div class="widget-body">
						<div class="widget-main" style='padding:0px;'>
							<div id="base_version_tree"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-sm-6" style='padding:0px 0px 0px 10px;'>
				<div class="widget-box widget-color-grey">
					<div class="widget-header">
						<h4 class="widget-title lighter smaller">增量版本：移除文件</h4>
						<div class="widget-toolbar  no-border">
							<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="top" data-original-title="清空文件" id='packDeleteRefreshButton'><i tabindex="-1" class="fa fa-undo green hand"></i></a>
						</div>
					</div>
					<div class="widget-body">
						<div class="widget-main" style='padding:0px;'>
							<div id="pack_delete_tree"></div>
						</div>
					</div>
				</div>
				<div class="widget-box">
					<div class="widget-header">
						<h4 class="widget-title lighter smaller">增量版本：<span id="pack_version_name"></span></h4>
						<div class="widget-toolbar  no-border">
							<div class="btn-group">
								<a href="#" tabindex="-1" class="dropdown-toggle" data-toggle="dropdown" data-placement="top" data-original-title="选择增量版本" id='packVersionSelectButton'>
									<i tabindex="-1" class="fa fa-object-ungroup purple hand"></i>
								</a>
								<ul class="dropdown-menu pull-right" role="menu" id="pack_version_menu">
							   	</ul>
						   	</div>
							<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="top" data-original-title="刷新版本" id='packVersionRefreshButton'><i tabindex="-1" class="fa fa-refresh green hand"></i></a>
						</div>
					</div>
					<div class="widget-body">
						<div class="widget-main" style='padding:0px;'>
							<div id="pack_version_tree"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" id="add_version_button">创建版本</button>
      </div>
    </div>
  </div>
</div>