@echo off

@REM SET JAVA_HOME=C:\jdk1.6.0_33
@REM SET Path=%JAVA_HOME%\bin;%Path%

if "%ANT_HOME%"=="" (
	SET ANT_HOME=C:\ANT
)

if "%M2_HOME%"=="" (
	SET M2_HOME=C:\maven
)

@REM if "%JAVA_HOME%" == "" (
@REM if exist "C:\jdk1.8.0_05" (
@REM 	SET "JAVA_HOME=C:\jdk1.8.0_05"
@REM ) else 
	if exist "C:\jdk1.7.0_67" (
		SET "JAVA_HOME=C:\jdk1.7.0_67"
	) else if exist "C:\Program Files\Java\jdk1.7.0_75" (
		SET "JAVA_HOME=C:\Program Files\Java\jdk1.7.0_75"
	) else if exist "C:\Program Files\Java\jdk1.7.0_67" (
		SET "JAVA_HOME=C:\Program Files\Java\jdk1.7.0_67"
	) else if exist "C:\Program Files\Java\jdk1.7.0_60" (
		SET "JAVA_HOME=C:\Program Files\Java\jdk1.7.0_60"
	) else if exist "C:\jdk1.7.0_55" (
		SET "JAVA_HOME=C:\jdk1.7.0_55"
	) else if exist "C:\Program Files (x86)\Java\jdk1.7.0_51" (
		SET "JAVA_HOME=C:\Program Files (x86)\Java\jdk1.7.0_51"
	) else if exist "C:\jdk1.7.0_51" (
		SET JAVA_HOME=C:\jdk1.7.0_51
	) else if exist "C:\jdk1.6.0_33" (
		SET JAVA_HOME=C:\jdk1.6.0_33
	) else if exist "C:\jdk1.6.0_31" (
		SET JAVA_HOME=C:\jdk1.6.0_31
	) else if exist "C:\jdk1.6.0_29" (
		SET JAVA_HOME=C:\jdk1.6.0_29
	) else if exist "C:\jdk1.6.0" (
		SET JAVA_HOME=C:\jdk1.6.0
	) else if exist "C:\jdk1.5.0_16" (
		SET JAVA_HOME=C:\jdk1.5.0_16
	) else if exist "C:\jdk1.5.0_04" (
		SET JAVA_HOME=C:\jdk1.5.0_04
	)
@REM )

set Path=%Path%;%JAVA_HOME%\bin;%ANT_HOME%\bin;%M2_HOME%\bin

echo ANT_HOME = %ANT_HOME%
echo JAVA_HOME = %JAVA_HOME%
echo Path = %Path%
