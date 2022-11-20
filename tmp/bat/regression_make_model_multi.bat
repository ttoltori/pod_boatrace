@echo off

if "%~3"=="" (
  echo usage: make_model_bayesnet_multi.bat {def name only} {fromYmd} {toYmd}
  goto :eof
)

set defname=%1
set fromYmd=%2
set toYmd=%3

echo creating model file...
java ^
  -cp C:/Dev/workspace/Oxygen/pod_boatrace_test/lib/*;. ^
  weka.classifiers.functions.LinearRegression -S 0 -R 1.0E-8 -num-decimal-places 4 ^
  -d ./regression_release/%defname%_%fromYmd%_%toYmd%.model ^
  -no-cv -split-percentage 80 ^
  -t "C:/Dev/workspace/Oxygen/pod_boatrace_test/wekamodels/regression_arff/%defname%.arff" ^
  >> .\regression_evaluation\%defname%.result

rem type .\regression_evaluation\%defname%.result
