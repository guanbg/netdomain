DELIMITER $$

DROP PROCEDURE IF EXISTS sp_offline_pack$$

CREATE PROCEDURE sp_offline_pack(IN in_pack_filepath VARCHAR(256), OUT out_ret INT)
ME:BEGIN
    DECLARE v_ids VARCHAR(9600);
    DECLARE v_tmp VARCHAR(9600);
    DECLARE v_id VARCHAR(64);
    DECLARE v_cnt INT;
    
    SET out_ret = -1;  
    SET max_sp_recursion_depth = 255;
    
    CREATE TEMPORARY TABLE IF NOT EXISTS libadd(id VARCHAR(64)) ENGINE=MEMORY;
    CREATE TEMPORARY TABLE IF NOT EXISTS libdel(id VARCHAR(64)) ENGINE=MEMORY;

    SET group_concat_max_len=9600;
    SELECT COUNT(1), GROUP_CONCAT(id) INTO v_cnt, v_ids FROM libdel;
    IF v_cnt > 0 THEN
	SET v_tmp = REPLACE(v_ids,',',''''',''''');
	-- SELECT v_tmp;
	-- SELECT CONCAT('DELETE FROM offline_formsfiles WHERE nav_id IN (''',v_tmp,''')') INTO OUTFILE CONCAT(in_pack_filepath,'1_offline_formsfiles_del.sql');
	-- SELECT CONCAT('DELETE FROM offline_nav WHERE nav_id IN (''',v_tmp,''')') INTO OUTFILE CONCAT(in_pack_filepath,'2_offline_nav_del.sql');
	SET out_ret = -2;
	SET @SQLStr=CONCAT('SELECT ''DELETE FROM offline_formsfiles WHERE nav_id IN (''''',v_tmp,''''');\r\n''',' INTO OUTFILE ''',in_pack_filepath,'1_offline_formsfiles_del.sql''');
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
	
	SET out_ret = -3;
	SET @SQLStr=CONCAT('SELECT ''DELETE FROM offline_nav WHERE nav_id IN (''''',v_tmp,''''');\r\n''',' INTO OUTFILE ''',in_pack_filepath,'2_offline_nav_del.sql''');
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    END IF;
    
    SELECT COUNT(1), GROUP_CONCAT(id) INTO v_cnt, v_ids FROM libadd;
    IF v_cnt > 0 THEN
	/*SELECT fs_ID,  parent_ID,  ID_type,  ID_status, fs_code,  fs_name,  fs_memo,  disp_order
	FROM fms_library 
	WHERE fs_id IN (SELECT id FROM libadd)
	INTO OUTFILE CONCAT(in_pack_filepath,'3_offline_nav_add.sql')
	FIELDS
		TERMINATED BY '\,'
		ESCAPED BY ''
		OPTIONALLY ENCLOSED BY ''''
	LINES
		STARTING BY 'insert into offline_nav(nav_id, parent_id, ID_type, ID_status, code, name, memo, disp_order) values('
		TERMINATED BY ');\r\n';
	*/
	SET out_ret = -4;
	SET @SQLStr=CONCAT(
	    'SELECT fs_ID,  parent_ID,  ID_type,  ID_status, fs_code,  fs_name,  fs_memo,  disp_order ',
	    'FROM fms_library ',
	    'WHERE fs_id IN (SELECT id FROM libadd) ',
	    'INTO OUTFILE ''',in_pack_filepath,'3_offline_nav_add.sql'' ',
	    'FIELDS TERMINATED BY ''\,'' ESCAPED BY'''' OPTIONALLY ENCLOSED BY '''''''' ',
	    'LINES STARTING BY ''insert into offline_nav(nav_id, parent_id, ID_type, ID_status, code, name, memo, disp_order) values('' TERMINATED BY '');\r\n'''
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
	
	/*SELECT A.tb_id,B.fs_id,
	    (SELECT file_identify FROM fms_files WHERE file_id=A.tmpl_file_id) AS tmpl_file_identify,
	    (SELECT file_identify FROM fms_files WHERE file_id=A.example_file_id) AS example_file_identify,
	    A.document_number,B.tb_sn,A.document_no,A.file_title,A.retention_period,A.ssecrecy_level,A.archived_copies,A.paper_size,A.total_pages,A.fill_in_rules,A.create_date,A.last_date 
	FROM fms_formsfiles A INNER JOIN fms_foldertemplate_formsfiles B ON A.tb_id=B.tb_id 
	WHERE B.fs_id IN (SELECT id FROM libadd)
	INTO OUTFILE CONCAT(in_pack_filepath,'4_offline_formsfiles_add.sql')
	FIELDS
		TERMINATED BY '\,'
		ESCAPED BY ''
		OPTIONALLY ENCLOSED BY ''''
	LINES
		STARTING BY 'insert into offline_formsfiles(tb_id, nav_id, tmpl_file_id, example_file_id, document_number, fnsort_table, document_no, file_title,retention_period,ssecrecy_level,archived_copies,paper_size,total_pages,fill_in_rules,create_date,last_date) values('
		TERMINATED BY ');\r\n';
	*/	
	SET out_ret = -5;
	SET @SQLStr=CONCAT(
	    'SELECT A.tb_id,B.fs_id, (SELECT file_identify FROM fms_files WHERE file_id=A.tmpl_file_id) AS tmpl_file_identify, (SELECT file_identify FROM fms_files WHERE file_id=A.example_file_id) AS example_file_identify,',
	    'A.document_number,B.tb_sn,A.document_no,A.file_title,A.retention_period,A.ssecrecy_level,A.archived_copies,A.paper_size,A.total_pages,A.fill_in_rules,A.create_date,A.last_date ',
	    'FROM fms_formsfiles A INNER JOIN fms_foldertemplate_formsfiles B ON A.tb_id=B.tb_id  ',
	    'WHERE B.fs_id IN (SELECT id FROM libadd) ',
	    'INTO OUTFILE ''',in_pack_filepath,'4_offline_formsfiles_add.sql'' ',
	    'FIELDS TERMINATED BY ''\,'' ESCAPED BY'''' OPTIONALLY ENCLOSED BY '''''''' ',
	    'LINES STARTING BY ''insert into offline_formsfiles(tb_id, nav_id, tmpl_file_id, example_file_id, document_number, fnsort_table, document_no, file_title,retention_period,ssecrecy_level,archived_copies,paper_size,total_pages,fill_in_rules,create_date,last_date) values('' TERMINATED BY '');\r\n'''
	);
	-- SELECT @SQLStr;
	PREPARE stmt FROM @SQLStr;
	EXECUTE stmt;
    END IF;
    
    SET out_ret = 0;
END ME$$

DELIMITER ;