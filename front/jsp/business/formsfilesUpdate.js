/*
 * 修改案卷表格
 */
jQuery(function($) {
	var dialog = $('#updateFormsfilesDialog'), busibtn = $('#updateFormsfilesButton'), addnew = $('#addNewFormsfilesButton');
	
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.parent().hide();
		busibtn.prop("disabled",false);
		addnew.prop("disabled",false);
	});
	
	tmpl_file_upload = new singleFileUpload(function(data){
		$('#tmpl_file_id').val("");
		$('#tmpl_file_name').text("");
		if(!data || !data.files){
			return false;
		}
		
		var file = data.files[0];
		$('#tmpl_file_id').val(file.file_id);
		$('#tmpl_file_name').text(file.file_name);
		xMsg("模板文件上传成功！");
	});
	
	example_file_upload = new singleFileUpload(function(data){
		$('#example_file_id').val("");
		$('#example_file_name').text("");
		if(!data || !data.files){
			return false;
		}
		
		var file = data.files[0];
		$('#example_file_id').val(file.file_id);
		$('#example_file_name').text(file.file_name);
		xMsg("样板文件上传成功！");
	});
	
	busibtn.click(function(){
		if (!myValidation("#updateFormsfilesDialog .modal-body")) {
	        return false;
        }
	   	
	   	busibtn.prop("disabled",true);
	   	
		var dt = packData("#updateFormsfilesDialog .modal-body");
		invockeServiceSync('busi.formsfiles.update.service', dt, function(data, isSucess){
			if(!isSucess){
				busibtn.prop("disabled",false);
				return;
			}
			dialog.modal('hide');
			xMsg("修改案卷表格成功");
		});
	});
	
	addnew.click(function(){
		if (!myValidation("#updateFormsfilesDialog .modal-body")) {
	        return false;
        }
	   	
		addnew.prop("disabled",true);
	   	
		var dt = packData("#updateFormsfilesDialog .modal-body");
		invockeServiceSync('busi.formsfiles.version.add.service', dt, function(data, isSucess){
			if(!isSucess){
				addnew.prop("disabled",false);
				return;
			}
			dialog.modal('hide');
			xMsg("新版创建成功");
		});
	});
});