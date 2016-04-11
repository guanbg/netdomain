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
   record_id            bigint not null auto_increment comment '��¼ID',
   object_id            varchar(64) comment '��˶���ID',
   record_datetime      datetime comment '��¼ʱ��',
   record_name          varchar(64) comment '������',
   audit_status         varchar(64) comment '��˶���״̬',
   record_status        varchar(64) comment '��¼״̬',
   audit_memo           text comment '��ע',
   object_name          varchar(64) comment '��������',
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
   id                   bigint not null auto_increment comment '���',
   parentid             bigint comment '���',
   identity             varchar(128) comment '���ұ�ʶ',
   nodetype             int comment '0-����
            1-�汾
            2-��ʶ
            3-����',
   disptype             int comment '��ʾ���',
   disporder            int,
   status               int comment '״̬',
   code                 varchar(128) comment '����',
   name                 varchar(256) comment '����',
   memo                 varchar(512) comment '��ע',
   value1               varchar(128) comment 'ֵ1',
   value2               varchar(128) comment 'ֵ2',
   value3               varchar(128) comment 'ֵ3',
   value4               varchar(128) comment 'ֵ4',
   value5               varchar(128) comment 'ֵ5',
   extend               text comment '��չ',
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
   id                   varchar(64) not null comment '���',
   parentid             varchar(64) comment '���',
   identity             varchar(128) comment '���ұ�ʶ',
   nodetype             varchar(8) comment '�ڵ����',
   disptype             int comment '��ʾ���',
   disporder            int,
   status               int comment '״̬',
   code                 varchar(128) comment '����',
   name                 varchar(256) comment '����',
   memo                 varchar(512) comment '��ע',
   value1               varchar(128) comment 'ֵ1',
   value2               varchar(128) comment 'ֵ2',
   value3               varchar(128) comment 'ֵ3',
   value4               varchar(128) comment 'ֵ4',
   value5               varchar(128) comment 'ֵ5',
   extend               text comment '��չ',
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
   version_status       int comment '0-��ѡ��
            1-����ѡ��',
   primary key (param_version_id)
);

/*==============================================================*/
/* Table: fms_config_version_items                              */
/*==============================================================*/
create table fms_config_version_items
(
   param_version_id     bigint not null,
   config_id            bigint not null comment '���',
   primary key (param_version_id, config_id)
);

