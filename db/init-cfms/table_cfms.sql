/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2016/3/18 17:58:23                           */
/*==============================================================*/


drop table if exists fms_audit_record;

drop table if exists fms_businessrule;

drop table if exists fms_businessrule_clause;

drop table if exists fms_businessrule_version_items;

drop table if exists fms_config;

drop table if exists fms_config_import;

drop table if exists fms_config_version;

drop table if exists fms_config_version_items;

drop table if exists fms_contractor_basicinfo;

drop table if exists fms_contractor_database;

drop table if exists fms_contractor_documentor;

drop table if exists fms_contractor_user;

drop table if exists fms_files;

drop table if exists fms_foldertemplate;

drop table if exists fms_foldertemplate_formsfiles;

drop table if exists fms_formsfile_attr;

drop table if exists fms_formsfiles;

drop table if exists fms_formsfiles_dir;

drop table if exists fms_library;

drop table if exists fms_library_import;

drop table if exists fms_progress_summary_report;

drop table if exists fms_version;

drop table if exists fms_version_pack;

drop table if exists sys_import_status;

drop table if exists sys_msg;

drop table if exists sys_msg_receive;

/*==============================================================*/
/* Table: fms_audit_record                                      */
/*==============================================================*/
create table fms_audit_record
(
   record_id            bigint not null auto_increment comment '记录ID',
   object_id            varchar(64) comment '审核对象ID',
   record_datetime      datetime comment '记录时间',
   record_name          varchar(64) comment '操作人',
   audit_status         varchar(64) comment '审核对象状态',
   record_status        varchar(64) comment '记录状态',
   audit_memo           text comment '备注',
   object_name          varchar(64) comment '对象名称',
   primary key (record_id)
);

/*==============================================================*/
/* Table: fms_businessrule                                      */
/*==============================================================*/
create table fms_businessrule
(
   ruleid               bigint not null auto_increment,
   ruleversion          varchar(128),
   rulecode             varchar(128),
   rulename             varchar(128),
   ruleexpress          varchar(2048),
   instanse             varchar(2048),
   sort                 varchar(8),
   remark               varchar(2048),
   status               varchar(8),
   primary key (ruleid)
);

/*==============================================================*/
/* Table: fms_businessrule_clause                               */
/*==============================================================*/
create table fms_businessrule_clause
(
   clauseid             bigint not null auto_increment,
   businessruleid       bigint,
   clausename           varchar(2048),
   clausecode           varchar(2048),
   clauseexample        varchar(2048),
   isedit               varchar(8),
   sort                 int,
   remark               varchar(2048),
   status               varchar(8),
   primary key (clauseid)
);

/*==============================================================*/
/* Table: fms_businessrule_version_items                        */
/*==============================================================*/
create table fms_businessrule_version_items
(
   param_version_id     bigint not null,
   ruleid               bigint not null,
   primary key (param_version_id, ruleid)
);

/*==============================================================*/
/* Table: fms_config                                            */
/*==============================================================*/
create table fms_config
(
   id                   bigint not null auto_increment comment '序号',
   parentid             bigint comment '序号',
   identity             varchar(128) comment '查找标识',
   nodetype             int comment '0-分类
            1-版本
            2-标识
            3-数据',
   disptype             int comment '显示类别',
   disporder            int,
   status               int comment '状态',
   code                 varchar(128) comment '编码',
   name                 varchar(256) comment '名称',
   memo                 varchar(512) comment '备注',
   value1               varchar(128) comment '值1',
   value2               varchar(128) comment '值2',
   value3               varchar(128) comment '值3',
   value4               varchar(128) comment '值4',
   value5               varchar(128) comment '值5',
   extend               text comment '扩展',
   create_user          bigint,
   create_date          datetime,
   last_user            bigint,
   last_date            datetime,
   primary key (id)
);

/*==============================================================*/
/* Table: fms_config_import                                     */
/*==============================================================*/
create table fms_config_import
(
   batch_number         varchar(64) not null,
   id                   varchar(64) not null comment '序号',
   parentid             varchar(64) comment '序号',
   identity             varchar(128) comment '查找标识',
   nodetype             varchar(8) comment '节点类别',
   disptype             int comment '显示类别',
   disporder            int,
   status               int comment '状态',
   code                 varchar(128) comment '编码',
   name                 varchar(256) comment '名称',
   memo                 varchar(512) comment '备注',
   value1               varchar(128) comment '值1',
   value2               varchar(128) comment '值2',
   value3               varchar(128) comment '值3',
   value4               varchar(128) comment '值4',
   value5               varchar(128) comment '值5',
   extend               text comment '扩展',
   create_user          bigint,
   create_date          datetime,
   last_user            bigint,
   last_date            datetime,
   primary key (batch_number, id)
);

