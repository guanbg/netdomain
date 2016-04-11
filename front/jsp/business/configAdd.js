/*
 * 新增业务参数
 */
jQuery(function($) {
	var changed = false, dialog = $('#configAddDialog'), busibtn = $('#configAddButton');
	
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.parent().hide();
		busibtn.prop("disabled",false);
	});
	
	$('#configAddCloseButton').click(function(){
		dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.modal('hide');
		dialog.parent().hide();
		busibtn.prop("disabled",false);
		if(changed){
			ischanged = false;
			$("#myTreeGrid").jqxTreeGrid('clear');
			$("#myTreeGrid").jqxTreeGrid('updateBoundData');
		}
	});
	busibtn.click(function(){
		if (!myValidation("#configAddDialog .modal-body")) {
	        return false;
        }
	   	
	   	busibtn.prop("disabled",true);
	   	
		var dt = packData("#configAddDialog .modal-body",function(id,v){var k=id.replace(/(.*)_add$/img,'$1'),r={};r[k]=v;return r;});
		invockeServiceSync('busi.config.add.service', dt, function(data, isSucess){
			busibtn.prop("disabled",false);
			if(!isSucess){
				return;
			}
			xMsg("新增业务参数成功");
			changed = true;
		});
	});
});