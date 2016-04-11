/**
 * 案卷表格
 */
jQuery(function($){
	checkChildWindow();
	
	$('#switch_button').on('click', function(){
		if($(this).prop('checked')){
			$('#switch_title').text('未关联表格');
			$('#switch_title').parent().removeClass("grey").addClass("orange");
		}
		else{
			$('#switch_title').text('全部表格');
			$('#switch_title').parent().removeClass("orange").addClass("grey");
		}
		$("#myJqGrid").jqGrid('setGridParam',{page:1,total:0,records:0}).trigger('reloadGrid');
	});
	
	$(window).bind('resize', function() { 	
		$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
	});
	
	var versionFmatter = function (cellvalue, options, rowObject) {
		if(!cellvalue){
			return "";
		}
		
		var tb_id=rowObject['tb_id'],isrelated=rowObject['isrelated'],haschild=rowObject['haschild'], parent_id=rowObject['parent_id'];
		
		if(haschild && haschild * 1 > 0){
			return "<span class='hand subversion'><i class='fa fa-plus-square-o blue'></i> "+cellvalue+"</span>";
		}
		else{
			if(!parent_id || parent_id.length <= 0){
				return "<i class='fa fa-square-o'></i> "+cellvalue;
			}
			var grid = $("#myJqGrid"),margin = 0;
			for(;;){
				if(!parent_id || parent_id.length <= 0){
					break;
				}
				var rowData = grid.getRowData(parent_id);
				parent_id = rowData['parent_id'];
				margin += 10;
			}
			return "<i class='fa fa-long-arrow-right grey'style='margin-left:"+margin+"px;'></i> "+cellvalue;
		}
	},
	fileTitleFormatter = function(cellvalue, options, rowObject){
		if(!cellvalue){
			return "";
		}
		return "<span class='file_title' role=button>"+cellvalue+"</span>";
	};
	
	var dir_id = '';//表格类别
	$("#myJqGrid").jqGrid({
		datatype: function(){
			if($('#switch_button').prop('checked')){//未关联表格
				setJqgridDataFunc('busi.formsfiles.query.service','#myJqGrid',{isrelated:0,dir_id:dir_id||''});
			}
			else{//全部表格
				setJqgridDataFunc('busi.formsfiles.query.service','#myJqGrid',{dir_id:dir_id||''});
			}
		},
		width:"100%",
		height:'auto',
		autowidth: true,
		shrinkToFit: true,
	    colNames:['案卷表格序号','上级','版本','文件编号','文件名称','纸张大小','总页数','目录名称'],   
	    colModel :[
			{name:'tb_id', index:'tb_id', key:true, hidden:true},
			{name:'parent_id', index:'parent_id', hidden:true},
		    {name:'tb_version', index:'tb_version',width:'50px',formatter:versionFmatter},
		    {name:'document_number', index:'document_number',width:'70px',align:'right',search:true},
		    {name:'file_title', index:'file_title',search:true,formatter:fileTitleFormatter},
		    {name:'paper_size', index:'paper_size',width:'30px',search:true},
		    {name:'total_pages', index:'total_pages',width:'30px',search:true},
		    {name:'dir_name', index:'dir_name',width:'60px',search:true}],
	    pager: "#myJqGridPager",
	    rowNum:12,
	    rowList:[10,12,30,50], 
		loadonce: true,
		cellEdit:false,
	    viewrecords: true,//定义是否在导航条上显示总的记录数
		multiselect : true,
		multiboxonly: true,
		gridComplete: function(){
			var grid=$("#myJqGrid")
			
			grid.find("span.file_title").unbind();
			grid.find("span.file_title").click(function(){
				setTimeout(function(){//防阻塞
					var key = grid.jqGrid("getGridParam","selrow"); //单条
					invockeServiceSync('busi.formsfiles.get.service',{tb_id:key}, function(data, isSucess){
						if(!isSucess || !data.formsfiles){
							return;
						}
						
						showDialog("formsfilesView.jsp","viewFormsfilesDialog",function(dialog){
							dialog.css('left','auto');
							
							dialog.find('.modal-body .form-control').text('');
							$('#tmpl_file_name').attr('href','#');
							$('#example_file_name').attr('href','#');

							loadDivData(data.formsfiles,dialog.find('.modal-body .tab-content'));
							var fileid = data.formsfiles.tmpl_file_identify || null;
							if(fileid){
								dialog.find('#tmpl_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
							}
							fileid = data.formsfiles.example_file_identify || null;
							if(fileid){
								dialog.find('#example_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
							}
						});
					});	
				},0);
			});
			
			grid.find("span.subversion").unbind();
			grid.find("span.subversion").click(function(){
				setTimeout(function(){
					var sub,
					key = grid.jqGrid("getGridParam","selrow"),
					currrow = grid.jqGrid('getGridRowById', key),
					minus  = $(currrow).find('span.subversion > i.fa-minus-square-o'),
					plus = $(currrow).find('span.subversion > i.fa-plus-square-o'),
					newRow;
					
					if(!currrow){
						return;
					}
					
					if(minus.length > 0){
						var rowData,parent_id=key;
						$(grid.jqGrid('getDataIDs')).each(function(){
							rowData = grid.jqGrid("getRowData",this);
							if(parent_id == rowData['parent_id']){
								parent_id = rowData['tb_id'];
								grid.jqGrid("delRowData", parent_id);
							}
						});
						$(currrow).find('span.subversion > i').removeClass('fa-minus-square-o').addClass('fa-plus-square-o');
					}
					
					if(plus.length > 0){
						invockeServiceSync('busi.formsfiles.subversion.query.service',{tb_id:key}, function(data, isSucess){
							if(!isSucess || !data.subversion){
								return;
							}
									
							for(var i=0; i<data.subversion.length; i++){
								sub = data.subversion[i];
//								newRow = {
//									tb_id:sub.tb_id,
//									parent_id:sub.parent_id,
//									tb_version:sub.tb_version,
//									file_title:sub.file_title,
//									document_number:sub.document_number,
//									fnsort_table:sub.fnsort_table,
//									retention_period:sub.retention_period,
//									ssecrecy_level:sub.ssecrecy_level,
//									archived_copies:sub.archived_copies,
//									total_pages:sub.total_pages
//								};
								grid.jqGrid("delRowData", sub.tb_id);
								grid.addRowData(sub.tb_id, sub,"after",key);
							}
							$(currrow).find('span.subversion > i').removeClass('fa-plus-square-o').addClass('fa-minus-square-o');
						});
					}
				},0);
			});
		}
  	}).navGrid('#myJqGridPager',
		{
			alertcap:"请选中您要操作的行",alerttext:"您还没有选中行，请选择后再重新操作！",
			edit: true, editicon : 'fa fa-pencil blue',edittitle:'编辑文件信息',
			add: true, addicon : 'fa fa-plus-circle purple',addtitle:'新建文件',
			del: true, delicon : 'fa fa-trash-o red',deltitle:'删除文件',
			search: true, searchicon : 'fa fa-search orange',searchtitle:'多条件查找',
			refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
			view: true, viewicon : 'fa fa-plug pink2',viewtitle:'关联条目',
			addfunc: function(){
				showDialog("formsfilesAdd.jsp","addFormsfilesDialog");
			},
			editfunc: function(){
				var grid = $('#myJqGrid');
				var keys = grid.jqGrid("getGridParam","selarrrow"); 
			    if(!keys || keys.length <= 0){
			    	xAlert("请选择要需要操作的行");
					return;
				}else if(keys.length > 1){
					xAlert("一次只能修改一项，请重新选择");
					return;
			    }else{
					var key = grid.jqGrid("getGridParam","selrow"); //单条
					invockeServiceSync('busi.formsfiles.get.service',{tb_id:key}, function(data, isSucess){
						if(!isSucess || !data.formsfiles){
							return;
						}
						
						showDialog("formsfilesUpdate.jsp",'updateFormsfilesDialog',function(dialog){
							loadDivData(data.formsfiles,dialog.find('.modal-body .widget-main'));
							var fileid = data.formsfiles.tmpl_file_identify || null, isrelated = data.formsfiles.isrelated;
							if(isrelated && isrelated * 1 > 0){
								$('#addNewFormsfilesButton').show();
							}
							else{
								$('#addNewFormsfilesButton').hide();
							}
							if(fileid){
								dialog.find('#tmpl_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
							}
							fileid = data.formsfiles.example_file_identify || null;
							if(fileid){
								dialog.find('#example_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
							}
						});
					});	
			    }
			},
			delfunc: function(){
				var grid = $('#myJqGrid');
				var keys = grid.jqGrid("getGridParam","selarrrow"); 
			    if(!keys || keys.length <= 0){
			    	xAlert("请选择要删除的行");
					return;
			    }
			    xConfirm("你确定要删除吗？",function(flag){
			    	if(!flag){
			    		return;
			    	}
			    	invockeServiceSync('busi.formsfiles.delete.service',{tb_id:keys}, function(data, isSucess){
						if(!isSucess){
							return;
						}
						xMsg("删除成功");
						var len = keys.length;  
						for(var i=0; i<len ;i++) {  
							grid.jqGrid("delRowData", keys[0]);  
						}
					});
				});
			},
			viewfunc:function(){
				var grid = $('#myJqGrid');
				var keys = grid.jqGrid("getGridParam","selarrrow"); 
			    if(!keys || keys.length <= 0){
			    	xAlert("请选择要需要操作的行");
					return;
				}else if(keys.length > 1){
					xAlert("一次只能管理一个条目，请重新选择");
					return;
			    }else{
					var key = grid.jqGrid("getGridParam","selrow"); //单条
					showDialog("formsfilesLibrary.jsp","formsfilesLibraryDialog");
			    }
			}
		},
		{}, // default settings for edit
		{}, // default settings for add
		{}, // delete instead that del:false we need this
		{
			closeOnEscape: true,
			closeAfterSearch: true,
			sopt :['cn','eq'],
			multipleSearch : true
		},// search options
		{} // view parameters
	).navButtonAdd("#myJqGridPager",{
		position:"last",
		title:"设置表格目录",
		caption:"",
		buttonicon:"fa fa-th-list blue",
		onClickButton: function(){
			var grid = $('#myJqGrid');
			var keys = grid.jqGrid("getGridParam","selarrrow"); 
		    if(!keys || keys.length <= 0){
		    	xAlert("请选择要操作的行");
				return;
		    }
		    showDialog("formsfilesDir.jsp","formsfilesDirDialog");
		}
	});

	setJqgridPageIcon();
	
	contextMenuFun = function(node) {
		var menuItems = $.jstree.defaults.contextmenu.items(),
		createMenu = menuItems.create,
		renameMenu = menuItems.rename,
		removeMenu = menuItems.remove;
		menu = {
			createItem: { 
				label: "创建类别",
				icon : "fa fa-plus purple",
				action: createMenu.action
		    },
		    renameItem: { 
		    	label: "修改名称",
		    	icon : "fa fa-pencil-square-o",
		    	action:renameMenu.action
		    },
		    deleteItem: { 
		    	label: "删除类别",
		    	icon : "fa fa-remove red",
		    	action: removeMenu.action,
		    	"separator_after": true
		    }
		};
		
		//menu['version'] = versionMenu(node);//版本发布
		return menu;
	};
	
	$('#formsfiles_tree').jstree({
        plugins: ["dnd","contextmenu","wholerow"],
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
    }).on('move_node.jstree', function (e, data) {
    	/**
		 * triggered when a node is moved
		 * @event
		 * @name move_node.jstree
		 * 
		 * @param {Object} node
		 * @param {String} parent the parent's ID
		 * @param {Number} position the position of the node among the parent's children
		 * @param {String} old_parent the old parent of the node
		 * @param {Number} old_position the old position of the node
		 * @param {Boolean} is_multi do the node and new parent belong to different instances
		 * @param {jsTree} old_instance the instance the node came from
		 * @param {jsTree} new_instance the instance of the new parent
		 */
    	xConfirm("你确定要移动吗？",function(flag){
        	if(!flag){
        		data.instance.refresh();
        		return;
        	}
        	var dt = {
        		dir_id:data.node.id,
    			parent_id:data.parent
        	};
        	invockeService('busi.formsfiles.dir.update.service', dt, function(data, isSucess){
    			if(!isSucess){
    				$('#formsfiles_tree').jstree('refresh');
    				return;
    			}
    			//data.instance.refresh();
    			var dirtree = $('#formsfiles_dirselect_tree').jstree(true);
    			if(dirtree){
    				dirtree.refresh();
    			}
    		});
    	});
	}).on('select_node.jstree',function(node,selected,event){//loaded.jstree ready.jstree 档案库加载完毕后加载表格
		dir_id = selected.node.id;
		$("#myJqGrid").jqGrid('setGridParam',{page:1,total:0,records:0}).trigger('reloadGrid');
	}).on('rename_node.jstree',function(node,event){
		var data = event.node.original;
		if(data && data.id){
			invockeService('busi.formsfiles.dir.update.service', {dir_id:data.id||"",dir_name:event.text}, function(retdata, isSucess){
				if(!isSucess){
					$('#formsfiles_tree').jstree('refresh');
					return;
				}
				$('#formsfiles_tree').jstree('refresh');
				var dirtree = $('#formsfiles_dirselect_tree').jstree(true);
				if(dirtree){
					dirtree.refresh();
				}
			});
		}
		else{
			var dt = {parent_id:event.node.parent=='#'?'':event.node.parent,dir_name:event.text};
			invockeService('busi.formsfiles.dir.add.service', dt, function(retdata, isSucess){
				if(!isSucess){
					$('#formsfiles_tree').jstree('refresh');
					return;
				}
				$('#formsfiles_tree').jstree('refresh');
				var dirtree = $('#formsfiles_dirselect_tree').jstree(true);
				if(dirtree){
					dirtree.refresh();
				}
			});
		}
	}).on('delete_node.jstree',function(node,event){
		if(!event.node.original || !event.node.original.id){
			return;
		}
		invockeService('busi.formsfiles.dir.delete.service', {dir_id:event.node.id}, function(retdata, isSucess){
			if(!isSucess){
				$('#formsfiles_tree').jstree('refresh');
				return;
			}
			$('#formsfiles_tree').jstree('refresh');
			var dirtree = $('#formsfiles_dirselect_tree').jstree(true);
			if(dirtree){
				dirtree.refresh();
			}
		});
	});/*.on('create_node.jstree',function(node,parent,position){
		alert(node);
	});*/
	
	$('#addDirButton').click(function(){//增加分类
		var ref = $('#formsfiles_tree').jstree(true),
		sel = ref.get_selected();
		if(!sel.length) { 
			sel = ref.create_node("#", "新建分类", "first" );
		}
		else{
			sel = sel[0];
			sel = ref.create_node(sel, "新建分类", "first");
		}
		
		if(sel) {
			ref.edit(sel);
		}
	});
	$('#refreshDirButton').on('click', function(){
		$('#formsfiles_tree').jstree('refresh');
	});
	$('#closeAllButton').on('click', function(){
		$('#formsfiles_tree').jstree('close_all');
	});
	
	$('[data-toggle="tooltip"]').tooltip({container:'body'});
});