/*
 * 系统消息
 */
jQuery(function($) { sysMsgInit = function(){//初始化函数
	$("#sysmsg_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('div:first i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#sysmsg_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('div:first i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	
	$('.padding-left-only-15').on('shown.bs.collapse hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a').toggleClass('active');
	});
	$('#sysmsg_collapse_all').hide();
	$('#sysmsg_expand_all').click(function(){
		$('.panel-collapse',$('#sysmsg_collapse')).addClass('in');
		$('.panel-heading>a',$('#sysmsg_collapse')).addClass('active');
		$('#sysmsg_expand_all').hide();
		$('#sysmsg_collapse_all').show();
	});
	$('#sysmsg_collapse_all').click(function(){
		$('.panel-collapse',$('#sysmsg_collapse')).removeClass('in');
		$('.panel-heading>a',$('#sysmsg_collapse')).removeClass('active');
		$('#sysmsg_collapse_all').hide();
		$('#sysmsg_expand_all').show();
	});
	
	var msg_tmpl = "<div class='panel panel-user-defined panel-default' id='msg_row{msg_id}_accordion'>"+
					   "<div class='panel-heading panel-heading-user-defined panel-heading-dashed'>"+
					      "<a id='a_msg_title{receive_id}' title='{msg_title}' data-toggle='collapse' data-parent='#msg_row{msg_id}_accordion' data-receive_id='{receive_id}' href='#msg_row{msg_id}_collapse' onclick='{a_click}'>"+
					      "<i id='i_msg_title{receive_id}' class='hand glyphicon glyphicon-{msg_class}' data-toggle='tooltip' data-placement='left'></i>"+   
					      "<span id='span_msg_title{receive_id}' style='margin-left:10px;font-weight:{msg_class_a_font};' >{msg_title}</span>"+
					      "</a>"+
					      "<span class='float-right' style='color:#999'>{send_user_name} 　　{send_time}</span>"+
					   "</div>"+
					   "<div class='panel-body panel-collapse collapse ' id='msg_row{msg_id}_collapse' style='padding: 10px 5px 5px 5px !important;line-height:150%;text-indent: 30px;'>"+
					      	"<p>{msg_text}</p>"+
					   "</div>"+
					"</div>";
	
	var contractor_id =getContractorId();
	if(getDocumentorId()!=null && getDocumentorId()!=''){
		invockeServiceSync('crop.documentor.get.service', {documentor_id:getDocumentorId()}, function(data, isSucess){
			if(!isSucess){
				return;
			}
			if(data.documentor['contractor_id']!=null && data.documentor['contractor_id']!=""){
		      contractor_id = data.documentor['contractor_id'];
			}
		});
	}
	getsysmsgData = function() {
		invockeService('msg.querybyuser.service', {contractor_id:contractor_id}, function(data,isSuccess){
			if(!isSuccess){
				return;
			}
			$(data.rows).each(function(){
				if(this.read_time && this.read_time.length > 0){
					this['msg_class'] = 'file gray';
					this['msg_class_a_font'] = 'nomal';
					this['a_click'] = 'javascript:void(0);';
				}
				else{
					this['msg_class'] = 'envelope green';
					this['msg_class_a_font'] = 'bold';
					this['a_click'] = 'javascript:viewmsg('+this.receive_id+');';//未读的消息独有事件
				}
				$('#sysmsg_collapse>div.panel-body').append(parserTemplate(msg_tmpl,this));
			});
			//$('#sysmsg_collapse>div.panel-body a').click(viewmsg);//读消息事件
			$('[data-toggle="tooltip"]').tooltip({container:'body'});
		});
	};
	
	viewmsg = function(receive_id){
		invockeServiceSync('msg.updbyuser.service', {receive_id:receive_id}, function(data, isSucess){
			if(!isSucess){
				return;
			}else{
				$('#span_msg_title'+receive_id).css('font-weight',100);//去掉粗体
				$('#i_msg_title'+receive_id).attr("class", "hand glyphicon glyphicon-file gray");//图标改版
				$('#a_msg_title'+receive_id).removeAttr("onclick");//移除首次阅读事件
				readMsg(contractor_id);
			}
		});
	};
	getsysmsgData();//加载初始数据
}});