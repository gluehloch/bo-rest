pipeline {
    agent any
    tools { 
        maven 'MAVEN_3.6.0' 
        jdk 'jdk11' 
    }
    
    /*
    agent {
        docker {
            image 'node:6-alpine' 
            args '-p 3000:3000' 
        }
    } 
    */
    stages {
        stage('Build') { 
            steps {
                echo 'Start build...'
                sh 'node  -v'
                sh 'npm -v'
                sh 'java -version'
                sh 'javac -version'
                sh 'mvn clean install'

                sh 'mvn --settings /var/jenkins_home/.m2/settings.xml deploy'
                //sshagent(credentials: ['maven.gluehloch.de']) {
                //    sh 'mvn --settings /var/jenkins_home/.m2/settings.xml deploy'
                //}

                // sh 'npm install'
                // sh 'npm uninstall @angular/cli'
                // sh 'npm cache clean'
                // sh 'npm install @angular/cli@latest'
                // sh 'npm run ng -- build'
            }
        }
        stage('Test') { 
            steps {
                echo 'Start test...'
                // 
            }
        }        
    }
}
