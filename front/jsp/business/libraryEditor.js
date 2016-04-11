/**
 * 业务数据配置
 */
jQuery(function($){
	invockeService('busi.library.parents.query.service', {id:getLibId()}, function(data, isSucess){
		if(!isSucess){
			return;
		}
		
		if(data && data.rows){
			var getClass = function(idType){
				if(idType == "0"){
					return "label-purple";
				}
				else if(idType == "1"){
					return "label-success";
				}
				else if(idType == "2"){
					return "label-info";
				}
				else if(idType == "3"){
					return "label-pink";
				}
				else if(idType == "4"){
					return "label-warning";
				}
				else if(idType == "5"){
					return "label-grey";
				}
				return "label-inverse";
			};
			for(var i=0; i<data.rows.length; i++){
				if(i == 0){
					$('#libraryPathName').append("<a href='libraryEditor.jsp?id="+data.rows[i].fs_id+"'><span class='label "+getClass(data.rows[i].id_type)+" arrowed-right'>"+data.rows[i].fs_name+"</span></a>");
				}
				else{
					$('#libraryPathName').append("<a href='libraryEditor.jsp?id="+data.rows[i].fs_id+"'><span class='label "+getClass(data.rows[i].id_type)+" arrowed-right arrowed-in'>"+data.rows[i].fs_name+"</span></a>");
				}
			}
			document.title = data.rows[data.rows.length-1].fs_name || "轨道建设领域企业综合管理平台";
		}
	});
	
	linkRenderer = function(instance, td, row, col, prop, value, cellProperties) {
		  Handsontable.renderers.TextRenderer.apply(this, arguments);
		  if(prop == 'fs_id' && null != value && !!value && (instance.getDataAtRowProp(row,'children') || 0)*1 > 0){
			  td.innerHTML = '<a href="libraryEditor.jsp?id='+value+'">'+value+'</a>';
		  }
		  //td.style.background = 'gray';
	};	
	centerRenderer = function(instance, td, row, col, prop, value, cellProperties) {
		  Handsontable.renderers.TextRenderer.apply(this, arguments);
		  //td.style.backgroundColor = '#EEE';
		  $(td).css({
			  "text-align": "center"
		  });

	};
	firstRowRenderer = function(instance, td, row, col, prop, value, cellProperties) {
		Handsontable.renderers.TextRenderer.apply(this, arguments);
		td.style.fontWeight = 'bold';
		td.style.color = 'green';
		td.style.background = '#CEC';
	};
	function guid() {
	    function S4() {
	       return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
	    }
	    return (S4()+S4()+ S4()+S4()+ S4()+ S4()+S4()+S4());
	}
	var updateID = [],deleteID = [],
	addUpdateID = function(id){
		for(var i=0; i<updateID.length; i++){
			if(updateID[i] == id){
				return;
			}
		}
		updateID.push(id);
	},
	addDeleteID = function(id){
		for(var i=0; i<deleteID.length; i++){
			if(deleteID[i] == id){
				return;
			}
		}
		deleteID.push(id);
	},
	saveChange = function(){
        var row,rw,add=[],upd=[],del=[];
		for(var i=0; i<excel.countRows(); i++){
			if(excel.isEmptyRow(i)){
				continue;
			}
			row = excel.getDataAtRow(i);
			rw = {parent_id:getLibId()};
			for(var j=0; j<row.length; j++){
				rw[excel.colToProp(j)] = row[j] || '';
			}
    		
			for(var j=0; j<deleteID.length; j++){
				if(deleteID[j] == rw['fs_id']){
					deleteID.splice(j,1);
				}
			}
			
			if(rw['fs_id']){
				for(var j=0; j<updateID.length; j++){
					if(updateID[j] == rw['fs_id']){
						upd.push(rw);
						break;
					}
				}
			}
			else if(rw['fs_code']&&rw['fs_name']){
				rw['fs_id']=guid();
				add.push(rw);
			}
		}
		
		for(var i=0; i<deleteID.length; i++){
			del.push({id:deleteID[i]});
		}
		
		if(add.length <= 0 && upd.length <= 0 && del.length <= 0){
        	return;
        }
        
        invockeService('busi.library.save.service', {add:add,upd:upd,del:del}, function(data, isSucess){
    		if(!isSucess){
    			return;
    		}
    		xMsg('保存成功');
    	});
	},
	getWidth = function(n,p){
		var winWidth = $(window).width(), nWidth = Math.floor((winWidth-p||0) * n / 100);
		return nWidth > 20?nWidth:20;
	};
	
	
	var excel = new Handsontable(document.getElementById('libraryEditor'),{
		//data:getData(),
		manualColumnMove: true,
		manualColumnResize: true,
		manualRowMove: true,
		manualRowResize: true,
		manualColumnFreeze: true,
		headerTooltips: true,
		persistentState: true,
		startRows: 1,
		minSpareRows: 0,
		//fixedRowsTop: 1,
		fixedColumnsLeft: 1,
		//stretchH: 'all',
		currentRowClassName: 'currentRow',
		currentColClassName: 'currentCol',
		contextMenu: ['row_above', 'row_below', 'remove_row','undo','redo','make_read_only'],
		autoWrapRow: true,
		//autoWrapCol: true,
		columnSorting: true,
		rowHeaders: true,
		colHeaders: true,
		//colWidths:[10,10,10,10,15],
		colHeaders: ["ID", "节点类别", "案卷类别", "次序", "编号", "名称", "备注"],
		columns: [
		    {data: "fs_id", width: 40, type: 'text', readOnly: true},
		    {data: "id_type", width: 40, type: 'dropdown',source: ['1', '2', '3', '4', '5'],renderer: centerRenderer},
		    {data: "fs_name_code", width: 40, type: 'text'},
		    {data: "disp_order", width: 40, type: 'numeric',renderer: centerRenderer},
		    {data: "fs_code", width: 40,type: 'text',renderer: centerRenderer},
		    {data: "fs_name", width: getWidth(60,40*5),type: 'text'},
		    {data: "fs_memo", width: getWidth(40,40*5),type: 'text'}
		],
		cells: function (row, col, prop) {
			/*if(col === 8){
				this.renderer = statusRenderer;
			}
			elseif (col === 0) {
				var cellProperties = {};
				cellProperties.readOnly = true;
				return cellProperties;
			}*/
			if (prop === "fs_id") {
				var cellProperties = {};
				cellProperties.readOnly = true;
				cellProperties.manualColumnMove = false;
				cellProperties.renderer = linkRenderer;
				return cellProperties;
			}
			else if (prop === "id_type") {
				return {readOnly:!!this.instance.getDataAtCell(row,col)};
			}
		},
		afterChange: function (change, source) {
		    if (source === 'loadData') {
		        return; //don't save this change
		    }
		    var row,rw;
		    for(var i=0; i<change.length; i++){
		    	row = excel.getDataAtRow(change[i][0]);
		    	rw = {};
		    	for(var j=0; j<row.length; j++){
					rw[excel.colToProp(j)] = row[j] || '';
				}
	    		
		    	addUpdateID(rw['fs_id']);
		    }
		},
		beforeRemoveRow:function(start, amount, rowids){
			var row,rw;
			for(var i=0; i<rowids.length; i++){
				row = excel.getDataAtRow(rowids[i]);
				rw = {parent_id:getLibId()};
				for(var j=0; j<row.length; j++){
					rw[excel.colToProp(j)] = row[j] || '';
				}
	    		
				addDeleteID(rw['fs_id']);
			}
		},
		beforeKeyDown: function (event) {
            if ((event.ctrlKey || event.metaKey) && event.keyCode == 'S'.charCodeAt(0)) {
                event.stopImmediatePropagation();
                event.stopPropagation();
                event.preventDefault();
                
                xConfirm("你确定要立刻保存更改吗？",function(flag){
                	if(!flag){
                		return;
                	}
                	saveChange();
                });
                
                return false;
            }
            else if((event.ctrlKey || event.metaKey) && event.keyCode == 'I'.charCodeAt(0)){//插入行
            	event.stopImmediatePropagation();
                event.stopPropagation();
                event.preventDefault();
                
                var sel = excel.getSelected();
            	excel.alter('insert_row',sel[2]+1);
            	
            	return false;
            }
            else if((event.ctrlKey || event.metaKey) && event.keyCode == 'D'.charCodeAt(0)){//删除行
            	event.stopImmediatePropagation();
                event.stopPropagation();
                event.preventDefault();
                
                var sel = excel.getSelected();
                for(var i=sel[0]; i<=sel[2]; i++){
                	excel.alter('remove_row',sel[0]);
                }
            	
            	return false;
            }
        }
	});
	
	excel.updateSettings({
		contextMenu: {
			callback: function (key, options) {
			      if (key === 'save') {
			    	  xConfirm("你确定要立刻保存更改吗？",function(flag){
			    		  if(!flag){
			    			  return;
			    		  }
			    		  saveChange();
			    	  });
			      }
			      else if(key === 'refresh'){
			    	  location.reload(true);
			      }
			},
			items: {
				"row_above": {
					name: '前面新增行'
				},
				"row_below": {
					name: '后面新增行(Ctrl+I)'
				},
				"hsep1": "---------",
				"remove_row": {
					name: '删除选中行(Ctrl+D)'
				},
				"hsep2": "---------",
				"undo": {
					name: '撤销(Ctrl+Z)'
				},
				"redo": {
					name: '恢复(Ctrl+Y)'
				},
				"make_read_only": {
					name: '只读'
				},
				"refresh": {
					name: '刷新'
				},
				"hsep3": "---------",
				"save": {
					name: '保存(Ctrl+S)'
				}
			}
		}
	});
	
	excel.addHook('beforeAutofill', function(start,end,data){
		if(start.col == end.col && (excel.colToProp(start.col) == 'fs_code' || excel.colToProp(start.col) == 'fs_name_code' || excel.colToProp(start.col) == 'disp_order')){
			var prefix = "", surfix = "", m,n,dt,str = data[data.length-1]+"";
			if(!isNaN(str)){
				surfix = parseInt(str,10)+"";
			}
			else{
				for(var i=str.length-1; i>=0; i--){
					if(!isNaN(str[i])){
						surfix = str[i]+surfix;
					}
					else{
						prefix = str.substr(0,i+1);
						break;
					}
				}
			}
			if(surfix.length > 0){
				data.splice(0,data.length);
				
				for(var i=start.row; i<=end.row; i++){
					n = parseInt(surfix,10)+i-(start.row)+1;
					m = surfix.length - (n+"").length;
					if(m > 0){
						n = surfix.substr(0,m) +""+ n;
					}
					dt = prefix+""+n;
					if(!isNaN(dt)){
						data.push([dt*1]);
					}
					else{
						data.push([dt]);
					}
				}
			}
			return false;
		}
		else{
			return true;
		}
	});
	
	
	invockeService('busi.library.query.service', {id:getLibId()}, function(data, isSucess){
		if(!isSucess){
			return;
		}
		if(data && data.library){
			excel.loadData(data.library);
		}
	});
});