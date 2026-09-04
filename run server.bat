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
rem Older jars pile up in build\libs across version bumps, and the wildcard install below
rem would happily copy every one of them. Start from an empty folder so only this build exists.
if exist "build\libs" rd /s /q "build\libs"
call gradlew.bat build -q
if errorlevel 1 (
	echo.
	echo The build failed, so the server has not been started. Scroll up for the error.
	pause
	exit /b 1
)

echo Installing the new jar ...
rem The version is part of the file name and moves with gradle.properties. A hard-coded name here
rem quietly installed whatever stale jar of that version was still lying in build\libs, so the
rem server ran 1.0.0 for days while the client was on 1.0.2 and every "works in single player,
rem not on the server" report was really this. Match by wildcard, skip the sources jar, and clear
rem out any older copy first so the loader never sees two versions of the mod at once.
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
