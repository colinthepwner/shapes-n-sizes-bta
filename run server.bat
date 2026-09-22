@echo off
setlocal
cd /d "%~dp0"

set "SERVER_DIR=%USERPROFILE%\bta-server"

if not exist "%SERVER_DIR%\server.jar" (
	echo Could not find a server at "%SERVER_DIR%".
	echo Nothing has been started.
	pause
	exit /b 1
)

echo Building Shapes n Sizes ...
if exist "build\libs" rd /s /q "build\libs"
call gradlew.bat build -q
if errorlevel 1 (
	echo.
	echo The build failed, so the server has not been started. Scroll up for the error.
	pause
	exit /b 1
)

echo Installing the new jar ...
del /q "%SERVER_DIR%\mods\shapesnsizes-*.jar" 2> nul
set "INSTALLED="
for %%f in ("build\libs\shapesnsizes-*.jar") do (
	echo %%~nf | findstr /i /c:"-sources" > nul || (
		copy /y "%%f" "%SERVER_DIR%\mods\" > nul && set "INSTALLED=%%~nxf"
	)
)
if not defined INSTALLED (
	echo.
	echo Could not copy the jar in. Is the server still running?
	pause
	exit /b 1
)
echo Installed %INSTALLED%.

echo Starting the server ...
echo.
call "%SERVER_DIR%\start.bat"
endlocal
