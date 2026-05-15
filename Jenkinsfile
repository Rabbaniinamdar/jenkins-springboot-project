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

        stage('Stop Old App') {
            steps {
                bat '''
                    @echo off
                    echo Stopping old Spring Boot application
                    schtasks /end /tn "SpringBootApp" >nul 2>&1
                    wmic process where "commandline like '%%usercrud%%'" get processid 2>nul | findstr /r "[0-9]" >nul
                    if %errorlevel%==0 (
                        echo Found running usercrud process. Killing...
                        wmic process where "commandline like '%%usercrud%%'" delete
                        ping -n 12 127.0.0.1 >nul
                    ) else (
                        echo No usercrud process found.
                    )
                    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%APP_PORT% 2^>nul') do (
                        taskkill /F /PID %%a >nul 2>&1
                    )
                    ping -n 6 127.0.0.1 >nul
                    echo Done stopping.
                    exit /b 0
                '''
            }
        }

        stage('Build') {
            steps {
                bat '''
                    @echo off
                    echo Building Spring Boot Application
                    if exist "%WORKSPACE%\\target" (
                        echo Deleting target folder...
                        rd /s /q "%WORKSPACE%\\target"
                        ping -n 4 127.0.0.1 >nul
                    )
                    mvn package -DskipTests
                '''
            }
        }

        stage('Start New App') {
            steps {
                powershell '''
                    Write-Host "======================================"
                    Write-Host "Starting Spring Boot Application"
                    Write-Host "======================================"

                    $javaExe = "$env:JAVA_HOME\\bin\\java.exe"
                    $jarPath = "$env:WORKSPACE\\target\\usercrud-0.0.1-SNAPSHOT.jar"

                    Write-Host "Java: $javaExe"
                    Write-Host "JAR:  $jarPath"

                    if (-not (Test-Path $javaExe)) {
                        Write-Host "ERROR: java.exe not found at $javaExe"
                        exit 1
                    }

                    if (-not (Test-Path $jarPath)) {
                        Write-Host "ERROR: JAR not found at $jarPath"
                        exit 1
                    }

                    # Remove old task if exists
                    Unregister-ScheduledTask -TaskName "SpringBootApp" -Confirm:$false -ErrorAction SilentlyContinue

                    # Create scheduled task running as SYSTEM - survives Jenkins shutdown
                    $action   = New-ScheduledTaskAction -Execute $javaExe -Argument "-jar `"$jarPath`""
                    $trigger  = New-ScheduledTaskTrigger -Once -At (Get-Date).AddSeconds(5)
                    $settings = New-ScheduledTaskSettingsSet -ExecutionTimeLimit (New-TimeSpan -Hours 24)
                    $principal = New-ScheduledTaskPrincipal -UserId "SYSTEM" -RunLevel Highest

                    Register-ScheduledTask -TaskName "SpringBootApp" `
                        -Action $action `
                        -Trigger $trigger `
                        -Settings $settings `
                        -Principal $principal `
                        -Force

                    Start-ScheduledTask -TaskName "SpringBootApp"
                    Write-Host "Scheduled task launched. Waiting 30s for Spring Boot to start..."
                    Start-Sleep -Seconds 30
                    Write-Host "Done waiting."
                '''
            }
        }

        stage('Verify Application') {
            steps {
                bat '''
                    @echo off
                    echo Verifying Application on Port %APP_PORT%
                    netstat -aon | findstr :%APP_PORT%
                    if %errorlevel% NEQ 0 (
                        echo ERROR: Application is NOT running on port %APP_PORT%
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
            echo 'SUCCESS: Spring Boot application deployed and running!'
        }
        failure {
            echo 'FAILURE: Deployment failed! Check console output above.'
        }
    }
}
