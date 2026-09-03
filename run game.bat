@echo off
rem Launches the BTA dev client with the mod loaded, straight from the source tree.
rem Builds first if needed, then opens the game window. Close the game to end the script.
setlocal
cd /d "%~dp0"
set USERNAME_ARG=%~1
if "%USERNAME_ARG%"=="" set USERNAME_ARG=Player
echo Starting Better than Adventure with Shapes n Sizes as %USERNAME_ARG% ...
call gradlew.bat runClient --args="--username %USERNAME_ARG%"
if errorlevel 1 (
	echo.
	echo The game did not start cleanly. Scroll up for the error.
	pause
)
endlocal
