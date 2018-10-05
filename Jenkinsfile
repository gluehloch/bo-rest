pipeline {
    agent any
    stages {
        stage('Build') {
            git url: 'https://andrewinkler@bitbucket.org/andrewinkler/betoffice-jweb.git'
 
            withMaven(
                // Maven installation declared in the Jenkins "Global Tool Configuration"
                maven: 'MAVEN_3.5.4') {
                    sh "mvn clean install"
                }
        }       
    }
}
