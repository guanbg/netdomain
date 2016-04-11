1.首先创建数据库 metrorcfms，数据库字符集为utf8
2.在创建的数据库中设置以下参数(可跳过该步骤)：
	GRANT SELECT ON mysql.proc TO '用户名称'@%; 或 
	GRANT SELECT ON mysql.proc TO '用户名称'@'localhost';
	set noAccessToProcedureBodies=true; --存储过程运行许可
	set global log_bin_trust_function_creators=TRUE;-- 函数运行许可

3.执行run.bat，运行完毕后数据库即初始化完毕,执行时间大约2分钟，请等待。

4.目录2016为表格模板，放到部署目录所设置的上传路径下即可