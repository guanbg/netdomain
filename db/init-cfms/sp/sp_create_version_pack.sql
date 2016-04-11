DELIMITER $$

DROP PROCEDURE IF EXISTS sp_create_version_pack$$

CREATE PROCEDURE sp_create_version_pack(
	IN in_base_vid INT, 
	IN in_pack_vid INT, 
	IN in_fs_id_add VARCHAR(9600), 
	IN in_fs_id_del VARCHAR(9600),
	IN in_pack_filepath VARCHAR(256),
	IN in_pack_num VARCHAR(128), 
	IN in_pack_name VARCHAR(256),
	IN in_pack_memo VARCHAR(512),
	IN in_create_user INT,
	OUT out_pack_id INT,
	OUT out_ret INT)
ME:BEGIN
    DECLARE v_prefix VARCHAR(64);
    DECLARE v_id VARCHAR(64);
    DECLARE v_ids VARCHAR(9600);
    DECLARE v_cnt INT;
    DECLARE v_idx INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION SET out_ret = -99;-- 数据错误
    
    SET v_idx = 0;
    SET out_ret = -999;
    SET max_sp_recursion_depth = 255;
        
    IF LENGTH(in_fs_id_add) <= 0 AND LENGTH(in_fs_id_del) <= 0 THEN
	SET out_ret = -1;-- 版本无增减则退出
        LEAVE ME;
    END IF;

    IF LENGTH(in_pack_num) <= 0 OR LENGTH(in_pack_name) <= 0 OR LENGTH(in_pack_filepath) <= 0 THEN
	SET out_ret = -2;-- 数据验证
        LEAVE ME;
    END IF;
    
    SELECT COUNT(1) INTO v_cnt FROM fms_version WHERE version_id=in_pack_vid;
    IF v_cnt <= 0 THEN
	SET out_ret = -3;-- 版本不存在
        LEAVE ME;
    END IF;
    
    IF in_base_vid <= 0 THEN
	SET v_prefix = ' ';
    ELSE
	SET v_prefix = CONCAT('_',in_base_vid,' ');
    END IF;
    
    CREATE TEMPORARY TABLE IF NOT EXISTS libadd(id VARCHAR(64)) ENGINE=MEMORY;
    CREATE TEMPORARY TABLE IF NOT EXISTS libdel(id VARCHAR(64)) ENGINE=MEMORY;
    CREATE TEMPORARY TABLE IF NOT EXISTS libchild(id VARCHAR(64)) ENGINE=MEMORY; -- 下级节点临时表
    
    SET v_ids = in_fs_id_add; -- 解析增加的组卷样板
    WHILE LENGTH(v_ids) > 0 DO
	SET v_id = SUBSTRING_INDEX(v_ids,',',1);
	SET v_ids = SUBSTRING(v_ids,LENGTH(v_id)+2);
	INSERT INTO libadd(id) VALUES(v_id);
	INSERT INTO libdel(id) VALUES(v_id);-- 增加前先清除
	CALL sp_copy_sublib(v_id, @ret);-- 解析下级组卷样板
    END WHILE;
    INSERT INTO libadd(id) SELECT id FROM libchild;--  将下级组卷样板合并
    INSERT INTO libdel(id) SELECT id FROM libchild;-- 增加前先清除
    TRUNCATE TABLE libchild;
    
    SET v_ids = in_fs_id_del;--  解析移除的组卷样板
    WHILE LENGTH(v_ids) > 0 DO
	SET v_id = SUBSTRING_INDEX(v_ids,',',1);
	SET v_ids = SUBSTRING(v_ids,LENGTH(v_id)+2);
	INSERT INTO libdel(id) VALUES(v_id);
	CALL sp_copy_sublib(v_id, @ret);-- 解析下级组卷样板
    END WHILE;
    INSERT INTO libdel(id) SELECT id FROM libchild;--  将下级组卷样板合并
    DROP TEMPORARY TABLE libchild;
    
    START TRANSACTION;
    SET out_ret = -3;-- 写入增量版本信息错误
    INSERT INTO fms_version_pack(version_id,version_num,version_name,version_memo,create_user,create_date)
    VALUES(in_pack_vid,in_pack_num,in_pack_name,in_pack_memo,in_create_user,NOW());
    SELECT LAST_INSERT_ID() INTO out_pack_id; 
    
    SELECT COUNT(1) INTO v_cnt FROM libdel;
    IF v_cnt > 0 THEN -- 判断是否有移除项
	SET out_ret = -4;-- 删除表fms_files模板文件数据错误
	SET @SQLStr=CONCAT(
	'DELETE FROM fms_files_',in_pack_vid,' ', 
	'WHERE file_id IN (',
	    'SELECT tmpl_file_id ',
	    'FROM fms_formsfiles_',in_pack_vid,' ',
	    'WHERE tb_id IN (',
		'SELECT tb_id ',
		'FROM fms_foldertemplate_formsfiles_',in_pack_vid,' ',
		'WHERE fs_ID IN (SELECT id FROM libdel)',
	    ') ',
	')'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
	
	SET out_ret = -5;-- 删除表fms_files样板文件数据错误
	SET @SQLStr=CONCAT(
	'DELETE FROM fms_files_',in_pack_vid,' ', 
	'WHERE file_id IN (',
	    'SELECT example_file_id ',
	    'FROM fms_formsfiles_',in_pack_vid,' ',
	    'WHERE tb_id IN (',
		'SELECT tb_id ',
		'FROM fms_foldertemplate_formsfiles_',in_pack_vid,' ',
		'WHERE fs_ID IN (SELECT id FROM libdel)',
	    ') ',
	')'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
	
	SET out_ret = -7;-- 向表fms_formsfiles插入数据错误
	SET @SQLStr=CONCAT(
	'DELETE FROM fms_formsfiles_',in_pack_vid,' ',
	'WHERE tb_id IN (',
	    'SELECT tb_id ',
	    'FROM fms_foldertemplate_formsfiles ',
	    'WHERE fs_ID IN (SELECT id FROM libdel)'
	')'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
	
	SET out_ret = -8;-- 向表fms_library插入数据错误
	SET @SQLStr=CONCAT(
	'DELETE FROM fms_library_',in_pack_vid,' WHERE fs_ID IN (SELECT id FROM libdel)'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    
	SET out_ret = -9;-- 向表fms_foldertemplate_formsfiles插入数据错误
	SET @SQLStr=CONCAT(
	'DELETE FROM fms_foldertemplate_formsfiles_',in_pack_vid,' WHERE fs_ID IN (SELECT id FROM libdel)'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    
	SET out_ret = -10;-- 向表fms_foldertemplate插入数据错误
	SET @SQLStr=CONCAT(
	'DELETE FROM fms_foldertemplate_',in_pack_vid,' WHERE fs_ID IN (SELECT id FROM libdel)'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    END IF;
    
    SELECT COUNT(1) INTO v_cnt FROM libadd;
    IF v_cnt > 0 THEN -- 判断是否有新增项
	SET out_ret = -14;-- 向表fms_files插入模板数据错误
	SET @SQLStr=CONCAT(
	'INSERT INTO fms_files_',in_pack_vid,'(file_id,file_size,file_name,file_path,file_identify,file_type,two_dimension_code,create_user,create_date,last_date,last_user,file_status) ',
	'SELECT file_id,file_size,file_name,file_path,file_identify,file_type,two_dimension_code,create_user,create_date,last_date,last_user,file_status ',
	'FROM fms_files',v_prefix,
	'WHERE file_id IN (',
	    'SELECT tmpl_file_id ',
	    'FROM fms_formsfiles',v_prefix,
	    'WHERE tb_id IN (',
		'SELECT tb_id ',
		'FROM fms_foldertemplate_formsfiles',v_prefix,
		'WHERE fs_ID IN (SELECT id FROM libadd)',
	    ') ',
	')'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    
	SET out_ret = -15;-- 向表fms_files插入样板数据错误
	SET @SQLStr=CONCAT(
	'INSERT INTO fms_files_',in_pack_vid,'(file_id,file_size,file_name,file_path,file_identify,file_type,two_dimension_code,create_user,create_date,last_date,last_user,file_status) ',
	'SELECT file_id,file_size,file_name,file_path,file_identify,file_type,two_dimension_code,create_user,create_date,last_date,last_user,file_status ',
	'FROM fms_files',v_prefix,
	'WHERE file_id IN (',
	    'SELECT example_file_id ',
	    'FROM fms_formsfiles',v_prefix,
	    'WHERE tb_id IN (',
		'SELECT tb_id ',
		'FROM fms_foldertemplate_formsfiles',v_prefix,
		'WHERE fs_ID IN (SELECT id FROM libadd)',
	    ')',
	')'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    
	SET out_ret = -16;-- 向表fms_formsfiles_dir插入样板数据错误
	SET @SQLStr=CONCAT(
	'INSERT INTO fms_formsfiles_dir_',in_pack_vid,'(dir_id,parent_id,dir_name,disp_order,dir_icon) ',
	'SELECT dir_id,parent_id,dir_name,disp_order,dir_icon ',
	'FROM fms_formsfiles_dir',v_prefix,
	'WHERE dir_id NOT IN (',
	    'SELECT dir_id FROM fms_formsfiles_dir_',in_pack_vid,
	')'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    
	SET out_ret = -17;-- 向表fms_formsfiles插入数据错误
	SET @SQLStr=CONCAT(
	'INSERT INTO fms_formsfiles_',in_pack_vid,'(tb_id,parent_id,dir_id,tmpl_file_id,example_file_id,document_number,fnsort_table,document_no,file_title,retention_period,ssecrecy_level,archived_copies,paper_size,total_pages,fill_in_rules,create_date,create_user,last_date,last_user,tb_version) ',
	'SELECT tb_id,parent_id,dir_id,tmpl_file_id,example_file_id,document_number,fnsort_table,document_no,file_title,retention_period,ssecrecy_level,archived_copies,paper_size,total_pages,fill_in_rules,create_date,create_user,last_date,last_user,tb_version ',
	'FROM fms_formsfiles',v_prefix,
	'WHERE tb_id IN (',
	    'SELECT tb_id ',
	    'FROM fms_foldertemplate_formsfiles',v_prefix,
	    'WHERE fs_ID IN (SELECT id FROM libadd)'
	')'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    
	SET out_ret = -18;-- 向表fms_library插入数据错误
	SET @SQLStr=CONCAT(
	'INSERT INTO fms_library_',in_pack_vid,'(fs_ID,parent_ID,fs_code,fs_name,fs_memo,create_date,create_user,last_date,last_user,disp_order,ID_type,ID_status) ',
	'SELECT fs_ID,parent_ID,fs_code,fs_name,fs_memo,create_date,create_user,last_date,last_user,disp_order,ID_type,ID_status ',
	'FROM fms_library',v_prefix,
	'WHERE fs_ID IN (SELECT id FROM libadd)'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    
	SET out_ret = -19;-- 向表fms_foldertemplate_formsfiles插入数据错误
	SET @SQLStr=CONCAT(
	'INSERT INTO fms_foldertemplate_formsfiles_',in_pack_vid,'(fs_ID,tb_id,tb_sn) ',
	'SELECT fs_ID,tb_id,tb_sn ',
	'FROM fms_foldertemplate_formsfiles',v_prefix,
	'WHERE fs_ID IN (SELECT id FROM libadd)'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    
	SET out_ret = -20;-- 向表fms_foldertemplate插入数据错误
	SET @SQLStr=CONCAT(
	'INSERT INTO fms_foldertemplate_',in_pack_vid,'(ag_ID,fs_ID,retention_period,security_classification,document_number,catalog_code,start_date,end_date,organization,organize_date,piece_number,archived_copies,total_pages,drawing_number,written_number,photo_number,build_user,build_date,check_user,check_date,storage_location,vice_location,handed_status,city_construction,create_date,create_user,last_date,last_user) ',
	'SELECT ag_ID,fs_ID,retention_period,security_classification,document_number,catalog_code,start_date,end_date,organization,organize_date,piece_number,archived_copies,total_pages,drawing_number,written_number,photo_number,build_user,build_date,check_user,check_date,storage_location,vice_location,handed_status,city_construction,create_date,create_user,last_date,last_user ',
	'FROM fms_foldertemplate',v_prefix,
	'WHERE fs_ID IN (SELECT id FROM libadd)'
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    END IF;
    
    DEALLOCATE PREPARE stmt;
    SET out_ret = -90;
    
    -- SELECT in_pack_filepath;
    CALL sp_offline_pack(in_pack_filepath, @ret);-- 生成离线端增量包
    IF @ret < 0 THEN
	SET out_ret = @ret;
    ELSE
	SET out_ret = 0;-- 成功
    END IF;
    
    DROP TEMPORARY TABLE libdel;
    DROP TEMPORARY TABLE libadd;
    
    IF out_ret < 0 THEN
	ROLLBACK;
    ELSE
	COMMIT;
    END IF;
END ME$$

DELIMITER ;