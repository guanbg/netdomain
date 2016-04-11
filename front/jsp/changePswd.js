/*
 * 忘记密码
 */
jQuery(function($) {
	$('#changePswdModalDialog').on('hidden.bs.modal', function (e) {
		$('#changePswdModalDialog .modal-body input').val('');
		$('#changePswdModalDialog').parent().hide();
		submitFlag = false;
		$('#changePswd_button').prop("disabled",false);
	});
	
	var submitFlag = false;
	
	$("#changePswdModalDialog input").keyup(function(event){
	   if(!submitFlag && event.keyCode == 13){
		   $('#changePswd_button').trigger("click");
	   }
	});
	
	$('#changePswd_button').click(function(){
		if(!$("#oldpswd").val()){
		   	$("#oldpswd").val("").focus();
			return false;
	   	}
	   	if(!$("#newpswd").val()){
		   	$("#newpswd").val("").focus();
			return false;
	   	}
	   	if(!$("#newpswd2").val()){
		   	$("#newpswd2").val("").focus();
			return false;
	   	}
	   	if($("#newpswd").val() != $("#newpswd2").val()){
	   		xAlert("两次密码输入不一致，请重新输入",$("#newpswd2"));
	   		return false;
	   	}
	   	
	   	submitFlag = true;
	   	$('#changePswd_button').prop("disabled",true);
			var dt = {
				newpswd:hex_md5(hex_md5($("#newpswd").val())),
				oldpswd:hex_md5(hex_md5($("#oldpswd").val())),
				userid:getLoginUserId()
			}
			
		invockeServiceSync('sys.user.password.update.service', dt, function(data, isSucess){
			if(!isSucess){
				submitFlag = false;
			   	$('#changePswd_button').prop("disabled",false);
				return;
			}
			$('#changePswdModalDialog').modal('hide');
			xMsg("修改密码成功");
			setTimeout("location='/login';",2000);//延时2秒 
		});
	});
});