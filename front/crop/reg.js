/*
 * 建设单位注册
 */
jQuery(function($) {
	$('#validatecodeimg').click(function(){
		$("#validatecode").val('');
		$('#validatecodeimg').attr('src','sys.validatecode.do?'+(new Date().getTime()));
	});
	
	$('#validatecodeimg').trigger("click");
	
	$('#cropRegModalDialog').on('shown', function(e) {
	    var modal = $(this);
	    modal.css('margin-top', (modal.outerHeight() / 2) * -1)
	         .css('margin-left', (modal.outerWidth() / 2) * -1);
	    return this;
	});
	
	$('#cropRegModalDialog').on('hidden.bs.modal', function (e) {
		$('#cropRegModalDialog .modal-body input').val('');
		$("#cropReg_button").show();
		$("#first_step").show();
    	$("#second_step").hide();
		$('#cropRegModalDialog').parent().hide();
		submitFlag = false;
        $('#cropReg_button').prop("disabled", false);
	});
	
	var submitFlag = false;
	
	$("#first_step input").keydown(function(event){
	   if(!submitFlag && event.keyCode == 13){
		   $('#cropReg_button').trigger("click");
	   }
	});
	
	$('#cropReg_button').click(function(){
		if(!$("#company_name").val()){
			//alert("请输入建设单位名称");
		   	$("#company_name").val("").focus();
			return false;
	   	}
		if(!$("#business_license_reg_num").val()){
			//alert("请输入建设单位名称");
		   	$("#business_license_reg_num").val("").focus();
			return false;
	   	}
	   	if(!$("#login_password").val()){
	   		//alert("请输入登录密码");
		   	$("#login_password").val("").focus();
			return false;
	   	}
	   	if($("#login_password").val().length<6){
	   		//alert("请输入登录密码");
	   		alert("密码最少为6位");
	   		$("#login_password").val("").focus();
			return false;
	   	}
	   	if(!$("#login_password2").val()){
	   		//alert("请输入确认密码");
		   	$("#login_password2").val("").focus();
			return false;
	   	}
	   	if($("#login_password").val() != $("#login_password2").val()){
			alert("两次密码输入不一样，请重新输入");
			$("#login_password2").val("").focus();
			return false;
		}
	   	if(!$("#email").val()){
	   		//alert("请输入电子邮件");
		   	$("#email").val("").focus();
			return false;
	   	}
		var telReg = !!$("#email").val().match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
		if(telReg==false){
			alert("请输入正确的邮箱");
			$("#email").val("").focus();
			return false;
		}	
	   	if(!$("#validatecode").val()){
	   		//alert("请输入验证码");
		   	$("#validatecode").val("").focus();
			return false;
	   	}
	   	submitFlag = true;
	   	$('#cropReg_button').prop("disabled",true);
	   	
		var dt = {
			company_name:$('#company_name').val(),
			login_name:$("#business_license_reg_num").val(),
			business_license_reg_num:$("#business_license_reg_num").val(),
			login_password:hex_md5(hex_md5($("#login_password").val())),
			email:$('#email').val(),
			validatecode:$('#validatecode').val()
		}
		
		$.ajax({
	    	type : "post", 
	        url : "CropRegister?"+(new Date().getTime()), 
	        data : JSON.stringify(dt),
	        async : false, 
	        success : function(data, textStatus, jqXHR){
	        	var ret = data.retHead || data.rethead || data;
	        	
	            if(ret.status == "S"){
	            	$("#cropReg_button").hide();
	            	$("#first_step").hide();
	            	$("#second_step").show();
	            	$("#resendemail").data("user_id",data['user_id']||'');
	        	}
	        	else{
		        	var msg = ret.msgarr || ret.msgArr;
		        	//bootboxAlert('提示信息', msg[0].code+" "+msg[0].desc, msg[0].level);
		            alert(msg[0].code+" "+msg[0].desc);
		            $('#validatecodeimg').trigger("click");
		            submitFlag = false;
		            $('#cropReg_button').prop("disabled", false);
	        	}
	        },
	        error:function(data){
	        	alert('系统错误！\n'+data.responseText);
	        	$('#validatecodeimg').trigger("click");
	        	submitFlag = false;
	            $('#cropReg_button').prop("disabled", false);
	        }
		});
	});
	
	$('#resendemail').click(function(){
		var user_id = $(this).data('user_id');
		if(!user_id){
			alert("注册信息不正确，不能重新发送邮件");
			return;
		}
		$.ajax({
	    	type : "get", 
	        url : "CropRegister?"+(new Date().getTime()), 
	        data : {user_id:user_id},
	        async : false, 
	        success : function(data, textStatus, jqXHR){
	        	var ret = data.retHead || data.rethead || data;
	        	
	            if(ret.status == "S"){
	            	alert("邮件重新发送成功，请查收！");
	        	}
	        	else{
		        	var msg = ret.msgarr || ret.msgArr;
		            alert(msg[0].code+" "+msg[0].desc);
	        	}
	        },
	        error:function(data){
	        	alert('系统错误！\n'+data.responseText);
	        }
		});
	});
});