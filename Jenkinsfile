pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    stages {

        stage('Clone') {
            steps {
                git 'https://github.com/Rabbaniinamdar/jenkins-springboot-project'
            }
        }

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
                cd target
                start "" "C:\\Program Files\\Java\\jdk-17\\bin\\java.exe" -jar usercrud-0.0.1-SNAPSHOT.jar
                '''
            }
        }
    }
}