/*==============================================================*/
/* Table: fms_contractor_basicinfo                              */
/*==============================================================*/
create table fms_contractor_basicinfo
(
   contractor_id        varchar(64) not null comment '�ν���λ���',
   participation_units  varchar(128) comment '�ν���λ',
   company_name         varchar(256) comment '��ҵ����',
   organizing_code      varchar(32) comment '��֯��������',
   registration_authority varchar(128) comment '��֯�����Ǽǻ���',
   business_license_reg_num varchar(32) comment 'Ӫҵִ��ע���',
   business_license_reg_auth varchar(128) comment 'Ӫҵִ�յǼǻ���',
   enterprise_type      varchar(32) comment '��ҵ����',
   registered_capital   varchar(32) comment 'ע���ʱ�',
   paid_registered_capital varchar(32) comment 'ʵ��ע���ʱ�',
   establishment_date   datetime comment '��������',
   domicile             varchar(256) comment 'ס��',
   operating_period     varchar(32) /*default '����'*/ comment '��Ӫ����',
   legal_representative varchar(128) comment '������������Ϣ',
   contact_phone        varchar(16) comment '��ϵ���ֻ�',
   business_scope       varchar(512) comment '��Ӫ��Χ',
   change_description   varchar(256) comment '���˵��',
   attachment           text comment '����',
   create_date          datetime comment '��������',
   create_user          varchar(32) comment '������',
   last_date            datetime comment '����޸�����',
   last_user            varchar(32) comment '����޸���',
   approval_status      varchar(32) comment '����״̬',
   approval_date        datetime comment '��������',
   approval_memo        text,
   approval_user        int,
   mobile_no            varchar(32) comment '�ֻ�����',
   email                varchar(128) comment '��������',
   isdel                int,
   skin                 varchar(32),
   account_code_pic     text,
   business_license_pic text,
   contractor_accessory text,
   submit_approval_date datetime,
   database_status      int default 0 comment '���Ͽ�״̬',
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
   version_id           int not null comment '��������ʶ��ͬ�Ŀ�',
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
   documentor_id        varchar(64) not null comment '���',
   register_date        datetime comment '����Ա�Ǽ�����',
   contractor_id        varchar(64) comment '�ν���λ���',
   unit_mobile_no       varchar(32) comment '��λ��ϵ�绰',
   documentor_name      varchar(32) comment '����',
   documentor_sex       varchar(8) comment '�Ա�',
   documentor_birthday  datetime comment '��������',
   documentor_cardid    varchar(32) comment '���֤��',
   documentor_post      varchar(32) comment 'ְ��',
   documentor_education varchar(32) comment 'ѧ��',
   graduate_institutions varchar(128) comment '��ҵԺУ',
   profession           varchar(64) comment 'רҵ',
   working_date         datetime comment '�μӹ���ʱ��',
   introduction         longtext comment '���',
   photo                varchar(128) comment '��Ƭ',
   mobile_no            varchar(32) comment '�ֻ�',
   email                varchar(128) comment '����',
   tenders_code         varchar(64) comment '��δ���',
   contract_name        varchar(128) comment '��ͬ����',
   project_name         text comment '������ε�λ��������',
   construction_unit_idea text comment 'ʩ����λ���',
   supervision_unit_idea text comment '����λ���',
   construction_division_idea text comment '���̴������',
   information_centre_idea text comment '��Ϣ���������',
   construction_unit_date datetime comment 'ʩ����λ����',
   supervision_unit_date datetime comment '����λ����',
   construction_division_date datetime comment '���̴�������',
   information_centre_date datetime comment '��Ϣ����������',
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
   contractor_id        varchar(64) comment '�ν���λ���',
   documentor_id        varchar(64) comment '���',
   login_name           varchar(128),
   login_password       varchar(128),
   login_email          varchar(64),
   login_mobile         varchar(32),
   user_status          int comment '0-δ����
            1-�
            2-����',
   user_name            varchar(128),
   user_type            int comment '0-���赥λ
            1-����Ա',
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
   file_id              varchar(64) not null comment '�ļ����',
   file_size            bigint comment '�ļ���С',
   file_name            varchar(256) comment '�ļ����ƣ��ļ��ϴ�ʱ�����ƣ������ؼ��鿴ʱʹ��',
   file_path            varchar(512) comment '�ļ�·�����ļ����·�����ļ����ƺ����ͣ�ͨ�����ֶ����ҵ���Ӧ�Ĵ����ļ�',
   file_identify        text comment '�ļ���ʶ��ϵͳ�������ļ����ƣ�����ڴ��̵��ļ���',
   file_type            varchar(32) comment '�ļ����ͣ��ļ���չ��',
   two_dimension_code   varchar(256) comment '��ά�룬�ļ�ɨ��ƥ��ʹ��',
   create_user          int comment '������',
   create_date          datetime comment '��������',
   last_date            datetime comment '����޸�����',
   last_user            int comment '����޸���',
   file_status          int,
   primary key (file_id)
);

