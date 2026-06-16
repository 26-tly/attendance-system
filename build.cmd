@echo off
cd /d C:\Users\LEGION\IdeaProjects\attendance-system
call mvnw.cmd package -DskipTests -B --no-transfer-progress > C:\Users\LEGION\IdeaProjects\attendance-system\package.log 2>&1
echo ExitCode: %ERRORLEVEL% >> C:\Users\LEGION\IdeaProjects\attendance-system\package.log