/*==============================================================*/
/* Table: fms_config_version                                    */
/*==============================================================*/
create table fms_config_version
(
   param_version_id     bigint not null auto_increment,
   version_num          varchar(128),
   version_name         varchar(512),
   version_memo         text,
   create_user          bigint,
   create_date          datetime,
   last_user            bigint,
   last_date            datetime,
   disp_order           int,
   version_status       int comment '0-可选择
            1-不可选择',
   primary key (param_version_id)
);

/*==============================================================*/
/* Table: fms_config_version_items                              */
/*==============================================================*/
create table fms_config_version_items
(
   param_version_id     bigint not null,
   config_id            bigint not null comment '序号',
   primary key (param_version_id, config_id)
);

/*==============================================================*/
/* Table: fms_contractor_basicinfo                              */
/*==============================================================*/
create table fms_contractor_basicinfo
(
   contractor_id        varchar(64) not null comment '参建单位序号',
   participation_units  varchar(128) comment '参建单位',
   company_name         varchar(256) comment '企业名称',
   organizing_code      varchar(32) comment '组织机构代码',
   registration_authority varchar(128) comment '组织机构登记机关',
   business_license_reg_num varchar(32) comment '营业执照注册号',
   business_license_reg_auth varchar(128) comment '营业执照登记机关',
   enterprise_type      varchar(32) comment '企业类型',
   registered_capital   varchar(32) comment '注册资本',
   paid_registered_capital varchar(32) comment '实缴注册资本',
   establishment_date   datetime comment '成立日期',
   domicile             varchar(256) comment '住所',
   operating_period     varchar(32) /*default '长期'*/ comment '经营期限',
   legal_representative varchar(128) comment '法定代表人信息',
   contact_phone        varchar(16) comment '联系人手机',
   business_scope       varchar(512) comment '经营范围',
   change_description   varchar(256) comment '变更说明',
   attachment           text comment '附件',
   create_date          datetime comment '创建日期',
   create_user          varchar(32) comment '创建人',
   last_date            datetime comment '最近修改日期',
   last_user            varchar(32) comment '最近修改人',
   approval_status      varchar(32) comment '审批状态',
   approval_date        datetime comment '审批日期',
   approval_memo        text,
   approval_user        int,
   mobile_no            varchar(32) comment '手机号码',
   email                varchar(128) comment '电子邮箱',
   isdel                int,
   skin                 varchar(32),
   account_code_pic     text,
   business_license_pic text,
   contractor_accessory text,
   submit_approval_date datetime,
   database_status      int default 0 comment '资料库状态',
   professional_list    varchar(64),
   primary key (contractor_id)
);

/*==============================================================*/
/* Table: fms_contractor_database                               */
/*==============================================================*/
create table fms_contractor_database
(
   lib_id               bigint not null auto_increment,
   user_id              bigint not null,
   version_id           int not null comment '自增，标识不同的库',
   fs_ids               text not null,
   fs_names             varchar(512),
   lib_aliase           varchar(512),
   download_num         int,
   create_user          bigint,
   create_date          datetime,
   last_user            bigint,
   last_date            datetime,
   ndfile               varchar(512),
   primary key (lib_id)
);

