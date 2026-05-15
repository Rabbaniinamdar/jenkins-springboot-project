pipeline {
    agent any

    tools {
        jdk 'jdk 21'
        maven 'maven'
    }

    stages {

        stage('Stop Old App') {
            steps {
                bat '''
                @echo off

                echo ======================================
                echo Stopping old Spring Boot application
                echo ======================================

                netstat -aon | findstr :8088 >nul

                if %errorlevel%==0 (
                    echo Application found on port 8088. Stopping...

                    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8088') do (
                        taskkill /F /PID %%a
                    )

                    timeout /t 5
                ) else (
                    echo No application running on port 8088
                )

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

                mvn clean package

                exit /b 0
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

                cd /d C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\springboot-pipeline\\target

                start "" "C:\\Program Files\\Java\\jdk-17\\bin\\java.exe" -jar usercrud-0.0.1-SNAPSHOT.jar

                timeout /t 10

                exit /b 0
                '''
            }
        }

        stage('Verify Application') {
            steps {
                bat '''
                @echo off

                echo ======================================
                echo Verifying Application on Port 8088
                echo ======================================

                netstat -aon | findstr :8088

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
            echo 'Deployment failed!'
        }
    }
}
