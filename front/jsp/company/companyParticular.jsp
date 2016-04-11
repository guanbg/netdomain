<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="viewCompanyParticularDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-lg"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title text-center">企业详细信息<input type="hidden" id="username"></h4>
      </div>
      <div class="modal-body">
      	<div class="panel panel-user-defined panel-default" id="account_accordion">
		   <div class="panel-heading panel-heading-user-defined">
		      <a class="margin-left-10 float-right" data-toggle="collapse" data-parent="#account_accordion" href="#account_collapse"><i class="glyphicon glyphicon-chevron-up"></i></a>
		      <h3 class="panel-title">账户信息</h3>
		   </div>
		   <div id="account_collapse" class="panel-collapse collapse in">
			   <div class="panel-body">
			      	<div class="row margin-0">
			      		<div class="col-sm-6 padding-0 main-padding-right-10">
			      			<span class="input-group margin-bottom-10">
								<label class="input-group-addon input-group-addon-label">企业名称：</label>
								<span class="form-control" id="company_name" style="max-height: 33px;height:auto;"></span>
								<input type="hidden" id="contractor_id">
							</span>
			      		</div>
			      		<div class="col-sm-6 padding-0 main-padding-left-10">
			      			<span class="input-group ">
								<label class="input-group-addon input-group-addon-label">电子邮箱：</label>
								<span class="form-control" id="email"></span>
							</span>
			      		</div>
			      	</div>
			   </div>
		   </div>
		  </div>
		  <div class="panel panel-user-defined panel-default" id="crop_accordion">
		   <div class="panel-heading panel-heading-user-defined">
		      <a class="margin-left-10 float-right" data-toggle="collapse" data-parent="crop_accordion" href="#crop_collapse"><i class="glyphicon glyphicon-chevron-down"></i></a>
		      <h3 class="panel-title">企业信息</h3>
		   </div>
		   <div id="crop_collapse" class="panel-collapse collapse">
			   <div class="panel-body ">
			      	<table width="100%" border="0" cellspacing="0" cellpadding="0" >
					  <tr>
					    <td align="right" class="padding-bottom-10" style="width:17%;" >组织机构登记机关：</td>
					    <td style="width: 250px;" class="padding-bottom-10"><span class="form-control" id="registration_authority" style="height: auto;min-height: 33px;" ></span></td>
					    <td align="right" class="padding-bottom-10" >营业执照登记机关：</td>
					    <td style="width: 250px;" class="padding-bottom-10"><span class="form-control" id="business_license_reg_auth" style="height: auto;min-height: 33px;"></span></td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10">营业执照注册号：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="business_license_reg_num" ></span></td>
					    <td align="right" class="padding-bottom-10">法定代表人信息：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="legal_representative" ></span></td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10">实缴注册资本(万元)：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="paid_registered_capital"></span></td>
					    <td align="right" class="padding-bottom-10">企业类型：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="enterprise_type" ></span></td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10 padding-left-5">注册资本(万元)：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="registered_capital" ></span></td>
					    <td align="right" class="padding-bottom-10">经营期限：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="operating_period" ></span></td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10">成立日期：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="establishment_date" ></span></td>
					    <td align="right" class="padding-bottom-10">联系人手机：</td>
					    <td class="padding-bottom-10"><span class="form-control" id="contact_phone" ></span></td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10">地址：</td>
					   <td class="padding-bottom-10" colspan="3"><span class="form-control" id="domicile"  style="height: auto;min-height: 33px;"></span></td>
					  </tr>
					  <tr>
					    <td class="padding-bottom-10" align="right">经营范围：</td>
					    <td colspan="3" class="padding-bottom-10">
						<div id="business_scope" class="form-control margin-bottom-10" style="min-height: 100px;height: auto"></div>
					    </td>
					  </tr>
					  <tr>
					    <td align="right" class="padding-bottom-10">专业分类：</td>
					   <td class="padding-bottom-10" colspan="3">
					    <div id="professional_list"  class="form-control" style="height: auto;min-height:33px;"></div>
					   </td>
					  </tr>
					</table>
		      	</div>
			   </div>
		   </div>
		   <div class="panel panel-user-defined panel-default" id="accessory_accordion">
		   <div class="panel-heading panel-heading-user-defined">
		      <a class="margin-left-10 float-right" data-toggle="collapse" data-parent="#accessory_accordion" href="#accessory_collapse"><i class="glyphicon glyphicon-chevron-down"></i></a>
		      <h3 class="panel-title">其他资料</h3>
		   </div>
		   <div id="accessory_collapse" class="panel-collapse collapse">
			   <div class="panel-body " >
			      	<div class="row margin-0">
			      		<div class="col-sm-6 padding-0 " >
			      		<div class="thumbnail text-center margin-bottom-0 main-margin-bottom-10"  style="margin-left: -10px;margin-right: 3px;">
			      			<img id="business_license_pic" class="pack-data" style="height:150px;max-height:150px;cursor: pointer;"   src=""/>
							<div class="caption">
					        		<h4>营业执照</h4>
					        		<!--  
					        		<a   href="">
					        			<button type="button" id="business_license_gallery" class="btn btn-primary btn-sm" style="margin-top:-20px;"><i class="glyphicon glyphicon-save-file"></i> 查看</button>
					        		</a>
					        		-->
				      			</div>
				      	</div>
			      		</div>
			      		<div class="col-sm-6 padding-0 ">
			      		<div class="thumbnail text-center margin-bottom-0 main-margin-bottom-10" style="margin-left: 3px;margin-right: -10px;" >
			      			<img id="account_code_pic" class="pack-data" style="height:150px;max-height:150px;cursor: pointer;"  src=""/>
							<div class="caption">
					        		<h4>开户许可证</h4>
					        		<!-- 
					        		<a id="account_code_gallery"  href="">
					        			<button type="button" class="btn btn-primary btn-sm" style="margin-top:-20px;"><i class="glyphicon glyphicon-save-file"></i> 查看</button>
					        		</a>
					        		-->
				      			</div>
				      	</div>
			      		</div>
			      	</div>
			      		<div class="space-4"></div>
		      	<div class="space-4"></div>
		      	<div class="row margin-0">
		      	<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">专业相关资质附件</h3>
				   	</div>
				   	<div class ="panel-body">
				      <ol id="accessory_ol">
				      </ol>
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
<!-- 模态框（Modal） -->
<div class="modal fade" id="approval_meno" tabindex="-1" role="dialog" >
   <div class="modal-dialog">
      <div class="modal-content">
         <div class="modal-header">
            <button type="button" class="close" 
               data-dismiss="modal" aria-hidden="true" >
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
            <button type="button" class="btn btn-default" data-dismiss="modal" id="close">关闭</button>
            <button type="button" class="btn btn-primary" id="approval_refusal">确定</button>
         </div>
      </div><!-- /.modal-content -->
	</div><!-- /.modal -->
</div>