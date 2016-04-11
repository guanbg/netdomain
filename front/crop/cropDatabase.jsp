<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="row margin-lr-0">
	<div class="col-md-offset-1 col-md-10 col-lg-offset-2 col-lg-8"><!-- xs:超小，sm:小，md:中，lg：大 -->
		<div class="panel panel-user-defined panel-default" id="softdownload_accordion">
		   <div class="panel-heading panel-heading-user-defined">
		      <a class="margin-left-10 float-right" data-toggle="collapse" data-parent="#softdownload_accordion" href="#softdownload_collapse"><i class="glyphicon glyphicon-chevron-up"></i></a>
		      <!--  <a href="#" class="float-right" data-original-title="历史版本下载" data-toggle="tooltip" data-placement="bottom" title="">[历史版本]</a>-->
		      <h3 class="panel-title">软件下载</h3>
		   </div>
		   <div id="softdownload_collapse" class="panel-body panel-collapse collapse in">
		      	<div class="row margin-0">
		      		<div class="col-sm-6 padding-0 main-padding-right-10">
						<a id="cs_main_zip" target="_blank"><i class="glyphicon glyphicon-save"></i>轨道建设领域信息采集系统（离线客户端）</a>
		      		</div>
		      		<div class="col-sm-6 padding-0 main-padding-left-10">
						软件版本 <span id="cs_version"> V2016.010523.08</span>
		      		</div>
			   </div>
		   </div>
		</div>
		
		
		<div class="panel panel-user-defined panel-default" id="busydb_accordion">
			<div class="panel-heading panel-heading-user-defined">
		      	<a class="float-right" data-toggle="collapse" data-parent="#busydb_accordion" href="#busydb_collapse"><i class="glyphicon glyphicon-chevron-up"></i></a>
			    <h3 class="panel-title">业务资料库</h3>
		   	</div>
		   	<div id="busydb_collapse" class="table-responsive panel-collapse collapse in">
	      	<table id="contractor_database" class="table table-striped table-hover"
				data-toggle="table"
			    data-side-pagination='server'
			    data-pagination="true" 
			    data-page-list="[10,20, 50, 100]"
			    data-page-number="1"
			    data-page-size="10"
			    data-click-to-select="true"
			    data-single-select="true"
			    data-content-type="application/json"
			    data-data-type="json"
			    data-id-field='documentor_id'>
	      		<thead>
   					<tr>
				        <th data-field="lib_id" data-visible="false">资料库主键</th>
				        <th data-field="xuhao" data-width="30px">序号</th>
				        <th data-field="lib_aliase">资料库名称</th>
				        <th data-field="fs_names">专业</th>
				        <th data-field="create_date" data-width="100px">创建时间</th>
				        <th data-field="action" data-width="110px" data-formatter="actionFormatter">操作</th>
				    </tr>
				</thead>
	      	</table>
	      	</div>
		</div>
		
		<div class="panel panel-user-defined panel-default main-margin-bottom-50" id="createdb_accordion">
		   <div class="panel-heading panel-heading-user-defined">
		      <a class="float-right" data-toggle="collapse" data-parent="#createdb_accordion" href="#createdb_collapse"><i class="glyphicon glyphicon-chevron-up"></i></a>
		      <h3 class="panel-title">创建业务资料库</h3>
		   </div>
		   <div id="createdb_collapse" class="panel-body panel-collapse collapse in" style="padding-left: 0px; padding-right: 0px;">
				<div class="row margin-0">
		      		<div class="col-sm-6 padding-0">
		      			<span class="input-group margin-bottom-10">
							<label class="input-group-addon input-group-addon-label">资料库名称：</label>
							<input class="form-control" id="library_alias" type="text">
						</span>
		      		</div>
		      		<div class="col-sm-6 padding-0 main-padding-left-10 margin-bottom-10">
						<font color="red">*</font> 例如：地铁1号线1标段基建工程项目 	
		      		</div>
			   </div>
			   <div class="row margin-0">
		      		<div class="col-sm-6 padding-0">
		      			<span class="input-group margin-bottom-10">
							<label class="input-group-addon input-group-addon-label">选择资料库：</label>
							<select class="form-control" id="library_selected"></select>
						</span>
		      		</div>
		      		<div class="col-sm-6 padding-0 main-padding-left-10">
		      			<span class="input-group margin-bottom-10">
							<label class="input-group-addon input-group-addon-label">确认企业信息：</label>
							<select class = "form-control" id="crop_confirm"></select>
   						</span>
		      		</div>
			   </div>
			   <div class="row margin-0">
		      		<div class="col-sm-6 padding-0">
		      			<div class="panel panel-default">
						   <div class="panel-heading">
						      <div class="widget-toolbar  no-border" style="margin-top: -10px; padding-right: 0px;">
								<a href="#" data-toggle="tooltip" data-placement="left" data-original-title="刷新专业" id='leftRefreshButton'><i class="fa fa-refresh green hand"></i></a>
							  </div>
						      <h3 class="panel-title">可选择专业</h3>
						   </div>
						   <div class="panel-body" style="padding:0px;padding-left:2px;">
						   		<div id="leftLibraryTree" class="tree"></div>
						   </div>
						</div>
		      		</div>
		      		<div class="col-sm-6 padding-0 main-padding-left-10 margin-bottom-10">
		      			<div class="panel panel-success">
						   <div class="panel-heading">
						   	  <div class="widget-toolbar  no-border" style="margin-top: -10px; padding-right: 0px;">
								<a href="#" data-toggle="tooltip" data-placement="left" data-original-title="重置专业" id='rightRefreshButton'><i class="fa fa-refresh green hand"></i></a>
							  </div>
						      <h3 class="panel-title">已选择专业</h3>
						   </div>
						   <div class="panel-body" style="padding:0px;padding-left:2px;">
					      		<div id="rightLibraryTree" class="tree"></div>
						   </div>
						</div>
		      		</div>
			   </div>
			   
			   <div class="row margin-0 text-center">
			   		<button type="button" class="btn btn-primary" id="create_lib_btn">确认并生成</button>
			   </div>
		   </div>
		</div>
		
	</div>
</div>