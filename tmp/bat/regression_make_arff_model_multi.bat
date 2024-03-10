@echo off

if "%~3"=="" (
  echo usage: make_arff_model_bayesnet_multi.bat {def name only} {fromYmd train} {toYmd train}
  goto :eof
)

cd /d %~dp0

call make_arff.bat %1 %2 %3
call make_model_bayesnet_multi.bat %1 %2 %3

:eof