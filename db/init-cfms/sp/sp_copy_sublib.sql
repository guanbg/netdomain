DELIMITER $$

DROP PROCEDURE IF EXISTS sp_copy_sublib$$

CREATE PROCEDURE sp_copy_sublib(IN in_id VARCHAR(64), OUT out_ret INT)
ME:BEGIN
    DECLARE v_ids VARCHAR(8000);
    DECLARE v_id VARCHAR(64);
    DECLARE v_cnt INT;

    SET out_ret = -1;  
    SET max_sp_recursion_depth = 255;
        
    IF in_id IS NULL OR LENGTH(in_id) <= 0 THEN
        LEAVE ME;
    END IF;
    
    CREATE TEMPORARY TABLE IF NOT EXISTS libchild(id VARCHAR(64)) ENGINE=MEMORY;

    INSERT INTO libchild(id) SELECT fs_ID FROM fms_library WHERE parent_ID=in_id;
    SET v_cnt = ROW_COUNT();	
    IF v_cnt <= 0 THEN
	SET out_ret = 0; 
	LEAVE ME;
    END IF;
	
    SET group_concat_max_len=8000;
    SELECT COUNT(1), GROUP_CONCAT(fs_ID) INTO v_cnt, v_ids 
    FROM fms_library 
    WHERE parent_ID=in_id;

    WHILE v_cnt > 0 DO
        SET v_cnt = v_cnt - 1;
        SET v_id = SUBSTRING_INDEX(v_ids,',',1);
        SET v_ids = SUBSTRING(v_ids,LENGTH(v_id)+2);
        
        -- SET out_ret = -1;  
        CALL sp_copy_sublib(v_id, @ret);
        /*SET out_ret = @ret;
        IF out_ret < 0 THEN
            LEAVE ME;
        END IF;*/
        
        SET out_ret = 0;
        IF v_ids IS NULL OR LENGTH(v_ids) <= 0 THEN
            LEAVE ME;
        END IF;
    END WHILE;
    
    SET out_ret = 0;
END ME$$

DELIMITER ;