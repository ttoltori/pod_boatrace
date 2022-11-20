@echo off

if "%~3"=="" (
  echo usage: make_arff_model_bayesnet.bat {def name only} {fromYmd train} {toYmd train}
  goto :eof
)

call make_arff.bat %1  %2 %3
call make_model_bayesnet.bat %1

:eof