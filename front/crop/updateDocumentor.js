/*
 * 修改资料员信息
 */
jQuery(function($) {
		var dialog = $('#updateDocumentorModalDialog'),updatebtn = $('#updateDocumentor_button'),updateemail=$("#updateDocumentor_email");
		
		dialog.on('hidden.bs.modal', function (e) {//清除资料员信息
			dialog.find(".modal-body input[id]:not(input[type='radio'])").val('');
			dialog.parent().hide();
			submitFlag = false;
			updatebtn.prop("disabled",false);
			updateemail.prop("disabled",false);
		});
		
		dialog.on('shown.bs.modal', function (e) {//回填资料员信息
			var documentor_id = $('#documentor_table').bootstrapTable('getSelections')[0]['documentor_id'];
			invockeService('crop.documentor.get.service', {documentor_id:documentor_id}, function(data, isSucess){
				if(!isSucess){
					dialog.modal('hide');
					return;
				}
				loadDivData(data.documentor,"#updateDocumentorModalDialog .modal-body",function(n,v){return n+'_updateDocumentor'});
			});
		});
		dialog.trigger('shown.bs.modal');//初始化时触发
		
		var submitFlag = false;
		
		updatebtn.click(function(){
			if (!myValidation("#updateDocumentorModalDialog .modal-body")) {
		        return false;
	        }
			
		   	submitFlag = true;
			var dt = packData("#updateDocumentorModalDialog .modal-body",function(id,v){var k=id.replace(/(.*)_updateDocumentor$/img,'$1'),r={};r[k]=v;return r;});
			dt['documentor_id'] = $('#documentor_table').bootstrapTable('getSelections')[0]['documentor_id'];
			var telReg = !!$("#email_updateDocumentor").val().match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
			if(telReg==false){
				xAlert("请输入正确的邮箱格式");
				return;
			}	
			invockeServiceSync('crop.documentor.update.service', dt, function(data, isSucess){
				if(!isSucess){
					submitFlag = false;
					updatebtn.prop("disabled",false);
					return;
				}
				dialog.modal('hide');
				getDocumentorData();//重新加载数据
				xMsg("操作成功");
				});
			});
		
		 updateemail.click(function(){
				if (!myValidation("#updateDocumentorModalDialog .modal-body")) {
			        return false;
		        }
				
			   	submitFlag = true;
				var dt = packData("#updateDocumentorModalDialog .modal-body",function(id,v){var k=id.replace(/(.*)_updateDocumentor$/img,'$1'),r={};r[k]=v;return r;});
				dt['documentor_id'] = $('#documentor_table').bootstrapTable('getSelections')[0]['documentor_id'];
				var telReg = !!$("#email_updateDocumentor").val().match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
				if(telReg==false){
					xAlert("请输入正确的邮箱格式");
					return;
				}	
				invockeServiceSync('crop.documentor.update.service', dt, function(data, isSucess){
					if(!isSucess){
						submitFlag = false;
						updatebtn.prop("disabled",false);
						return;
					}
					login_name=data.documentor;
					dialog.modal('hide');
					getDocumentorData();//重新加载数据
					var emailData = {
							tmplname:'email.docureg.tmpl',
							title:'资料员变更确认',
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
						xMsg("重新发送成功，请查收");
					});
				});
				
			});
		 $('[data-toggle="tooltip"]').tooltip({container:'body'});
});
		
