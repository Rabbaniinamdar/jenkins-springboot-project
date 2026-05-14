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
                for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8088') do taskkill /F /PID %%a
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
    }
}
