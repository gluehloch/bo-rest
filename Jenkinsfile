pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Start betoffice jweb build ...'
                git url: 'https://andrewinkler@bitbucket.org/andrewinkler/betoffice-jweb.git'
     
                withMaven(
                    maven: 'MAVEN_3.5.4') {
                        sh "mvn -B clean install"
                    }                
            }
        }       
    }
}
