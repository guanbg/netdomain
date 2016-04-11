/**
 * 企业详细及审核
 */
jQuery(function($) {
	$("#docu_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#docu_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	$("#accessory_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#accessory_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	$("#documentor_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#documentor_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
    var dialog = $('#viewdocumentorParticularDialog');
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body .form-control').text('');
		dialog.parent().hide();
	});
	//审核通过
	$("#approval_pass").click(function(){
		xConfirm("你确定要审核通过吗？",function(flag){
	    	if(!flag){
	    		return;
	    	}
	    	
	    	var name;
			var approval_user=$("#username").val();
			invockeServiceSync('sys.user.getuser.service',{id:approval_user},function(data,isSuccess){
				if(!isSuccess){
					return;
				}
				name=data.user['username'];
			});
			var dt ={
					approval_user:approval_user,
					documentor_id:$("#documentor_id").val(),
					approval_meno:"审核通过",
					approval_status:2,
					record_name:name,
					object_name:$("#documentor_name").text(),
					contractor_id:$("#contractor_id").val(),
					datebase_status:1,
					status_name:"审核通过"
			}
			invockeServiceSync('crop.documentor.pass.refusal.service',dt, function(data, isSucess){
				if(!isSucess){
					return;
				}
				xMsg("审核通过成功");
				$("#viewdocumentorParticularDialog").hide();
				window.location.reload();
				//window.location="/jsp/documentor/documentorAudit.jsp";
			});	
		});
	});
	//驳回
	$("#approval_refusal").click(function(){
		
		var approval_memo =$("#meno").val();
		var approval_user=$("#username").val();
		if(approval_memo=='' || approval_memo==null){
			xAlert("请填写驳回意见");
			return;
		}
		xConfirm("你确定要审核驳回吗？",function(flag){
	    	if(!flag){
	    		return;
	    	}
	    	
	    	var name;
			invockeServiceSync('sys.user.getuser.service',{id:approval_user},function(data,isSuccess){
				if(!isSuccess){
					return;
				}
				name=data.user['username'];
			});
			var dt ={
					approval_user:approval_user,
					documentor_id:$("#documentor_id").val(),
					approval_memo:approval_memo,
					record_name:name,
					approval_status:3,
					object_name:$("#documentor_name").text(),
					contractor_id:$("#contractor_id").val(),
					datebase_status:0,
					status_name:"审核驳回"
			}
			invockeServiceSync('crop.documentor.pass.refusal.service',dt, function(data, isSucess){
				if(!isSucess){
					return;
				}
				$("#viewdocumentorParticularDialog").hide();
				$("#close").click();
				xMsg("审核驳回成功");
				window.location.reload();
				//window.location="/jsp/documentor/documentorAudit.jsp";
			});	
		});
	});
});