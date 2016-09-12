
call setpaths.bat

IF EXIST "%VCToolkitInstallDir%"vcvars32.bat call "%VCToolkitInstallDir%"vcvars32.bat

cd /d %DOOM_PATH%
start CynD.sln 

rem Doom3.exe +set net_serverDedicated 0 +set net_LANServer 1  +editor +set com_allowConsole 1 +exec botserver.cfg


