/*
 * 新增案卷表格
 */
jQuery(function($) {
	var dialog = $('#viewFormsfilesDialog');
	
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body .form-control').text('');
		$('#tmpl_file_name').attr('href','#');
		$('#example_file_name').attr('href','#');
		dialog.parent().hide();
	});
});