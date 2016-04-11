DELIMITER $$

DROP PROCEDURE IF EXISTS sp_copy_param$$

CREATE PROCEDURE sp_copy_param(IN in_srcid BIGINT, IN in_distid BIGINT, IN in_create_user BIGINT, OUT out_ret INT)
ME:BEGIN
    DECLARE v_chlidren_ids VARCHAR(8000);
    DECLARE v_srcid VARCHAR(32);
    DECLARE v_distid BIGINT;
    DECLARE v_cnt INT;
    
    SET out_ret = -1;  
    SET max_sp_recursion_depth = 255;
        
    SELECT COUNT(1) INTO v_cnt FROM fms_config WHERE id=in_srcid;
    IF v_cnt <= 0 THEN
        SET out_ret = 0; 
        LEAVE ME;
    END IF;
    
    SET group_concat_max_len=8000;
    SELECT COUNT(1), GROUP_CONCAT(id) INTO v_cnt, v_chlidren_ids 
    FROM fms_config 
    WHERE parentid=in_srcid;

    INSERT INTO fms_config(identity,nodetype,disptype,disporder,STATUS,CODE,NAME,memo,value1,value2,value3,value4,value5,extend,parentID,create_user,create_date) 
    SELECT identity,nodetype,disptype,disporder,STATUS,CODE,NAME,memo,value1,value2,value3,value4,value5,extend,in_distid parentID,in_create_user create_user,NOW() create_date
    FROM fms_config
    WHERE id=in_srcid;

    SELECT LAST_INSERT_ID() INTO v_distid; 
    
    WHILE v_cnt > 0 DO
        SET v_cnt = v_cnt - 1;
        SET v_srcid = SUBSTRING_INDEX(v_chlidren_ids,',',1);
        SET v_chlidren_ids = SUBSTRING(v_chlidren_ids,LENGTH(v_srcid)+2);
        
        -- SET out_ret = -1;  
        CALL sp_copy_param(CAST(v_srcid AS UNSIGNED), v_distid, in_create_user, @ret);
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