pipeline {
  options {
    buildDiscarder(logRotator(numToKeepStr: '10'))
    disableConcurrentBuilds()
  }
  agent any
  stages {
    stage('Prepare') {
        steps {
          sh 'docker pull maven:3-jdk-8'
      }
    }
    stage('Build') {
      steps {
        sh 'mkdir -p $(pwd)/.m2'
        sh 'docker run --rm -v $(pwd):/src -v $(pwd)/.m2:/root/.m2 -w /src maven:3-jdk-8 mvn clean package'
      }
    }
    stage('Publish') {
      when {
        branch 'master'
      }
      steps {
        dir(path: 'target') {
            archiveArtifacts(artifacts: '*.hpi', caseSensitive: true, onlyIfSuccessful: true)
        }
      }
    }
  }
  triggers {
    bitbucketPush()
  }
  post {
    success {
      juxtapose event: 'success'
      sh 'figlet "SUCCESS"'
    }
    failure {
      juxtapose event: 'failure'
      sh 'figlet "FAILURE"'
    }
  }
}
