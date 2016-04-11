//打印
jQuery(function($) {
	$("#printDocumentor_button").click(function(){
		var HKEY_Root,HKEY_Path,HKEY_Key;
		HKEY_Root="HKEY_CURRENT_USER";
		HKEY_Path="\\Software\\Microsoft\\Internet Explorer\\PageSetup\\";
		//设置网页打印的页眉页脚为空
		function PageSetup_Null(){
			try{
			var Wsh=new ActiveXObject("WScript.Shell");
			HKEY_Key="header";
			Wsh.RegWrite(HKEY_Root+HKEY_Path+HKEY_Key,"");
			HKEY_Key="footer";
			Wsh.RegWrite(HKEY_Root+HKEY_Path+HKEY_Key,"");
			}
			catch(e)
			{}
		} 
		PageSetup_Null();//调用
		
		$("#edit_division_idea").prepend($("#edit_division_idea_select").val());
		$("#edit_division_idea_select").remove();
		
		var newstr = $("#printWrite").html();
		if (!!newstr && newstr.length > 0){
			  var newwin=window.open('','_blank');  
			  newwin.document.write('<html><body>'+newstr+'</body></html>');
			  newwin.document.location.reload();
			  newwin.print();
			  newwin.close();
		}
		return;
	});
	
	$("#edit_division_idea").dblclick(function(){
		invockeServiceSync("pklist.sys_data_dictionary.service",{datavalue:'construction_division_idea'},function(data,isSuccess){
			if(!isSuccess){
				return;
			}
			var $pklist = $("<select id='edit_division_idea_select'></select>");
			$(data.pklist).each(function() {
					$pklist.append("<option value='"+this.value+"'>"+this.text+"</option>"); 
			});
			$("#edit_division_idea").text("").append($pklist);
		});
	});
});


