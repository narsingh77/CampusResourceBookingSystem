@echo off
echo ======================================================================
echo  Compiling and Running Campus Resource Booking System
echo ======================================================================

if not exist bin mkdir bin

echo Compiling Java source files...
javac -encoding UTF-8 -d bin src\com\vityarthi\booking\model\*.java src\com\vityarthi\booking\exception\*.java src\com\vityarthi\booking\service\*.java src\com\vityarthi\booking\util\*.java src\com\vityarthi\booking\Main.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Compiling Test suite...
javac -encoding UTF-8 -cp bin -d bin test\com\vityarthi\booking\*.java

echo.
echo Launching Interactive CLI...
java -cp bin com.vityarthi.booking.Main
pause