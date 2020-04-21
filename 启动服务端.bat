@echo off
@title ZEVMS Ver.27

set CLASSPATH=.;Dist\*
java -Xms2048m -Xmx4096m -Dwzpath=wz\ launch.Start
pause