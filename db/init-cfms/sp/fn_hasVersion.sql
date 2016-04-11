DELIMITER $$

DROP FUNCTION IF EXISTS fn_hasVersion$$

CREATE
    FUNCTION fn_hasVersion(in_id BIGINT)
    RETURNS BOOLEAN
    BEGIN
	DECLARE ret BOOLEAN;
	DECLARE v_cnt INT;
	DECLARE v_id BIGINT;
	DECLARE done INT DEFAULT FALSE;
	DECLARE cur CURSOR FOR SELECT id FROM fms_config WHERE nodetype=1;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	
	SET ret = FALSE;
	OPEN cur;
	looptag: LOOP
	    FETCH cur INTO v_id;
	    
	    SELECT COUNT(1)
	    INTO v_cnt
	    FROM (
		SELECT @id AS _id, (SELECT @id := parentid FROM fms_config WHERE id=_id) AS parentid
		FROM (SELECT @id := v_id) vars, fms_config a
		WHERE @id <> 0
	    )b
	    WHERE _id=in_id;
	     
	    IF v_cnt > 0 THEN
		SET ret = TRUE;
	        LEAVE looptag;
	    END IF;
	    
	    IF done THEN
	        LEAVE looptag;
	    END IF;
	END LOOP;
	CLOSE cur;	
	
	RETURN ret;
    END$$

DELIMITER ;