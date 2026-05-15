pipeline {
    agent any

    tools {
        jdk 'jdk 21'
        maven 'maven'
    }

    environment {
        APP_PORT = '8088'
        JAVA_EXE = 'C:\\Program Files\\Java\\jdk-21\\bin\\java.exe'
    }

    stages {

        stage('Stop Old App') {
            steps {
                bat '''
                    @echo off
                    echo ======================================
                    echo Stopping old Spring Boot application
                    echo ======================================

                    REM Kill any java process running the usercrud jar
                    wmic process where "commandline like '%%usercrud%%'" get processid 2>nul | findstr /r "[0-9]" >nul
                    if %errorlevel%==0 (
                        echo Found running usercrud process. Killing...
                        wmic process where "commandline like '%%usercrud%%'" delete
                        echo Waiting for file handles to release...
                        timeout /t 8 /nobreak >nul
                    ) else (
                        echo No usercrud process found.
                    )

                    REM Also kill by port as a backup
                    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%APP_PORT% 2^>nul') do (
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    timeout /t 5 /nobreak >nul
                    echo Done. Proceeding to build...
                    exit /b 0
                '''
            }
        }

        stage('Build') {
            steps {
                bat '''
                    @echo off
                    echo ======================================
                    echo Building Spring Boot Application
                    echo ======================================
                    mvn clean package -DskipTests
                '''
            }
        }

        stage('Start New App') {
            steps {
                bat '''
                    @echo off
                    echo ======================================
                    echo Starting Spring Boot Application
                    echo ======================================

                    cd /d %WORKSPACE%\\target

                    for %%f in (*-SNAPSHOT.jar) do set JAR_FILE=%%f

                    if not defined JAR_FILE (
                        echo ERROR: No JAR file found in target folder!
                        exit /b 1
                    )

                    echo Launching: %JAR_FILE%
                    start "" "%JAVA_EXE%" -jar "%JAR_FILE%"

                    timeout /t 15 /nobreak >nul
                    exit /b 0
                '''
            }
        }

        stage('Verify Application') {
            steps {
                bat '''
                    @echo off
                    echo ======================================
                    echo Verifying Application on Port %APP_PORT%
                    echo ======================================

                    netstat -aon | findstr :%APP_PORT%
                    if %errorlevel% NEQ 0 (
                        echo ERROR: Application is NOT running on port %APP_PORT%!
                        exit /b 1
                    )

                    echo SUCCESS: Application is running on port %APP_PORT%
                    exit /b 0
                '''
            }
        }
    }

    post {
        success {
            echo 'Spring Boot application deployed successfully!'
        }
        failure {
            echo 'Deployment failed! Check console output above.'
        }
    }
}
