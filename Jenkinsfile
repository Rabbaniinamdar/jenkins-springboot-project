pipeline {
    agent any

    tools {
        jdk 'jdk 21'
        maven 'maven'
    }

    environment {
        APP_PORT = '8088'
        JAR_PATTERN = 'target\\*-SNAPSHOT.jar'   // wildcard — survives version bumps
        JAVA_EXE = "C:\\Program Files\\Java\\jdk-21.0.10\\bin\\java.exe"
    }

    stages {

        stage('Stop Old App') {
            steps {
                bat '''
                    @echo off
                    echo ======================================
                    echo Stopping old Spring Boot application
                    echo ======================================

                    netstat -aon | findstr :%APP_PORT% >nul 2>&1
                    if %errorlevel% NEQ 0 (
                        echo No application running on port %APP_PORT%
                        exit /b 0
                    )

                    echo Application found on port %APP_PORT%. Stopping...
                    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%APP_PORT%') do (
                        echo Killing PID %%a
                        taskkill /F /PID %%a >nul 2>&1
                    )
                    timeout /t 5 /nobreak >nul
                    echo Old application stopped.
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
                // Do NOT add "exit /b 0" here — let Maven's exit code control success/failure
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

                    :: Find the JAR dynamically
                    for %%f in (*-SNAPSHOT.jar) do set JAR_FILE=%%f

                    if not defined JAR_FILE (
                        echo ERROR: No JAR file found in target folder!
                        exit /b 1
                    )

                    echo Launching: %JAR_FILE%
                    start "" "%JAVA_EXE%" -jar "%JAR_FILE%"

                    echo Waiting for app to start...
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
