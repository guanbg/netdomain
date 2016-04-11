/*
 * 新增业务参数
 */
jQuery(function($) {
	var ischanged = false, dialog = $('#configVersionDialog'), busibtn = $('#configVersionButton');
	
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body input[id], .modal-body textarea, .modal-body select').val('');
		dialog.parent().hide();
		busibtn.prop("disabled",false);
	});
	
	$('#configVersionCloseButton').click(function(){
		dialog.modal('hide');
		if(ischanged){
			ischanged = false;
			$("#myTreeGrid").jqxTreeGrid('clear');
			$("#myTreeGrid").jqxTreeGrid('updateBoundData');
		}
	});
	
	busibtn.click(function(){
		if (!myValidation("#configVersionDialog .modal-body")) {
	        return false;
        }
	   	if(!$('#base_version').val() && $('#base_version option').length){
	   		xAlert("请选择基础版本",$('#base_version'));
	   		return;
	   	}
	   	busibtn.prop("disabled",true);
	   	
		var dt = packData("#configVersionDialog .modal-body");
		invockeServiceSync('busi.config.version.add.service', dt, function(data, isSucess){
			if(!isSucess){
				busibtn.prop("disabled",false);
				return;
			}
			xMsg("创建版本成功");
			ischanged = true;
		});
	});
});