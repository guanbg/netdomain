/*
 * 查看业务规则版本
 */
jQuery(function($) {
	var dialog = $('#ruleVersionViewDialog');
	
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.parent().hide();
	});
	
	$('[data-toggle="tooltip"]').tooltip({container:'body'});
});