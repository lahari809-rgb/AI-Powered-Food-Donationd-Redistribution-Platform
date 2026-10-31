@REM Maven Wrapper startup batch script
@REM ---------------------------------------------------------------------------
@setlocal
set MAVEN_PROJECTBASEDIR=%~dp0
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

@REM Download maven-wrapper.jar if not exists
if not exist %WRAPPER_JAR% (
    echo Downloading Maven Wrapper...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URL:"=%', '%WRAPPER_JAR:"=%')"
)

@REM Find java.exe
set JAVA_EXE=java.exe
%JAVA_EXE% -jar %WRAPPER_JAR% %*
