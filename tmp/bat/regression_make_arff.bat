@echo off

if "%~3"=="" (
  echo usage: make_arff.bat {def file} {fromYmd} {toYmd}
  goto :eof
)

set workdir=C:/Dev/workspace/Oxygen/pod_boatrace_test/wekamodels
set defname=%1
set fromymd=%2
set toymd=%3

echo creatting arff file...
java ^
  -cp C:/Dev/workspace/Oxygen/pod_boatrace/target/classes;C:/Dev/workspace/Oxygen/pod_boatrace_test/lib/*;. ^
  com.pengkong.boatrace.weka.automation.RegressionArffCreator %workdir% %defname%.def %fromymd% %toymd%
 
:eof