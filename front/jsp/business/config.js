/**
 * 业务数据配置
 */
jQuery(function($){
	checkChildWindow();
	
	var statusCellsRenderer = function (row, dataField, cellValue, rowData, cellText){
		var status = rowData[dataField];
        if (status == '1') {
            return "<span style='color: red;'>未使用</span>";
        }
        else{
        	return "使用中";
        }
	},
	memoCellsRenderer = function (row, dataField, cellValue, rowData, cellText){
		var memo = rowData[dataField];
        if (memo) {
            return "<span class='nowrap' title='"+memo+"'>"+memo+"</span>";
        }
        else{
        	return "";
        }
	},
	identityCellsRenderer = function (row, dataField, cellValue, rowData, cellText){
		var identity = rowData[dataField];
        if (identity) {
            return "<span class='nowrap' title='"+identity+"'>"+identity+"</span>";
        }
        else{
        	return "";
        }
	};
	$(window).bind("scroll",function(){ //支持ie8
		var header = $('#contentmyTreeGrid>.jqx-widget-header');
		var scrollTop = $(document).scrollTop();
		if(scrollTop > 20){
			if(!header.hasClass('jqx-widget-header-fixed')){
				header.addClass('jqx-widget-header-fixed');
			}
		}
		else{
			header.removeClass('jqx-widget-header-fixed');
		}
	});	
	var setTDWidth = function(){
		$('#tablemyTreeGrid tr').find('td.jqx-grid-cell:not(.xwidth):first').each(function(){
			//$(this).addClass('xwidth');
			
    		var $td = $(this),$spans = $td.find('>span'),$titlespan= $td.find('>span.jqx-tree-grid-title'),w=$td.width(),m=($spans.length-2)*14,mx=w-Math.abs(m);
    		if(m <= 0){
    			return;
    		}
    		if(m > 0 && mx > 0 && $titlespan.width() > mx){
    			$titlespan.width(mx);
    		}
    	});
	};
	
	$("#myTreeGrid").jqxTreeGrid({
		width: '100%',
		//pageable: true,
        //pagerMode: 'advanced',
		//pagerMode: 'advanced',
        //pageSizeMode: 'root',
        //pageSize: 2,
        //pageSizeOptions: ['2', '3', '4'],
        //filterable: true,
        //filterMode: 'advanced',
		altRows: true,
		scrollBarSize:0,
		//checkboxes: true,
		columnsResize: true,
		//showToolbar: true,
		//editable: true,
		//editmode:'programmatic',
		//selectionmode: 'singlerow',
		virtualModeCreateRecords: function (expandedRecord, done) {
			// expandedRecord is equal to null when the function is initially called, because there is still no record to be expanded.

			// prepare the data
			var source = {
				dataType: "json",//json
				dataFields: [
				    {name: "id", type: "string"},
				    {name: "parentid", type: "string"},
				    {name: "identity", type: "string"},
				    {name: "nodetype", type: "string"},
				    {name: "disptype", type: "number"},
				    {name: "disporder", type: "number"},
				    {name: "haschild", type: "number"},
				    {name: "status", type: "number"},
				    {name: "code", type: "string"},
				    {name: "name", type: "string"},
				    {name: "memo", type: "string"},
				    {name: "value1", type: "string"},
				    {name: "value2", type: "string"},
				    {name: "value3", type: "string"},
				    {name: "value4", type: "string"},
				    {name: "value5", type: "string"},
				    {name: "extend", type: "string"}
				],
				id: 'id',
				hierarchy:{
	                keyDataField:{name: 'id'},
	                parentDataField:{name: 'parentid'}
	            },
				localData: function(){
					var dt = {};
					if(expandedRecord){
						dt['id'] = expandedRecord['id'] || '';
					}
					var treedata = invockeServiceSync('busi.config.query.service', dt);
					return treedata.rows || null;
				}//expandedRecord === null ? function(){return data;} :  function(){return data2;}
			};
			var dataAdapter = new $.jqx.dataAdapter(source, {
				loadComplete: function() {
					done(dataAdapter.records);
					
					setTDWidth();
				}
			});
			dataAdapter.dataBind();
		},
		virtualModeRecordCreating: function (record) {
			record['leaf'] = true;
			if ((record['haschild'] || 0) * 1 > 0) {
				// by setting the record's leaf member to true, you will define the record as a leaf node.
				record['leaf'] = false;
			}
		},
		columns: [
		    {text: '名称', dataField: "name", align: 'center', width: 300 },
		    {text: '代码', dataField: "code", cellsAlign: 'center', align: 'center', width: 50 },
		    {text: '显示代码', dataField: "value1", cellsAlign: 'center', align: 'center', width: 60},
		    {text: '年限', dataField: "value2", cellsAlign: 'center', align: 'center', width: 50},
		    {text: '页数', dataField: "value3", cellsAlign: 'center', align: 'center', width: 50},
		    {text: '次序', dataField: "disporder", cellsAlign: 'center', align: 'center', width: 50},
		    {text: '标识', dataField: "identity", cellsAlign: 'left', align: 'center', width: 190,cellsRenderer: identityCellsRenderer},
		    {text: '状态', dataField: "status", cellsAlign: 'center', align: 'center', width: 60,cellsRenderer: statusCellsRenderer},
		    {text: '备注', dataField: "memo", cellsAlign: 'left', align: 'center',cellsRenderer: memoCellsRenderer},
		    {text:"扩展", dataField:"extend", cellsAlign:"center", align:"center", hidden:true},
		    {text: 'ID', dataField: 'id', editable: false, hidden:true}
		]
	});
	
	// create context menu
    var contextMenu = $("#myMenu").jqxMenu({ width: 160, autoOpenPopup: false,autoCloseOnClick:true, mode: 'popup', theme: 'summer' });
    
    $("#myTreeGrid").on('rowExpand', function () {
    	setTDWidth();
    });
    
    $("#myTreeGrid").on('contextmenu', function () {
        return false;
    });
    
    $("#myTreeGrid").on('click', function (event) {
    	;//无数据时显示右键菜单
    });
    
    $("#myTreeGrid").on('rowClick', function (event) {
        var args = event.args;
        if (args.originalEvent.button == 2) {
            var scrollTop = $(window).scrollTop();
            var scrollLeft = $(window).scrollLeft();
            contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + scrollLeft, parseInt(event.args.originalEvent.clientY) + scrollTop);
            return false;
        }
        else{
        	contextMenu.jqxMenu('close');
        }
    });
    
    var copy_node_id = null,copy_node_name = null;
    $('#myMenu').on('shown', function (event) {
        var grid = $("#myTreeGrid"), selection = grid.jqxTreeGrid('getSelection'), row = selection[0], rowid = row.uid;
        if(/*(row['code'] && (row['code']+"").length > 0 && row['nodetype'] != '1')*/ !row['parentid'] || row['nodetype'] == '3'){//隐藏创建版本菜单
        	$("#myMenu li[data-xtype='version']").hide();
        }
        else{
        	$("#myMenu li[data-xtype='version']").show();
        }
        
        if(copy_node_id){
        	$("#myMenu li[data-xtype='paste']").show();
        }
        else{
        	$("#myMenu li[data-xtype='paste']").hide();
        }
        
        if(row['leaf']){
        	$("#myMenu li[data-xtype='update_batch']").hide();
        }
        else{
        	$("#myMenu li[data-xtype='update_batch']").show();
        	$("#myMenu li[data-xtype='update_batch']").html("<a href='configEditor.jsp?id="+rowid+"' target='_blank' style='width:140px;display:block;'>批量修改</a>");
        }
    });
    
    $("#myMenu").on('itemclick', function (event) {
        var grid = $("#myTreeGrid"), selection = grid.jqxTreeGrid('getSelection'), row = selection[0], rowid = row.uid;
        var args = event.args, xtype = $.trim($(args).data('xtype'));
        
        if(!row['status'] || (row['status']+"").length <= 0){//修改的时候显示状态
        	row['status'] = '0';
        }
        
        if (xtype == "add_top") {//新增同级
        	grid.jqxTreeGrid('clearSelection');
        	grid.jqxTreeGrid('selectRow', rowid);
        	
        	showDialog("configAdd.jsp","configAddDialog",function(dialog){
        		if(row['parentid']){
        			var prow = grid.jqxTreeGrid('getRow', row['parentid']);
        			$('#parent_name_add').text(prow['name'] || '');
        			$('#parentid_add').val(row['parentid']);
        		}
        	});
        }
        else if (xtype == "add") {//新增下级
        	grid.jqxTreeGrid('clearSelection');
        	grid.jqxTreeGrid('selectRow', rowid);
        	
        	showDialog("configAdd.jsp","configAddDialog",function(dialog){
        		$('#parent_name_add').text(row['name']);
        		$('#parentid_add').val(row['id']);
        	});
        }
        else if (xtype == "copy") {//复制
        	grid.jqxTreeGrid('clearSelection');
        	grid.jqxTreeGrid('selectRow', rowid);
        	
        	copy_node_id= rowid;
        	copy_node_name = row['name'];
        }
        else if (xtype == "paste") {//粘贴
        	if(!copy_node_id){
        		xAlert("请先复制后再粘贴！");
        		return;
        	}
        	xConfirm("是否将参数["+copy_node_name+"]粘贴到["+row['name']+"]？",function(flag){
            	if(!flag){
            		return;
            	}
            	grid.jqxTreeGrid('clearSelection');
            	grid.jqxTreeGrid('selectRow', rowid);
            	
            	invockeServiceSync('busi.config.paste.add.service', {copy_id:copy_node_id,paste_id:rowid}, function(data, isSucess){
        			if(!isSucess){
        				return;
        			}
        			xMsg("粘贴成功");
        			
        			if(confirm("粘贴成功,是否立刻刷新数据？")){
        				$("#myTreeGrid").jqxTreeGrid('clear');
        				$("#myTreeGrid").jqxTreeGrid('updateBoundData');
        			}
        		});
        	});
        }
        else if (xtype == "update") {//修改
        	grid.jqxTreeGrid('clearSelection');
        	grid.jqxTreeGrid('selectRow', rowid);
        	
        	showDialog("configUpdate.jsp","configUpdateDialog",function(dialog){
        		if(row['parentid']){
        			var prow = grid.jqxTreeGrid('getRow', row['parentid']);
        			$('#parent_name_update').text(prow['name'] || '');
        		}
        		
        		$('#id_update').val(row['id']);
        		loadDivData(row,dialog.find('.modal-body .tab-content'),function(name,val){return name+'_update';});
        	});
        }
        else if (xtype == "update_batch") {//批量修改
        	grid.jqxTreeGrid('clearSelection');
        	grid.jqxTreeGrid('selectRow', rowid);
        	
        	select_node_id = rowid;
        	select_node_name = row['name'];
        }
        else if (xtype == "delete") {//删除
        	xConfirm("你确定要删除此行信息吗？",function(flag){
            	if(!flag){
            		return;
            	}
            	invockeServiceSync('busi.config.delete.service', {id:rowid}, function(data, isSucess){
        			if(!isSucess){
        				return;
        			}
        			xMsg("删除业务参数成功");
                	grid.jqxTreeGrid('deleteRow', rowid);
        		});
        	});
        }
        else if (xtype == "version") {//创建版本
        	grid.jqxTreeGrid('clearSelection');
        	grid.jqxTreeGrid('selectRow', rowid);
        	
        	showDialog("configVersion.jsp","configVersionDialog",function(dialog){
        		if(row['nodetype'] == '1'){
        			$('#parent_name_version').text(row.parent['name']);
            		$('#id_version').val(row['parentid']);
        			$("#base_version").empty();
        			$("#base_version").append("<option value='"+row['id']+"'>"+row['code']+"-"+row['name']+"</option>");
        		}
        		else{
        			$('#parent_name_version').text(row['name']);
            		$('#id_version').val(row['id']);
        			loadPKList({svrName:"pklist.fms_config.version.service",svrData:{id:row['id']}},"base_version",false,false);
        		}
        	});
        }
        else if (xtype == "import_top") {//导入同级
        	grid.jqxTreeGrid('clearSelection');
        	grid.jqxTreeGrid('selectRow', rowid);
        	
        	if(typeof(import_file_upload) == "undefined"){
        		import_file_upload = new singleFileUpload(function(data){
        			$('#import_file_id').val("");
        			$('#import_file_name').text("");
        			if(!data || !data.files){
        				return false;
        			}
        			
        			var file = data.files[0];
        			$('#import_file_id').val(file.fileidentify);
        			$('#import_file_name').text(file.filename);
        			xMsg("文件上传完毕");
        		});
        	}
        	showDialog("configImport.jsp","configImportDialog",function(dialog){
        		if(row['parentid']){
        			var prow = grid.jqxTreeGrid('getRow', row['parentid']);
        			$('#parent_name_update').text(prow['name'] || '');
        		}
        	});
        }
        else if (xtype == "import") {//导入下级
        	grid.jqxTreeGrid('clearSelection');
        	grid.jqxTreeGrid('selectRow', rowid);
        	
        	if(typeof(import_file_upload) == "undefined"){
        		import_file_upload = new singleFileUpload(function(data){
        			$('#import_file_id').val("");
        			$('#import_file_name').text("");
        			if(!data || !data.files){
        				return false;
        			}
        			
        			var file = data.files[0];
        			$('#import_file_id').val(file.fileidentify);
        			$('#import_file_name').text(file.filename);
        			xMsg("文件上传完毕");
        		});
        	}
        	showDialog("configImport.jsp","configImportDialog",function(dialog){
        		$('#parent_name_import').text(row['name']);
        		$('#parentid_import').val(row['id']);        		
        	});
        }
    });
});