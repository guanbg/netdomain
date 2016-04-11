/**
 * 
 */
jQuery(function ($){
	var userId=getLoginUserId();//审核人ID
	invockeService('sys.login.user.get.service',{id:userId}, function(data, isSucess){
		if(!isSucess){
			return;
		}
		$("#datetime").text(jqGridFormatDate(data.syshead['datetime']));
		$("#ip").text(data.syshead['ip']);
		$("#ie").text(data.syshead['useragent']);
	});
	
	invockeService('com.platform.cubism.ConfigService.class',{name:'sys.config'}, function(data, isSucess){
		if(!isSucess){
			return;
		}
		$("#version").text(data['sys_config_version']);
		$("#name").text(data['sys_config_systemname']);
		$("#href").html("<a href='"+data['sys_config_sysabout']+"' target='_blank'>"+data['sys_config_sysabout']+"</a>");
	});
	
	invockeService('crop.approval.query.service',{},function(data, isSucess){
		if(!isSucess){
			return;
		}
		for(var i=0;i<data.rows.length;i++){
			data.rows[i]['submit_approval_date'] = jqGridFormatDate(data.rows[i]['submit_approval_date']);
		}
		var rows = data.rows, body = $('#crop_table tbody'), tmpl =
			"<tr>"+
				"<td class='noborder'>{xuhao}</td>"+
				"<td class='noborder'><a href='company/companyAudit.jsp'><font color='blue'>{company_name}<font></a></td>"+
				"<td class='noborder'>{business_license_reg_num}</td>"+
				"<td class='noborder'>{email}</td>"+
				"<td class='noborder'>{enterprise_type}</td>"+
				"<td class='noborder'>{legal_representative}</td>"+
				"<td class='noborder'>{submit_approval_date}</td>"+
			"</tr>";
		$(rows).each(function(){
			body.append(parserTemplate(tmpl,this));
		});
	});
		
		invockeService('crop.doucmentorApproval.query.service',{},function(data, isSucess){
			if(!isSucess){
				return;
			}
			for(var i=0;i<data.rows.length;i++){
				data.rows[i]['submit_documentor_audit'] = jqGridFormatDate(data.rows[i]['submit_documentor_audit']);
			}
			var rows = data.rows, body = $('#documentor_table tbody'), tmpl =
				"<tr>"+
					"<td class='noborder'>{xuhao}</td>"+
					"<td class='noborder'><a href='documentor/documentorAudit.jsp'><font color='blue'>{documentor_name}</font></a></td>"+
					"<td class='noborder'>{contract_name}</td>"+
					"<td class='noborder'>{company_name}</td>"+
					"<td class='noborder'>{documentor_post}</td>"+
					"<td class='noborder'>{tenders_code}</td>"+
					"<td class='noborder'>{documentor_cardid}</td>"+
					"<td class='noborder'>{submit_documentor_audit}</td>"+
				"</tr>";
			$(rows).each(function(){
				body.append(parserTemplate(tmpl,this));
			});
		});
		
		invockeService('sys.desktoplogs.query.service',{},function(data, isSucess){
			if(!isSucess){
				return;
			}
			for(var i=0;i<data.rows.length;i++){
				data.rows[i]['servicetime'] = jqGridFormatDate(data.rows[i]['servicetime']);
			}
			var rows = data.rows, body = $('#logs_table tbody'),tmpl =
				"<tr>"+
					"<td class='noborder'>{xuhao}</td>"+
					"<td class='noborder'>{loginname}</td>"+
					"<td class='noborder'>{servicedesc}</td>"+
					"<td class='noborder'>{servicetime}</td>"+
					"<td class='noborder'>{ip}</td>"+
					"<td class='noborder'>{msgdesc}</td>"+
				"</tr>";
			$(rows).each(function(){
				body.append(parserTemplate(tmpl,this));
			});
		});
		
});