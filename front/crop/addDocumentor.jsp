<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	String ctx = request.getContextPath();
%>

<div class="modal fade" id="addDocumentorModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog modal-xm"><!--  modal-lg -->
    <div class="modal-content"><!-- 页面显示背景 -->
      <div class="modal-header"><!-- 页眉 -->
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
        <h4 class="modal-title">新增资料员信息</h4>
      </div>
      <div class="modal-body">
      	<div class="widget-main padding-10 no-padding-left no-padding-right" style="padding-bottom:0px;">
	      	<div class="tab-content padding-0 margin-right-20" >
	      	<div class="space-4"></div>
	      	<div width="100%">
				<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
				  <tr>
				    <td align="right" class="padding-bottom-10" >姓名：</td>
				    <td width="70%" class="padding-bottom-10" ><input type="text" id="documentor_name_addDocumentor" class="form-control" placeholder="请输入姓名"  data-validation-required-message="资料员姓名必填"  maxlength="7" ></td>
				  </tr>
				   <tr>
				  <td align="right" class="padding-bottom-10">资料员联系电话：</td>
				    <td class="padding-bottom-10"><input type="text" id="mobile_no_addDocumentor" class="form-control" placeholder="请输入联系电话" data-validation-required-message="联系电话必填" maxlength="11"></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">资料员邮箱：</td>
				    <td class="padding-bottom-10"><input type="text" id="email_addDocumentor" class="form-control" placeholder="请输入个人邮箱" data-validation-required-message="资料员邮箱必填" ></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">标段代号：</td>
				    <td class="padding-bottom-10"><input type="text" id="tenders_code_addDocumentor" class="form-control" placeholder="请输入标段代号" data-validation-required-message="标段代号必填" maxlength="20"></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">单位名称：</td>
				    <td class="padding-bottom-10"><input type="text" id="company_name_addDocumentor" class="form-control" readonly="readonly"></td>
				  </tr>
				  <tr>
				    <td align="right" class="padding-bottom-10">单位联系电话：</td>
				    <td class="padding-bottom-10"><input type="text" id="contact_phone_addDocumentor" class="form-control" readonly="readonly"></td>
				  </tr>
                </table>
				</div>
			</div>
      	</div>
      </div>
      <div class="space-4"></div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" id="addDocumentor_button">新增</button>
      </div>
    </div>
  </div>
</div>
<script src="<%=ctx%>/js/bootstrap-tagsinput.min.js"></script>