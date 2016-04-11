<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.platform.cubism.front.login.Logon"%> 
<%
	String ctx = request.getContextPath();
%>
<div class="row margin-lr-0">
	<div class="col-md-offset-1 col-md-10 col-lg-offset-2 col-lg-8" id="contractorDiv"><!-- xs:超小，sm:小，md:中，lg：大 -->
		<div class="panel panel-user-defined panel-default" id="account_accordion">
		   <div class="panel-heading panel-heading-user-defined">
		      <a class="margin-left-10 float-right" data-toggle="collapse" data-parent="#account_accordion" href="#account_collapse"><i class="glyphicon glyphicon-chevron-up"></i></a>
		      <a href="#" class="float-right" data-original-title="编辑邮箱信息" data-toggle="tooltip" data-placement="bottom" title="" id="btnEditAccount">[编辑]</a>
		      <h3 class="panel-title">账户信息</h3>
		   </div>
		   <div id="account_collapse" class="panel-collapse collapse in">
			   <div class="panel-body">
			      	<div class="row margin-0">
			      		<div class="col-sm-6 padding-0 main-padding-right-10">
			      			<span class="input-group margin-bottom-10">
								<label class="input-group-addon input-group-addon-label">　　　企业名称：</label>
								<input class="form-control" id="company_name" value="<%=Logon.getUser(request,"contractor.company_name")%>" readonly="readonly">
							</span>
			      		</div>
			      		<div class="col-sm-6 padding-0 main-padding-left-10">
			      			<span class="input-group">
								<label class="input-group-addon input-group-addon-label">电子邮箱：</label>
								<input class="form-control" id="email" value="<%=Logon.getUser(request,"contractor.email")%>" readonly="readonly">
							</span>
			      		</div>
			      	</div>
			      	<div class="row margin-0">
				      	<div class="col-sm-6 padding-0 main-padding-right-10">
				      			<span class="input-group margin-bottom-10">
									<label class="input-group-addon input-group-addon-label">营业执照注册号：</label>
									<input class="form-control" id="business_license_reg_num" value="<%=Logon.getUser(request,"contractor.business_license_reg_num")%>" readonly="readonly">
								</span>
				      		</div>
				      		<div class="col-sm-6 padding-0 main-padding-left-10">
				      			<span class="input-group">
									<label class="input-group-addon input-group-addon-label">　　备注：</label>
									<input class="form-control" style="height: auto;" value="营业执照注册号为企业登录账号" readonly="readonly">
								</span>
				      		</div>
				      </div>
			   </div>
		   </div>
		</div>
		<div id="editAccountModalDialogContainer" class="hide2"></div>
		<div class="panel panel-user-defined panel-default" id="crop_accordion">
			<div class="panel-heading panel-heading-user-defined">
		   		<a class="margin-left-10 float-right" data-toggle="collapse" data-parent="#crop_accordion" href="#crop_collapse"><i class="glyphicon glyphicon-chevron-up"></i></a>
		      	<a href="#" class="float-right" data-original-title="编辑企业信息" data-toggle="tooltip" data-placement="top" title="" id="btnEditCropInfo">[编辑]</a>
		      	<h3 class="panel-title">企业信息　<font color="red" style="font-size: 12px;">带*为必填项</font></h3>
		   	</div>
		   	<div id="crop_collapse" class="panel-collapse collapse in " >
		   	<div class = "panel-body " >
		      		<table width="100%" border="0" cellspacing="0" cellpadding="0" >
					  <tr>
					    <td align="right" class="padding-bottom-10" style="width: 150px;">组织机构登记机关：</td>
					    <td  class="padding-bottom-10" style="width: 250px;"><div class="form-control" id="registration_authority" data-type="text" style="height:auto;min-height: 33px;"><%=Logon.getUser(request,"contractor.registration_authority")%></div></td>
					    <td align="right" class="padding-bottom-10" style="width: 150px" >营业执照登记机关：</td>
					    <td  class="padding-bottom-10" style="width: 250px;"><div class="form-control" id="business_license_reg_auth" data-type="text" style="height:auto;min-height: 33px;"><%=Logon.getUser(request,"contractor.business_license_reg_auth")%></div></td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10"><font color="red">*</font> 法定代表人信息：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="legal_representative" data-type="text"><%=Logon.getUser(request,"contractor.legal_representative")%></span></td>
					      <td align="right" class="padding-bottom-10"><font color="red">*</font> 联系人手机：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="contact_phone" data-type="text"><%=Logon.getUser(request,"contractor.contact_phone")%></span></td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10">实缴注册资本(万元)：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="paid_registered_capital" data-type="text"><%=Logon.getUser(request,"contractor.paid_registered_capital")%></span></td>
					    <td align="right" class="padding-bottom-10"><font color="red">*</font> 企业类型：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="enterprise_type" data-type="select"><%=Logon.getUser(request,"contractor.enterprise_type")%></span></td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10 padding-left-5">注册资本(万元)：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="registered_capital" data-type="text"><%=Logon.getUser(request,"contractor.registered_capital")%></span></td>
					    <td align="right" class="padding-bottom-10">经营期限：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="operating_period" data-type="text"><%=Logon.getUser(request,"contractor.operating_period")%></span></td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10">成立日期：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="establishment_date" data-type="date"><%=Logon.getUser(request,"contractor.establishment_date")%></span></td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10">地址：</td>
					   <td class="padding-bottom-10" colspan="3"><div class="form-control" id="domicile" data-type="textarea" style="max-width:700px;height:auto;min-height: 33px;"><%=Logon.getUser(request,"contractor.domicile")%></div></td>
					  </tr>
					  <tr>
					    <td class="padding-bottom-10" align="right">经营范围：</td>
					    <td colspan="3" class="padding-bottom-10"><div id="business_scope" class="form-control margin-bottom-10" style="height:auto;min-height:200px;max-width:700px;"  data-type="textarea"><%=Logon.getUser(request,"contractor.business_scope")%></div></td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10"><font color="red">*</font> 专业分类：</td>
					   <td class="padding-bottom-10" colspan="3">
					    <div id="professional_list" data-type="checklist" class="form-control" style="min-height:50px;height: auto;"><%=Logon.getUser(request,"contractor.professional_list")%></div>
					   </td>
					  </tr>
					</table>
		      	<div class="row margin-0">
		      		<div class="col-sm-6 padding-0 main-padding-right-10">
		      			<div class="thumbnail text-center margin-bottom-0 main-margin-bottom-10">
			      			<form action="netdomain.sys_diskfile_upload" method="post" enctype="multipart/form-data">
								<img id="business_license_pic"  class="pack-data business pointer" style="height:212px;max-height:212px;" alt="营业执照" src="netdomain.sys_diskfile_download?percentage=40&downtype=image&fileid=<%=Logon.getUser(request,"contractor.business_license_pic")%>" data-fileid="<%=Logon.getUser(request,"contractor.business_license_pic")%>"/>
								<div class="caption">
					        		<h4><font color="red">*</font> 营业执照</h4>
					        		<a id="business_license_gallery"   href="netdomain.sys_diskfile_download?downtype=image&fileid=<%=Logon.getUser(request,"contractor.business_license_pic")%>">
					        		     <input type="image" src='<%=ctx%>/skin/default/crop/img/view.png' id="busfileView"  >
					        		</a>
					        		<span class="file-upload" >
					        			<input type="image" src='<%=ctx%>/skin/default/crop/img/load.png' style="margin-bottom:-5px;" >
					        			<input type="file" onchange="javascript:business_license_imgUpload.upload(this);" name="busfileUploadName" id="busfileUpload"  />
					        		</span>
				      			</div>
							</form>
			    		</div>
		      		</div>
		      		<div class="col-sm-6 padding-0 main-padding-left-10" >
		      			<div class="thumbnail text-center margin-bottom-0 main-margin-bottom-10" >
			      			<form action="netdomain.sys_diskfile_upload" method="post" enctype="multipart/form-data">
			      			   <br><br>
								<img id="account_code_pic"  class="pack-data account pointer" style="height:150px;max-height:150px;" alt="开户许可证" src="netdomain.sys_diskfile_download?percentage=40&downtype=image&fileid=<%=Logon.getUser(request,"contractor.account_code_pic")%>" data-fileid="<%=Logon.getUser(request,"contractor.account_code_pic")%>"/>
								<br><br>
								<div class="caption">
					        		<h4><font color="red">*</font> 开户许可证</h4>
					        		<a id="account_code_gallery" href="netdomain.sys_diskfile_download?downtype=image&fileid=<%=Logon.getUser(request,"contractor.account_code_pic")%>">
					        			<input type="image" src='<%=ctx%>/skin/default/crop/img/view.png' id="accfileView"  >
					        			<!--  <button type="button" class="btn btn-primary btn-sm" style="margin-top:-20px;" id="accfileView"><i class="glyphicon glyphicon-save-file"></i> 查看</button>-->
					        		</a>
					        		<span class="file-upload" >
					        			<input type="image" src='<%=ctx%>/skin/default/crop/img/load.png' style="margin-bottom:-5px;" >
					        			<!--  <button type="button" class="btn btn-info btn-sm pointer "><i class="glyphicon glyphicon-open-file"></i> 上传</button>-->
					        			<input type="file" onchange="javascript:account_code_imgUpload.upload(this);" name="accfileUploadName" id="accfileUpload"/>
					        		</span>
				      			</div>
							</form>
			    		</div>
		      		</div>
		      	</div>
		      	<div class="space-4"></div>
		      	<div class="space-4"></div>
		      	<div class="row margin-0">
		      	<div class="panel panel-default">
					<div class="panel-heading">
				      	<form class="float-right" action="netdomain.sys_diskfile_upload" method="post" enctype="multipart/form-data">
			      			<span class="file-upload">
			      			    <input type="image" src='<%=ctx%>/skin/default/crop/img/load.png' style="margin-bottom:-5px;" >
				        		<!--  <button type="button" class="btn btn-info btn-sm"><i class="glyphicon glyphicon-open-file"></i> 上传</button>-->
								<input type="file" onchange="javascript:contractor_file_upload.upload(this);" name="fileUploadName" id="fileUpload"/>
				        	</span>
						</form>
						<h3 class="panel-title">专业相关资质附件<font color="red" style="font-size:10px; ">　注：上传附件建议不要超过500K,以便于查看</font></h3>
				   	</div>
				   	<div class ="panel-body">
				      <ol id="accessory_ol">
				      </ol>
				   	</div>
		   		</div>
		      	</div>
		      	<div class="space-4"></div>
		      	<div align="right">
		      	<input type="button" id="contractor_meno"  class="btn btn-info" value="审核流程记录">
		      	<input type="button" id="update_info" class="btn btn-primary hide2" value="企业信息变更申请">
		      	<input type="button"  id="contractor_submit_audit"  value="企业主体信息提交审核" class="btn btn-primary" />
		        </div>
		   </div>
		   </div>
		</div>
		<br>
		<div class="panel panel-user-defined panel-default main-margin-bottom-50" id="documentor_accordion">
			<div class="panel-heading panel-heading-user-defined main-margin-bottom-13	">
		      	<a class="float-right" data-toggle="collapse" data-parent="#documentor_accordion" href="#documentor_collapse"><i class="glyphicon glyphicon-chevron-up"></i></a>
			   	<a href="#" id="documentor_delete_btn" class="float-right margin-right-10"><input type="image" src='<%=ctx%>/skin/default/crop/img/del.png'></a>
			   	<a href="#" id="documentor_update_btn" class="float-right margin-right-10"><input type="image" src='<%=ctx%>/skin/default/crop/img/upd.png'></a>
			   	<a href="#" id="documentor_add_btn" class="float-right margin-right-10 "><input type="image" src='<%=ctx%>/skin/default/crop/img/add.png'></a>
			   <!-- 屏蔽管理员操作 提交审核/打印预览 
			    <a href="#" id="documentor_print_btn" class="float-right margin-right-10"><img class="print" width="82" height="22"/></a>
			   	<a href="#" id="documentor_submit_btn" class="float-right margin-right-10 " ><img class="audit" width="82" height="22"/></a>
			   	-->
			    <h3 class="panel-title">资料员信息<span style="color: red;font-size: 10px;">　注：企业未审核通过时，资料员提交后暂时无法进行审核。</span></h3>
		   	</div>	<!-- data-single-select="true" 不可多选 -->
		   	<div id="documentor_collapse" class="table-responsive panel-collapse collapse in">
		   		<table id="documentor_table" class="table table-striped table-hover margin-0"
		   			data-toggle="table"
			       	data-side-pagination='server'
			       	data-pagination="true" 
			       	data-page-list="[10,20, 50, 100, 200]"
			       	data-page-number="1"
			       	data-page-size="10"
			       	data-click-to-select="true"
			       	data-single-select="true"
			       	data-content-type="application/json"
			       	data-data-type="json"
			       	data-id-field='documentor_id'>
			    <thead>
			    <tr>
			        <th data-field="state" data-checkbox="true" data-formatter="stateFormatter"></th>
			        <th data-field="documentor_id" data-visible="false">资料员主键</th>
			        <th data-field="xuhao">序号</th>
			        <th data-field="documentor_name">姓名</th>
			        <th data-field="login_name">账号</th>
			        <th data-field="mobile_no">手机</th>
			        <th data-field="email">邮箱</th>
			        <th data-field="tenders_code">标段代号</th>
			        <th data-field="documentor_status" data-formatter="documentorStateFormatter">状态</th>
			    </tr>
			    </thead>
				</table>
	      	</div>
		</div>
		<div id="addDocumentorModalDialogContainer" class="hide2"></div>
		<div id="updateDocumentorModalDialogContainer" class="hide2"></div>	
		<div id="printDocumentorModalDialogContainer" class="hide2"></div>
		<div id="auditDocumentorModalDialogContainer" class="hide2"></div>
		<div class="modal fade" id="viewDodumentorIdeaDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
			  <div class="modal-dialog">
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">审核流程记录</h4>
			      </div>
			      <div class="modal-body">
				      <div style="width:auto;max-height:350px; overflow-y:scroll;">
				      <table class="dmb_table table table-striped table-hover margin-bottom-0" id="crop_table">
				      		<thead>
							</thead>
							<tbody>
							</tbody>
				      	</table>
				      </div>
			      </div>
			        <div class="modal-footer">
			        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			       </div>
			    </div>
			 </div>
	</div>
	</div>
</div>