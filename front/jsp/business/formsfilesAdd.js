/*
 * 新增案卷表格
 */
jQuery(function($) {
	var dialog = $('#addFormsfilesDialog'), busibtn = $('#addFormsfilesButton');
	
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.parent().hide();
		busibtn.prop("disabled",false);
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
		if (!myValidation("#addFormsfilesDialog .modal-body")) {
	        return false;
        }
	   	
	   	busibtn.prop("disabled",true);
	   	
		var dt = packData("#addFormsfilesDialog .modal-body",function(id,v){var k=id.replace(/(.*)_add$/img,'$1'),r={};r[k]=v;return r;});
		invockeServiceSync('busi.formsfiles.add.service', dt, function(data, isSucess){
			if(!isSucess){
				busibtn.prop("disabled",false);
				return;
			}
			dialog.modal('hide');
			xMsg("新增案卷表格成功");
			$("#myJqGrid").trigger("reloadGrid");
		});
	});
});