/*
 * (C) Copyright 2021 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
library identifier: "platform-ci-shared-library@v0.0.25"

def lib

pipeline {
  agent {
    label 'jenkins-nuxeo-package-lts-2021'
  }
  options {
    buildDiscarder(logRotator(daysToKeepStr: '60', numToKeepStr: '60', artifactNumToKeepStr: '5'))
    disableConcurrentBuilds(abortPrevious: true)
    githubProjectProperty(projectUrlStr: 'https://github.com/nuxeo/nuxeo-java-client')
  }
  environment {
    CURRENT_NAMESPACE = nxK8s.getCurrentNamespace()
    MAVEN_ARGS = '-B -nsu -DskipPrePostIntegration'
    MAVEN_OPTS = "$MAVEN_OPTS -Xms512m -Xmx3072m"
    VERSION = nxUtils.getVersion()
  }
  stages {

    stage('Load library / Set labels') {
      steps {
        container('maven') {
          script {
            lib = load 'ci/Jenkinsfiles/lib.groovy'
            nxK8s.setPodLabels()
          }
        }
      }
    }

    stage('Build Maven project') {
      parallel {
        stage('With Okhttp 3 (default)') {
          steps {
            container('maven') {
              echo """
              ------------------------------------------------
              Compile with default Okhttp version (Okhttp 3.x)
              ------------------------------------------------
              """
              echo "MAVEN_OPTS=$MAVEN_OPTS"
              sh "mvn ${MAVEN_ARGS} -DskipITs install"
            }
          }
          post {
            always {
              archiveArtifacts artifacts: '**/target/*.jar, **/target/nuxeo-java-client-*.zip, **/target/**/*.log'
              junit testResults: '**/target/surefire-reports/*.xml'
            }
          }
        }
        stage('With Okhttp 4') {
          steps {
            container('maven') {
              echo """
              ------------------------------------------------
              Compile with Okhttp version 4.x
              ------------------------------------------------
              """
              echo "MAVEN_OPTS=$MAVEN_OPTS"
              sh "mvn ${MAVEN_ARGS} -Pokhttp4 -Dcustom.environment=okhttp-4 test"
            }
          }
          post {
            always {
              junit testResults: '**/target-okhttp-4/surefire-reports/*.xml'
            }
          }
        }
      }
    }

    stage('Build functional Docker images') {
      steps {
        script {
          lib.buildFunctionalDockerImages()
        }
      }
    }

    stage('Run functional tests') {
      steps {
        script {
          lib.runFunctionalTests()
        }
      }
    }

    stage('Deploy the artifacts') {
      when {
        expression { nxUtils.isNotPullRequestAndNotDryRun() }
      }
      steps {
        container('maven') {
          echo """
          ----------------------------------------
          Deploy
          ----------------------------------------
          """
          echo "MAVEN_OPTS=$MAVEN_OPTS"
          sh "mvn ${MAVEN_ARGS} -DskipTests -DskipITs deploy"
        }
      }
    }

  }

  post {
    always {
      script {
        nxJira.updateIssues()
      }
    }
  }
}
