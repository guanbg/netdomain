DELIMITER $$

DROP FUNCTION IF EXISTS fn_getChildren$$

CREATE
    FUNCTION fn_getChildren(in_id VARCHAR(64) )
    RETURNS VARCHAR(8000)
    BEGIN
	DECLARE ret VARCHAR(8000);
	DECLARE tmp VARCHAR(4000);
	
	SET group_concat_max_len=8000;
	SET ret = '$';
	SET tmp = in_id;
	
	WHILE tmp IS NOT NULL DO
	    IF tmp != in_id THEN
		SET ret = CONCAT(ret,',',tmp);
	    END IF;
	    SELECT GROUP_CONCAT(tb_id) INTO tmp FROM fms_formsfiles WHERE FIND_IN_SET(parent_id,tmp)>0;
	END WHILE;
	
	RETURN ret;
    END$$

DELIMITER ;