
SET GAME_PATH=C:\doom3\base

cd /D %GAME_PATH%

cd ..

SET DOOM_PATH=%CD%

IF NOT EXIST "%JAVA_HOME%" SET JAVA_HOME="C:\Program Files\Java\JDK1.5"
IF NOT EXIST "%JREHOME%" SET JREHOME=%JAVA_HOME%\jre
IF NOT EXIST "%JREHOME%" SET JREHOME=%JAVA_HOME%

SET LIBPATH=%GAME_PATH%;%DOOM_PATH%;%JREHOME%\lib;%JAVA_HOME%\lib;%LIBPATH%
SET PATH=%GAME_PATH%;%DOOM_PATH%;%JREHOME%\bin;%JAVA_HOME%\bin;%PATH%
SET CLASSPATH=%GAME_PATH%\classes;%GAME_PATH%\zzDaxmoo.pk4;%CLASSPATH%

