/*
 * 忘记密码
 */
jQuery(function($) {
	$('#validatecodeimg_findPsw').click(function(){
		$("#validatecode_findPsw").val('');
		$('#validatecodeimg_findPsw').attr('src','sys.validatecode.do?'+(new Date().getTime()));
	});
	
	$('#validatecodeimg_findPsw').trigger("click");
	
	$('#findPswdModalDialog').on('hidden.bs.modal', function (e) {
		$('#findPswdModalDialog .modal-body input').val('');
		$("#findPswd_button").show();
		$("#first_step_findPswd").show();
    	$("#second_step_findPswd").hide();
		$('#findPswdModalDialog').parent().hide();
		submitFlag = false;
		$('#findPswd_button').prop("disabled", false);
	});
	
	var submitFlag = false;
	
	$("#first_step_findPswd input").keydown(function(event){
	   if(!submitFlag && event.keyCode == 13){
		   $('#findPswd_button').trigger("click");
	   }
	});
	
	$('#findPswd_button').click(function(){
		if(!$("#login_name_findPsw").val()){
			//alert("请输入建设单位名称");
		   	$("#login_name_findPsw").val("").focus();
			return false;
	   	}
	   	if(!$("#email_findPsw").val()){
	   		//alert("请输入电子邮件");
		   	$("#email_findPsw").val("").focus();
			return false;
	   	}
	   	var telReg = !!$("#email_findPsw").val().match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
		if(telReg==false){
			alert("请输入正确的邮箱");
			return;
		}	
	   	if(!$("#validatecode_findPsw").val()){
	   		//alert("请输入验证码");
		   	$("#validatecode_findPsw").val("").focus();
			return false;
	   	}
	   	
	   	submitFlag = true;
	   	$('#findPswd_button').prop("disabled",true);
	   	
		var dt = {
			login_name:$('#login_name_findPsw').val(),
			email:$('#email_findPsw').val(),
			validatecode:$('#validatecode_findPsw').val()
		}
		
		$.ajax({
	    	type : "post", 
	        url : "FindPswd?"+(new Date().getTime()), 
	        data : JSON.stringify(dt),
	        async : false, 
	        success : function(data, textStatus, jqXHR){
	        	var ret = data.retHead || data.rethead || data;
	        	
	            if(ret.status == "S"){
	            	$("#findPswd_button").hide();
	            	$("#first_step_findPswd").hide();
	            	$("#second_step_findPswd").show();
	            	$("#email_findPswd").text(dt['email']);
	            	$("#resendemail_findPswd").data("user_id",data['user_id']||'');
	        	}
	        	else{
		        	var msg = ret.msgarr || ret.msgArr;
		        	//bootboxAlert('提示信息', msg[0].code+" "+msg[0].desc, msg[0].level);
		            alert(msg[0].code+" "+msg[0].desc);
		            $('#validatecodeimg_findPsw').trigger("click");
		            submitFlag = false;
		            $('#findPswd_button').prop("disabled", false);
	        	}
	        },
	        error:function(data){
	        	alert('系统错误！\n'+data.responseText);
	        	$('#validatecodeimg_findPsw').trigger("click");
	        	submitFlag = false;
	            $('#findPswd_button').prop("disabled", false);
	        }
		});
	});
	
	$('#resendemail_findPswd').click(function(){
		var user_id = $(this).data('user_id');
		if(!user_id){
			alert("注册信息不正确，不能重新发送邮件");
			return;
		}
		$.ajax({
	    	type : "get", 
	        url : "FindPswd?"+(new Date().getTime()), 
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