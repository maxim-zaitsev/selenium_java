@echo OFF
call mvn package install
set TARGET=%CD%\target
set VERSION=0.0.2-SNAPSHOT
set MAIN_APP_PACKAGE=study.myswt.menus_toolbars
set MAIN_APP_PACKAGE=study.myswt.layouts
set MAIN_APP_CLASS=SimpleToolBarEx
set MAIN_APP_CLASS=ScrolledTextEx
set MAIN_APP_CLASS=BreadCrumpEx
java -cp target\myswt-%VERSION%.jar;target\lib\* ^
%MAIN_APP_PACKAGE%.%MAIN_APP_CLASS%

goto :EOF
