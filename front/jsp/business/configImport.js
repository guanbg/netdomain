/*
 * 新增业务参数
 */
jQuery(function($) {
	var changed = false, dialog = $('#configImportDialog'), busibtn = $('#configImportButton');
	
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.parent().hide();
		busibtn.prop("disabled",false);
		if(changed){
			changed = false;
			$("#myTreeGrid").jqxTreeGrid('clear');
			$("#myTreeGrid").jqxTreeGrid('updateBoundData');
		}
	});
	
	busibtn.click(function(){
		if (!myValidation("#configImportDialog .modal-body")) {
	        return false;
        }
	   	
	   	busibtn.prop("disabled",true);
		
		if (!$("#import_file_id").val()) {
			xAlert("请上传文件");
	        return false;
        }
		showLoading(180);//180秒后关闭
		var dt = {
			fileid:$("#import_file_id").val(),
			parentid:$("#parentid_import").val() || '',
			servicename:"busi.config.import"
		};
		invockeService('com.platform.cubism.excel.ImportExcel.class', dt, function(data, isSucess){
			if(!isSucess || !data.batch_number){
				showLoading(0);
				//dialog.trigger('click');
				return;
			}
			xMsg('正在导入业务参数信息...',6);
			import_file_upload.reset();
			
			var total = 0, batchNumber = data.batch_number;
			getImportStatus = function(){
				total++;
				
				invockeService("sys.import.status.get.service",{batch_number:batchNumber},function(data, isSucess){
					if(!isSucess){
						showLoading(0);
						xAlert("无法查询导入状态，请手动查询导入信息是否成功。");
						return;
					}
					if(!!data && !!data.status && !!data.status.state_code){
						if(data.status.state_code == '00000'){
							showLoading(0);
							xMsg('业务参数信息导入成功！！！');
							changed = true;
							dialog.modal('hide');
							return;
						}
						else{
							xMsg(data.status.state_code+" "+data.status.state_desc);
							return;
						}
					}
					else{
						xMsg("正在导入业务参数信息，请稍后...",6);
						if(total > 10){
							showLoading(0);
							xAlert("查询不到导入状态，请手动查询导入信息是否成功。");
							return;
						}
					}
					
					setTimeout("getImportStatus()", 1000*6);//6秒
				});
			}
			setTimeout(getImportStatus,1000*6);//每6秒查询一次导入状态
		});
	});
});