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

                taskkill /F /IM java.exe /T >nul 2>&1

                timeout /t 5

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

                netstat -ano | findstr :8088

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
