DELIMITER $$

DROP PROCEDURE IF EXISTS sp_create_version$$

CREATE PROCEDURE sp_create_version(
	IN in_lib_id VARCHAR(64), 
	IN in_version_num VARCHAR(128), 
	IN in_version_name VARCHAR(256),
	IN in_version_memo VARCHAR(512),
	IN in_disp_order INT,
	IN in_create_user INT,
	OUT out_version_id INT,
	OUT out_ret INT)
ME:BEGIN
    DECLARE v_version_id INT;-- 版本序号对应表后缀
    DECLARE v_lvl INT;
    DECLARE v_cnt INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION SET out_ret = -99;-- 数据错误
    
    SET out_ret = -999;  
    SET max_sp_recursion_depth = 255;
        
    SELECT COUNT(1) INTO v_cnt FROM fms_library WHERE fs_id=in_lib_id;
    IF v_cnt <= 0 THEN
	SET out_ret = -1;-- 库不存在
        LEAVE ME;
    END IF;
    
    SELECT COUNT(1) INTO v_cnt FROM fms_version WHERE version_num=in_version_num;
    IF v_cnt > 0 THEN
	SET out_ret = -2;-- 版本已存在
        LEAVE ME;
    END IF;
    
    START TRANSACTION;
    SET out_ret = -3;-- 写入版本信息错误
    INSERT INTO fms_version(version_num,version_name,version_memo,create_user,create_date,disp_order,fs_id,lib_type)
    VALUES(in_version_num,in_version_name,in_version_memo,in_create_user,NOW(),in_disp_order,in_lib_id,(SELECT id_type FROM fms_library WHERE fs_id=in_lib_id));
    SELECT LAST_INSERT_ID() INTO v_version_id; 
    SET out_version_id = v_version_id;     
    
    SET out_ret = -4;-- 创建fms_files表错误
    SET @SQLStr=CONCAT(
	'CREATE TABLE fms_files_',v_version_id,'(',
	'file_id            VARCHAR(64) NOT NULL,',
	'file_size          BIGINT,',
	'file_name          VARCHAR(256),',
	'file_path          VARCHAR(512),',
	'file_identify      TEXT,',
	'file_type          VARCHAR(32),',
	'file_status        INT,',
	'two_dimension_code VARCHAR(256),',
	'create_user        INT,',
	'create_date        DATETIME,',
	'last_user          INT,',
	'last_date          DATETIME,',
	'PRIMARY KEY (file_id)',
	')'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -5;-- 创建fms_files表错误
    SET @SQLStr=CONCAT(
	'CREATE TABLE fms_formsfiles_dir_',v_version_id,'(',
	'dir_id               varchar(64) NOT NULL,',
	'parent_id            varchar(64),',
	'dir_name             varchar(512),',
	'disp_order           int,',
	'dir_icon             varchar(64),',
	'PRIMARY KEY (dir_id)',
	')'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -6;-- 创建fms_formsfiles表错误
    SET @SQLStr=CONCAT(
	'CREATE TABLE fms_formsfiles_',v_version_id,'(',
	'tb_id            VARCHAR(64) NOT NULL,',
	'parent_id        VARCHAR(64),',
	'dir_id               varchar(64),',
	'tmpl_file_id     VARCHAR(64),',
	'example_file_id  VARCHAR(64),',
	'document_number  VARCHAR(128) NOT NULL,',
	'fnsort_table     INT,',
	'document_no      VARCHAR(64),',
	'file_title       VARCHAR(256),',
	'retention_period VARCHAR(64),',
	'ssecrecy_level   VARCHAR(32),',
	'archived_copies  INT,',
	'paper_size       VARCHAR(16),',
	'total_pages      INT,',
	'fill_in_rules    LONGTEXT,',
	'tb_version       VARCHAR(32),',
	'create_user      INT,',
	'create_date      DATETIME,',
	'last_user        INT,',
	'last_date        DATETIME,',
	'PRIMARY KEY (tb_id)',
	')'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    /*
    SET @SQLStr=CONCAT(
	' ALTER TABLE fms_formsfiles_',v_version_id,' ADD CONSTRAINT FK_Reference_49_',v_version_id,' FOREIGN KEY (parent_id) REFERENCES fms_formsfiles_',v_version_id,' (tb_id) ON DELETE CASCADE ON UPDATE RESTRICT;'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    */
    SET out_ret = -7;-- 创建fms_library表错误
    SET @SQLStr=CONCAT(
	'CREATE TABLE fms_library_',v_version_id,'(',
	'fs_ID              VARCHAR(64) NOT NULL,',
	'parent_ID          VARCHAR(64),',
	'fs_code            VARCHAR(64),',
	'fs_name            VARCHAR(2048),',
	'fs_memo            TEXT,',
	'ID_type            INT,',
	'ID_status          INT,',
	'disp_order         INT,',
	'create_user        INT,',
	'create_date        DATETIME,',
	'last_user          INT,',
	'last_date          DATETIME,',
	'PRIMARY KEY (fs_ID)',
	')'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    /*
    SET @SQLStr=CONCAT(
	'ALTER TABLE fms_library_',v_version_id,' ADD CONSTRAINT FK_Reference_44_',v_version_id,' FOREIGN KEY (parent_ID) REFERENCES fms_library_',v_version_id,' (fs_ID) ON DELETE CASCADE ON UPDATE RESTRICT;'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    */
    SET out_ret = -8;-- 创建fms_foldertemplate_formsfiles表错误
    SET @SQLStr=CONCAT(
	'CREATE TABLE fms_foldertemplate_formsfiles_',v_version_id,'(',
	'tb_id          VARCHAR(64) NOT NULL,',
	'fs_ID          VARCHAR(64),',
	'tb_sn          INT,',
	'PRIMARY KEY (tb_id, fs_ID)',
	')'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -9;-- 创建fms_foldertemplate_formsfiles表外键错误
    SET @SQLStr=CONCAT(
	'ALTER TABLE fms_foldertemplate_formsfiles_',v_version_id,' ADD CONSTRAINT FK_Reference_17_',v_version_id,' FOREIGN KEY (tb_id) REFERENCES fms_formsfiles_',v_version_id,' (tb_id) ON DELETE RESTRICT ON UPDATE RESTRICT;'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -10;-- 创建fms_foldertemplate_formsfiles表外键错误
    SET @SQLStr=CONCAT(
	'ALTER TABLE fms_foldertemplate_formsfiles_',v_version_id,' ADD CONSTRAINT FK_Reference_48_',v_version_id,' FOREIGN KEY (fs_ID) REFERENCES fms_library_',v_version_id,' (fs_ID) ON DELETE CASCADE ON UPDATE RESTRICT;'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -11;-- 创建fms_foldertemplate表错误
    SET @SQLStr=CONCAT(
	'CREATE TABLE fms_foldertemplate_',v_version_id,'(',
	'ag_ID                    VARCHAR(64) NOT NULL,',
	'fs_ID                    VARCHAR(64),',
	'retention_period         VARCHAR(32),',
	'security_classification  VARCHAR(32),',
	'document_number          VARCHAR(128),',
	'catalog_code             VARCHAR(128),',
	'start_date               DATETIME,',
	'end_date                 DATETIME,',
	'organization             VARCHAR(128),',
	'organize_date            DATETIME,',
	'piece_number             INT,',
	'archived_copies          INT,',
	'total_pages              INT,',
	'drawing_number           INT,',
	'written_number           INT,',
	'photo_number             INT,',
	'build_user               VARCHAR(64),',
	'build_date               DATETIME,',
	'check_user               VARCHAR(64),',
	'check_date               DATETIME,',
	'storage_location         VARCHAR(128),',
	'vice_location            VARCHAR(128),',
	'handed_status            VARCHAR(16),',
	'city_construction        VARCHAR(128),',
	'create_user              INT,',
	'create_date              DATETIME,',
	'last_user                INT,',
	'last_date                DATETIME,',
	'PRIMARY KEY (ag_ID)',
	')'
    );
    
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;    

    SET out_ret = -12;-- 创建fms_foldertemplate表外键错误
    SET @SQLStr=CONCAT(
	'ALTER TABLE fms_foldertemplate_',v_version_id,' ADD CONSTRAINT FK_Reference_31_',v_version_id,' FOREIGN KEY (fs_ID) REFERENCES fms_library_',v_version_id,' (fs_ID) ON DELETE CASCADE ON UPDATE RESTRICT;'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -13;-- 查询子节点错误

    CREATE TEMPORARY TABLE IF NOT EXISTS libchild(id VARCHAR(64)) ENGINE=MEMORY;
    INSERT INTO libchild(id) VALUES(in_lib_id);
    CALL sp_copy_sublib(in_lib_id, @ret);
    
    SET out_ret = -14;-- 向表fms_files插入模板数据错误
    SET @SQLStr=CONCAT(
	'INSERT INTO fms_files_',v_version_id,'(file_id,file_size,file_name,file_path,file_identify,file_type,two_dimension_code,create_user,create_date,last_date,last_user,file_status) ',
	'SELECT file_id,file_size,file_name,file_path,file_identify,file_type,two_dimension_code,create_user,create_date,last_date,last_user,file_status ',
	'FROM fms_files ',
	'WHERE file_id IN (',
	    'SELECT tmpl_file_id ',
	    'FROM fms_formsfiles ',
	    'WHERE tb_id IN (',
		'SELECT tb_id ',
		'FROM fms_foldertemplate_formsfiles ',
		'WHERE fs_ID IN (SELECT id FROM libchild)',
	    ') ',
	')'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -15;-- 向表fms_files插入样板数据错误
    SET @SQLStr=CONCAT(
	'INSERT INTO fms_files_',v_version_id,'(file_id,file_size,file_name,file_path,file_identify,file_type,two_dimension_code,create_user,create_date,last_date,last_user,file_status) ',
	'SELECT file_id,file_size,file_name,file_path,file_identify,file_type,two_dimension_code,create_user,create_date,last_date,last_user,file_status ',
	'FROM fms_files ',
	'WHERE file_id IN (',
	    'SELECT example_file_id ',
	    'FROM fms_formsfiles ',
	    'WHERE tb_id IN (',
		'SELECT tb_id ',
		'FROM fms_foldertemplate_formsfiles ',
		'WHERE fs_ID IN (SELECT id FROM libchild)',
	    ')',
	')'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -16;-- 向表fms_formsfiles_dir插入样板数据错误
    SET @SQLStr=CONCAT(
	'INSERT INTO fms_formsfiles_dir_',v_version_id,'(dir_id,parent_id,dir_name,disp_order,dir_icon) ',
	'SELECT dir_id,parent_id,dir_name,disp_order,dir_icon ',
	'FROM fms_formsfiles_dir '
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -17;-- 向表fms_formsfiles插入数据错误
    SET @SQLStr=CONCAT(
	'INSERT INTO fms_formsfiles_',v_version_id,'(tb_id,parent_id,dir_id,tmpl_file_id,example_file_id,document_number,fnsort_table,document_no,file_title,retention_period,ssecrecy_level,archived_copies,paper_size,total_pages,fill_in_rules,create_date,create_user,last_date,last_user,tb_version) ',
	'SELECT tb_id,parent_id,dir_id,tmpl_file_id,example_file_id,document_number,fnsort_table,document_no,file_title,retention_period,ssecrecy_level,archived_copies,paper_size,total_pages,fill_in_rules,create_date,create_user,last_date,last_user,tb_version ',
	'FROM fms_formsfiles ',
	'WHERE tb_id IN (',
	    'SELECT tb_id ',
	    'FROM fms_foldertemplate_formsfiles ',
	    'WHERE fs_ID IN (SELECT id FROM libchild)'
	')'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -18;-- 向表fms_library插入数据错误
    SET @SQLStr=CONCAT(
	'INSERT INTO fms_library_',v_version_id,'(fs_ID,parent_ID,fs_code,fs_name,fs_memo,create_date,create_user,last_date,last_user,disp_order,ID_type,ID_status) ',
	'SELECT fs_ID,parent_ID,fs_code,fs_name,fs_memo,create_date,create_user,last_date,last_user,disp_order,ID_type,ID_status ',
	'FROM fms_library ',
	'WHERE fs_ID IN (SELECT id FROM libchild)'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -19;-- 向表fms_foldertemplate_formsfiles插入数据错误
    SET @SQLStr=CONCAT(
	'INSERT INTO fms_foldertemplate_formsfiles_',v_version_id,'(fs_ID,tb_id,tb_sn) ',
	'SELECT fs_ID,tb_id,tb_sn ',
	'FROM fms_foldertemplate_formsfiles ',
	'WHERE fs_ID IN (SELECT id FROM libchild)'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    SET out_ret = -20;-- 向表fms_foldertemplate插入数据错误
    SET @SQLStr=CONCAT(
	'INSERT INTO fms_foldertemplate_',v_version_id,'(ag_ID,fs_ID,retention_period,security_classification,document_number,catalog_code,start_date,end_date,organization,organize_date,piece_number,archived_copies,total_pages,drawing_number,written_number,photo_number,build_user,build_date,check_user,check_date,storage_location,vice_location,handed_status,city_construction,create_date,create_user,last_date,last_user) ',
	'SELECT ag_ID,fs_ID,retention_period,security_classification,document_number,catalog_code,start_date,end_date,organization,organize_date,piece_number,archived_copies,total_pages,drawing_number,written_number,photo_number,build_user,build_date,check_user,check_date,storage_location,vice_location,handed_status,city_construction,create_date,create_user,last_date,last_user ',
	'FROM fms_foldertemplate ',
	'WHERE fs_ID IN (SELECT id FROM libchild)'
    );
    PREPARE stmt FROM @SQLStr;
    EXECUTE stmt;
    
    DEALLOCATE PREPARE stmt;
    DROP TEMPORARY TABLE libchild;
    
    SET out_ret = 0;-- 成功
    
    IF out_ret < 0 THEN
	ROLLBACK;
    ELSE
	COMMIT;
    END IF;
END ME$$

DELIMITER ;