/*
 * 新增业务参数
 */
jQuery(function($) {
	var ischanged = false, dialog = $('#configUpdateDialog'), busibtn = $('#configUpdateButton');
	
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.parent().hide();
		busibtn.prop("disabled",false);
	});
	
	$('#configCloseButton').click(function(){
		dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.modal('hide');
		dialog.parent().hide();
		busibtn.prop("disabled",false);
		if(ischanged){
			ischanged = false;
			$("#myTreeGrid").jqxTreeGrid('clear');
			$("#myTreeGrid").jqxTreeGrid('updateBoundData');
		}
	});
	
	busibtn.click(function(){
		if (!myValidation("#configUpdateDialog .modal-body")) {
	        return false;
        }
	   	
	   	busibtn.prop("disabled",true);
	   	
		var dt = packData("#configUpdateDialog .modal-body",function(id,v){var k=id.replace(/(.*)_update$/img,'$1'),r={};r[k]=v;return r;});
		invockeServiceSync('busi.config.update.service', dt, function(data, isSucess){
			busibtn.prop("disabled",false);
			if(!isSucess){
				return;
			}
			xMsg("修改业务参数成功");
			ischanged = true;
		});
	});
});