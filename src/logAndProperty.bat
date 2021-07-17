@echo off
set EXEC_DIR=%cd%
set LIB=%EXEC_DIR%\lib\

set CLASSPATH=%LIB%logAndProperty.jar
set CLASSPATH=%CLASSPATH%;%LIB%\logback-class-1.2.3.jar
set CLASSPATH=%CLASSPATH%;%LIB%\logback-core-1.2.3.jar
set CLASSPATH=%CLASSPATH%;%LIB%\slf4j-api-1.7.25.jar
set CLASSPATH=%CLASSPATH%;%LIB%\jackson-annotations-2.9.9.jar
set CLASSPATH=%CLASSPATH%;%LIB%\jackson-core-2.9.9.jar
set CLASSPATH=%CLASSPATH%;%LIB%\jackson-databind-2.9.9.jar

rem input paramter info:====================================
echo %EXEC_DIR%
set JAVA_EXE=E:\Java\jre1.8.0_181\bin\java.exe

pause
rem=========================================================

"%JAVA_EXE%" -classpath %CLASSPATH% com.wang.startup.StartUp %*
@echo on