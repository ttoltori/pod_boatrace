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
  weka.classifiers.meta.FilteredClassifier ^
  -d ./model_release/%defname%_%fromYmd%_%toYmd%.model ^
  -no-cv -split-percentage 80 ^
  -t "C:/Dev/workspace/Oxygen/pod_boatrace_test/wekamodels/model_arff/%defname%.arff" ^
  -F "weka.filters.supervised.instance.ClassBalancer -num-intervals 10" -S 1 ^
  -W weka.classifiers.bayes.BayesNet -- -D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5 ^
  >> .\model_evaluation\%defname%.result

rem type .\model_evaluation\%defname%.result