/*==============================================================*/
/* Table: fms_foldertemplate                                    */
/*==============================================================*/
create table fms_foldertemplate
(
   ag_id                varchar(64) not null comment '�������',
   fs_id                varchar(64) comment '���������',
   retention_period     varchar(32) comment '��������',
   security_classification varchar(32) comment '�ܼ�',
   document_number      varchar(128) comment '����',
   catalog_code         varchar(128) comment 'Ŀ¼����',
   start_date           datetime comment '��ʼ����',
   end_date             datetime comment '��ֹ����',
   organization         varchar(128) comment '���Ƶ�λ',
   organize_date        datetime comment '��������',
   piece_number         int comment '����',
   archived_copies      int comment '�鵵����',
   total_pages          int comment '��ҳ��',
   drawing_number       int comment 'ͼֽҳ��',
   written_number       int comment '���ֲ���ҳ��',
   photo_number         int comment '��Ƭ����',
   build_user           varchar(64) comment '������',
   build_date           datetime comment '��������',
   check_user           varchar(64) comment '�����',
   check_date           datetime comment '�������',
   storage_location     varchar(128) comment '��λ��',
   vice_location        varchar(128) comment '����λ��',
   handed_status        varchar(16) comment '�ƽ�״̬',
   city_construction    varchar(128) comment '�ƽ��ǽ�',
   create_date          datetime comment '��������',
   create_user          int comment '������',
   last_date            datetime comment '����޸�����',
   last_user            int comment '����޸���',
   primary key (ag_id)
);

/*==============================================================*/
/* Table: fms_foldertemplate_formsfiles                         */
/*==============================================================*/
create table fms_foldertemplate_formsfiles
(
   tb_id                varchar(64) not null comment '���������',
   fs_id                varchar(64) not null comment '���������',
   tb_sn                int,
   primary key (tb_id, fs_id)
);

/*==============================================================*/
/* Table: fms_formsfile_attr                                    */
/*==============================================================*/
create table fms_formsfile_attr
(
   ta_id                varchar(64) not null comment '������',
   contractor_id        varchar(64) comment '�ν���λ���',
   elect_file_id        varchar(64) comment '�����ļ�',
   scan_file_id         varchar(64) comment 'ɨ���ļ�',
   tb_id                varchar(64) comment '���������',
   ag_id                varchar(64) comment '�������',
   block_code           varchar(32) comment '��δ���',
   single_project_code  varchar(32) comment '����̴���',
   station_interval     varchar(32) comment '��վ����',
   duty_officer         varchar(32) comment '������',
   create_date          datetime comment '��������',
   page_num             varchar(16) comment 'ҳ��',
   page_no              varchar(16) comment 'ҳ��',
   memo                 longtext comment '��ע˵��',
   document_number      varchar(128) comment '�ļ����',
   fnsort_table         varchar(32) comment '�������',
   document_no          varchar(32) comment '�ļ�����',
   file_title           varchar(256) comment '�ļ�����',
   retention_period     varchar(32) comment '��������',
   ssecrecy_level       varchar(32) comment '���ܼ���',
   archived_copies      int comment '�鵵����',
   paper_size           varchar(16) comment 'ֽ�Ŵ�С',
   total_pages          int comment '��ҳ��',
   is_third_file        int comment '�Ƿ�������ļ�',
   progress             int comment '��ɽ���',
   primary key (ta_id)
);

/*==============================================================*/
/* Table: fms_formsfiles                                        */
/*==============================================================*/
create table fms_formsfiles
(
   tb_id                varchar(64) not null comment '���������',
   parent_id            varchar(64) comment '���������',
   tmpl_file_id         varchar(64) comment 'ģ���ļ�',
   example_file_id      varchar(64) comment '�����ļ�',
   dir_id               varchar(64),
   document_number      varchar(128) not null comment '�ļ����',
   fnsort_table         int comment '�������',
   document_no          varchar(64) comment '�ļ�����',
   file_title           varchar(256) comment '�ļ�����',
   retention_period     varchar(64) comment '��������',
   ssecrecy_level       varchar(32) comment '���ܼ���',
   archived_copies      int comment '�鵵����',
   paper_size           varchar(16) comment 'ֽ�Ŵ�С',
   total_pages          int comment '��ҳ��',
   fill_in_rules        longtext comment '��д����',
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
   fs_id                varchar(64) not null comment '���������',
   parent_id            varchar(64) comment '�ϼ����������',
   fs_code              varchar(64) not null comment '��������',
   fs_name              varchar(2048) comment '����������',
   fs_name_code         varchar(64) comment '�����',
   fs_memo              text comment '������˵��',
   create_date          datetime comment '��������',
   create_user          int comment '������',
   last_date            datetime comment '����޸�����',
   last_user            int comment '����޸���',
   disp_order           float,
   id_type              int comment '�μ������ֵ�fms_library_id_type',
   id_status            int comment '0-δ���� 1-�ѷ���',
   primary key (fs_id)
);

