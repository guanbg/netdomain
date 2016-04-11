<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="formsfilesDirDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
  <div class="modal-dialog"><!--  modal-lg -->
    <div class="modal-content">
      <div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
      	<a href="#" tabindex="-1" class="pull-right" style="padding:6px;" data-toggle="tooltip" data-placement="bottom" data-original-title="刷新分类" id='refreshDirDialogButton'><i tabindex="-1" class="fa fa-refresh green hand padding-4"></i></a>
        <h4 class="modal-title text-center">请选择表格目录</h4>
      </div>
      <div class="modal-body">
      	<div class="widget-main padding-8 no-padding-left no-padding-right" style="padding-bottom:0px;">
      		<div id="formsfiles_dirselect_tree"></div>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button type="button" class="btn btn-primary" id="saveFormsfilesDirButton">确定</button>
      </div>
    </div>
  </div>
</div>