/*==============================================================*/
/* Table: fms_contractor_documentor                             */
/*==============================================================*/
create table fms_contractor_documentor
(
   documentor_id        varchar(64) not null comment '序号',
   register_date        datetime comment '资料员登记日期',
   contractor_id        varchar(64) comment '参建单位序号',
   unit_mobile_no       varchar(32) comment '单位联系电话',
   documentor_name      varchar(32) comment '姓名',
   documentor_sex       varchar(8) comment '性别',
   documentor_birthday  datetime comment '出生日期',
   documentor_cardid    varchar(32) comment '身份证号',
   documentor_post      varchar(32) comment '职称',
   documentor_education varchar(32) comment '学历',
   graduate_institutions varchar(128) comment '毕业院校',
   profession           varchar(64) comment '专业',
   working_date         datetime comment '参加工作时间',
   introduction         longtext comment '简介',
   photo                varchar(128) comment '照片',
   mobile_no            varchar(32) comment '手机',
   email                varchar(128) comment '邮箱',
   tenders_code         varchar(64) comment '标段代号',
   contract_name        varchar(128) comment '合同名称',
   project_name         text comment '所属标段单位工程名称',
   construction_unit_idea text comment '施工单位意见',
   supervision_unit_idea text comment '监理单位意见',
   construction_division_idea text comment '工程处审定意见',
   information_centre_idea text comment '信息中心审定意见',
   construction_unit_date datetime comment '施工单位日期',
   supervision_unit_date datetime comment '监理单位日期',
   construction_division_date datetime comment '工程处审定日期',
   information_centre_date datetime comment '信息中心审定日期',
   service_year         varchar(32),
   approval_memo        text,
   approval_status      varchar(32),
   approval_date        datetime,
   approval_user        int,
   company_name         varchar(64),
   scan_accessory       text,
   submit_documentor_audit datetime,
   login_name           varchar(32),
   datebase_status      varchar(32),
   professional_list    varchar(64),
   last_date            datetime,
   contact_phone        varchar(32),
   primary key (documentor_id)
);

/*==============================================================*/
/* Table: fms_contractor_user                                   */
/*==============================================================*/
create table fms_contractor_user
(
   user_id              bigint not null auto_increment,
   contractor_id        varchar(64) comment '参建单位序号',
   documentor_id        varchar(64) comment '序号',
   login_name           varchar(128),
   login_password       varchar(128),
   login_email          varchar(64),
   login_mobile         varchar(32),
   user_status          int comment '0-未激活
            1-活动
            2-冻结',
   user_name            varchar(128),
   user_type            int comment '0-建设单位
            1-资料员',
   isdel                int,
   create_date          datetime,
   last_date            datetime,
   primary key (user_id)
);

/*==============================================================*/
/* Table: fms_files                                             */
/*==============================================================*/
create table fms_files
(
   file_id              varchar(64) not null comment '文件序号',
   file_size            bigint comment '文件大小',
   file_name            varchar(256) comment '文件名称，文件上传时的名称，供下载及查看时使用',
   file_path            varchar(512) comment '文件路径，文件相对路径及文件名称和类型，通过此字段能找到对应的磁盘文件',
   file_identify        text comment '文件标识，系统处理后的文件名称，存放在磁盘的文件名',
   file_type            varchar(32) comment '文件类型，文件扩展名',
   two_dimension_code   varchar(256) comment '二维码，文件扫描匹配使用',
   create_user          int comment '创建人',
   create_date          datetime comment '创建日期',
   last_date            datetime comment '最近修改日期',
   last_user            int comment '最近修改人',
   file_status          int,
   primary key (file_id)
);

/*==============================================================*/
/* Table: fms_foldertemplate                                    */
/*==============================================================*/
create table fms_foldertemplate
(
   ag_id                varchar(64) not null comment '案卷序号',
   fs_id                varchar(64) comment '档案库序号',
   retention_period     varchar(32) comment '保管期限',
   security_classification varchar(32) comment '密级',
   document_number      varchar(128) comment '档号',
   catalog_code         varchar(128) comment '目录代号',
   start_date           datetime comment '起始日期',
   end_date             datetime comment '终止日期',
   organization         varchar(128) comment '编制单位',
   organize_date        datetime comment '编制日期',
   piece_number         int comment '件数',
   archived_copies      int comment '归档份数',
   total_pages          int comment '总页数',
   drawing_number       int comment '图纸页数',
   written_number       int comment '文字材料页数',
   photo_number         int comment '照片张数',
   build_user           varchar(64) comment '立卷人',
   build_date           datetime comment '立卷日期',
   check_user           varchar(64) comment '检查人',
   check_date           datetime comment '检查日期',
   storage_location     varchar(128) comment '库位号',
   vice_location        varchar(128) comment '副库位号',
   handed_status        varchar(16) comment '移交状态',
   city_construction    varchar(128) comment '移交城建',
   create_date          datetime comment '创建日期',
   create_user          int comment '创建人',
   last_date            datetime comment '最近修改日期',
   last_user            int comment '最近修改人',
   primary key (ag_id)
);

