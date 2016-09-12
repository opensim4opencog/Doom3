call setpaths.bat


:LOOPER
rem start CynD.sln 
Doom3.exe +set net_serverDedicated 0 +set net_LANServer 1  +editor +set com_allowConsole 1 +exec botserver.cfg

pause
GOTO :LOOPER
