/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2016/2/4 16:13:26                            */
/*==============================================================*/

/*==============================================================*/
/* Table: offline__config                                       */
/*==============================================================*/
create table offline_config
(
   id                   integer NOT NULL,
   parentid             integer,
   identity             text,
   nodetype             integer,
   disptype             integer,
   disporder            integer,
   status               integer,
   code                 text,
   name                 text,
   memo                 text,
   value1               text,
   value2               text,
   value3               text,
   value4               text,
   value5               text,
   extend               text,
   create_user          integer,
   create_date          text,
   last_user            integer,
   last_date            text,
   primary key (id),
   foreign key (parentid)  references offline__config (id) on delete cascade on update restrict
);

/*==============================================================*/
/* Table: offline_achive_box                                    */
/*==============================================================*/
create table offline_achive_box
(
   box_code             text NOT NULL,
   nav_id               text,
   box_order            integer,
   box_name             text,
   backbone             text,
   create_date          text,
   create_user          text,
   last_date            text,
   last_user            text,
   document_number      text,
   record_title         text,
   organization_unit    text,
   compile_date         text,
   security_classification text,
   roll_total           integer,
   roll_current         integer,
   primary key (box_code),
   foreign key (nav_id) references offline_nav (nav_id) on delete restrict on update restrict
);

/*==============================================================*/
/* Table: offline_achive_box_dir                                */
/*==============================================================*/
create table offline_achive_box_dir
(
   dir_code             text NOT NULL,
   box_code             text,
   dir_num              integer,
   file_num             text,
   principal            text,
   record_title         text,
   file_date            text,
   page_numbering       integer,
   memo                 text,
   tb_id                text,
   primary key (dir_code),
   foreign key (box_code) references offline_achive_box (box_code) on delete restrict on update restrict
);

/*==============================================================*/
/* Table: offline_businessrule                                  */
/*==============================================================*/
create table offline_businessrule
(
   ruleid               integer NOT NULL,
   ruleversion          text,
   rulecode             text,
   rulename             text,
   ruleexpress          text,
   instanse             text,
   sort                 text,
   remark               text,
   status               text,
   primary key (ruleid)
);

/*==============================================================*/
/* Table: offline_businessrule_clause                           */
/*==============================================================*/
create table offline_businessrule_clause
(
   clauseid             integer NOT NULL,
   ruleid               integer,
   businessruleid       integer,
   clausename           text,
   clausecode           text,
   clauseexample        text,
   isedit               text,
   sort                 integer,
   remark               text,
   status               text,
   primary key (clauseid),
   foreign key (ruleid) references offline_businessrule (ruleid) on delete restrict on update restrict
);

/*==============================================================*/
/* Table: offline_contractor_basicinfo                          */
/*==============================================================*/
create table offline_contractor_basicinfo
(
   contractor_id        text NOT NULL,
   participation_units  text,
   company_name         text,
   organizing_code      text,
   registration_authority text,
   business_license_reg_num text,
   business_license_reg_auth text,
   enterprise_type      text,
   registered_capital   text,
   paid_registered_capital text,
   establishment_date   text,
   domicile             text,
   operating_period     text,
   legal_representative text,
   contact_phone        text,
   business_scope       text,
   change_description   text,
   attachment           text,
   create_date          text,
   create_user          text,
   last_date            text,
   last_user            text,
   approval_status      text,
   approval_date        text,
   approval_memo        text,
   approval_user        integer,
   mobile_no            text,
   email                text,
   isdel                integer,
   skin                 text,
   account_code_pic  text,
   business_license_pic text,
   contractor_accessory text,
   submit_approval_date text,
   database_status      integer,
   primary key (contractor_id)
);

/*==============================================================*/
/* Table: offline_contractor_documentor                         */
/*==============================================================*/
create table offline_contractor_documentor
(
   documentor_id        text NOT NULL,
   contractor_id        text,
   register_date        text,
   unit_mobile_no       text,
   documentor_name      text,
   documentor_sex       text,
   documentor_birthday  text,
   documentor_cardid    text,
   documentor_post      text,
   documentor_education text,
   graduate_institutions text,
   profession           text,
   working_date         text,
   introduction         text,
   photo                text,
   mobile_no            text,
   email                text,
   tenders_code         text,
   contract_name        text,
   project_name         text,
   construction_unit_idea text,
   supervision_unit_idea text,
   construction_division_idea text,
   information_centre_idea text,
   construction_unit_date text,
   supervision_unit_date text,
   construction_division_date text,
   information_centre_date text,
   primary key (documentor_id),
   foreign key (contractor_id) references offline_contractor_basicinfo (contractor_id) on delete restrict on update restrict
);

