@echo off
rem Builds the mod, drops it into the server, and starts the server.
rem The server lives outside this folder on purpose: a world being written to inside a folder that
rem something else is syncing or indexing means constant re-reads and file locks, which is a good
rem way to corrupt a save. This just points at it.
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
call gradlew.bat build -q
if errorlevel 1 (
	echo.
	echo The build failed, so the server has not been started. Scroll up for the error.
	pause
	exit /b 1
)

echo Installing the new jar ...
copy /y "build\libs\shapesnsizes-1.0.0+8.0.1.jar" "%SERVER_DIR%\mods\" > nul
if errorlevel 1 (
	echo.
	echo Could not copy the jar in. Is the server still running?
	pause
	exit /b 1
)

echo Starting the server ...
echo.
call "%SERVER_DIR%\start.bat"
endlocal
