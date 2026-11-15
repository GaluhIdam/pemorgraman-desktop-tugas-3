@echo off
setlocal
cd /d "%~dp0"
if not exist "build\classes" mkdir "build\classes"
if not exist "dist" mkdir "dist"
if exist "build\sources.txt" del "build\sources.txt"
for /R "src\main\java" %%F in (*.java) do echo "%%F">>"build\sources.txt"
javac --release 17 -encoding UTF-8 -d "build\classes" @build\sources.txt
if errorlevel 1 exit /b 1
jar --create --file "dist\manajemen-restoran.jar" --main-class id.ac.restoran.Main -C "build\classes" .
if errorlevel 1 exit /b 1
echo Build selesai: dist\manajemen-restoran.jar