/*==============================================================*/
/* Table: offline_files                                         */
/*==============================================================*/
create table offline_files
(
   file_id              text NOT NULL,
   ta_id                text,
   file_size            integer,
   file_name            text,
   file_path            text,
   file_identify        text,
   file_type            text,
   two_dimension_code   text,
   create_user          integer,
   create_date          text,
   last_date            text,
   last_user            integer,
   file_status          text,
   primary key (file_id),
   foreign key (ta_id) references offline_formsfile_attr (ta_id) on delete restrict on update restrict
);

/*==============================================================*/
/* Table: offline_fileversion                                   */
/*==============================================================*/
create table offline_fileversion
(
   id                   text NOT NULL,
   file_id              text,
   version_id           integer,
   create_user          text,
   create_date          text,
   official_version     integer,
   primary key (id),
   foreign key (file_id) references offline_files (file_id) on delete restrict on update restrict
);

/*==============================================================*/
/* Table: offline_foldertemplate                                */
/*==============================================================*/
create table offline_foldertemplate
(
   ag_id                text NOT NULL,
   nav_id               text,
   fs_id                text,
   retention_period     text,
   security_classification text,
   document_number      text,
   catalog_code         text,
   block_code           text,
   start_date           text,
   end_date             text,
   organization         text,
   organize_date        text,
   piece_number         integer,
   archived_copies      integer,
   total_pages          integer,
   total_sheets         integer,
   drawing_number       integer,
   written_number       integer,
   photo_number         integer,
   build_user           text,
   build_date           text,
   check_user           text,
   check_date           text,
   storage_location     text,
   vice_location        text,
   handed_status        text,
   city_construction    text,
   create_date          text,
   last_date            text,
   ag_memo              text,
   memo                 text,
   class_code           text,
   class_name           text,
   folder_year          integer,
   catalog_type         text,
   single_project_code  text,
   station_interva      text,
   ag_name              text,
   primary key (ag_id),
   foreign key (nav_id) references offline_nav (nav_id) on delete restrict on update restrict
);

/*==============================================================*/
/* Table: offline_formsfile_attr                                */
/*==============================================================*/
create table offline_formsfile_attr
(
   ta_id                text NOT NULL,
   documentor_id        text,
   nav_id               text,
   box_code             text,
   elect_file_id        text,
   scan_file_id         text,
   tb_id                text,
   ag_id                text,
   block_code           text,
   single_project_code  text,
   duty_officer         text,
   create_date          text,
   station_interval     text,
   page_num             text,
   page_no              text,
   memo                 text,
   document_number      text,
   fnsort_table         integer,
   document_no          text,
   file_title           text,
   retention_period     text,
   ssecrecy_level       text,
   archived_copies      integer,
   paper_size           text,
   total_pages          integer,
   is_third_file        integer,
   progress             integer,
   class_code           text,
   class_name           text,
   catalog_id           text,
   primary key (ta_id),
   foreign key (documentor_id) references offline_contractor_documentor (documentor_id) on delete restrict on update restrict,
   foreign key (nav_id) references offline_nav (nav_id) on delete restrict on update restrict,
   foreign key (box_code) references offline_achive_box (box_code) on delete restrict on update restrict
);

/*==============================================================*/
/* Table: offline_formsfiles                                    */
/*==============================================================*/
create table offline_formsfiles
(
   tb_id                text NOT NULL,
   nav_id               text,
   tmpl_file_id         text,
   example_file_id      text,
   document_number      text NOT NULL,
   fnsort_table         integer,
   document_no          text,
   file_title           text,
   retention_period     text,
   ssecrecy_level       text,
   archived_copies      integer,
   paper_size           text,
   total_pages          integer,
   fill_in_rules        text,
   create_date          text,
   last_date            text,
   two_dimension_code   text,
   primary key (tb_id),
   foreign key (nav_id) references offline_nav (nav_id) on delete restrict on update restrict
);

/*==============================================================*/
/* Table: offline_nav                                           */
/*==============================================================*/
create table offline_nav
(
   nav_id               text NOT NULL,
   parent_id            text,
   id_type              integer,
   code                 text,
   name                 text,
   file_type            text,
   memo                 text,
   id_status            integer,
   disp_order           integer,
   user_add             integer,
   primary key (nav_id),
   foreign key (parent_id) references offline_nav (nav_id) on delete cascade on update restrict
);

/*==============================================================*/
/* Table: offline_sys_fixed                                     */
/*==============================================================*/
create table offline_sys_fixed
(
   id                   integer NOT NULL,
   fix_code             text,
   fix_name             text,
   fix_value            text,
   fix_col_type         text,
   fix_type             integer,
   fix_state            text,
   remark               text,
   primary key (id)
);

/*==============================================================*/
/* Table: offline_version                                       */
/*==============================================================*/
create table offline_version
(
   version_id           integer NOT NULL,
   version_num          text NOT NULL,
   version_name         text,
   version_memo         text,
   create_date          text,
   last_date            text,
   lib_id               integer,
   primary key (version_id)
);