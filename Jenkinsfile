pipeline {
    agent any

    tools {
        jdk 'jdk 21'
        maven 'maven'
    }

    environment {
        APP_PORT = '8088'
        JAVA_EXE = 'C:\\Program Files\\Java\\jdk-21.0.10\\bin\\java.exe'
    }

    stages {

        // ─────────────────────────────────────────────
        // STAGE 1 — Stop Old App
        // ─────────────────────────────────────────────
        stage('Stop Old App') {
            steps {
                bat '''
                    @echo off
                    echo ======================================
                    echo Stopping old Spring Boot application
                    echo ======================================

                    REM Kill by JAR name using wmic
                    wmic process where "commandline like '%%usercrud%%'" get processid 2>nul | findstr /r "[0-9]" >nul
                    if %errorlevel%==0 (
                        echo Found running usercrud process. Killing...
                        wmic process where "commandline like '%%usercrud%%'" delete
                        echo Waiting for file handles to release...
                        ping -n 12 127.0.0.1 >nul
                    ) else (
                        echo No usercrud process found.
                    )

                    REM Also kill by port as backup
                    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%APP_PORT% 2^>nul') do (
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    ping -n 6 127.0.0.1 >nul
                    echo Done stopping. Proceeding to build...
                    exit /b 0
                '''
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 2 — Build
        // ─────────────────────────────────────────────
        stage('Build') {
            steps {
                bat '''
                    @echo off
                    echo ======================================
                    echo Building Spring Boot Application
                    echo ======================================

                    REM Force delete target folder to avoid file lock issues
                    if exist "%WORKSPACE%\\target" (
                        echo Deleting target folder...
                        rd /s /q "%WORKSPACE%\\target"
                        ping -n 4 127.0.0.1 >nul
                    )

                    REM Use package since we deleted target manually
                    mvn package -DskipTests
                '''
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 3 — Start New App
        // ─────────────────────────────────────────────
        stage('Start New App') {
            steps {
                bat '''
                    @echo off
                    echo ======================================
                    echo Starting Spring Boot Application
                    echo ======================================

                    cd /d %WORKSPACE%\\target

                    REM Find JAR dynamically so version changes don't break pipeline
                    for %%f in (*-SNAPSHOT.jar) do set JAR_FILE=%%f

                    if not defined JAR_FILE (
                        echo ERROR: No JAR file found in target folder!
                        exit /b 1
                    )

                    echo Found JAR: %JAR_FILE%

                    REM Launch as fully detached background process using PowerShell
                    powershell -Command "Start-Process -FilePath '%JAVA_EXE%' -ArgumentList '-jar', '%WORKSPACE%\\target\\%JAR_FILE%' -RedirectStandardOutput '%WORKSPACE%\\app.log' -RedirectStandardError '%WORKSPACE%\\app-error.log' -WindowStyle Hidden -PassThru"

                    echo Process launched via PowerShell.
                    echo Waiting for application to start...
                    ping -n 25 127.0.0.1 >nul
                    echo Done waiting.
                    exit /b 0
                '''
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 4 — Verify Application
        // ─────────────────────────────────────────────
        stage('Verify Application') {
            steps {
                bat '''
                    @echo off
                    echo ======================================
                    echo App Log Output (last 30 lines)
                    echo ======================================

                    if exist "%WORKSPACE%\\app.log" (
                        powershell -Command "Get-Content '%WORKSPACE%\\app.log' -Tail 30"
                    ) else (
                        echo WARNING: app.log not found
                    )

                    if exist "%WORKSPACE%\\app-error.log" (
                        echo --- Error Log (last 20 lines) ---
                        powershell -Command "Get-Content '%WORKSPACE%\\app-error.log' -Tail 20"
                    )

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

    // ─────────────────────────────────────────────
    // POST ACTIONS
    // ─────────────────────────────────────────────
    post {
        success {
            echo '✅ Spring Boot application deployed successfully!'
        }
        failure {
            echo '❌ Deployment failed! Check console output above.'
        }
    }
}
