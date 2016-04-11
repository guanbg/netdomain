<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="editAccountModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title">修改邮箱信息</h4>
      </div>
      <div class="modal-body">
      	<div class="padding-lr-20" id="again_file_step">
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="editAccount_company_name" >　企业名称：</label>
				<input type="text" id="editAccount_company_name" class="form-control" readonly>
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="editAccount_email">原电子邮箱：</label>
				<input type="text" id="editAccount_email" class="form-control" placeholder="电子邮箱必须输入" readonly>
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="newAccount_email">新电子邮箱：</label>
				<input type="text" id="newAccount_email" class="form-control" placeholder="请输入您新的电子邮箱" >
			</span>
			<div class="space-4"></div>
			<div class="space-4"></div>
		</div>
		 <div class="panel panel-default hide2" id="again_second_step">
             <div class="panel-heading text-center">
                 <h4>提示</h4>
             </div>
             <div class="panel-body text-center">
				<div class="alert alert-info">
					您申请的邮箱变更信息已经发送，请登录您原有的电子邮箱，确认变更信息!
				</div>
                 <p>未收到邮件，请点击【<a href="#" id="resendemailAgain">重新发送</a>】</p>
             </div>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" id="editEmail">变更邮箱</button>
      </div>
    </div>
   
  </div>
  		
</div>
