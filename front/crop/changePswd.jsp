<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="changePswdModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title">修改密码</h4>
      </div>
      <div class="modal-body">
      	<div class="padding-lr-20">
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="oldpswd">原有密码：</label>
				<input type="password" id="oldpswd" class="form-control userpswd" placeholder="原有密码必须输入">
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="newpswd">新设密码：</label>
				<input type="password" id="newpswd" class="form-control userpswd" placeholder="新设密码必须输入">
			</span>
			<div class="space-4"></div>
			<span class="input-group">
				<label class="input-group-addon no-padding-right input-group-addon-label" for="newpswd2">确认密码：</label>
				<input type="password" id="newpswd2" class="form-control userpswd" placeholder="确认密码必须输入">
			</span>
			<div class="space-4"></div>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" id="changePswd_button">修改</button>
      </div>
    </div>
  </div>
</div>