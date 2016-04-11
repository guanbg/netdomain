@echo off
title 数据库初始化
COLOR 02

:params
echo.
set /p mysqlpath=请输入MYSQL命令绝对路径 ，（不输入则从系统环境变量中读取）：
set /p dbname=请输入数据库名称,（不输入默认ndcfms） ：
set /p dbuser=请输入连接用户,（不输入默认cfms） ：
:pswd
set /p dbpswd=请输入用户密码 ：

if {%dbname%}=={} set dbname=ndcfms
if {%dbuser%}=={} set dbuser=cfms
if {%dbpswd%}=={} goto pswd

echo.
echo 您输入的信息如下：
echo 	*数据库名称：%dbname%		*连接用户：%dbuser%		*用户密码：%dbpswd%		*MYSQL路径：%mysqlpath%

CHOICE /C YRC /M "确认请按 Y，重新输入请按 R，取消请按 C，"
if ERRORLEVEL 3 goto end
if ERRORLEVEL 2 goto params
if ERRORLEVEL 1 goto run

:run
echo.
echo 开始初始化数据库信息，请耐心等候...
CALL %mysqlpath%mysql -f -u%dbuser% -p%dbpswd% %dbname% < init.sql
if %ERRORLEVEL% EQU 0 (
	echo 运行完毕
) else (
	echo.
	echo 初始化出错	
	echo.
	echo 	*数据库名称：%dbname%		*连接用户：%dbuser%		*用户密码：%dbpswd%		*mysql路径：%mysqlpath%
	CHOICE /C YRC /M "确认请按 Y，重新输入请按 R，取消请按 C，"
	if ERRORLEVEL 3 goto end
	if ERRORLEVEL 2 goto params
	if ERRORLEVEL 1 goto run
)

:end
echo.
pause
