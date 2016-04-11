-- set noAccessToProcedureBodies=true;
set global log_bin_trust_function_creators=TRUE;

source table_sys.sql;
source table_cfms.sql;

set autocommit=0;
begin;
	
source data/sys_init.sql;
source data/fms_config.sql;
source data/fms_businessrule.sql;
source data/fms_businessrule_clause.sql;
source data/fms_library.sql;
source data/fms_files.sql;
source data/fms_formsfiles.sql;

commit;
set autocommit=1;

source sp/fn_getChildren.sql;
source sp/fn_getConfigVersionChildren.sql;
source sp/fn_hasVersion.sql;

source sp/sp_copy_library.sql;
source sp/sp_copy_param.sql;
source sp/sp_copy_sublib.sql;
source sp/sp_create_version.sql;

source sp/sp_offline_pack.sql;
source sp/sp_create_version_pack.sql;

source table_constraint.sql;
