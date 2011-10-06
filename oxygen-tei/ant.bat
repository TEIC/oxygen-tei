@echo off

SET PATH="%JAVA_HOME%\bin";%PATH%
SET SCRIPT_DIR=%0\..\
rem It was %~dp0, which was wrong, it was the current directory 
rem of the first script that call the current one.
SET PATH=%PATH%;%SCRIPT_DIR%tools\cvs_client


SET ANT_OPTS=-Xmx800m -Xms512m

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of arguments (up to the command line limit, anyway).
set CUSTOM_CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
set CUSTOM_CMD_LINE_ARGS=%CUSTOM_CMD_LINE_ARGS% %1
shift
goto setupArgs
rem This label provides a place for the argument list loop to break out
rem and for NT handling to skip to.

:doneStart
call %SCRIPT_DIR%tools\ant\bin\ant.bat %CUSTOM_CMD_LINE_ARGS%