/*==============================================================*/
/* Table: fms_library_import                                    */
/*==============================================================*/
create table fms_library_import
(
   batch_number         varchar(64) not null,
   fs_id                varchar(64) not null comment '���������',
   parent_id            varchar(64) comment '�ϼ����������',
   fs_code              varchar(64) not null comment '��������',
   fs_name              varchar(2048) comment '����������',
   fs_memo              text comment '������˵��',
   create_date          datetime comment '��������',
   create_user          int comment '������',
   last_date            datetime comment '����޸�����',
   last_user            int comment '����޸���',
   disp_order           int,
   id_type              int comment '�μ������ֵ�fms_library_id_type',
   id_status            int comment '0-δ���� 1-�ѷ���',
   fs_name_code         varchar(64) comment '�����',
   primary key (batch_number, fs_id)
);

/*==============================================================*/
/* Table: fms_progress_summary_report                           */
/*==============================================================*/
create table fms_progress_summary_report
(
   report_id            bigint not null auto_increment comment '��չ���',
   documentor_id        varchar(64) comment '���',
   lib_id               bigint,
   documentor_name      varchar(32) comment '����Ա����',
   foldertemplate_num   int comment '��������',
   entryfile_num        int comment '��Ŀ����',
   formsfiles_num       int comment 'ϵͳ�������',
   files_num            int comment '�����ļ�����',
   progress             int comment '�������',
   create_date          datetime comment '�ϱ�����',
   ip                   varchar(64),
   mac                  varchar(64) comment '����MAC',
   primary key (report_id)
);

/*==============================================================*/
/* Table: fms_version                                           */
/*==============================================================*/
create table fms_version
(
   version_id           int not null auto_increment comment '��������ʶ��ͬ�İ汾',
   fs_id                varchar(64) comment '���������',
   param_version_id     bigint,
   lib_type             int,
   version_num          varchar(128) not null comment '�汾�ţ�����Ψһ',
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
   pack_id              int not null auto_increment comment '�汾�������',
   version_id           int comment '��������ʶ��ͬ�İ汾',
   version_num          varchar(128) comment '�����汾��',
   version_name         varchar(512) comment '�����汾����',
   version_memo         text comment '�汾˵��',
   version_file_id      varchar(512) comment '�������ļ�',
   version_status       int comment '�汾״̬',
   create_user          int comment '������',
   create_date          datetime comment '��������',
   last_user            int comment '����޸���',
   last_date            datetime comment '����޸�����',
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
   msg_id               bigint not null auto_increment comment '��Ϣ���',
   send_user            int comment '������',
   msg_title            varchar(1024) comment '��Ϣ����',
   msg_text             longtext comment '��Ϣ����',
   attechment           varchar(256) comment '����',
   send_time            datetime comment '����ʱ��',
   msg_status           int default 0 comment '0��ʾ����  1��ʾ����',
   primary key (msg_id)
);

/*==============================================================*/
/* Table: sys_msg_receive                                       */
/*==============================================================*/
create table sys_msg_receive
(
   receive_id           bigint not null auto_increment comment '������Ϣ���',
   receive_user         varchar(64) not null comment '������',
   msg_id               bigint not null comment '��Ϣ���',
   read_time            datetime comment '�Ķ�����',
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

