@echo off

echo You need to execute
echo ./gradlew installDist
echo before running this test
pause

set PDML=..\app\build\install\app\bin\pdml.bat

call %PDML% test_01.pdml
echo.
call %PDML% test_02.pdml
echo.
call %PDML% test_03.pdml

pause
