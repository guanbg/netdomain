<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="findPswdModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title">忘记密码</h4>
      </div>
      <div class="modal-body cropRegModalDialog-margin">
      	<div id="first_step_findPswd">
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="login_name">登录账号：</label>
				<input type="text" id="login_name_findPsw" class="form-control" placeholder="登录账号必须输入" maxlength="23"/>
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="email">电子邮箱：</label>
				<input type="text" id="email_findPsw" class="form-control" placeholder="电子邮箱必须输入"/>
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="validatecode">　验证码：</label>
				<input type="text" id="validatecode_findPsw" class="form-control" placeholder="验证码必须输入"/>
				<span class="input-group-addon"><img id="validatecodeimg_findPsw" src="" class="margin-top-6-" style="width:100px;cursor:pointer"></span>
			</span>
			<div class="space-4"></div>
      	</div>
      	
      	<div class="panel panel-default hide2" id="second_step_findPswd">
             <div class="panel-heading text-center">
                 <h4>找回密码成功</h4>
             </div>
             <div class="panel-body text-center">
				<div class="alert alert-info">
					最新密码信息已发送至您预留的邮箱，请您登录<span id="email_findPswd"></span>邮箱进行查阅！
				</div>
                 <p>未收到忘记密码邮件，请点击【<a href="#" id="resendemail_findPswd">重新发送</a>】</p>
             </div>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" id="findPswd_button">获取密码</button>
      </div>
    </div>
  </div>
</div>