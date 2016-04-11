/*
 * 发布增量版本
 */
jQuery(function($) {
	var dialog = $('#versionPackDialog');
	$('body').css('overflow','hidden');
	
	dialog.on('show.bs.modal', function (e) {
		$('body').css('overflow','hidden');
	});
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body .form-control').text('');
		dialog.parent().hide();
		dialog.find('.modal-body input[id], .modal-body textarea').val('');
		$('#add_version_button').prop("disabled",false);
		$('#packAddRefreshButton').trigger("click");
		$('#packDeleteRefreshButton').trigger("click");
		$('body').css('overflow','auto');
	});
	
	var base_version_fun = function(){
		if($('#pack_version_name').text() == $(this).text()){
			xAlert("不能同时选择相同版本，请重新选择");
			return;
		}
		if($('#base_version_name').text() == $(this).text()){
			return;
		}
		$('#base_version_name').text($(this).text());
		var versionId = $(this).data("value");
		$('#base_version_menu').data("value",versionId);
		getTreeBase(versionId);
	},
	pack_version_fun = function(){
		if($('#base_version_name').text() == $(this).text()){
			xAlert("不能同时选择相同版本，请重新选择");
			return;
		}
		if($('#pack_version_name').text() == $(this).text()){
			return;
		}
		$('#pack_version_name').text($(this).text());
		var versionId = $(this).data("value");
		$('#pack_version_menu').data("value",versionId);
		getTreePack(versionId);
	},
	getLastNode = function(data){
		var ids=[],pid;
		
		for(var i=0; i<data.length; i++){
			ids.push(data[i].id);
		}
		
		for(var i=0; i<data.length; i++){
			pid = data[i].parent;
			
			for(var j=0; j<ids.length; j++){
				if(ids[j] == pid){
					ids.splice(j,1);
					break;
				}
			}
		}
		
		return ids.join(",");
	};
	
	invockeService("pklist.fms_version.service",{},function(data,isSuccess){
		if(!isSuccess){
			return;
		}
		var base_version_menu=$('#base_version_menu'), pack_version_menu=$('#pack_version_menu');
		$(data.pklist).each(function() {
			base_version_menu.append("<li><a href='#' data-value='"+this.value+"'>"+this.text+"</a></li>");
			pack_version_menu.append("<li><a href='#' data-value='"+this.value+"'>"+this.text+"</a></li>");
		});
		
		base_version_menu.find('>li>a').click(base_version_fun);
		pack_version_menu.find('>li>a').click(pack_version_fun);
	});
	
	$('#add_version_button').on('click', function(){
		if (!$("#version_num").val()) {
			xAlert("请输入版本编号",$("#version_num"));
	        return false;
        }
		if (!$("#version_name").val()) {
			xAlert("请输入版本名称",$("#version_name"));
	        return false;
        }
		if(pack_add_tree_data.length <= 0 && pack_delete_tree_data.length <= 0){
			xAlert("请选择版本需要变动的内容");
	        return false;
		}
		var baseVersionId = $('#base_version_menu').data("value"), packVersionId = $('#pack_version_menu').data("value");
		if(!packVersionId){
			xAlert("请选择增量版本信息");
	        return false;
		}
		
		xConfirm("确定要发布增量版本吗？",function(flag){
	    	if(!flag){
	    		return;
	    	}
	    	$(this).prop("disabled",true);
	    	showLoading(180);
	    	var dt = {
	    		base_vid:baseVersionId,
	    		pack_vid:packVersionId,
	    		version_num:$("#version_num").val(),
	    		version_name:$("#version_name").val(),
	    		version_memo:$("#version_memo").val(),
	    		fs_id_add:getLastNode(pack_add_tree_data),
	    		fs_id_del:getLastNode(pack_delete_tree_data)
	    	};
	    	invockeService('busi.library.version.pack.add.service', dt, function(data, isSucess){
	    		showLoading(0);
        		if(!isSucess){
        			$('#add_version_button').prop("disabled",false);
					return;
				}
        		xMsg('创建增量版本成功.');
        	});
		});
	});
	
	//基础版本树
    var getTreeBase = function(versionId){
    	$('#base_version_tree').jstree('destroy',true);
    	$('#base_version_tree').bind('ready.jstree', function(e, data) {
	        $('#base_version_tree').jstree(true).add_action("all", {
				id: "action_add_"+data.instance._id,
				'class': "",
				text: "",
				after: false,
				append:true,//true-内部插入,false-外部插入
				selector: "div",
				event: "click",
				callback: function(node_id, node, action_id, action_el){
					var inst = $('#base_version_tree').jstree(true),
					ids = node.parents.concat([node.id]),
					pid,id,flag;
					
					for(var i=0; i<ids.length; i++){
						pid = ids[i];
						if(pid == '#'){//根
							continue;
						}
						flag = false;
						for(var j=0; j<pack_add_tree_data.length; j++){
							id = pack_add_tree_data[j].id;
							if(id == pid){
								flag = true;
								break;
							}
						}
						if(!flag){
							nd = inst.get_node(pid);
							pack_add_tree_data.push({
								id:nd.id,
								parent:nd.parent,
								text:nd.text,
								type:nd.type,
								state:{opened:true}
							});
						}
					}
					$('#pack_add_tree').jstree('refresh');
					inst.delete_node(node);
				},
				types: {
					libtype : {//档案库门类
		              	icon : "fa fa-plus-square fa-2 purple margin-top-2 pull-right"
		            },
		            library : {//档案库
		              	icon : "fa fa-plus-square fa-2 green margin-top-2 pull-right"
		            },
		            profession : {//专业
		              	icon : "fa fa-plus-square fa-2 blue margin-top-2 pull-right"
		            },
		            classify : {//案卷分类
		              	icon : "fa fa-plus-square fa-2 pink margin-top-2 pull-right"
		            },
		            archives : {//案卷
		              	icon : "fa fa-plus-square fa-2 orange margin-top-2 pull-right"
		            },
		            files : {//未关联表格文件
		            	icon : "fa fa fa-plus fa-2 purple margin-top-2 pull-right"
		            },
		            files2 : {//已关联表格文件
		            	icon : "fa fa-plus fa-2 green margin-top-2 pull-right"
		            },
		            common : {//
		            	icon : "fa fa-plus-square fa-2 grey margin-top-2 pull-right"
		            },
		            action : {
		              	icon : "fa fa-plus fa-2 light-orange margin-top-2 pull-right"
		            },
		            unknow : {//未知
		            	icon : "fa fa-plus-square brown margin-top-2 pull-right"
		            }
		        }
			});
	    }).jstree({
	        plugins: ["wholerow","types","actions"],
	        types: {
	        	libtype : {//档案库门类
	              	icon : "fa fa-sitemap purple"
	            },
	            library : {//档案库
	              	icon : "fa fa-university green"
	            },
	            profession : {//专业
	              	icon : "fa fa-shield blue"
	            },
	            classify : {//案卷分类
	              	icon : "fa fa-th-large pink"
	            },
	            archives : {//案卷
	              	icon : "fa fa-cube orange"
	            },
	            files : {//未关联表格文件
	            	icon : "fa fa-file-o grey"
	            },
	            files2 : {//已关联表格文件
	            	icon : "fa fa-file-word-o green"
	            },
	            common : {//
	            	icon : "fa fa-folder-o grey"
	            },
	            unknow : {//未知
	            	icon : "fa fa-exclamation-triangle brown"
	            }
	        },
	        core: {
	            themes: {
	                name: 'proton',
	                icons : true,//false
	                responsive: true
	            },
	            multiple:false,
	            check_callback:true,
	            data:function(node,treecallback){
	        		var d = {version_id:versionId};
	        		var serviceName;
	        		if(node.id === "#") {
	        			d['id'] = '';
	                }
	                else {
	                	d['id'] = node.id;
	                }
	        		if(versionId != 0){
	        			serviceName ='busi.version.library.query.service';
	        		}else{
	        			serviceName ='busi.library.query.service';
	        		}
	            	invockeService(serviceName, d, function(data, isSucess){
						if(!isSucess){
							treecallback([]);
							return;
						}
						var cnt = 0;
						for(var i=0; i<data.library.length; i++){
							cnt = (data.library[i].children || 0)*1;
							if(cnt > 0){
								data.library[i].children = true;
							}
							else{
								data.library[i].children = false;
							}
						}
						treecallback(data.library || []);
					});
	            }
	        }
	    });
	};
    
    	
    //增量版本树
    var getTreePack = function(versionId){
    	$('#pack_version_tree').jstree('destroy',true);
    	$('#pack_version_tree').bind('ready.jstree', function(e, data) {
	        $('#pack_version_tree').jstree(true).add_action("all", {
				id: "action_add_"+data.instance._id,
				'class': "",
				text: "",
				after: false,
				append:true,//true-内部插入,false-外部插入
				selector: "div",
				event: "click",
				callback: function(node_id, node, action_id, action_el){
					var inst = $('#pack_version_tree').jstree(true),
					ids = node.parents.concat([node.id]),
					pid,id,flag;
					
					for(var i=0; i<ids.length; i++){
						pid = ids[i];
						if(pid == '#'){//根
							continue;
						}
						flag = false;
						for(var j=0; j<pack_delete_tree_data.length; j++){
							id = pack_delete_tree_data[j].id;
							if(id == pid){
								flag = true;
								break;
							}
						}
						if(!flag){
							nd = inst.get_node(pid);
							pack_delete_tree_data.push({
								id:nd.id,
								parent:nd.parent,
								text:nd.text,
								type:nd.type,
								state:{opened:true}
							});
						}
					}
					$('#pack_delete_tree').jstree('refresh');
					inst.delete_node(node);
				},
				types: {
					libtype : {//档案库门类
		              	icon : "fa fa-minus-square fa-2 purple margin-top-2 pull-right"
		            },
		            library : {//档案库
		              	icon : "fa fa-minus-square fa-2 green margin-top-2 pull-right"
		            },
		            profession : {//专业
		              	icon : "fa fa-minus-square fa-2 blue margin-top-2 pull-right"
		            },
		            classify : {//案卷分类
		              	icon : "fa fa-minus-square fa-2 pink margin-top-2 pull-right"
		            },
		            archives : {//案卷
		              	icon : "fa fa-minus-square fa-2 orange margin-top-2 pull-right"
		            },
		            files : {//未关联表格文件
		            	icon : "fa fa fa-times fa-2 red margin-top-2 pull-right"
		            },
		            files2 : {//已关联表格文件
		            	icon : "fa fa fa-times fa-2 green margin-top-2 pull-right"
		            },
		            common : {//
		            	icon : "fa fa-minus-square fa-2 grey margin-top-2 pull-right"
		            },
		            action : {
		              	icon : "fa fa-trash-o fa-2 light-orange margin-top-2 pull-right"
		            },
		            unknow : {//未知
		            	icon : "fa fa-minus-square brown margin-top-2 pull-right"
		            }
		        }
			});
	    }).jstree({
	        plugins: ["wholerow","types","actions"],
	        types: {
	        	libtype : {//档案库门类
	              	icon : "fa fa-sitemap purple"
	            },
	            library : {//档案库
	              	icon : "fa fa-university green"
	            },
	            profession : {//专业
	              	icon : "fa fa-shield blue"
	            },
	            classify : {//案卷分类
	              	icon : "fa fa-th-large pink"
	            },
	            archives : {//案卷
	              	icon : "fa fa-cube orange"
	            },
	            files : {//未关联表格文件
	            	icon : "fa fa-file-o grey"
	            },
	            files2 : {//已关联表格文件
	            	icon : "fa fa-file-word-o green"
	            },
	            common : {//
	            	icon : "fa fa-folder-o grey"
	            },
	            unknow : {//未知
	            	icon : "fa fa-exclamation-triangle brown"
	            }
	        },
	        core: {
	            themes: {
	                name: 'proton',
	                icons : true,//false
	                responsive: true
	            },
	            multiple:false,
	            check_callback:true,
	            data:function(node,treecallback){
	        		var d = {version_id:versionId};
	        		if(node.id === "#") {
	        			d['id'] = '';
	                }
	                else {
	                	d['id'] = node.id;
	                }
	            	invockeService('busi.version.library.query.service', d, function(data, isSucess){
						if(!isSucess){
							treecallback([]);
							return;
						}
						var cnt = 0;
						for(var i=0; i<data.library.length; i++){
							cnt = (data.library[i].children || 0)*1;
							if(cnt > 0){
								data.library[i].children = true;
							}
							else{
								data.library[i].children = false;
							}
						}
						treecallback(data.library || []);
					});
	            }
	        }
	    });
	};
	
	//刷新
	$('#baseVersionRefreshButton').on('click', function(){
		$('#base_version_tree').jstree('refresh');
	});
	$('#packVersionRefreshButton').on('click', function(){
		$('#pack_version_tree').jstree('refresh');
	});
	
	$('#packAddRefreshButton').on('click', function(){
		pack_add_tree_data = [];
		$('#pack_add_tree').jstree('refresh');
	});
	$('#packDeleteRefreshButton').on('click', function(){
		pack_delete_tree_data = [];
		$('#pack_delete_tree').jstree('refresh');
	});
	
	var pack_add_tree_data = [];
	$("#pack_add_tree").jstree({
		plugins : [ "wholerow","types"],
		types: {
			libtype : {//档案库门类
              	icon : "fa fa-sitemap purple"
            },
            library : {//档案库
              	icon : "fa fa-university green"
            },
            profession : {//专业
              	icon : "fa fa-shield blue"
            },
            classify : {//案卷分类
              	icon : "fa fa-th-large pink"
            },
            archives : {//案卷
              	icon : "fa fa-cube orange"
            },
            files : {//未关联表格文件
            	icon : "fa fa-file-o grey"
            },
            files2 : {//已关联表格文件
            	icon : "fa fa-file-word-o green"
            },
            common : {//
            	icon : "fa fa-folder-o grey"
            },
            unknow : {//未知
            	icon : "fa fa-exclamation-triangle brown"
            }
		},
		core : {
			themes: {
				name: 'proton',
				icons : true,//false
				responsive: true
			},
			multiple:false,
			data:function(node,treecallback){
				treecallback.call(this, pack_add_tree_data);
    		}
		}
	});
	
	var pack_delete_tree_data = [];
	$("#pack_delete_tree").jstree({
		plugins : [ "wholerow","types"],
		types: {
			libtype : {//档案库门类
              	icon : "fa fa-sitemap purple"
            },
            library : {//档案库
              	icon : "fa fa-university green"
            },
            profession : {//专业
              	icon : "fa fa-shield blue"
            },
            classify : {//案卷分类
              	icon : "fa fa-th-large pink"
            },
            archives : {//案卷
              	icon : "fa fa-cube orange"
            },
            files : {//未关联表格文件
            	icon : "fa fa-file-o grey"
            },
            files2 : {//已关联表格文件
            	icon : "fa fa-file-word-o green"
            },
            common : {//
            	icon : "fa fa-folder-o grey"
            },
            unknow : {//未知
            	icon : "fa fa-exclamation-triangle brown"
            }
		},
		core : {
			themes: {
				name: 'proton',
				icons : true,//false
				responsive: true
			},
			multiple:false,
			data:function(node,treecallback){
				treecallback.call(this, pack_delete_tree_data);
    		}
		}
	});
});