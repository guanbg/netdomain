<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	String ctx = request.getContextPath();
%>
<div class="modal fade" id="viewdocumentorParticularDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-lg"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title text-center">资料员详细信息<input type="hidden" id="username"></h4>
      </div>
      <div class="modal-body">
      <div class="panel panel-user-defined panel-default" id="documentor_accordion">
		   <div class="panel-heading panel-heading-user-defined">
		      <a class="margin-left-10 float-right" data-toggle="collapse" data-parent="#documentor_accordion" href="#documentor_collapse"><i class="glyphicon glyphicon-chevron-up"></i></a>
		      <h3 class="panel-title">基本信息</h3>
		   </div>
		   <div id="documentor_collapse" class="panel-collapse collapse in">
			   <div class="panel-body">
			      	<div class="row margin-0">
			      		<div class="col-sm-6 padding-0 main-padding-right-10">
			      		<span class="input-group margin-bottom-10">
								<label class="input-group-addon input-group-addon-label">姓名：</label>
								<span class="form-control" id="documentor_name"></span>
								 <input type="hidden" id="documentor_id">
								 <input type="hidden" id="contractor_id">
							</span>
			      		</div>
			      		<div class="col-sm-6 padding-0 main-padding-left-10">
							<span class="input-group margin-bottom-10 margin-left-10">
								<label class="input-group-addon input-group-addon-label">性别：</label>
								<span class="form-control" id="documentor_sex"></span>
							</span>
			      		</div>
			      	</div>
			   </div>
		   </div>
		  </div>
		  <div class="panel panel-user-defined panel-default" id="docu_accordion">
		   <div class="panel-heading panel-heading-user-defined">
		      <a class="margin-left-10 float-right" data-toggle="collapse" data-parent="docu_accordion" href="#docu_collapse"><i class="glyphicon glyphicon-chevron-down"></i></a>
		      <h3 class="panel-title">资料员信息</h3>
		   </div>
		   <div id="docu_collapse" class="panel-collapse collapse " style="margin-right: 100px;">
	      	   <div class="space-4"></div>
				<table width="100%" border="0" cellspacing="0" cellpadding="0" >
				  <tr>
				    <td align="right" class="padding-bottom-10" style="width:20%;">出生日期：</td>
				    <td class="padding-bottom-10" style="width:30%;"><span  id="documentor_birthday" class="form-control"></span></td>
				    <td align="right" class="padding-bottom-10" style="width:20%;">身份证号：</td>
				    <td class="padding-bottom-10" style="width:30%;"><span  id="documentor_cardid" class="form-control"></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">学历选择：</td>
				    <td class="padding-bottom-10">
				    <span  id="documentor_education" class="form-control"></span>
				    </td>
				    <td align="right" class="padding-bottom-10">职称：</td>
				    <td class="padding-bottom-10"><span  id="documentor_post" class="form-control"></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">毕业院校：</td>
				    <td class="padding-bottom-10"><span  id="graduate_institutions" class="form-control" style="height: auto;min-height: 33px;"></span></td>
				    <td align="right" class="padding-bottom-10">参加工作时间：</td>
				    <td class="padding-bottom-10"><span  id="working_date" class="form-control"></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">所学专业：</td>
				    <td class="padding-bottom-10"><span  id="profession" class="form-control" style="height: auto;min-height: 33px;"></span></td>
				    <td align="right" class="padding-bottom-10">从事本专业工作年限：</td>
				    <td class="padding-bottom-10"><span  id="service_year" class="form-control"></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">个人邮箱：</td>
				    <td class="padding-bottom-10"><span  id="email" class="form-control"></span></td>
				    <td align="right" class="padding-bottom-10">联系电话：</td>
				    <td class="padding-bottom-10"><span  id="mobile_no" class="form-control"></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">标段代号：</td>
				    <td class="padding-bottom-10"><span  id="tenders_code" class="form-control" style="height: auto;"></span></td>
				    <td align="right" class="padding-bottom-10">合同名称：</td>
				    <td class="padding-bottom-10">
				    <span id="contract_name" class="form-control" style="height: auto;min-height: 33px;"></span>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">注册时间：</td>
				    <td class="padding-bottom-10"><span  id="register_date" class="form-control" ></span></td>
				    <td align="right" class="padding-bottom-10">专业分类：</td>
				    <td class="padding-bottom-10">
				    <span  id="professional_list" class="form-control"></span>
				  </td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">单位名称：</td>
				    <td class="padding-bottom-10"><span  id="company_name" class="form-control" style="height: auto;"></span></td>
				    <td align="right" class="padding-bottom-10">单位联系电话：</td>
				    <td class="padding-bottom-10"><span  id="contact_phone" class="form-control" style="height: auto;"style="height: auto;"></span></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">所属标段单位<br>工程名称：</td>
				    <td class="padding-bottom-10" colspan="3">
				    <div id="project_name_add" style="min-height:70px;height:auto;" class="form-control" ></div>
				  </tr>
				  <tr>
				  <td align="right" class="padding-bottom-10">个人简介：</td>
				    <td class="padding-bottom-10" colspan="3">
				    <div id="introduction" class="form-control" style="min-height:100px;height:auto;"></div>
				  </tr>
				</table>
		   </div>
		  </div>
		 <div class="panel panel-user-defined panel-default" id="accessory_accordion">
		   <div class="panel-heading panel-heading-user-defined">
		      <a class="margin-left-10 float-right" data-toggle="collapse" data-parent="#accessory_accordion" href="#accessory_collapse"><i class="glyphicon glyphicon-chevron-down"></i></a>
		      <h3 class="panel-title">其他资料</h3>
		   </div>
		   <div id="accessory_collapse" class="panel-collapse collapse">
			   <div class="panel-body">
			      	<div class="row margin-0">
			      		<div class="col-sm-6 padding-0 main-padding-right-10">
			      		<div class="thumbnail text-center margin-bottom-0 main-margin-bottom-10" style="margin-right:10px;">
			      			<img id="photo" class="pack-data"   style="height: 240px;max-height: 240px;cursor: pointer;" src=""/>
							<div class="caption">
					        		<h4>本人照片</h4>
					        		<!-- 
					        	    <a id="photo_gallery"  href="">
					        			<button type="button" class="btn btn-primary btn-sm" style="margin-top:-20px;"><i class="glyphicon glyphicon-save-file"></i> 查看</button>
					        		</a>
					        		-->
				      			</div>
				      	   </div>
			      		</div>
			      		<div class="col-sm-6 padding-0 main-padding-left-10">
			      			<div class="thumbnail text-center margin-bottom-0 main-margin-bottom-10">
			      			<img id="scan_accessory_pic" class="pack-data" style="height: 240px;max-height: 240px;max-width:200px;cursor: pointer;" src=""/>
							<div class="caption">
					        		<h4>扫描附件</h4>
					        		<!-- 
					        		<a id="scan_accessory_gallery"  href="">
					        			<button type="button" class="btn btn-primary btn-sm" style="margin-top:-20px;"><i class="glyphicon glyphicon-save-file"></i> 查看</button>
					        		</a>
					        		-->
				      			</div>
				      	</div>
			      		</div>
			      	</div>
			   </div>
		   </div>
		  </div> 
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#approval_meno" id="refusal">审核驳回</button>
        <button type="button" class="btn btn-success" id="approval_pass">审核通过</button>
      </div>
    </div>
  </div>
  </div>
<!-- 模态框（Modal） -->
<div class="modal fade" id="approval_meno" tabindex="-1" role="dialog" >
   <div class="modal-dialog">
      <div class="modal-content">
         <div class="modal-header">
            <button type="button" class="close" 
               data-dismiss="modal" aria-hidden="true" id="close">
                  &times;
            </button>
            <h4 class="modal-title" id="myModalLabel">
                            驳回意见
            </h4>
         </div>
         <div class="modal-body" >
            <textarea style="width: 100%;height: 100%;" id="meno"></textarea>
         </div>
         <div class="modal-footer">
            <button type="button" class="btn btn-default" 
               data-dismiss="modal">关闭
            </button>
            <button type="button" class="btn btn-primary" id="approval_refusal">
                               确定
            </button>
         </div>
      </div><!-- /.modal-content -->
</div><!-- /.modal -->
</div>
