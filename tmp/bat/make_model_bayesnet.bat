@echo off

if "%~1"=="" (
  echo usage: make_model_bayesnet.bat {def name only}
  goto :eof
)

set defname=%1

echo creating model file...
java ^
  -cp C:/Dev/workspace/Oxygen/pod_boatrace_test/lib/weka.jar;. ^
  weka.classifiers.meta.FilteredClassifier ^
  -d ./model_bin/%defname%.model ^
  -no-cv -split-percentage 80 ^
  -t "C:/Dev/workspace/Oxygen/pod_boatrace_test/wekamodels/model_arff/%defname%.arff" ^
  -F "weka.filters.supervised.instance.ClassBalancer -num-intervals 10" -S 1 ^
  -W weka.classifiers.bayes.BayesNet -- -D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5 ^
  > .\model_evaluation\%defname%.result

type .\model_evaluation\%defname%.result
