/**
 * 
 */
jQuery(function($){
	var dialog = $('#formsfilesLibraryDialog'), busibtn = $('#relatedFormsfilesButton');
	
	dialog.on('hidden.bs.modal', function (e) {
		//dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.parent().hide();
		busibtn.prop("disabled",false);
	});
	busibtn.click(function(){
		var nodeId = getSelectedLibNodeID(),nodeType=getSelectedLibNodeType();
		if(!nodeId || nodeId.length <= 0){
			xAlert("请选择需要关联的条目");
			return false;
		}
	   	if(nodeType == "files2"){
	   		xConfirm("该条目已经关联，是否重新关联？",function(flag){
		    	if(!flag){
		    		return;
		    	}
		    	busibtn.prop("disabled",true);
			   	var grid = $('#myJqGrid'),key = grid.jqGrid("getGridParam","selrow");
			   	invockeServiceSync('busi.foldertemplate_formsfiles.add.service',{tb_id:key,fs_id:getSelectedLibNodeID()}, function(data, isSucess){
					if(!isSucess){
						busibtn.prop("disabled",false);
						return;
					}
					$('#library_tree').jstree('refresh');

					xMsg('关联成功');
					dialog.modal('hide');
				});	
			});
	   	}
	   	else if(nodeType != "files"){
	   		xAlert("请选择需要关联的条目，只能选择条目");
			return false;
	   	}
		
    	busibtn.prop("disabled",true);
	   	var grid = $('#myJqGrid'),key = grid.jqGrid("getGridParam","selrow");
	   	invockeServiceSync('busi.foldertemplate_formsfiles.add.service',{tb_id:key,fs_id:getSelectedLibNodeID()}, function(data, isSucess){
			if(!isSucess){
				busibtn.prop("disabled",false);
				return;
			}
			$('#library_tree').jstree('refresh');

			xMsg('关联成功');
			dialog.modal('hide');
		});	
	});
	
	$('#library_tree').jstree({
        plugins: ["wholerow","types"],
        types: {
        	libtype : {//档案库类别
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
            data:function(node,treecallback){
            	var d = {id_type:node.type || ''};
        		if(node.id === "#") {
        			d['id'] = '';
                }
                else {
                	d['id'] = node.id;
                }
            	invockeService('busi.library.query.service', d, function(data, isSucess){
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
    }).on('dblclick.jstree',function(node,selected,event){//双击关联表格
    	xConfirm("你确定要将表格关联到该条目吗？",function(flag){
        	if(!flag){
        		return;
        	}
        	busibtn.trigger('click');
    	});
	}).on('select_node.jstree',function(node,selected,event){//loaded.jstree ready.jstree 档案库加载完毕后加载表格
		showFileDetail(selected.node.type == 'files2');//已关联卷内文件
	});
	var getSelectedLibNodeID = function(){
		 var ref = $('#library_tree').jstree(true),
		 sel = ref.get_selected();
		 if(!sel.length) { 
			 return ""; 
		 }
		 else{
			 return sel.id || sel[0];
		 }
	},
	getSelectedLibNodeType = function(){
		 var ref = $('#library_tree').jstree(true),
		 sel = ref.get_selected();
		 if(!sel.length) { 
			 return ""; 
		 }
		 else{
			 return ref.get_node(sel).type
		 }
	},
	showFileDetail = function(isshow){
		
	};
	
	$('#refreshLibraryButton').on('click', function(){
		$('#library_tree').jstree('refresh');
	});
	$('#closeAllButton').on('click', function(){
		$('#library_tree').jstree('close_all');
	});
});