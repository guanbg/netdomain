DELIMITER $$

DROP FUNCTION IF EXISTS fn_getConfigVersionChildren$$

CREATE
    FUNCTION fn_getConfigVersionChildren(in_id BIGINT )
    RETURNS VARCHAR(8000)
    BEGIN
	DECLARE ret VARCHAR(8000);
	DECLARE tmp VARCHAR(4000);
	
	SET group_concat_max_len=8000;
	SET ret = '$';
	SET tmp = CAST(in_id AS CHAR);
	
	WHILE tmp IS NOT NULL DO
	    IF tmp != in_id THEN
		SET ret = CONCAT(ret,',',tmp);
	    END IF;
	    SELECT GROUP_CONCAT(id) INTO tmp FROM fms_config WHERE FIND_IN_SET(CAST(parentid AS CHAR),tmp)>0 AND nodetype=1;
	END WHILE;
	
	RETURN ret;
    END$$

DELIMITER ;