@echo off

if "%~9"=="" (
  echo usage: make_all.bat {def name only} {fromYmd train} {toYmd train} {bettype} {bettype name} {description} {define files} {fromYmd test} {toYmd test}
  goto :eof
)

call make_arff.bat %1 %2 %3
call make_model_bayesnet.bat %1
call make_result.bat %4 %5 %6 %7 %8 %9

:eof