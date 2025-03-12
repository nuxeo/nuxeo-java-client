/*
 * (C) Copyright 2021-2025 Nuxeo (http://nuxeo.com/) and others.
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

library identifier: "platform-ci-shared-library@v0.0.45"

def lib

pipeline {
  agent {
    label 'jenkins-nuxeo-package-lts-2023'
  }
  options {
    buildDiscarder(logRotator(daysToKeepStr: '60', numToKeepStr: '60', artifactNumToKeepStr: '5'))
    disableConcurrentBuilds()
    githubProjectProperty(projectUrlStr: 'https://github.com/nuxeo/nuxeo-java-client')
  }
  environment {
    CURRENT_NAMESPACE = nxK8s.getCurrentNamespace()
    MAVEN_ARGS = '-B -nsu -DskipPrePostIntegration'
    MAVEN_OPTS = "$MAVEN_OPTS -Xms512m -Xmx3072m"
    JIRA_PROJECT = 'JAVACLIENT'
    JIRA_MOVING_VERSION = '"next"'
    JIRA_RELEASED_VERSION = "${VERSION}"
    VERSION = nxUtils.getReleaseVersion()
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

    stage('Update version') {
      steps {
        container('maven') {
          script {
            echo """
            ----------------------------------------
            Update version
            ----------------------------------------
            New version: ${VERSION}
            """.stripIndent()
            nxMvn.updateVersion()
          }
        }
      }
    }

    stage('Build Maven project') {
      steps {
        container('maven') {
          echo """
          ----------------------------------------
          Compile
          ----------------------------------------
          """
          echo "MAVEN_OPTS=$MAVEN_OPTS"
          sh "mvn ${MAVEN_ARGS} -Prelease -DskipITs install"
        }
      }
      post {
        always {
          archiveArtifacts artifacts: '**/target/*.jar, **/target/nuxeo-java-client-*.zip, **/target/**/*.log'
          junit testResults: '**/target/surefire-reports/*.xml'
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
        expression { !nxUtils.isDryRun() }
      }
      steps {
        container('maven') {
          echo """
          ----------------------------------------
          Deploy
          ----------------------------------------
          """
          echo "MAVEN_OPTS=$MAVEN_OPTS"
          sh "mvn ${MAVEN_ARGS} -Prelease -DskipTests -DskipITs deploy"
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
            nxGit.commitTagPush(tag: "release-${VERSION}")
          }
        }
      }
    }

    stage('Bump branch') {
      steps {
        container('maven') {
          script {
            String nextVersion = nxUtils.getNextVersion(increment: 'patch') + '-SNAPSHOT'
            echo """
            -------------------------------------------------
            Bump ${BRANCH_NAME} branch
            -------------------------------------------------
            New version: ${nextVersion}
            """
            sh "mvn ${MAVEN_ARGS} versions:set -DnewVersion=${nextVersion} -DgenerateBackupPoms=false"
            nxGit.commitPush(message: "Post release ${VERSION}")
          }
        }
      }
    }

    stage('Release Project') {
      steps {
        container('maven') {
          script {
            def jiraIssueFetchers = [
                type                 : 'jira',
                jql                  : "project = ${JIRA_PROJECT} and fixVersion = ${JIRA_MOVING_VERSION}",
                newJiraVersion       : [
                    project    : env.JIRA_PROJECT,
                    name       : env.JIRA_RELEASED_VERSION,
                    releaseDate: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    released   : true,
                ],
                jiraMovingVersionName: env.JIRA_MOVING_VERSION,
            ]
            nxProject.release(issuesFetchers: [jiraIssueFetchers], tagPrefix: 'release-')
          }
        }
      }
    }

  }

  post {
    success {
      script {
        currentBuild.description = "Release ${VERSION}"
        nxSlack.success(message: "Successfully released nuxeo/nuxeo-java-client ${VERSION}: ${BUILD_URL}")
      }
    }
    unsuccessful {
      script {
        nxSlack.error(message: "Failed to release nuxeo/nuxeo-java-client ${VERSION}: ${BUILD_URL}")
      }
    }
  }
}
