<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.platform.cubism.front.login.Logon"%> 

<div class="row margin-lr-0">
	<div class="col-md-offset-1 col-md-10 col-lg-offset-2 col-lg-8" id="documentorDiv"><!-- xs:超小，sm:小，md:中，lg：大 -->
		<div class="panel panel-user-defined panel-default" id="docu_accordion">
		   <div class="panel-heading panel-heading-user-defined">
		      <a class="margin-left-10 float-right" data-toggle="collapse" data-parent="#docu_accordion" href="#docu_collapse"><i class="glyphicon glyphicon-chevron-down"></i></a>
		      <h3 class="panel-title">基本信息
		      <input type="hidden" id="documentor_id" value="<%=Logon.getUser(request,"documentor.documentor_id")%>">
		      <input type="hidden" id="contractor_id" value="<%=Logon.getUser(request,"documentor.contractor_id")%>">
		      </h3>
		   </div>
		   <div id="docu_collapse" class="panel-collapse collapse ">
			   <div class="panel-body">
			      	<div class="row margin-0">
			      	<div class="col-sm-6 padding-0 main-padding-right-10">
			      			<span class="input-group">
								<label class="input-group-addon input-group-addon-label">　　姓名：</label>
								<span class="form-control" id="documentor_name"><%=Logon.getUser(request,"documentor.documentor_name")%></span>
							</span>
			      		</div>
			      		<div class="col-sm-6 padding-0  main-padding-left-10">
			      			<span class="input-group margin-bottom-10">
								<label class="input-group-addon input-group-addon-label">　　登录账号：</label>
								<span class="form-control" id="login_name"><%=Logon.getUser(request,"documentor.login_name")%></span>
							</span>
			      		</div>
			      	</div>
			      		<div class="row margin-0">
			      		<div class="col-sm-6 padding-0 main-padding-right-10">
			      			<span class="input-group margin-bottom-10">
								<label class="input-group-addon input-group-addon-label">单位名称：</label>
								<span class="form-control" id="company_name"><%=Logon.getUser(request,"documentor.company_name")%></span>
							</span>
			      		</div>
			      		<div class="col-sm-6 padding-0 main-padding-left-10">
			      			<span class="input-group">
								<label class="input-group-addon input-group-addon-label">单位联系电话：</label>
								<span class="form-control" id="contact_phone"><%=Logon.getUser(request,"documentor.contact_phone")%></span>
							</span>
			      		</div>
			      	</div>
			   </div>
		   </div>
		</div>
		<div class="panel panel-user-defined panel-default" id="docuor_accordion">
		   <div class="panel-heading panel-heading-user-defined">
		      <a class="margin-left-10 float-right" data-toggle="collapse" data-parent="#docuor_accordion" href="#docuor_collapse"><i class="glyphicon glyphicon-chevron-up"></i></a>
		      	<a href="#" class="float-right" data-original-title="编辑信息" data-toggle="tooltip" data-placement="top" title="" id="btnEditDocumentor">[编辑]</a>
		      <h3 class="panel-title">详细信息　<font color="red" style="font-size: 12px;">带*为必填项</font></h3>
		   </div>
		   <div id="docuor_collapse" class="panel-collapse collapse in" style="margin-right: 50px;">
	      	  <div class="panel-body">
	      	   <div class="space-4"></div>
				<table width="100%" border="0" cellspacing="0" cellpadding="0" >
				<tr>
				    <td align="right" class="padding-bottom-10" width="13%">姓名：</td>
				    <td class="padding-bottom-10" style="width:250px;"><span  id="documentor_name" class="form-control" ><%=Logon.getUser(request,"documentor.documentor_name")%></span></td>
				    <td align="right" class="padding-bottom-10" ><font color="red">*</font> 性别：</td>
				    <td class="padding-bottom-10" style="width:250px;"><span  id="documentor_sex" class="form-control" data-type="select"><%=Logon.getUser(request,"documentor.documentor_sex")%></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10" >出生日期：</td>
				    <td class="padding-bottom-10" ><span  id="documentor_birthday" class="form-control" data-type="date"><%=Logon.getUser(request,"documentor.documentor_birthday")%></span></td>
				    <td align="right" class="padding-bottom-10" ><font color="red">*</font> 身份证号：</td>
				    <td class="padding-bottom-10"><span  id="documentor_cardid" class="form-control" data-type="text"><%=Logon.getUser(request,"documentor.documentor_cardid")%></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">学历选择：</td>
				    <td class="padding-bottom-10">
				    <span class="form-control" id="documentor_education" data-type="select"><%=Logon.getUser(request,"documentor.documentor_education")%></span>
				    </td>
				    <td align="right" class="padding-bottom-10"><font color="red">*</font> 职称：</td>
				    <td class="padding-bottom-10"><span  id="documentor_post" class="form-control" data-type="select"><%=Logon.getUser(request,"documentor.documentor_post")%></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">毕业院校：</td>
				    <td class="padding-bottom-10"><span  id="graduate_institutions" class="form-control" data-type="text"><%=Logon.getUser(request,"documentor.graduate_institutions")%></span></td>
				    <td align="right" class="padding-bottom-10">参加工作时间：</td>
				    <td class="padding-bottom-10"><span  id="working_date" class="form-control" data-type="date"><%=Logon.getUser(request,"documentor.working_date")%></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">所学专业：</td>
				    <td class="padding-bottom-10"><span  id="profession" class="form-control" data-type="text"><%=Logon.getUser(request,"documentor.profession")%></span></td>
				    <td align="right" class="padding-bottom-10">从事本专业工作年限：</td>
				    <td class="padding-bottom-10"><span  id="service_year" class="form-control" data-type="select"><%=Logon.getUser(request,"documentor.service_year")%></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">个人邮箱：</td>
				    <td class="padding-bottom-10"><span  id="email" class="form-control" ><%=Logon.getUser(request,"documentor.email")%></span></td>
				    <td align="right" class="padding-bottom-10">联系电话：</td>
				    <td class="padding-bottom-10"><span  id="mobile_no" class="form-control" data-type="text"><%=Logon.getUser(request,"documentor.mobile_no")%></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">标段代号：</td>
				    <td class="padding-bottom-10"><span  id="tenders_code" class="form-control"><%=Logon.getUser(request,"documentor.tenders_code")%></span></td>
				    <td align="right" class="padding-bottom-10">合同名称：</td>
				    <td class="padding-bottom-10">
				    <span  id="contract_name" class="form-control" data-type="text" style="height:auto;min-height: 30px;" ><%=Logon.getUser(request,"documentor.contract_name")%></span>
				  </td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">注册时间：</td>
				    <td class="padding-bottom-10"><span  id="register_date" class="form-control" ><%=Logon.getUser(request,"documentor.register_date")%></span></td>
				    <td align="right" class="padding-bottom-10"><font color="red">*</font> 专业分类：</td>
				    <td class="padding-bottom-10">
				    <span  id="professional_list" class="form-control" data-type="select"><%=Logon.getUser(request,"documentor.professional_list")%></span>
				  </td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10 "><font color="red" >提示：</font></td>
				    <td class="padding-bottom-10" colspan="3">
				          <font color="gray">当所属标段单位工程名称存在多个时，以","隔开</font>
				    </td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10 ">所属标段单位<br>工程名称：</td>
				    <td class="padding-bottom-10" colspan="3">
				    <div id="project_name" class="form-control margin-bottom-10" style="height:auto;min-height:100px;max-width:650px;" data-type="textarea"><%=Logon.getUser(request,"documentor.project_name")%></div>
				    </td>
				  </tr>
				  <tr>
				  <td align="right" class="padding-bottom-10">个人简介：</td>
				    <td class="padding-bottom-10" colspan="3">
				    <div id="introduction" class="form-control margin-bottom-10" style="height:auto;min-height:100px;max-width:650px;" data-type="textarea"><%=Logon.getUser(request,"documentor.introduction")%></div>
				    </td>
				  </tr>
				</table>
				<div class="space-4" ></div>
				<div class="space-4"></div>
				<div class="row margin-0">
		      		<div class="col-sm-6 padding-0 main-padding-right-10">
			    		<div class="thumbnail text-center margin-bottom-0 main-margin-bottom-10" >
			      			<form action="netdomain.sys_diskfile_upload" method="post" enctype="multipart/form-data">
								<img id="photo"   class="pack-data photo pointer" width="160" height="240"   alt="照片" src="netdomain.sys_diskfile_download?percentage=40&downtype=image&fileid=<%=Logon.getUser(request,"documentor.photo")%>" data-fileid="<%=Logon.getUser(request,"documentor.photo")%>" />
								<div class="caption">
					        		<h4><font color="red">*</font> 本人照片(一寸)</h4>
					        		<a id="photo_gallery" href="netdomain.sys_diskfile_download?downtype=image&fileid=<%=Logon.getUser(request,"documentor.photo")%>">
					        			<!--  <button type="button" class="btn btn-primary btn-sm" style="margin-top:-20px;" id="photoView"><i class="glyphicon glyphicon-save-file"></i>查看</button>-->
					        			<input type="image" src='../skin/default/crop/img/view.png' id="photoView"  >
					        		</a>
					        		<span class="file-upload" >
					        		    <input type="image" src='../skin/default/crop/img/load.png' style="margin-bottom:-5px;" >
					        			<!-- <button type="button" class="btn btn-info btn-sm"><i class="glyphicon glyphicon-open-file"></i> 上传</button> -->
					        			<input type="file" onchange="javascript:photo_imgUpload.upload(this);" id="photoUpload" />
					        		</span>
				      			</div>
							</form>
			    		</div>
		      		</div>
		      		<div class="col-sm-6 padding-0 main-padding-left-10" >
		      			<div class="thumbnail text-center margin-bottom-0 main-margin-bottom-10" >
			      			<form action="netdomain.sys_diskfile_upload" method="post" enctype="multipart/form-data">
								<img id="scan_accessory"  class="pack-data imgvalue pointer"  width="160" height="240" alt="扫描附件" src="netdomain.sys_diskfile_download?percentage=40&downtype=image&fileid=<%=Logon.getUser(request,"documentor.scan_accessory")%>" data-fileid="<%=Logon.getUser(request,"documentor.scan_accessory")%>"/>
								<div class="caption">
					        		<h4><font color="red">*</font> 扫描附件</h4>
					        		<a id="scan_license_gallery" href="netdomain.sys_diskfile_download?downtype=image&fileid=<%=Logon.getUser(request,"documentor.scan_accessory")%>">
					        			<input type="image" src='../skin/default/crop/img/view.png' id="scanView"  >
					        			<!--  <button type="button" class="btn btn-primary btn-sm" style="margin-top:-20px;" id="scanView"><i class="glyphicon glyphicon-save-file"></i> 查看</button>-->
					        		</a>
					        		<span class="file-upload" >
					        			<!--<button type="button" class="btn btn-info btn-sm"><i class="glyphicon glyphicon-open-file"></i> 上传</button>-->
					        			  <input type="image" src='../skin/default/crop/img/load.png' style="margin-bottom:-5px;" >
					        			<input type="file" onchange="javascript:audit_code_imgUpload.upload(this);" id="scanUpload"/>
					        		</span>
				      			</div>
							</form>
			    		</div>
		      		</div>
		      	</div>
		    <div class="space-4"></div>
		    <div class="space-4"></div>
		    <div>
			      <span  class="form-control margin-bottom-10" style="height: auto;">
			                 <ul><font color="red" size="+1">注意事项：</font>
								  <li>照片，扫描附件为必填项</li>
								  <li>照片为本人一寸免冠照片 </li>
								  <li>扫描附件为该资料员所填表格扫描件<br>建议不要超过500K,以便于查看<br><font color="gray" size="-1">注：已通过参建单位内部审核，且盖有公章</font></li>
								  <li>照片、扫描件类型为（png,jpg,jpeg）</li>
								  <li>请您确认资料员基本信息，扫描件无误后，再行提交</li>
						       </ul>
						       <ul><font color="red" size="+1">提示：</font>
						         <li>若您想手动填写个人信息，请点击　<a style="color: blue;" href="../source/documentorPrint.doc">[模板下载]</a></li>
						       </ul>
				</span>
			    </div>
	      	<div align="right">
	      	<input type="button" id="contractor_meno"  class="btn btn-info" value="审核流程记录">
	      	<input type="button"  id="documentor_print"  value="打印预览" class="btn btn-primary"/>
	       <!--   <input type="button" id="update_docu" class="btn btn-primary hide2" value="申请变更">--> 
	      	<input type="button"  id="documentor_submit_btn"  value="提交审核" class="btn btn-primary"/>
	       </div>
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
		   <div id="printDocumentorModalDialogContainer" class="hide2"></div>
		   <div id="templateDocumentorModalDialogContainer" class="hide2"></div>
		 </div>
	   </div>
	</div>
</div>