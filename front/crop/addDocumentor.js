/*
 * 新增资料员信息
 */
jQuery(function($) {
	var dialog = $('#addDocumentorModalDialog'), addbtn = $('#addDocumentor_button');
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find(".modal-body input[id]:not(input[type='radio'])").val('');
		dialog.parent().hide();
		submitFlag = false;
		addbtn.prop("disabled",false);
	});
	var submitFlag = false;
	
	addbtn.click(function(){
		var telReg = !!$("#mobile_no_addDocumentor").val().match(/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/);
        if(telReg==false ){
        	xAlert("请输入正确的手机号码");
        	$("#mobile_no_addDocumentor").val("").focus();
 			return;
         }
		var telReg = !!$("#email_addDocumentor").val().match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
			if(telReg==false){
				xAlert("请输入正确的邮箱");
				$("#email_addDocumentor").val("").focus();
				return;
			}
		if (!myValidation("#addDocumentorModalDialog .modal-body")) {
	        return false;
        }
	   	submitFlag = true;
	   	addbtn.prop("disabled",true);
	   	var dt ={
				documentor_name:$("#documentor_name_addDocumentor").val(),
				mobile_no:$("#mobile_no_addDocumentor").val(),
				email:$("#email_addDocumentor").val(),
				tenders_code:$("#tenders_code_addDocumentor").val(),
				company_name:$("#company_name_addDocumentor").val(),
				contact_phone:$("#contact_phone_addDocumentor").val(),
				login_name:$("#business_license_reg_num").val(),
				login_password:hex_md5(hex_md5('123456178')),
				contractor_id:getContractorId()
				}
		invockeServiceSync('crop.documentor.add.service',dt, function(data, isSucess){
			if(!isSucess){
				submitFlag = false;
				addbtn.prop("disabled",false);
				return;
			}
			login_name=data.documentor;
			dialog.modal('hide');
			getDocumentorData();//重新加载数据
			var emailData = {
					tmplname:'email.docureg.tmpl',
					title:'资料员邮箱确认',
					email:dt.email,
					company_name:dt.company_name,
					contractor_id:getContractorId(),
					login_name:login_name,
					login_password:'123456178',
					url:''
				}
			invockeServiceSync('com.platform.cubism.mail.SendMail.class',emailData, function(data, isSucess){
				if(!isSucess){
					return;
				}
				xMsg("新增资料员成功，详细信息已发至所填邮箱，请查收");
			});
		});
	});
});