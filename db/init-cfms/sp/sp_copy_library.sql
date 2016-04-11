DELIMITER $$

DROP PROCEDURE IF EXISTS sp_copy_library$$

CREATE PROCEDURE sp_copy_library(IN in_id VARCHAR(64), IN in_pid VARCHAR(64), OUT out_ret INT)
ME:BEGIN
    DECLARE v_chlidren_ids VARCHAR(8000);-- 保存下级ID
    DECLARE v_id VARCHAR(64);-- 下级的ID
    DECLARE v_pid VARCHAR(64);-- 下级的父ID
    DECLARE v_cnt INT;
    
    SET out_ret = -1;  
    SET max_sp_recursion_depth = 255;
        
    SELECT COUNT(1) INTO v_cnt FROM fms_library WHERE fs_id=in_id;
    IF v_cnt <= 0 THEN
        SET out_ret = 0; 
        LEAVE ME;
    END IF;
    
    SET group_concat_max_len=8000;
    SELECT COUNT(1), GROUP_CONCAT(fs_id) ids INTO v_cnt, v_chlidren_ids 
    FROM fms_library 
    WHERE parent_id=in_id;

    SET v_pid = REPLACE(UUID(),'-','');-- 去掉中线
    IF in_pid IS NULL OR LENGTH(in_pid) <= 0 THEN
        INSERT INTO fms_library(fs_id,parent_id,fs_code,fs_name,fs_name_code,fs_memo,disp_order,id_type,id_status,create_date,create_user,last_user,last_date)
        SELECT v_pid fs_id,parent_id,fs_code,fs_name,fs_name_code,fs_memo,disp_order,id_type,id_status,create_date,create_user,last_user, NOW() last_date
        FROM fms_library
        WHERE fs_id=in_id;
    ELSE
        INSERT INTO fms_library(fs_id,parent_id,fs_code,fs_name,fs_name_code,fs_memo,disp_order,id_type,id_status,create_date,create_user,last_user,last_date)
        SELECT v_pid fs_id,in_pid parent_id,fs_code,fs_name,fs_name_code,fs_memo,disp_order,id_type,id_status,create_date,create_user,last_user, NOW() last_date
        FROM fms_library
        WHERE fs_id=in_id;
    END IF;

    WHILE v_cnt > 0 DO
        SET v_cnt = v_cnt - 1;
        SET v_id = SUBSTRING_INDEX(v_chlidren_ids,',',1);
        SET v_chlidren_ids = SUBSTRING(v_chlidren_ids,LENGTH(v_id)+2);
        
        -- SET out_ret = -1; 
        CALL sp_copy_library(v_id, v_pid, @ret);
        /*SET out_ret = @ret;
        IF out_ret < 0 THEN
            LEAVE ME;
        END IF;*/
        
        SET out_ret = 0; 
        IF v_chlidren_ids IS NULL OR LENGTH(v_chlidren_ids) <= 0 THEN
            LEAVE ME;
        END IF;
    END WHILE;
    SET out_ret = 0; 
END ME$$

DELIMITER ;