/**
 * 企业详细及审核
 */
jQuery(function($) {
	$("#account_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#account_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	$("#accessory_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#accessory_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	$("#crop_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#crop_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});

    var dialog = $('#viewCompanyParticularDialog');
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body .form-control').text('');
		dialog.parent().hide();
		$("#accessory_ol").text('');
	});
	//审核通过
	$("#approval_pass").click(function(){
		xConfirm("你确定要审核通过吗？",function(flag){
	    	if(!flag){
	    		return;
	    	}
	    	var approval_user=$("#username").val();
			var objectname=$("#company_name").text();
			var name;
			invockeServiceSync('sys.user.getuser.service',{id:approval_user},function(data,isSuccess){
				if(!isSuccess){
					return;
				}
				name=data.user['username'];
			});
			var contractorid=$("#contractor_id").val();
			var dt ={
					approval_user:approval_user,
					approval_status:2,
					contractor_id:contractorid,
					approval_memo:"审核通过",
					record_name:name,
					object_name:objectname,
					status_name:'审核通过'
			}
			invockeServiceSync('crop.approval.pass.refusal.service',dt, function(data, isSucess){
				if(!isSucess){
					return;
				}
				xMsg("审核通过成功");
				$("#viewCompanyParticularDialog").hide();
				window.location.reload();
				//window.location="/jsp/company/companyAudit.jsp";
			});	
		});
	});
	//驳回
	$("#approval_refusal").click(function(){
		var approval_user=$("#username").val();
		var contractorid=$("#contractor_id").val();
		var approval_memo=$("#meno").val();
		var objectname=$("#company_name").text();
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
					approval_status:3,
					contractor_id:contractorid,
					approval_memo:approval_memo,
					record_name:name,
					object_name:objectname,
					status_name:'审核驳回'
			}
			invockeServiceSync('crop.approval.pass.refusal.service',dt, function(data, isSucess){
				if(!isSucess){
					return;
				}
				xMsg("审核驳回成功");
				$("#viewCompanyParticularDialog").hide();
				$("#close").click();
				window.location.reload();
				//window.location="/jsp/company/companyAudit.jsp";
			});	
		});
	});
});