@echo off
setlocal
cd /d "%~dp0"
call build.bat
if errorlevel 1 exit /b 1
if not exist "build\test-classes" mkdir "build\test-classes"
if exist "build\testsources.txt" del "build\testsources.txt"
for /R "src\test\java" %%F in (*.java) do echo "%%F">>"build\testsources.txt"
javac --release 17 -encoding UTF-8 -cp "build\classes" -d "build\test-classes" @build\testsources.txt
if errorlevel 1 exit /b 1
java -ea -cp "build\classes;build\test-classes" id.ac.restoran.RestaurantTest
pause