/*==============================================================*/
/* Table: fms_foldertemplate_formsfiles                         */
/*==============================================================*/
create table fms_foldertemplate_formsfiles
(
   tb_id                varchar(64) not null comment '案卷表格序号',
   fs_id                varchar(64) not null comment '档案库序号',
   tb_sn                int,
   primary key (tb_id, fs_id)
);

/*==============================================================*/
/* Table: fms_formsfile_attr                                    */
/*==============================================================*/
create table fms_formsfile_attr
(
   ta_id                varchar(64) not null comment '表格序号',
   contractor_id        varchar(64) comment '参建单位序号',
   elect_file_id        varchar(64) comment '电子文件',
   scan_file_id         varchar(64) comment '扫描文件',
   tb_id                varchar(64) comment '案卷表格序号',
   ag_id                varchar(64) comment '案卷序号',
   block_code           varchar(32) comment '标段代号',
   single_project_code  varchar(32) comment '单项工程代号',
   station_interval     varchar(32) comment '车站区间',
   duty_officer         varchar(32) comment '责任者',
   create_date          datetime comment '成文日期',
   page_num             varchar(16) comment '页数',
   page_no              varchar(16) comment '页号',
   memo                 longtext comment '备注说明',
   document_number      varchar(128) comment '文件编号',
   fnsort_table         varchar(32) comment '卷内序号',
   document_no          varchar(32) comment '文件档号',
   file_title           varchar(256) comment '文件题名',
   retention_period     varchar(32) comment '保管期限',
   ssecrecy_level       varchar(32) comment '保密级别',
   archived_copies      int comment '归档份数',
   paper_size           varchar(16) comment '纸张大小',
   total_pages          int comment '总页数',
   is_third_file        int comment '是否第三方文件',
   progress             int comment '完成进度',
   primary key (ta_id)
);

/*==============================================================*/
/* Table: fms_formsfiles                                        */
/*==============================================================*/
create table fms_formsfiles
(
   tb_id                varchar(64) not null comment '案卷表格序号',
   parent_id            varchar(64) comment '案卷表格序号',
   tmpl_file_id         varchar(64) comment '模板文件',
   example_file_id      varchar(64) comment '样板文件',
   dir_id               varchar(64),
   document_number      varchar(128) not null comment '文件编号',
   fnsort_table         int comment '卷内序号',
   document_no          varchar(64) comment '文件档号',
   file_title           varchar(256) comment '文件题名',
   retention_period     varchar(64) comment '保管期限',
   ssecrecy_level       varchar(32) comment '保密级别',
   archived_copies      int comment '归档份数',
   paper_size           varchar(16) comment '纸张大小',
   total_pages          int comment '总页数',
   fill_in_rules        longtext comment '填写规则',
   create_date          datetime,
   create_user          int,
   last_date            datetime,
   last_user            int,
   tb_version           varchar(64),
   primary key (tb_id)
);

/*==============================================================*/
/* Table: fms_formsfiles_dir                                    */
/*==============================================================*/
create table fms_formsfiles_dir
(
   dir_id               varchar(64) not null,
   parent_id            varchar(64),
   dir_name             varchar(512),
   disp_order           int,
   dir_icon             varchar(64),
   primary key (dir_id)
);

/*==============================================================*/
/* Table: fms_library                                           */
/*==============================================================*/
create table fms_library
(
   fs_id                varchar(64) not null comment '档案库序号',
   parent_id            varchar(64) comment '上级档案库序号',
   fs_code              varchar(64) not null comment '档案库编号',
   fs_name              varchar(2048) comment '档案库名称',
   fs_name_code         varchar(64) comment '类别编号',
   fs_memo              text comment '档案库说明',
   create_date          datetime comment '创建日期',
   create_user          int comment '创建人',
   last_date            datetime comment '最近修改日期',
   last_user            int comment '最近修改人',
   disp_order           float,
   id_type              int comment '参见数据字典fms_library_id_type',
   id_status            int comment '0-未发布 1-已发布',
   primary key (fs_id)
);

