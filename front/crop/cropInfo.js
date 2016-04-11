/*
 * 企业信息
 */
jQuery(function($) {
	$("#account_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#account_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	$("#crop_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#crop_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	$("#employee_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#employee_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	$("#documentor_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#documentor_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});

	$('#business_license_pic').click(function(){
		var flag = $(this).data('fileid');
		if(flag && flag.length > 0){
			$('#busfileView').trigger('click');
		}
		else{
			$('#busfileUpload').trigger('click');
		}
	});
	$('#account_code_pic').click(function(){
		var flag = $(this).data('fileid');
		if(flag && flag.length > 0){
			$('#accfileView').trigger('click');
		}
		else{
			$('#accfileUpload').trigger('click');
		}
	});
	
	$('#btnEditAccount').click(function(){//修改账户信息
		if($('#editAccountModalDialogContainer').html()){
			$('#editAccountModalDialogContainer').show();
			$('#editAccountModalDialog').modal({backdrop:false,show:true});
			$('#editAccount_company_name').val($('#company_name').val());
			$('#editAccount_email').val($('#email').val());
		}
		else{
			var timestamp = (new Date()).valueOf();
			$.ajaxSetup({cache: false});//关闭AJAX相应的缓存
			$("#editAccountModalDialogContainer").load("crop/cropInfoAccount.jsp?"+timestamp, null, function(){
				$.getScript('crop/cropInfoAccount.js');

				$('#editAccountModalDialogContainer').show();
				$('#editAccountModalDialog').modal({backdrop:false,show:true});
				
				$('#editAccount_company_name').val($('#company_name').val());
				$('#editAccount_email').val($('#email').val());
			});
		}
	});
	
	//editables 
	$.fn.editable.defaults.mode = 'inline';
	$.fn.editableform.loading = "<span class='editableform-loading'><i class='fa fa-spinner fa-spin fa-2x light-blue'></i></span>";
    $.fn.editableform.buttons = '<button type="submit" class="btn btn-info editable-submit"><i class="glyphicon glyphicon-ok"></i></button><button type="button" class="btn editable-cancel"><i class="glyphicon glyphicon-remove"></i></button>';    
    var opt = {
		placement: 'right', 
        send:'always',
        emptytext: '', // default is 'Empty'
        pk:getContractorId(),
        url:'crop.info.base.update.service',
        ajaxOptions: {
        	dataType: 'json',//预期服务器返回的数据类型
        	type: 'post'	//请求方式 ("POST" 或 "GET")， 默认为 "GET"
    	},
    	params:function(param){
    		if($.isArray(param['value'])){
    			param['value'] = param['value'].join(',');
    		}
    		if(param['name']=='professional_list'){
      		   param['value2']=$('#professional_list').html().split("<br>").join(",");
      	    }
    		return param;
    	},
    	success: function(data, newValue) {//请求成功后回调函数
    		var ret = data.rethead || data.retHead || data;
    		if(ret && ret.status == "S"){
            	//xMsg('操作成功', s, msg[0].level || 'show');
    			//return {newValue: newValue};
    			$(this).editable('show');
            }
            else{
	        	var msg = ret.msgarr || ret.msgArr,
	            s='';
	        	for(var i in msg){
	        		s += msg[i].code+" "+msg[i].desc+"\n";//+"<br/>";
	        	}
	        	xAlert(s || data,'','D');
	        	return s || data;
            }
    	},
        validate: function(value) {
        	//组织机构
            if($(this).attr('id') == 'registration_authority'  ) {
                if($.trim(value).length>40){
                	return '字数限制,请重输';
                }
            }
            //营业执照登记机关
            if($(this).attr('id') == 'business_license_reg_auth'  ) {
                if($.trim(value).length>40){
                	return '字数限制,请重输';
                }
            }
            //法定代表人
            if($(this).attr('id') == 'legal_representative'  ) {
            	if($.trim(value)===null || $.trim(value)===''){
                	return '法定代表人必填';
                }
                if($.trim(value).length>7){
                	return '字数限制,请重输';
                }
            }
            //实缴注册资本
            if($(this).attr('id') == 'paid_registered_capital'  ) {
            	if(isNaN($.trim(value))){
                	return '数值类型';
                }
                if($.trim(value).length>6){
                	return '字数限制,请重输';
                }
            }
            //注册资本
            if($(this).attr('id') == 'registered_capital'  ) {
            	if(isNaN($.trim(value))){
                	return '数值类型';
                }
                if($.trim(value).length>6){
                	return '字数限制,请重输';
                }
            }
            //经营期限
            if($(this).attr('id') == 'operating_period'  ) {
                if($.trim(value).length>4){
                	return '字数限制,请重输';
                }
            }
          //成立日期校验
            if($(this).attr('id') == 'establishment_date'  ) {
            	var date =Date.parse($.trim(value))
            	var d = new Date();
            	var datenew = Date.parse(d);
            	if(date>datenew){
            		return '请输入正确的时间';
            	}
            }
            //手机
            if($(this).attr('id') == 'contact_phone'  ) {
            	if($.trim(value)===null || $.trim(value)===''){
                	return '联系人手机必填';
                }
                var telReg = !!$.trim(value).match(/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/);
                if(telReg==false ){
                	return '请输入正确的手机号码';
                }
            }
            //住所
            if($(this).attr('id') == 'domicile'  ) {
                if($.trim(value).length>100){
                	return '字数限制,请重输';
                }
            }
            //企业类型
            if($(this).attr('id') == 'enterprise_type'  ) {
            	if($.trim(value)===null || $.trim(value)===''){
                	return '企业类型必选';
                }
            }
            //专业分类
            if($(this).attr('id') == 'professional_list'  ) {
            	if($.trim(value)===null || $.trim(value)===''){
                	return '专业分类必选';
                }
            }
          //经营范围
            if($(this).attr('id') == 'business_scope'  ) {
                if($.trim(value).length>500){
                	return '字数限制,请重输';
                }
            }
        }
    };
	$('#btnEditCropInfo').click(function(){//修改企业信息
		
		if($('#crop_collapse .editable').length <= 0){
		    $('#establishment_date').editable($.extend({
		        format: 'yyyy-mm-dd',    
		        viewformat: 'yyyy-mm-dd',
		        datepicker: {
		        	weekStart: 1,
		        	language:'zh-CN'
		        }
		    }, opt));
		    setEditableOptions({svrName:"pklist.sys_data_dictionary.service",svrData:{datavalue:'enterprise_type'}},function(data,isSuccess){
		    	$('#enterprise_type').editable($.extend({source: data}, opt));
		    });
		    setEditableOptions({svrName:"pklist.sys_data_dictionary.service",svrData:{datavalue:'professional_list'}},function(data,isSuccess){
		    	$('#professional_list').editable($.extend({source:data}, opt));
		    });
			$("#crop_collapse [data-type='text'], #crop_collapse [data-type='textarea']").each(function(){
		    	$(this).editable($.extend({rows: 4}, opt));
		    });
			
		} 
		else{
			$('#crop_collapse .editable').editable('toggleDisabled');
		}
	});
	
	

	//信息初始化
	//附件信息初始化
	var accessory =function(){
		var contractorId=getContractorId();
		invockeService('crop.registeuser.get.service', {contractor_id:contractorId}, function(data, isSucess){
			if(!isSucess){
				return;
			}
			var operating_period =data.crop['operating_period'];
			if(operating_period==='长期' || operating_period===''){
				$("#operating_period").text("长期");
			}
		    //初始化专业分类
		    var professional_list = data.crop['professional_list_name'].split(",").join("<br>");
		    if(professional_list!=null && professional_list!=''){
		    	$("#professional_list").text("");
		    	$("#professional_list").html(professional_list);
		    }
		    //判断审核状态
		    var cropStatus = data.crop['approval_status'];
			if(cropStatus==1 || cropStatus==2){
				$("#crop_collapse .file-upload").css('visibility','hidden');
				$("#btnEditAccount").hide();
				$("#btnEditCropInfo").hide();
				$("#contractor_submit_audit").hide();
			}
			if(cropStatus==2){
				$("#update_info").show();
			}
			//判断是否有附件
			if(data.crop['contractor_accessory']===null || data.crop['contractor_accessory']===''){
				return;
			}
			var acc=data.crop['contractor_accessory'],accname=data.crop['contractor_accessory_filename'];
			var accessory = acc.split(","), accessoryname = accname.split(",");
			var ol =$("#accessory_ol");//获取ol
			for(var i=0;i<accessory.length;i++){
				fileId=accessory[i];
				var name = accessoryname[i].split(".");//拆分图片名称
				var nameValue = name[1].toUpperCase();
				if(nameValue==='PNG' || nameValue==='JPG' || nameValue ==="BMP" || nameValue ==="JPEG"){
					ol.append("<li data-fileid='"+fileId+"'><a  id="+fileId+" href='netdomain.sys_diskfile_download?downtype=image&fileid="+fileId+"'>"+accessoryname[i]+"</a><i class='glyphicon glyphicon-remove hand' style='margin-left:15px;'></i></li>");
					$("#"+fileId+"").lightBox();
				}else{
				    ol.append("<li data-fileid='"+fileId+"'><a target='_blank' href='netdomain.sys_diskfile_download?fileid="+fileId+"'>"+accessoryname[i]+"</a><i class='glyphicon glyphicon-remove hand' style='margin-left:15px;'></i></li>");
				}
			}
			if(cropStatus==1 || cropStatus==2){
				$("#accessory_ol i.glyphicon-remove").css('visibility','hidden');
			}
				ol.find('i').unbind();//取消绑定(确定无重复数值)
				ol.find('i').click(function(){
					var accessory = [], fid, fileid = $(this).parent().data('fileid'),
					li = $(this).parent();
					
					ol.find("a").each(function(){
						fid = $(this).parent().data('fileid');
						if(fileid != fid){
							accessory.push(fid);
						}
					});
					invockeService('crop.info.base.update.service', {pk:getContractorId(),name:'contractor_accessory',value:accessory.join(',') || ''},function(data, isSucess){
						if(!isSucess){
							return;
						}
						li.remove();
					});
				});
		});
	}
	//审核记录
	var getRecord = function(){
			var contractor_id = getContractorId();
			invockeServiceSync('audit.record.list.service', {contractor_id:contractor_id}, function(data, isSucess){
				if(!isSucess){
					return;
				}
				if(data.rows.length==0){
					$('#crop_table tbody').html("　暂无审核记录");
				}else{
					$('#crop_table tbody').text("");
				}
				for(var i=0;i<data.rows.length;i++){
					switch(data.rows[i]['audit_status']){
					case '0':data.rows[i]['audit_status']="<font color='blue'>申请变更</font>";data.rows[i]['audit_memo']="申请变更成功";break;
					case '1':data.rows[i]['audit_status']="<font color='gray'>提交审核</font>";data.rows[i]['audit_memo']="提交审核";break;
					case '2':data.rows[i]['audit_status']="<font color='green'>审核通过</font>";data.rows[i]['audit_memo']="审核通过";break;
					case '3':data.rows[i]['audit_status']="<font color='red'>审核驳回</font>";break;
					case '4':data.rows[i]['audit_status']="<font color='purple'>业务被冻结</font>";
					data.rows[i]['audit_memo']="您的企业业务已被冻结,请重新提交审核";
					if(data.rows[i]['record_status']=='1'){
						data.rows[i]['audit_memo']="您的资料员已被冻结,请重新提交审核";
						};
					break;
					}
					if(data.rows[i]['record_status']=='1'){
						data.rows[i]['object_name']="资料员　"+data.rows[i]['object_name']+"";
					}
				}
				var rows = data.rows, body = $('#crop_table tbody'), tmpl =
					"<tr>"+
						"<td class='noborder'>{xuhao}:  {object_name} 于 {record_datetime}　{audit_status}<br><br>	　操作人为【{record_name}】<br/><br/>	　备注:　{audit_memo}</td>"
					"</tr>"+
					"<li role='separator' class='divider'></li>";
				$(rows).each(function(){
					body.append(parserTemplate(tmpl,this));
				});
			});	
	}
	getRecord();//审核记录初始化
	accessory();//附件初始化
	//营业执照
	business_license_imgUpload = new imgFileUpload(function(data){
		if(!data){
			return false;
		}
		var fileid = data.files[0].fileidentify;
		var name = data.files[0].filename.split(".");
		var nameValue = name[1].toUpperCase();
		if(nameValue !='PNG' && nameValue !='JPG' && nameValue !="BMP" && nameValue !="JPEG"){
			xAlert("错误,只能上传jpg,bmp,jpeg,png格式的图片");
			business_license_imgUpload.setImgSrc(false);
			return;
		}
		if(fileid && fileid.length > 0){
			invockeServiceSync('crop.info.base.update.service', {pk:getContractorId(),name:'business_license_pic',value:fileid,value2:data.files[0].filename}, function(dt, isSucess){
				if(!isSucess){
					business_license_imgUpload.setImgSrc(false);
					return;
				}
				xMsg("营业执照上传成功！");
				$('#business_license_gallery').attr('href',business_license_imgUpload.getImgUrl(fileid));
				$('#business_license_pic').data("fileid",fileid);
			});
		}
	});
	//开户许可证
	account_code_imgUpload = new imgFileUpload(function(data){
		if(!data){
			return false;
		}
		
		var fileid = data.files[0].fileidentify;
		var name = data.files[0].filename.split(".");
		var nameValue = name[1].toUpperCase();
		if(nameValue !='PNG' && nameValue !='JPG' && nameValue !="BMP" && nameValue !="JPEG"){
			xAlert("错误,只能上传jpg,bmp,jpeg,png格式的图片");
			account_code_imgUpload.setImgSrc(false);
			return;
		}
		if(fileid && fileid.length > 0){
				invockeServiceSync('crop.info.base.update.service', {pk:getContractorId(),name:'account_code_pic',value:fileid,value2:data.files[0].filename}, function(dt, isSucess){
					if(!isSucess){
						organizing_code_imgUpload.setImgSrc(false);
						return;
					}
					xMsg("开户许可证上传成功！");
					$('#account_code_gallery').attr('href',account_code_imgUpload.getImgUrl(fileid));
					$('#account_code_pic').data("fileid",fileid);
				});
			}
	});
	//审核记录
	$("#contractor_meno").click(function(){
		    getRecord();
			$('#viewDodumentorIdeaDialog .modal-header h4').text('审核流程记录');
			$('#viewDodumentorIdeaDialog').modal({backdrop:false,show:true});
		});
	//参建单位附件
	contractor_file_upload = new singleFileUpload(function(data){
		if(!data || !data.files){
			return false;
		}
	    var file = data.files[0];//获得file的属性
		var name = file.filename.split(".");
		var nameValue = name[1].toUpperCase();
		var ol =$("#accessory_ol");//获取ol
		//图片查看判断
		if(nameValue==='PNG' || nameValue==='JPG' || nameValue ==="BMP" || nameValue ==="JPEG"){
			ol.append("<li data-fileid='"+file.fileidentify+"'><a  id="+file.fileidentify+" href='netdomain.sys_diskfile_download?downtype=image&fileid="+file.fileidentify+"'>"+file.filename+"</a><i class='glyphicon glyphicon-remove hand' style='margin-left:15px;'></i></li>");
			$("#"+file.fileidentify+"").lightBox();
		}else{
		    ol.append("<li data-fileid='"+file.fileidentify+"'><a target='_blank' href='netdomain.sys_diskfile_download?fileid="+file.fileidentify+"'>"+file.filename+"</a><i class='glyphicon glyphicon-remove hand' style='margin-left:15px;'></i></li>");
		}
		var accessory = [];
		var filename= [];
		ol.find("a").each(function(){//each function 
			accessory.push($(this).parent().data('fileid'));//parent找到该子级的父级
			filename.push($(this).text());
		});
		invockeService('crop.info.base.update.service', {pk:getContractorId(),name:'contractor_accessory',value:accessory.join(',') || '',value2:filename.join(",")});//转换join()并以,分开
		xMsg("附件上传成功");
		ol.find('i').unbind();//取消绑定(确定无重复数值)
		ol.find('i').click(function(){
			var accessory = [], fid, fileid = $(this).parent().data('fileid'),
			li = $(this).parent();
			
			ol.find("a").each(function(){
				fid = $(this).parent().data('fileid');
				if(fileid != fid){
					accessory.push(fid);
				}
			});
			invockeService('crop.info.base.update.service', {pk:getContractorId(),name:'contractor_accessory',value:accessory.join(',') || ''},function(data, isSucess){
				if(!isSucess){
					return;
				}
				li.remove();
			});
		});
	});
	//企业提交审核
	$("#contractor_submit_audit").click(function(){
		
		if($("#legal_representative").text()===null || $("#legal_representative").text()==='' ){
			xAlert("法定代表人必填");
			return;
		}
		if($("#contact_phone").text()===null || $("#contact_phone").text()==='' ){
			xAlert("联系人手机必填");
			return;
		}
		if($("#enterprise_type").text()===null || $("#enterprise_type").text()==='' ){
			xAlert("企业类型必选");
			return;
		}
		if($("#professional_list").text()===null || $("#professional_list").text()==='' ){
			xAlert("专业分类必选");
			return;
		}
		if($("#business_license_pic").data('fileid')===null || $("#business_license_pic").data('fileid')==='' ){
			xAlert("营业执照必须上传");
			return;
		}
		if($("#account_code_pic").data('fileid')===null || $("#account_code_pic").data('fileid')==='' ){
			xAlert("开户许可证必须上传");
			return;
		}
		xConfirm("你确定要提交审核吗？",function(flag){
	    	if(!flag){
	    		return;
	    	}
	    	var contractorId=getContractorId();
			var name=$("#company_name").val();
		    invockeServiceSync('crop.audit.submit.service', {contractor_id:contractorId,record_name:name}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					$("#close").click();
					xMsg("提交审核成功");
					$("#crop_collapse .file-upload,#accessory_ol i.glyphicon-remove").css('visibility','hidden');
					$("#btnEditAccount").hide();
					$("#btnEditCropInfo").hide();
					$("#contractor_submit_audit").hide();
		    });
		});
	});
	//企业变更申请
	$("#update_info").click(function(){
		xConfirm("你确定要申请变更吗？",function(flag){
	    	if(!flag){
	    		return;
	    	}
	    	var contractorId=getContractorId();
			var name = $("#company_name").val();
			invockeServiceSync('crop.update.info.service', {contractor_id:contractorId,record_name:name,object_name:name}, function(data, isSucess){
				if(!isSucess){
					return;
				}
					$("#crop_collapse .file-upload,#accessory_ol i.glyphicon-remove").css('visibility','visible');
					$("#btnEditAccount").show();
					$("#btnEditCropInfo").show();
					$("#contractor_submit_audit").show();
					$("#update_info").hide();
					xAlert("申请变更成功，请变更后重新提交审核");
			});
		});
	});
	/**资料员信息处理*********************************************************************************************************/
	//资料员扫描附件
	audit_code_imgUpload = new imgFileUpload(function(data){
		if(!data){
			return false;
		}
		var fileid = data.files[0].fileidentify;
		if(fileid && fileid.length > 0){
			var dt = packData("#auditDocumentorModalDialog .modal-body",function(id,v){var k=id.replace(/(.*)_auditDocumentor$/img,'$1'),r={};r[k]=v;return r;});
			var documentor_id = dt['documentor_id'];
			var scan_accessory = dt['scan_accessory'];
			invockeServiceSync('crop.audit.base.update.service', {pk:documentor_id,name:'scan_accessory_auditDocumentor',value:fileid,scan:scan_accessory}, function(dt, isSucess){
				if(!isSucess){
					audit_code_imgUpload.setImgSrc(false);
					return;
				}
				xMsg("扫描附件上传成功！");
				$('#scan_license_gallery').attr('href',audit_code_imgUpload.getImgUrl(fileid));
			});
		}
	});
	//获取企业信息
	var getCompanyData = function(){
		$("#addDocumentorModalDialog").hide();
		var dt = packData("#addDocumentorModalDialog .modal-body",function(id,v){var k=id.replace(/(.*)_addDocumentor$/img,'$1'),r={};r[k]=v;return r;});
		dt['contractor_id'] = getContractorId();
		invockeService('crop.croplist.get.service', {contractor_id:dt['contractor_id']}, function(data, isSucess){
			if(!isSucess){
				return;
			}
			if(data.crop['contact_phone']==null || data.crop['contact_phone']==''){
				xAlert("请完善企业信息");
				return;
			}
			$("#addDocumentorModalDialog").show();
			var name = data.crop['company_name'];
			var phone = data.crop['contact_phone'];
			$("#company_name_addDocumentor").val(name);
			$("#contact_phone_addDocumentor").val(phone);
		});
	};
	

	var $table = $('#documentor_table');
	//复选框置灰
	 stateFormatter = function(value, row, index) {//row行 index ID value 
	    if (row['approval_status'] == '2' || row['approval_status']=='1' ) {
	        return {
	            disabled: true
	        };
	    }
	    return value;
	};
	//状态格式化
	 documentorStateFormatter  = function(value, row, index) {
	    switch(row['approval_status']){
	      case '0':return "<font color='gray'>未审核</font>";
	      case '1':return  "<font color='bule'>审核中</font>";
	      case '2':return  "<font color='green'>审核通过</font>";
	      case '3':return  "<font color='red'>驳回</font>";
	      case '4':return  "<font color='red'>业务冻结</font>";
	    }
	};
	getDocumentorData = function() {
		var tableOptions = $table.bootstrapTable('getOptions'),
		page = tableOptions.pageNumber, // 当前页
		rowNum = tableOptions.pageSize,//每页记录数
		records = tableOptions.totalRows,//总记录数
		condition = {contractor_id:getContractorId()},//查询条件
		callback = function(data,isSuccess){
			if(!isSuccess){
				return;
			}
			$table.bootstrapTable('load', data);//回填数据
		}
		invockeService('crop.documentor.query.service', condition, callback, page, rowNum, records);
	};
	$table.bootstrapTable();//初始化表格
	getDocumentorData();//加载初始数据
    $table.on('page-change.bs.table',getDocumentorData);//翻页
    
    $('#documentor_print_btn').click(function(){//打印预览
    	var selectdata = $table.bootstrapTable('getSelections');
    	if(!selectdata || selectdata.length <=0){
    		xAlert('请选择资料员信息');
    		return;
    	}
    	showDialog("crop/printDocumentor.jsp",'printDocumentorModalDialog',function(dialog){
	    		var documentor_id = selectdata[0].documentor_id;
				invockeService('crop.documentor.get.service', {documentor_id:documentor_id}, function(data, isSucess){
					if(!isSucess){
						dialog.modal('hide');
						return;
					}
					var documentorSex = data.documentor['documentor_sex'];
					if(documentorSex!=null && documentorSex!="" ){
						if(documentorSex==0){
							data.documentor['documentor_sex']="女";
						}else
						if(documentorSex==1){
							data.documentor['documentor_sex']="男";
						}
					}
					loadDivData(data.documentor,"#printDocumentorModalDialog .modal-body",function(n,v){return n+'_printDocumentor'});
					var project_name = data.documentor['project_name'].split(",");
					if(project_name!=null && project_name!=''){
						$("#project_addname_printDocumentor").empty("");
						var a=0;
						for(var i=0;i<project_name.length;i++){
							a++;
							jQuery("#project_addname_printDocumentor").append(
									"<tr  >"+
									"<td width='50' height='60' align='center'>"+a+"</td>"+
									"<td height='60' class='td' >"+project_name[i]+"</td>"+
									"</tr>");
									//"<td align='center'>33"+a+"</td>"+"<td>"+project_name[i]+"</td><br/><br/>");
						}
					}
					var photo = data.documentor['photo'];
					$('#photo_printDocumentor').attr('src',imgUpload.getImgUrl(photo));
					$('#photo_printDocumentor').data('packdata',photo);
				});
		});
    });

//    $("#documentor_submit_btn").click(function(){//跳转审核
//    	var selectdata = $table.bootstrapTable('getSelections');
//    	if(!selectdata || selectdata.length <=0){
//    		xAlert('请选择资料员信息');
//    		return;
//    	}
//     showDialog("crop/auditDocumentor.jsp",'auditDocumentorModalDialog',function(dialog){
//    		var documentor_id = selectdata[0].documentor_id;
//			invockeService('crop.documentor.get.service', {documentor_id:documentor_id}, function(data, isSucess){
//				if(!isSucess){
//					dialog.modal('hide');
//					return;
//				}
//				var documentorSex = data.documentor['documentor_sex'];
//				if(documentorSex!=null && documentorSex!="" ){
//					if(documentorSex==0){
//						data.documentor['documentor_sex']="女";
//					}else{
//						data.documentor['documentor_sex']="男";
//					}
//				}
//				loadDivData(data.documentor,"#auditDocumentorModalDialog .modal-body",function(n,v){return n+'_auditDocumentor'});
//				var project_name = data.documentor['project_name'].split(",");
//				if(project_name!=null && project_name!=''){
//					var a=0;
//					for(var i=0;i<project_name.length;i++){
//						a++;
//						jQuery("#project_addname_auditDocumentor").append(a+":"+project_name[i]+"\n");
//					}
//				}
//				var photo = data.documentor['photo'];
//				var scan_accessory = data.documentor['scan_accessory'];
//				var professional = data.documentor['professional_list_name'];
//				if(professional!=null && professional!=''){
//					$("#professional_list_auditDocumentor").text(professional);
//				}
//				$('#photo_auditDocumentor').attr('src',imgUpload.getImgUrl(photo));
//				$('#photo_auditDocumentor').data('packdata',photo);
//				if(scan_accessory!=null&&scan_accessory!=''){
//					$("#scan_accessory_auditDocumentor").attr("src",'netdomain.sys_diskfile_download?downtype=image&fileid='+scan_accessory);
//					$("#scan_license_gallery").attr('href','netdomain.sys_diskfile_download?downtype=image&fileid='+scan_accessory);
//				}
//			});
//	    });
//  });
    
    
    
    $('#documentor_delete_btn').click(function(){//删除资料员
    	var selectdata = $table.bootstrapTable('getSelections');
    	if(!selectdata || selectdata.length <=0){
    		xAlert('请选择需要删除的资料员信息');
    		return;
    	}
    	var ids = [];
    	for(var i=0; i<selectdata.length; i++){
    		ids.push(selectdata[i].documentor_id);
    	}
    	xConfirm("你确定要删除该资料员信息吗？",function(flag){
        	if(!flag){
        		return;
        	}
        	invockeServiceSync('crop.documentor.delete.service', {documentor_id:ids}, function(data, isSucess){
				if(!isSucess){
					return;
				}
				xMsg("删除资料员信息成功");
				getDocumentorData();//重新加载数据
			});
    	});
	});
    
    $('#documentor_update_btn').click(function(){//修改资料员
    	var selectdata = $table.bootstrapTable('getSelections');
    	
    	if(!selectdata || selectdata.length <=0){
    		xAlert('请选择需要修改的资料员信息');
    		return;
    	}
         	container = $('#updateDocumentorModalDialogContainer');
        	if(container.html()){
        		container.show();
    			$('#updateDocumentorModalDialog').modal({backdrop:false,show:true});
    			//getCompanyData();
    		}
    		else{
    				var timestamp = (new Date()).valueOf();
    				$.ajaxSetup({cache: false});//关闭AJAX相应的缓存
    				container.load("crop/updateDocumentor.jsp?"+timestamp, null, function(){
    					$.getScript('crop/updateDocumentor.js');
    	
    					container.show();
    					$('#updateDocumentorModalDialog').modal({backdrop:false,show:true});
    					
//    					loadPKList({svrName:"pklist.sys_data_dictionary.service",svrData:{datavalue:'documentor_post'}}, "documentor_post_updateDocumentor");
//    					loadPKList({svrName:"pklist.sys_data_dictionary_professional_list.service",svrData:{datavalue:'professional_list',contractor_id: selectdata[0].contractor_id}}, "professional_list_updateDocumentor");
//    					getCompanyData();
    					//$('.bootstrap-tagsinput input').attr('style',"");
    				});
    			}
	});
    
    $('#documentor_add_btn').click(function(){//新增资料员
    	
    	var container = $('#addDocumentorModalDialogContainer');
    	$("#introduction_addDocumentor").val('');
    	$('#project_name_addDocumentor').tagsinput('removeAll');
    	$("#contract_name_addDocumentor").val('');
    	if(container.html()){
    		container.show();
			$('#addDocumentorModalDialog').modal({backdrop:false,show:true});
			getCompanyData();
		}
		else{
			var timestamp = (new Date()).valueOf();
			$.ajaxSetup({cache: false});//关闭AJAX相应的缓存
			
			container.load("crop/addDocumentor.jsp?"+timestamp, null, function(){
				$.getScript('crop/addDocumentor.js');
				container.show();
				$('#addDocumentorModalDialog').modal({backdrop:false,show:true});
				loadPKList({svrName:"pklist.sys_data_dictionary.service",svrData:{datavalue:'documentor_post'}}, "documentor_post_addDocumentor");
				getCompanyData();
				//$('.bootstrap-tagsinput input').attr('style',"");
			});
		}
	});
    
    $('#business_license_gallery').lightBox();
    $('#account_code_gallery').lightBox();
   
});
