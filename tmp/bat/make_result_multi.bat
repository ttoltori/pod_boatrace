@echo off

if "%~6"=="" (
  echo usage: make_result_multi.bat {bettype} {bettype name} {description} {fromYmd} {toYmd} {model filter string}
  goto :eof
)

set bettype=%1
set bettypename=%2
set description=%3
set fromYmd=%4
set toYmd=%5
set filterstr=%6

echo simulation proceeding...
java -Xms10000m -Xmx10000m ^
  -cp C:/Dev/workspace/Oxygen/pod_boatrace/target/classes;C:/Dev/workspace/Oxygen/pod_boatrace_test/lib/*;. ^
  com.pengkong.boatrace.weka.automation.MLMultiSimulator ^
  %bettype% ^
  %bettypename% ^
  %description% ^
  %fromYmd% ^
  %toYmd% ^
  %filterstr%

:eof