/*==============================================================*/
/* Table: fms_library_import                                    */
/*==============================================================*/
create table fms_library_import
(
   batch_number         varchar(64) not null,
   fs_id                varchar(64) not null comment '档案库序号',
   parent_id            varchar(64) comment '上级档案库序号',
   fs_code              varchar(64) not null comment '档案库编号',
   fs_name              varchar(2048) comment '档案库名称',
   fs_memo              text comment '档案库说明',
   create_date          datetime comment '创建日期',
   create_user          int comment '创建人',
   last_date            datetime comment '最近修改日期',
   last_user            int comment '最近修改人',
   disp_order           int,
   id_type              int comment '参见数据字典fms_library_id_type',
   id_status            int comment '0-未发布 1-已发布',
   fs_name_code         varchar(64) comment '类别编号',
   primary key (batch_number, fs_id)
);

/*==============================================================*/
/* Table: fms_progress_summary_report                           */
/*==============================================================*/
create table fms_progress_summary_report
(
   report_id            bigint not null auto_increment comment '进展序号',
   documentor_id        varchar(64) comment '序号',
   lib_id               bigint,
   documentor_name      varchar(32) comment '资料员名称',
   foldertemplate_num   int comment '案卷数量',
   entryfile_num        int comment '条目数量',
   formsfiles_num       int comment '系统表格数量',
   files_num            int comment '电子文件数量',
   progress             int comment '总体进度',
   create_date          datetime comment '上报日期',
   ip                   varchar(64),
   mac                  varchar(64) comment '网卡MAC',
   primary key (report_id)
);

/*==============================================================*/
/* Table: fms_version                                           */
/*==============================================================*/
create table fms_version
(
   version_id           int not null auto_increment comment '自增，标识不同的版本',
   fs_id                varchar(64) comment '档案库序号',
   param_version_id     bigint,
   lib_type             int,
   version_num          varchar(128) not null comment '版本号，必须唯一',
   version_name         varchar(512),
   version_memo         text,
   is_current           int,
   create_user          int,
   create_date          datetime,
   last_user            int,
   last_date            datetime,
   version_status       int,
   disp_order           int,
   current_pack_id      int,
   primary key (version_id)
);

/*==============================================================*/
/* Table: fms_version_pack                                      */
/*==============================================================*/
create table fms_version_pack
(
   pack_id              int not null auto_increment comment '版本增量序号',
   version_id           int comment '自增，标识不同的版本',
   version_num          varchar(128) comment '增量版本号',
   version_name         varchar(512) comment '增量版本名称',
   version_memo         text comment '版本说明',
   version_file_id      varchar(512) comment '增量包文件',
   version_status       int comment '版本状态',
   create_user          int comment '创建人',
   create_date          datetime comment '创建日期',
   last_user            int comment '最近修改人',
   last_date            datetime comment '最近修改日期',
   primary key (pack_id)
);

/*==============================================================*/
/* Table: sys_import_status                                     */
/*==============================================================*/
create table sys_import_status
(
   batch_number         varchar(64) not null,
   state_code           varchar(16),
   state_desc           varchar(512),
   filename             varchar(512),
   fileid               varchar(512),
   update_time          datetime,
   user_id              varchar(64),
   primary key (batch_number)
);

/*==============================================================*/
/* Table: sys_msg                                               */
/*==============================================================*/
create table sys_msg
(
   msg_id               bigint not null auto_increment comment '消息序号',
   send_user            int comment '发送人',
   msg_title            varchar(1024) comment '消息标题',
   msg_text             longtext comment '消息内容',
   attechment           varchar(256) comment '附件',
   send_time            datetime comment '发送时间',
   msg_status           int default 0 comment '0表示保存  1表示发送',
   primary key (msg_id)
);

/*==============================================================*/
/* Table: sys_msg_receive                                       */
/*==============================================================*/
create table sys_msg_receive
(
   receive_id           bigint not null auto_increment comment '接收消息序号',
   receive_user         varchar(64) not null comment '接收人',
   msg_id               bigint not null comment '消息序号',
   read_time            datetime comment '阅读日期',
   primary key (receive_id)
);

alter table fms_businessrule_clause add constraint fk_reference_52 foreign key (businessruleid)
      references fms_businessrule (ruleid) on delete restrict on update restrict;

alter table fms_businessrule_version_items add constraint fk_reference_58 foreign key (param_version_id)
      references fms_config_version (param_version_id) on delete cascade on update restrict;

alter table fms_businessrule_version_items add constraint fk_reference_59 foreign key (ruleid)
      references fms_businessrule (ruleid) on delete restrict on update restrict;

alter table fms_config add constraint fk_reference_51 foreign key (parentid)
      references fms_config (id) on delete cascade on update restrict;

