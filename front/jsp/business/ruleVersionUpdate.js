/*
 * 修改业务规则版本
 */
jQuery(function($) {
	var dialog = $('#ruleVersionUpdateDialog'), busibtn = $('#update_version_button');
	
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.parent().hide();
		busibtn.prop("disabled",false);
	});
	
	busibtn.click(function(){
		if (!myValidation("#ruleVersionUpdateDialog .modal-body")) {
	        return false;
        }
		
	   	busibtn.prop("disabled",true);
	   	
		var dt = {
			version_num:$('#version_num_upd').val(),
			version_name:$('#version_name_upd').val(),
			disp_order:$('#disp_order_upd').val(),
			version_memo:$('#version_memo_upd').val(),
			param_version_id:$('#param_version_id_upd').val()
		};
		
		invockeServiceSync('busi.configrule.version.update.service', dt, function(data, isSucess){
			if(!isSucess){
				busibtn.prop("disabled",false);
				return;
			}
			dialog.modal('hide');
			xMsg("修改业务规则版本成功");
			$("#myJqGrid").trigger("reloadGrid");
		});
	});
	
	$('[data-toggle="tooltip"]').tooltip({container:'body'});
});