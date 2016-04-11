/*
 * 修改账户信息
 */
jQuery(function($) {
    	$('#editAccountModalDialog').on('hidden.bs.modal', function (e) {
		$('#editAccountModalDialog .modal-body input').val('');
		$('#editAccountModalDialog').parent().hide();
		$("#again_file_step").show();
		$("#again_second_step").hide();
		$("#editEmail").show();
		submitFlag = false;
		$('#editAccount_button').prop("disabled",false);
	});
	
	var submitFlag = false;
	
	$("#editAccountModalDialog input").keyup(function(event){
	   if(!submitFlag && event.keyCode == 13){
		   $('#editAccount_button').trigger("click");
	   }
	});
	//变更邮箱
	$("#editEmail").click(function(data){
		if(!$("#editAccount_email").val()){
		   	$("#editAccount_email").val("").focus();
			return false;
	   	}
		if(!$("#newAccount_email").val()){
		   	$("#newAccount_email").val("").focus();
			return false;
	   	}
		var telReg = !!$("#newAccount_email").val().match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
		if(telReg==false){
			xAlert("邮箱格式不正确，请重新输入");
			return;
		}
		if($("#newAccount_email").val()==$("#editAccount_email").val()){
			xAlert("邮箱已绑定，请重新输入您的新邮箱");
			return;
		}
		var dt = {
				tmplname:'email.userchangemail.tmpl',
				title:'修改邮箱地址说明',
				email:$("#editAccount_email").val(),
				email_new:$("#newAccount_email").val(),
				company_name:$("#editAccount_company_name").val(),
				contractor_id:getContractorId(),
				url:'/crop/cropEmailUpdate.jsp'
			}
		invockeService("crop.useremail.find.service",{contractor_id:dt['contractor_id'],email:dt['email_new']},function(data,isSucess){
			if(!isSucess){
				return;
			}
			$("#again_file_step").hide();
			$("#again_second_step").show();
			$("#editEmail").hide();
			invockeServiceSync('com.platform.cubism.mail.SendMail.class', dt, function(data, isSucess){
				if(!isSucess){
					return;
				}
				xMsg("发送成功，请查收");
				setTimeout("location='/';",10000);//延时10秒 
			});
		});
	});
	//重新发送
	$('#resendemailAgain').click(function(){
		var dt = {
				tmplname:'email.userchangemail.tmpl',
				title:'修改邮件',
				email:$("#editAccount_email").val(),
				email_new:$("#newAccount_email").val(),
				company_name:$("#editAccount_company_name").val(),
				contractor_id:getContractorId(),
				url:'/crop/cropEmailUpdate.jsp'
			}
		if(!dt['contractor_id']){
			xAlert("企业信息不正确，不能重新发送邮件");
			return;
		}
		invockeServiceSync('com.platform.cubism.mail.SendMail.class', dt, function(data, isSucess){
			if(!isSucess){
				return;
			}
			xMsg("重新发送成功，请查收");
			setTimeout("location='/';",10000);//延时10秒 
		});
	});
	
	$('#editAccount_button').click(function(){
		if(!$("#editAccount_email").val()){
		   	$("#editAccount_email").val("").focus();
			return false;
	   	}
	   	
	   	submitFlag = true;
	   	$('#editAccount_button').prop("disabled",true);
		var dt = {
			email:$("#editAccount_email").val(),
			company_name:$("#editAccount_company_name").val(),
			contractor_id:getContractorId()
		}
		
		invockeServiceSync('crop.info.account.update.service', dt, function(data, isSucess){
			if(!isSucess){
				submitFlag = false;
			   	$('#editAccount_button').prop("disabled",false);
				return;
			}
		
			$('#editAccountModalDialog').modal('hide');
			$('#company_name').text($('#editAccount_company_name').val());
			$('#email').text($('#editAccount_email').val());
			xMsg("修改账户信息成功");
		});
	});
});