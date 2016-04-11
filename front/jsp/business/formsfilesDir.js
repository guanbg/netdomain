/**
 * 
 */
jQuery(function($){
	var dialog = $('#formsfilesDirDialog'), busibtn = $('#saveFormsfilesDirButton');
	
	dialog.on('hidden.bs.modal', function (e) {
		//dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.parent().hide();
		busibtn.prop("disabled",false);
	});
	busibtn.click(function(){
		var ref = $('#formsfiles_dirselect_tree').jstree(true),sel = ref.get_selected();
		if(!sel.length) { 
			xAlert("请选择表格目录");
			return ; 
		}
		var grid = $('#myJqGrid');
		var keys = grid.jqGrid("getGridParam","selarrrow"); 
		busibtn.prop("disabled",true);
		invockeServiceSync('busi.formsfilesdir.update.service',{tb_id:keys,dir_id:sel.id || sel[0]}, function(data, isSucess){
			if(!isSucess){
				return;
			}
			xMsg("修改表格目录成功");
			dialog.modal('hide');
		});
	});
	
	$('#formsfiles_dirselect_tree').jstree({
        plugins: ["wholerow"],
        contextmenu: {items: contextMenuFun, select_node: true},
        core: {
            themes: {
                name: 'proton',
                dots : true,
                icons : true,//false
                responsive: true
            },
            strings : {
                'New node' : "新建分类",
                'Loading ...' : "加载中..."
            },
            multiple:false,
            //check_callback:true,
            check_callback: function (operation, node, node_parent, node_position, more) {
            	//determines what happens when a user tries to modify the structure of the tree
            	//If left as false all operations like create, rename, delete, move or copy are prevented.
            	//You can set this to true to allow all interactions or use a function to have better control.
            	
	            // operation can be 'create_node', 'rename_node', 'delete_node', 'move_node' or 'copy_node'
            	
	            // in case of 'rename_node' node_position is filled with the new node name
	            //return operation === 'rename_node' ? true : false;
            	
            	
				return true;
	        },
            data:function(node,treecallback){
        		var d = {id_type:node.type || ''};
        		if(node.id === "#") {
        			d['id'] = '';
                }
                else {
                	d['id'] = node.id;
                }
            	invockeService('busi.formsfiles.dir.query.service', d, function(data, isSucess){
					if(!isSucess){
						treecallback([]);
						return;
					}
					var cnt = 0;
					for(var i=0; i<data.formsfilesdir.length; i++){
						cnt = (data.formsfilesdir[i].children || 0)*1;
						if(cnt > 0){
							data.formsfilesdir[i].children = true;
						}
						else{
							data.formsfilesdir[i].children = false;
						}
						data.formsfilesdir[i]['a_attr']={title:data.formsfilesdir[i].text};
					}
					treecallback(data.formsfilesdir || []);
				});
            }
        }
    }).on('dblclick.jstree',function(node,selected,event){//双击关联表格
    	xConfirm("你确定要将表格关联到该目录吗？",function(flag){
        	if(!flag){
        		return;
        	}
        	busibtn.trigger('click');
    	});
	});
	
	$('#refreshDirDialogButton').on('click', function(){
		$('#formsfiles_dirselect_tree').jstree('refresh');
	});
});