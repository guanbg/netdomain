/*
 * 资料员信息
 */
jQuery(function($) {
	$("#docu_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#docu_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	$("#docuor_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#docuor_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	
	
	 //初始加载
	var getList = function(){
	 invockeServiceSync('crop.documentor.get.service',{documentor_id:$("#documentor_id").val()}, function(data, isSucess){
			if(!isSucess){
				return;
			}
			var status = data.documentor['approval_status'];
			if(status=='1' || status=='2'){
				$("#docuor_collapse .file-upload").css('visibility','hidden');
				$("#documentor_submit_btn").hide();
	    		$("#btnEditDocumentor").hide();
			}
//			if(status=='2'){
//				$("#update_docu").show();
//			}
			var docuSex = data.documentor['documentor_sex'];
			if(docuSex=='0'){
				$("#documentor_sex").text("女");
			}
			if(docuSex=='1'){
				$("#documentor_sex").text("男");
			}
			var professional = data.documentor['professional_list_name'];
			if(professional!=null && professional!=''){
				$("#professional_list").text(professional);
			}
			var datetime = data.documentor['register_date'].split(" ");
			$("#register_date").text(jqGridFormatDate(datetime[0]));
	   });
	};
	getList();//初始加载
	
	$.fn.editable.defaults.mode = 'inline';
	$.fn.editableform.loading = "<span class='editableform-loading'><i class='fa fa-spinner fa-spin fa-2x light-blue'></i></span>";
    $.fn.editableform.buttons = '<button type="submit" class="btn btn-info editable-submit"><i class="glyphicon glyphicon-ok"></i></button><button type="button" class="btn editable-cancel"><i class="glyphicon glyphicon-remove"></i></button>';    
    var opt = {
		placement: 'right', 
        send:'always',
        emptytext: '', // default is 'Empty'
        pk:$("#documentor_id").val(),
        url:'crop.docu.base.update.service',
        ajaxOptions: {
        	dataType: 'json',//预期服务器返回的数据类型
        	type: 'post'	//请求方式 ("POST" 或 "GET")， 默认为 "GET"
    	},
    	params:function(param){
    	   if(param['name']=='project_name'){
    		   array=param['value'].split("，");
    		   param['value']=array.join(",");
    	   }
    	   if(param['name']=='documentor_sex'){
    		   if(param['value']==1){
    			   param['value2']="男";
    		   }else{
    			   param['value2']="女";
    		   }
    	   }
    	   if(param['name']=='professional_list'){
    		   param['value2']=$('#professional_list').text();
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
        	//性别
            if($(this).attr('id') == 'documentor_sex'  ) {
                if($.trim(value)=='' || $.trim(value)==null){
                	return '性别必选';
                }
            }
          //身份证号
            if($(this).attr('id') == 'documentor_cardid'  ) {
            	if($.trim(value)===null || $.trim(value)=='' ){
                 	return '身份证号必填';
                 }
               var idRex = !!$.trim(value).match(/^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/);
               if(idRex==false){
            	   return '请输入正确的身份证';
               }
            }
          //职称
            if($(this).attr('id') == 'documentor_post'  ) {
            	 if($.trim(value)===null || $.trim(value)=='' ){
                  	return '职称必选';
                  }
            }
            //专业分类
            if($(this).attr('id') == 'professional_list'  ) {
            	 if($.trim(value)===null || $.trim(value)=='' ){
                  	return '专业分类必选';
                  }
            }
            //毕业院校
            if($(this).attr('id') == 'graduate_institutions'  ) {
                if($.trim(value).length>14){
                	return '字数限制,请重输';
                }
            }
            //合同名称
            if($(this).attr('id') == 'contract_name'  ) {
                if($.trim(value).length>30){
                	return '字数限制,请重输';
                }
            }
            //所学专业
            if($(this).attr('id') == 'profession'  ) {
                if($.trim(value).length>10){
                	return '字数限制,请重输';
                }
            }
            //联系电话
            if($(this).attr('id') == 'mobile_no'){
            	 var telReg = !!$.trim(value).match(/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/);
                 if(telReg==false ){
                 	return '请输入正确的手机号码';
                 }
            }
            //出生日期
            if($(this).attr('id') == 'documentor_birthday'){
            	var date =Date.parse($.trim(value));
            	var d = new Date();
            	var datenew = Date.parse(d);
            	if(date>datenew){
            		return '请输入正确的时间';
            	}
            }
            //参加工作时间
            if($(this).attr('id') == 'working_date'){
            	var date =Date.parse($.trim(value));
            	var d = new Date();
            	var datenew = Date.parse(d);
            	if(date>datenew){
            		return '请输入正确的时间';
            	}
            }
          //所属标段单位工程名称
            if($(this).attr('id') == 'project_name'){
            	  if($.trim(value).length>100){
                  	return '字数限制,请重输';
                  }
            }
            //个人简介
            if($(this).attr('id') == 'introduction'){
            	  if($.trim(value).length>500){
                  	return '字数限制,请重输';
                  }
            }
           
        }
    };
    
   $('#btnEditDocumentor').click(function(){//修改资料员信息
		if($('#docuor_collapse .editable').length <= 0){
		    $('#documentor_birthday').editable($.extend({
		        format: 'yyyy-mm-dd',    
		        viewformat: 'yyyy-mm-dd',
		        datepicker: {
		        	weekStart: 1,
		        	language:'zh-CN'
		        }
		    }, opt));
		    $('#working_date').editable($.extend({
		        format: 'yyyy-mm-dd',    
		        viewformat: 'yyyy-mm-dd',
		        datepicker: {
		        	weekStart: 1,
		        	language:'zh-CN'
		        }
		    }, opt));
		    setEditableOptions({svrName:"pklist.sys_data_dictionary.service",svrData:{datavalue:'documentor_post'}},function(data,isSuccess){
		    	$('#documentor_post').editable($.extend({source: data}, opt));
		    });
			$("#docuor_collapse [data-type='text'], #docuor_collapse [data-type='textarea']").each(function(){
		    	$(this).editable($.extend({rows: 4}, opt));
		    });
			$('#documentor_sex').editable($.extend({
				        value: 2,    
				        source: [
				              {value: 0, text: '女'},
				              {value: 1, text: '男'}
				           ]
		    }, opt));
			
			$('#documentor_education').editable($.extend({
		        value: 6,    
		        source: [
		              {value: '中专', text: '中专'},
		              {value: '高等中学', text: '高等中学'},
		              {value: '大专', text: '大专'},
		              {value: '本科', text: '本科'},
		              {value: '硕士', text: '硕士'},
		              {value: '其他', text: '其他'},
		           ]
            }, opt));
			
			$('#service_year').editable($.extend({
		        value: 6,    
		        source: [
		              {value: '一年以下', text: '一年以下'},
		              {value: '一年', text: '一年'},
		              {value: '两年', text: '两年'},
		              {value: '三年', text: '三年'},
		              {value: '四年', text: '四年'},
		              {value: '五年及以上', text: '五年及以上'},
		           ]
            }, opt));
			
			setEditableOptions({svrName:"pklist.sys_data_dictionary_professional_list.service",svrData:{datavalue:'professional_list',contractor_id:$("#contractor_id").val()}},function(data,isSuccess){
		    	$('#professional_list').editable($.extend({source: data}, opt));
		    });
	      } 
		else{
			$('#docuor_collapse .editable').editable('toggleDisabled');
		}
	});
   //照片
   photo_imgUpload =   new imgFileUpload(function(data){
		if(!data){
			return false;
		}
		var fileid = data.files[0].fileidentify;
		var name = data.files[0].filename.split(".");
		var nameValue = name[1].toUpperCase();
		if(nameValue !='PNG' && nameValue !='JPG' && nameValue !="BMP" && nameValue !="JPEG"){
			xAlert("错误，只能上传jpg,bmp,jpeg,png格式的图片");
			photo_imgUpload.setImgSrc(false);
			return;
		}
		if(fileid && fileid.length > 0){
			var dt = packData("#docuor_collapse .panel-body",function(id,v){var k=id.replace(/(.*)_auditDocumentor$/img,'$1'),r={};r[k]=v;return r;});
			var documentor_id = $("#documentor_id").val();
			var photo = dt['photo'];
			invockeServiceSync('crop.photo.base.update.service', {pk:documentor_id,name:'photo_auditDocumentor',value:fileid,scan:photo}, function(dt, isSucess){
				if(!isSucess){
					photo_imgUpload.setImgSrc(false);
					return;
				}
				xMsg("照片上传成功！");
				$('#photo').attr('src',audit_code_imgUpload.getImgUrl(fileid));
				$("#photo_gallery").attr('href','netdomain.sys_diskfile_download?downtype=image&fileid='+fileid);
				$('#photo').data("fileid",fileid);
			});
		}
	});
   //附件
   audit_code_imgUpload = new imgFileUpload(function(data){
		if(!data){
			return false;
		}
		var fileid = data.files[0].fileidentify;
		var name = data.files[0].filename.split(".");
		var nameValue = name[1].toUpperCase();
		if(nameValue !='PNG' && nameValue !='JPG' && nameValue !="BMP" && nameValue !="JPEG"){
			xAlert("错误,只能上传jpg,bmp,jpeg,png格式的图片");
			audit_code_imgUpload.setImgSrc(false);
			return;
		}
		if(fileid && fileid.length > 0){
			var dt = packData("#docuor_collapse .panel-body",function(id,v){var k=id.replace(/(.*)_auditDocumentor$/img,'$1'),r={};r[k]=v;return r;});
			var documentor_id = $("#documentor_id").val();
			var scan_accessory = dt['scan_accessory'];
			invockeServiceSync('crop.audit.base.update.service', {pk:documentor_id,name:'scan_accessory_auditDocumentor',value:fileid,scan:scan_accessory}, function(dt, isSucess){
				if(!isSucess){
					audit_code_imgUpload.setImgSrc(false);
					return;
				}
				xMsg("扫描附件上传成功！");
				$('#scan_accessory').attr('src',audit_code_imgUpload.getImgUrl(fileid));
				$("#scan_license_gallery").attr('href','netdomain.sys_diskfile_download?downtype=image&fileid='+fileid);
				$('#scan_accessory').data("fileid",fileid);
			});
		}
	});
    //打印预览
   $('#documentor_print').click(function(){
   	showDialog("crop/printDocumentor.jsp",'printDocumentorModalDialog',function(dialog){
	    		var documentor_id = $("#documentor_id").val();
				invockeService('crop.documentor.get.service', {documentor_id:documentor_id}, function(data, isSucess){
					if(!isSucess){
						dialog.modal('hide');
						return;
					}
					var documentorSex = data.documentor['documentor_sex'];
					if(documentorSex!=null && documentorSex!="" ){
						if(documentorSex==0){
							data.documentor['documentor_sex']="　女";
						}else
						if(documentorSex==1){
							data.documentor['documentor_sex']="　男";
						}
					}
					data.documentor['documentor_name']="　"+data.documentor['documentor_name'];
					data.documentor['documentor_cardid']="　"+data.documentor['documentor_cardid'];
					data.documentor['documentor_education']="　"+data.documentor['documentor_education'];
					data.documentor['profession']="　"+data.documentor['profession'];
					data.documentor['service_year']="　"+data.documentor['service_year'];
					data.documentor['company_name']="　"+data.documentor['company_name'];
					data.documentor['contact_phone']="　"+data.documentor['contact_phone'];
					data.documentor['contract_name']="　"+data.documentor['contract_name'];
					
					loadDivData(data.documentor,"#printDocumentorModalDialog .modal-body",function(n,v){return n+'_printDocumentor'});
					var project_name = data.documentor['project_name'].split(",");
					if(project_name!=null && project_name!=''){
						$("#project_addname_printDocumentor").empty("");
						var a=0;
						for(var i=0;i<project_name.length;i++){
							a++;
							$("#project_addname_printDocumentor").append(
									"<tr>"+
									"<td width='50' height='60' align='center'>"+a+"</td>"+
									"<td height='60' class='td' >"+project_name[i]+"</td>"+
									"</tr>");
						}
					}
				});
		});
   });
   
   //审核记录
   var getRecord = function(){
		var contractor_id = $("#contractor_id").val();
		var name =$("#documentor_name").text();
		invockeServiceSync('audit.docurecord.list.service', {contractor_id:contractor_id,object_name:name}, function(data, isSucess){
			if(!isSucess){
				return;
			}
			if(data.rows.length==0){
				$('#crop_table tbody').html("　暂无审核记录");
			}else{
				$('#crop_table tbody').text("");
			}
			for(var i=0;i<data.rows.length;i++){
				if(data.rows[i]['record_status']=='1'){
					data.rows[i]['object_name']="资料员　"+data.rows[i]['object_name']+"";
				}
				switch(data.rows[i]['audit_status']){
				//case '0':data.rows[i]['audit_status']="<font color='blue'>申请变更</font>";data.rows[i]['audit_memo']="申请变更成功";break;
				case '1':data.rows[i]['audit_status']="<font color='gray'>提交审核</font>";data.rows[i]['audit_memo']="提交审核";break;
				case '2':data.rows[i]['audit_status']="<font color='green'>审核通过</font>";data.rows[i]['audit_memo']="审核通过";break;
				case '3':data.rows[i]['audit_status']="<font color='red'>审核驳回</font>";break;
				case '4':data.rows[i]['audit_status']="<font color='purple'>业务被冻结</font>";data.rows[i]['audit_memo']="该资料员已被冻结,请重新提交审核";break;
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
   //审核流程
   $("#contractor_meno").click(function(){
	    getRecord();
     	$('#viewDodumentorIdeaDialog .modal-header h4').text('审核流程记录');
		$('#viewDodumentorIdeaDialog').modal({backdrop:false,show:true});
   });
   
   //提交审核
    $("#documentor_submit_btn").click(function(){
    	if($("#documentor_sex").text()==='' || $("#documentor_sex").text()==null){
			xAlert("性别必选");
			return;
		}
    	if($("#documentor_cardid").text()==='' || $("#documentor_cardid").text()==null){
			xAlert("身份证号必填");
			return;
		}
    	if($("#documentor_post").text()==='' || $("#documentor_post").text()==null){
			xAlert("职称必选");
			return;
		}
    	if($("#professional_list").text()==='' || $("#professional_list").text()==null){
			xAlert("专业分类必选");
			return;
		}
    	if($("#photo").data('fileid')==='' || $("#photo").data('fileid')===null){
			xAlert("本人照片必须上传");
			return;
		}
    	if($("#scan_accessory").data('fileid')==='' || $("#scan_accessory").data('fileid')===null){
			xAlert("扫描附件必须上传");
			return;
		}
		if (!myValidation("#docu_collapse .panel-body")) {
	        return false;
        }
		var documentor_id =  $("#documentor_id").val();
		var company_name = $("#company_name").text();
		invockeService('crop.documentor.get.service', {documentor_id:documentor_id}, function(data, isSucess){
			if(!isSucess){
				dialog.modal('hide');
				return;
			}
			var objectname = $("#documentor_name").text();
			var contractor_id = $("#contractor_id").val();
			xConfirm("你确定提交审核吗？",function(flag){
		    	if(!flag){
		    		return false;
		    	}
			submitFlag = true;
			var dt ={
					documentor_id:documentor_id,
					record_name:objectname,
					object_name:objectname,
					contractor_id:contractor_id,
					company_name:company_name
			}
		    invockeServiceSync('crop.documentor.submit.service',dt,function(data,isSucess){
	    		if(!isSucess){
					return;
				}
	    		$("#docuor_collapse .file-upload").css('visibility','hidden');
	    		$("#documentor_submit_btn").hide();
	    		$("#btnEditDocumentor").hide();
	    		xMsg("资料员信息提交审核成功");
	    	});
		});
	});
  });
//    //申请变更
//    $("#update_docu").click(function(){
//    	if(!confirm("您确定要申请变更吗？")){
//    		return false;
//    	}
//		var contractorId=$("#contractor_id").val();
//		var name = $("#documentor_name").text();
//		var documentor_id =$("#documentor_id").val();
//		invockeServiceSync('docu.update.info.service', {documentor_id:documentor_id,contractor_id:contractorId,record_name:name,object_name:name}, function(data, isSucess){
//			if(!isSucess){
//				return;
//			}
//			$("#documentor_submit_btn").show();
//    		$("#update_docu").hide();
//    		$("#btnEditDocumentor").show();
//    		$("#docuor_collapse .file-upload").css('visibility','visible');
//			xAlert("申请变更成功，请变更后重新提交审核");
//		});
//    });
    //照片点击（1.上传 2.查看）
    $('#photo').click(function(){
		var flag = $(this).data('fileid');
		if(flag && flag.length > 0){
			$('#photoView').trigger('click');
		}
		else{
			$('#photoUpload').trigger('click');
		}
	});
	$("#scan_accessory").click(function(){
		var flag = $(this).data('fileid');
		if(flag && flag.length > 0){
			$('#scanView').trigger('click');
		}
		else{
			$('#scanUpload').trigger('click');
		}
	});

   $('#photo_gallery').lightBox();
   $('#scan_license_gallery').lightBox();
});
