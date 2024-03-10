@echo off

if "%~6"=="" (
  echo usage: make_result.bat {bettype} {bettype name} {description} {define files} {fromYmd} {toYmd}
  goto :eof
)

set bettype=%1
set bettypename=%2
set description=%3
set workdir=C:/Dev/workspace/Oxygen/pod_boatrace_test/wekamodels
set deffiles=%4
set fromYmd=%5
set toYmd=%6

echo simulation proceeding...
java ^
  -cp C:/Dev/workspace/Oxygen/pod_boatrace/target/classes;C:/Dev/workspace/Oxygen/pod_boatrace_test/lib/*;. ^
  com.pengkong.boatrace.weka.automation.MLSimulator ^
  %bettype% ^
  %bettypename% ^
  %description% ^
  %workdir% ^
  %deffiles% ^
  %fromYmd% ^
  %toYmd%

:eof