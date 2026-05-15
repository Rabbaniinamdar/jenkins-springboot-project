pipeline {
    agent any

    tools {
        jdk 'jdk 21'
        maven 'maven'
    }

    environment {
        APP_NAME = "usercrud"
        JAR_FILE = "target/usercrud-0.0.1-SNAPSHOT.jar"
        APP_PORT = "8088"
        JAVA_EXE  = 'C:\\Program Files\\Java\\jdk-21.0.10\\bin\\java.exe'
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                url: 'https://github.com/Rabbaniinamdar/jenkins-springboot-project'
            }
        }

        stage('Stop Old Application') {
            steps {
                bat '''
                echo ======================================
                echo Stopping Old Spring Boot Application
                echo ======================================

                for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%APP_PORT%') do (
                    echo Killing process running on port %APP_PORT%
                    taskkill /F /PID %%a
                )

                echo Old application stopped.
                '''
            }
        }

        stage('Clean Workspace') {
            steps {
                bat '''
                echo ======================================
                echo Cleaning Old Build Files
                echo ======================================

                if exist target (
                    rmdir /s /q target
                )

                if exist app.log (
                    del app.log
                )
                '''
            }
        }

        stage('Build Application') {
            steps {
                bat '''
                echo ======================================
                echo Building Spring Boot Application
                echo ======================================

                mvn clean install -DskipTests
                '''
            }
        }

        stage('Start Application') {
            steps {
                bat '''
                echo ======================================
                echo Starting Spring Boot Application
                echo ======================================

                if not exist %JAR_FILE% (
                    echo ERROR: JAR file not found
                    exit /b 1
                )

                echo Starting JAR: %JAR_FILE%

                start /B java -jar %JAR_FILE% > app.log 2>&1

                echo Waiting for application to start...
                timeout /t 60

                echo ======================================
                echo Application Logs
                echo ======================================

                type app.log
                '''
            }
        }

        stage('Verify Application') {
            steps {
                bat '''
                echo ======================================
                echo Verifying Application on Port %APP_PORT%
                echo ======================================

                netstat -ano | findstr :%APP_PORT%

                if %ERRORLEVEL% NEQ 0 (
                    echo.
                    echo ERROR: Application failed to start
                    echo.
                    echo ======================================
                    echo Printing Application Logs
                    echo ======================================
                    type app.log
                    exit /b 1
                )

                echo.
                echo SUCCESS: Application is running on port %APP_PORT%
                '''
            }
        }
    }

    post {

        success {
            echo '======================================'
            echo 'APPLICATION DEPLOYED SUCCESSFULLY'
            echo '======================================'
        }

        failure {
            echo '======================================'
            echo 'DEPLOYMENT FAILED'
            echo '======================================'

            bat '''
            echo ===== FINAL APPLICATION LOG =====
            if exist app.log (
                type app.log
            )
            '''
        }

        always {
            echo 'Pipeline execution completed.'
        }
    }
}
