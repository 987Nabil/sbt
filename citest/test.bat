@echo on

cd "%~dp0"

mkdir freshly-baked
unzip ..\target\universal\sbt.zip -d freshly-baked

SETLOCAL

"freshly-baked\sbt\bin\sbt" about

SET JAVA_HOME=C:\jdk10
SET PATH=C:\jdk10\bin;%PATH%
SET SBT_OPTS=-Xmx4g -Dfile.encoding=UTF8

"freshly-baked\sbt\bin\sbt" about

ENDLOCAL
