pipeline {
    agent any

    tools {
        jdk 'jdk 21'
        maven 'maven'
    }

    stages {

        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Stop Old App') {
            steps {
                bat '''
                netstat -aon | findstr :8088 > nul

                IF %ERRORLEVEL%==0 (
                    echo Killing existing Spring Boot app...

                    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8088') do (
                        taskkill /F /PID %%a
                    )

                    timeout /t 5
                ) ELSE (
                    echo No application running on port 8088
                )
                '''
            }
        }

        stage('Start New App') {
            steps {
                bat '''
                wmic process call create "C:\\Program Files\\Java\\jdk-17\\bin\\java.exe -jar C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\springboot-pipeline\\target\\usercrud-0.0.1-SNAPSHOT.jar"
                '''
            }
        }

        stage('Verify Application') {
            steps {
                bat '''
                timeout /t 10

                netstat -aon | findstr :8088
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