alter table fms_config_version_items add constraint fk_reference_56 foreign key (param_version_id)
      references fms_config_version (param_version_id) on delete cascade on update restrict;

alter table fms_config_version_items add constraint fk_reference_57 foreign key (config_id)
      references fms_config (id) on delete restrict on update restrict;

alter table fms_contractor_database add constraint fk_reference_39 foreign key (version_id)
      references fms_version (version_id) on delete restrict on update restrict;

alter table fms_contractor_database add constraint fk_reference_55 foreign key (user_id)
      references fms_contractor_user (user_id) on delete restrict on update restrict;

alter table fms_contractor_documentor add constraint fk_reference_24 foreign key (contractor_id)
      references fms_contractor_basicinfo (contractor_id) on delete restrict on update restrict;

alter table fms_contractor_user add constraint fk_reference_53 foreign key (contractor_id)
      references fms_contractor_basicinfo (contractor_id) on delete restrict on update restrict;

alter table fms_contractor_user add constraint fk_reference_54 foreign key (documentor_id)
      references fms_contractor_documentor (documentor_id) on delete restrict on update restrict;

alter table fms_foldertemplate add constraint fk_reference_31 foreign key (fs_id)
      references fms_library (fs_id) on delete cascade on update restrict;

alter table fms_foldertemplate_formsfiles add constraint fk_reference_17 foreign key (tb_id)
      references fms_formsfiles (tb_id) on delete restrict on update restrict;

alter table fms_foldertemplate_formsfiles add constraint fk_reference_48 foreign key (fs_id)
      references fms_library (fs_id) on delete cascade on update restrict;

alter table fms_formsfile_attr add constraint fk_reference_22 foreign key (contractor_id)
      references fms_contractor_basicinfo (contractor_id) on delete restrict on update restrict;

alter table fms_formsfile_attr add constraint fk_reference_36 foreign key (elect_file_id)
      references fms_files (file_id) on delete restrict on update restrict;

alter table fms_formsfile_attr add constraint fk_reference_37 foreign key (scan_file_id)
      references fms_files (file_id) on delete restrict on update restrict;

alter table fms_formsfile_attr add constraint fk_reference_46 foreign key (tb_id)
      references fms_formsfiles (tb_id) on delete restrict on update restrict;

alter table fms_formsfile_attr add constraint fk_reference_47 foreign key (ag_id)
      references fms_foldertemplate (ag_id) on delete restrict on update restrict;

alter table fms_formsfiles add constraint fk_reference_34 foreign key (tmpl_file_id)
      references fms_files (file_id) on delete restrict on update restrict;

alter table fms_formsfiles add constraint fk_reference_35 foreign key (example_file_id)
      references fms_files (file_id) on delete restrict on update restrict;

alter table fms_formsfiles add constraint fk_reference_49 foreign key (parent_id)
      references fms_formsfiles (tb_id) on delete cascade on update restrict;

alter table fms_formsfiles add constraint fk_reference_74 foreign key (dir_id)
      references fms_formsfiles_dir (dir_id) on delete restrict on update restrict;

alter table fms_formsfiles_dir add constraint fk_reference_73 foreign key (parent_id)
      references fms_formsfiles_dir (dir_id) on delete cascade on update restrict;

-- alter table fms_library add constraint fk_reference_44 foreign key (parent_id)
--      references fms_library (fs_id) on delete cascade on update restrict;

alter table fms_progress_summary_report add constraint fk_reference_82 foreign key (documentor_id)
      references fms_contractor_documentor (documentor_id) on delete restrict on update restrict;

alter table fms_progress_summary_report add constraint fk_reference_83 foreign key (lib_id)
      references fms_contractor_database (lib_id) on delete restrict on update restrict;

alter table fms_version add constraint fk_reference_50 foreign key (fs_id)
      references fms_library (fs_id) on delete restrict on update restrict;

alter table fms_version add constraint fk_reference_60 foreign key (param_version_id)
      references fms_config_version (param_version_id) on delete restrict on update restrict;

alter table fms_version_pack add constraint fk_reference_81 foreign key (version_id)
      references fms_version (version_id) on delete restrict on update restrict;

alter table sys_msg_receive add constraint fk_reference_40 foreign key (msg_id)
      references sys_msg (msg_id) on delete cascade on update restrict;

