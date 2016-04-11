<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="modal fade" id="printDocumentorModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog modal-lg"><!--  modal-lg -->
    <div class="modal-content"><!-- 页面显示背景 -->
      <div class="modal-header"><!-- 页眉 -->
        <button type="button" class="close" data-dismiss="modal" id="close"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title">打印预览</h4>
      </div>
 <div class="modal-body body" >
 
<div id="printWrite" >
<div style="font-size:22px;margin-top:10px;" align="center"><strong>西安地铁____号线资料员登记表</strong></div><br /><br />
	  <div style=" font-size:18px;" align='center'><span><strong>年　　月　　日</strong> </span> 　　　　　　　　　　　　　　　<span  style="font-size:15px;"><strong>NO:</strong></span></div>
	  <div style="margin-bottom:3px;"></div>
<div align="center" style="margin-right:-10px;">
<table height="820"   align="center" cellpadding="0" cellspacing="0" >
  <tr>
    <td width="70" height="50" align="center"  style="border:1px solid;">姓名</td>
    <td width="100" align="left" id="documentor_name_printDocumentor"  style="border:1px solid;border-left:0px;">&nbsp;</td>    
    <td width="80" align="center" style="border-top:1px solid;border-bottom:1px solid;border-right:1px solid;">身份证号</td>
    <td colspan="3" id="documentor_cardid_printDocumentor" align="left" style="border:1px solid;border-left:0px;">&nbsp;</td>
    <td width="117" colspan="2" rowspan="3" align="center" style="border-top:1px solid;border-right:1px solid;border-bottom:1px solid;">照片</td>
  </tr>
  <tr>
    <td height="50" align="center" style="border:1px solid;border-top:0px;">性别</td>
    <td id="documentor_sex_printDocumentor" align="left"  style="border:1px solid;border-left:0px;border-top:0px;" >　&nbsp;</td>
    <td align="center" style="border:1px solid;border-left:0px ;border-top:0px ;" >学历</td>
    <td width="80"  align="left" id="documentor_education_printDocumentor" class="four" style="border:1px solid;border-left:0px;border-top:0px ;">　&nbsp;</td>
    <td width="81" align="center" style="border:1px solid;border-left:0px;border-top:0px;">所学专业</td>
    <td width="100" align="left"  id="profession_printDocumentor"  style="border:1px solid;border-left:0px;border-top:0px;">　&nbsp;</td>
  </tr>
  <tr>
    <td height="50" align="center" style="border:1px solid;border-top:0px;">参加工<br />
    作时间</td>
    <td align="center" id="working_date_printDocumentor" style="border:1px solid;border-left:0px;border-top:0px;">　&nbsp;</td>
    <td align="center" style="border:1px solid;border-left:0px ;border-top:0px ;" >从事本专业<br />
    工作年限</td>
    <td  align="left" id="service_year_printDocumentor" style="border:1px solid;border-left:0px;border-top:0px ;">　&nbsp;</td>
    <td align="center" style="border:1px solid;border-left:0px;border-top:0px;">本人<br />联系电话</td>
    <td id="mobile_no_printDocumentor" align="center"  style="border:1px solid;border-left:0px;border-top:0px;">　&nbsp;</td>
  </tr>
  <tr>
    <td height="50" align="center" style="border:1px solid;border-top:0px;">单位名称</td>
    <td colspan="4" id="company_name_printDocumentor" align="left" style="border:1px solid;border-left:0px;border-top:0px;">　&nbsp;</td>
    <td align="center"  style="border:1px solid;border-left:0px ;border-top:0px ;">联系电话</td>
	<td colspan="4"   id="contact_phone_printDocumentor" align="left" style="border:1px solid;border-left:0px;border-top:0px ;">　&nbsp;</td>
  </tr>
  <tr>
    <td height="50" align="center"  style="border:1px solid;border-top:0px;">标段代号</td>
    <td align="center" id="tenders_code_printDocumentor"   style="border:1px solid;border-left:0px;border-top:0px;">　&nbsp;</td>
    <td align="center"  style="border:1px solid;border-left:0px ;border-top:0px ;">合同名称</td>
    <td colspan="5"  id="contract_name_printDocumentor" align="left"   style="border:1px solid;border-left:0px;border-top:0px ;">&nbsp;</td>
  </tr>
  <tr>
    <td height="100" align="center" style="border:1px solid;border-top:0px;" >所属标段<br />
    单位工程<br />名称</td>
    <td colspan="7"style="height: 200px;border:1px solid;border-left:0px;border-top:0px;"  >
     <table id="project_addname_printDocumentor" style="width: 100%;">
     </table>
    </td>
  </tr>
  <tr>
    <td height="100" colspan="4" align="center" valign="top" style="border:1px solid;border-top:0px;"><br />
      施工单位意见<br /><br /><br /><br /><br />
      (公章)
      
      <br /><br />
	<div align="right">年　　月　　日</div></td>
    <td height="100" colspan="4" align="center" valign="top"   style="border:1px solid;border-left:0px;border-top:0px;"><br />
      监理单位意见<br /><br /><br /><br /><br />
      (公章)
      
      <br /><br />
	<div align="right">年　　月　　日</div></td>
  </tr>
  <tr>
    <td height="100" colspan="4"  align="center" valign="top"  style="border:1px solid;border-top:0px;"><br />
    <span id="edit_division_idea">工程处审定意见</span><br /><br /><br /><br /><br />
      (公章)
      <br />
      <br />
	<div align="right">年　　月　　日</div></td>
    <td height="100" colspan="4" align="center" valign="top "  style="border:1px solid;border-left:0px;border-top:0px;"><br />
      信息中心审定意见<br /><br /><br /><br /><br />
      (公章)
      <br /><br />
	<div align="right">年　　月　　日</div></td>
  </tr>
</table>
</div>
</div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" id="printDocumentor_button">打印</button>
      </div><br><br><br>
    </div>
  </div>
</div>
</div>