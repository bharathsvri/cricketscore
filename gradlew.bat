@echo off
set PATH=%PATH:"=%
set GRADLE_BIN=C:\Users\m073\.gradle\wrapper\dists\gradle-9.5.0-bin\bvnork1r7n8i6kp5cnkibsc9q\gradle-9.5.0\bin\gradle.bat
if exist "%GRADLE_BIN%" (
    call "%GRADLE_BIN%" %*
) else (
    gradle %*
)
