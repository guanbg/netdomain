@echo off
title ���ݿ��ʼ��
COLOR 02

:params
echo.
set /p mysqlpath=������MYSQL�������·�� �������������ϵͳ���������ж�ȡ����
set /p dbname=���������ݿ�����,��������Ĭ��ndcfms�� ��
set /p dbuser=�����������û�,��������Ĭ��cfms�� ��
:pswd
set /p dbpswd=�������û����� ��

if {%dbname%}=={} set dbname=ndcfms
if {%dbuser%}=={} set dbuser=cfms
if {%dbpswd%}=={} goto pswd

echo.
echo ���������Ϣ���£�
echo 	*���ݿ����ƣ�%dbname%		*�����û���%dbuser%		*�û����룺%dbpswd%		*MYSQL·����%mysqlpath%

CHOICE /C YRC /M "ȷ���밴 Y�����������밴 R��ȡ���밴 C��"
if ERRORLEVEL 3 goto end
if ERRORLEVEL 2 goto params
if ERRORLEVEL 1 goto run

:run
echo.
echo ��ʼ��ʼ�����ݿ���Ϣ�������ĵȺ�...
CALL %mysqlpath%mysql -f -u%dbuser% -p%dbpswd% %dbname% < init.sql
if %ERRORLEVEL% EQU 0 (
	echo �������
) else (
	echo.
	echo ��ʼ������	
	echo.
	echo 	*���ݿ����ƣ�%dbname%		*�����û���%dbuser%		*�û����룺%dbpswd%		*mysql·����%mysqlpath%
	CHOICE /C YRC /M "ȷ���밴 Y�����������밴 R��ȡ���밴 C��"
	if ERRORLEVEL 3 goto end
	if ERRORLEVEL 2 goto params
	if ERRORLEVEL 1 goto run
)

:end
echo.
pause
