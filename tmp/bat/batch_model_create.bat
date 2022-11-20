@echo off

if "%~5"=="" (
  echo usage: batch_model_create.bat {def name} {fromYmd} {toYmd1} {toYmd2} {creation interval}
  echo monthly model creation example: batch_model_create.bat 40_1T_1_entry_multi 20100101 20161231 20181231 31
  goto :eof
)

set workdir=C:/Dev/workspace/Oxygen/pod_boatrace_test/wekamodels
set executable=make_arff_model_bayesnet_multi.bat
set defname=%1
set fromYmd=%2
set toYmd1=%3
set toYmd2=%4
set interval=%5

echo batch model creation proceeding...
java ^
  -cp C:/Dev/workspace/Oxygen/pod_boatrace/target/classes;C:/Dev/workspace/Oxygen/pod_boatrace_test/lib/*;. ^
  com.pengkong.boatrace.weka.automation.ModelBatchCreator ^
  %workdir% ^
  %executable% ^
  %defname% ^
  %fromYmd% ^
  %toYmd1% ^
  %toYmd2% ^
  %interval%

:eof