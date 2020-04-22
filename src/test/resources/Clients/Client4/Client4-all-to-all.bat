set path = "C:\Program Files\Java\jdk1.8.0_151\bin"
timeout /t 5

@echo off

:: Fetch pushOrPull
set "pushOrPull=%~1"
goto :pushOrPullCheck
:pushOrPullPrompt
set /p "pushOrPull=Enter Push or Pull: "
:pushOrPullCheck
if "%pushOrPull%"=="" goto :pushOrPullPrompt

:: Fetch TTR
set "TTR=%~2"
goto :TTRCheck
:TTRPrompt
set /p "TTR=Enter value for TTR: "
:TTRCheck
if "%TTR%"=="" goto :TTRPrompt

:: Process the params
echo pushOrPull=%pushOrPull%
echo TTR=%TTR%


java -classpath "F:\Workspace\GnutellaP2PFileSharing\target\GnutellaP2PFileSharing\lib\classes;F:\Workspace\GnutellaP2PFileSharing\target\GnutellaP2PFileSharing\lib\*" -Dlog4j.configurationFile=file:///f:/Workspace/GnutellaP2PFileSharing/target/GnutellaP2PFileSharing/lib/classes/log4j2.properties -Djava.rmi.server.codebase=file:f:/Workspace/GnutellaP2PFileSharing/target/GnutellaP2PFileSharing/lib/classes -Djava.security.policy=file:///f:/Workspace/GnutellaP2PFileSharing/target/GnutellaP2PFileSharing/lib/classes/security.policy -Djava.rmi.server.logCalls=true -Dsun.rmi.server.exceptionTrace=true client.Client all-to-all Client4 %pushOrPull% %TTR%
timeout /t 50