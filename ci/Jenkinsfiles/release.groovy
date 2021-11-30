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

String getCurrentNamespace() {
  container('maven') {
    return sh(returnStdout: true, script: "kubectl get pod ${NODE_NAME} -ojsonpath='{..namespace}'")
  }
}

String getVersion() {
  return readMavenPom().getVersion().replace('-SNAPSHOT', '')
}

boolean isMinorRelease() {
  container('maven') {
    return sh(returnStdout: true, script: "semver get patch ${VERSION} | tr -d '\n'") == '0'
  }
}

def lib

pipeline {
  agent {
    label 'jenkins-nuxeo-package-lts-2021'
  }
  options {
    buildDiscarder(logRotator(daysToKeepStr: '60', numToKeepStr: '60', artifactNumToKeepStr: '5'))
    disableConcurrentBuilds()
    githubProjectProperty(projectUrlStr: 'https://github.com/nuxeo/nuxeo-java-client')
  }
  environment {
    CURRENT_NAMESPACE = getCurrentNamespace()
    MAVEN_ARGS = '-B -nsu -DskipPrePostIntegration'
    MAVEN_OPTS = "$MAVEN_OPTS -Xms512m -Xmx3072m"
    SLACK_CHANNEL = 'platform-notifs'
    VERSION = "${getVersion()}"
  }
  stages {

    stage('Load library / Set labels') {
      steps {
        container('maven') {
          script {
            lib = load 'ci/Jenkinsfiles/lib.groovy'
            lib.setPodsLabel()
          }
        }
      }
    }

    stage('Update version') {
      steps {
        container('maven') {
          echo """
          ----------------------------------------
          Update version
          ----------------------------------------
          New version: ${VERSION}
          """
          sh "mvn ${MAVEN_ARGS} versions:set -DnewVersion=${VERSION} -DgenerateBackupPoms=false"
        }
      }
    }

    stage('Build and deploy Maven project') {
      steps {
        container('maven') {
          echo """
          ----------------------------------------
          Compile
          ----------------------------------------"""
          echo "MAVEN_OPTS=$MAVEN_OPTS"
          sh "mvn ${MAVEN_ARGS} -DskipITs ${env.DRY_RUN != 'true' ? 'deploy' : 'install'}"
        }
      }
      post {
        always {
          archiveArtifacts artifacts: '**/target/*.jar, **/target/nuxeo-java-client-*.zip, **/target/**/*.log'
          junit testResults: '**/target/surefire-reports/*.xml'
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

    stage('Create tag') {
      steps {
        container('maven') {
          script {
            echo """
            -------------------------------------------------
            Tag nuxeo-java-client ${VERSION}
            -------------------------------------------------
            """
            sh """
              git commit -a -m "Release ${VERSION}"
              git tag -a release-${VERSION} -m "Release ${VERSION}"
            """

            if (env.DRY_RUN != 'true') {
              sh """
                jx step git credentials
                git config credential.helper store
                git push origin release-${VERSION}
              """
            }
          }
        }
      }
    }

    stage('Bump branch') {
      steps {
        container('maven') {
          script {
            String nextVersion
            if (isMinorRelease()) {
              // it was a minor release
              nextVersion = sh(returnStdout: true, script: "semver bump minor ${VERSION} | tr -d '\n'")
            } else {
              // it was a patch release
              nextVersion = sh(returnStdout: true, script: "semver bump patch ${VERSION} | tr -d '\n'")
            }
            nextVersion += '-SNAPSHOT'
            echo """
            -------------------------------------------------
            Bump ${BRANCH_NAME} branch
            -------------------------------------------------
            New version: ${nextVersion}
            """
            sh """
              mvn ${MAVEN_ARGS} versions:set -DnewVersion=${nextVersion} -DgenerateBackupPoms=false
              git commit -a -m "Post release ${VERSION}"
            """

            if (env.DRY_RUN != 'true') {
              sh """
                jx step git credentials
                git config credential.helper store
                git push origin HEAD:${BRANCH_NAME}
              """
            }
          }
        }
      }
    }

  }

  post {
    success {
      script {
        if (env.DRY_RUN != 'true') {
          currentBuild.description = "Release ${VERSION}"
          slackSend(channel: "${SLACK_CHANNEL}", color: 'good', message: "Successfully released nuxeo/nuxeo-java-client ${VERSION}: ${BUILD_URL}")
        }
      }
    }
    unsuccessful {
      script {
        if (env.DRY_RUN != 'true') {
          slackSend(channel: "${SLACK_CHANNEL}", color: 'danger', message: "Failed to release nuxeo/nuxeo-java-client ${VERSION}: ${BUILD_URL}")
        }
      }
    }
  }
}
