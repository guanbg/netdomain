<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="cropRegModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title">参建单位注册</h4>
      </div>
      <div class="modal-body cropRegModalDialog-margin">
      	<div id="first_step">
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="company_name" >　　　参建单位：</label>
				<input type="text" id="company_name" class="form-control" placeholder="参建单位名称必须输入" maxlength="32"/>
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="business_license_reg_num">营业执照注册号：</label>
				<input type="text" id="business_license_reg_num" class="form-control" placeholder="营业执照注册号必须输入(用于登录)" maxlength="20" />
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="login_password">　　　登录密码：</label>
				<input type="password" id="login_password" class="form-control" placeholder="登录密码必须输入" />
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="login_password2">　　　确认密码：</label>
				<input type="password" id="login_password2" class="form-control" placeholder="确认密码必须输入"/>
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="email">　　　电子邮箱：</label>
				<input type="text" id="email" class="form-control" placeholder="电子邮箱必须输入"/>
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="validatecode">　　　　验证码：</label>
				<input type="text" id="validatecode" class="form-control" placeholder="验证码必须输入"/>
				<span class="input-group-addon"><img id="validatecodeimg" src="" class="margin-top-6-" style="width:100px;cursor:pointer"></span>
			</span>
			<div class="space-4"></div>
      	</div>
      	
      	<div class="panel panel-default hide2" id="second_step">
             <div class="panel-heading text-center">
                 <h4>注册完毕</h4>
             </div>
             <div class="panel-body text-center">
				<div class="alert alert-info">
					您申请的账号信息已注册成功，请登录您预留的电子邮箱，激活验证账号！
				</div>
                 <p>未收到激活邮件，请点击【<a href="#" id="resendemail">重新发送</a>】</p>
             </div>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" id="cropReg_button">注册</button>
      </div>
    </div>
  </div>
</div>