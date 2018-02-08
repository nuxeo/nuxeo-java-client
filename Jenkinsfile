/*
 * (C) Copyright 2017-2018 Nuxeo (http://nuxeo.com/) and others.
 *
 * Contributors:
 *     Thomas Roger <troger@nuxeo.com>
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
properties([
        [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', daysToKeepStr: '60', numToKeepStr: '60', artifactNumToKeepStr: '1']],
        disableConcurrentBuilds(),
        [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
        pipelineTriggers([
                triggers: [
                        [
                                $class          : 'ReverseBuildTrigger',
                                upstreamProjects: "${env.UPSTREAM_PROJECT}",
                                threshold       : hudson.model.Result.SUCCESS
                        ]
                ]
        ])
])

node(env.SLAVE) {
    try {
        timestamps {
            timeout(30) {
                def masterBuild = env.BRANCH_NAME == 'master' && env.STATUS_CONTEXT_NAME == 'nuxeo/master'

                stage('checkout') {
                    checkout scm
                }

                stage('build and test') {
                    step([$class            : 'GitHubCommitStatusSetter',
                          reposSource       : [$class: 'ManuallyEnteredRepositorySource', url: 'https://github.com/nuxeo/nuxeo-java-client'],
                          contextSource     : [$class: 'ManuallyEnteredCommitContextSource', context: "${env.STATUS_CONTEXT_NAME}"],
                          statusResultSource: [$class : 'ConditionalStatusResultSource',
                                               results: [[$class: 'AnyBuildResult', message: 'Building on Nuxeo CI', state: 'PENDING']]]])

                    def jdk = tool name: 'java-8-oracle'
                    env.JAVA_HOME = "${jdk}"
                    def mvnHome = tool name: 'maven-3', type: 'hudson.tasks.Maven$MavenInstallation'
                    def mvnGoals = 'clean install'
                    if (masterBuild) {
                        mvnGoals += ' deploy'
                    }
                    sh "${mvnHome}/bin/mvn ${mvnGoals} -P ${env.TARGET_PLATFORM}"
                }

                stage('post build') {
                    step([$class        : 'WarningsPublisher', canComputeNew: false, canResolveRelativePaths: false,
                          consoleParsers: [[parserName: 'Maven']], defaultEncoding: '', excludePattern: '',
                          healthy       : '', includePattern: '', messagesPattern: '', unHealthy: ''])
                    archive 'nuxeo-java-client/target/*.jar, nuxeo-java-client-test/target/tomcat/log/*.log'
                    junit 'nuxeo-java-client/target/surefire-reports/*.xml'
                    junit 'nuxeo-java-client-test/target/failsafe-reports/*.xml'
                    if (masterBuild) {
                        step([$class: 'JiraIssueUpdater', issueSelector: [$class: 'DefaultIssueSelector'], scm: scm])
                    }
                    if (currentBuild.getPreviousBuild() != null && 'SUCCESS' != currentBuild.getPreviousBuild().getResult()) {
                        mail(to: 'ecm@lists.nuxeo.com', subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) - Back to normal",
                                body: "Build back to normal: ${env.BUILD_URL}.")
                    }
                    step([$class            : 'GitHubCommitStatusSetter',
                          reposSource       : [$class: 'ManuallyEnteredRepositorySource', url: 'https://github.com/nuxeo/nuxeo-java-client'],
                          contextSource     : [$class: 'ManuallyEnteredCommitContextSource', context: "${env.STATUS_CONTEXT_NAME}"],
                          statusResultSource: [$class : 'ConditionalStatusResultSource',
                                               results: [[$class: 'AnyBuildResult', message: 'Successfully built on Nuxeo CI', state: 'SUCCESS']]]])
                }
            }
        }
    } catch (e) {
        currentBuild.result = "FAILURE"
        step([$class: 'ClaimPublisher'])
        archive 'nuxeo-java-client/target/*.jar, nuxeo-java-client-test/target/tomcat/log/*.log'
        mail(to: 'ecm@lists.nuxeo.com', subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) - Failure!",
                body: "Build failed ${env.BUILD_URL}.")
        step([$class            : 'GitHubCommitStatusSetter',
              reposSource       : [$class: 'ManuallyEnteredRepositorySource', url: 'https://github.com/nuxeo/nuxeo-java-client'],
              contextSource     : [$class: 'ManuallyEnteredCommitContextSource', context: "${env.STATUS_CONTEXT_NAME}"],
              statusResultSource: [$class : 'ConditionalStatusResultSource',
                                   results: [[$class: 'AnyBuildResult', message: 'Failed to build on Nuxeo CI', state: 'FAILURE']]]])
        throw e
    } finally {
        step([$class : 'CheckStylePublisher', canComputeNew: false, defaultEncoding: '', healthy: '',
              pattern: 'ftest/target/checkstyle-result.xml', unHealthy: ''])
    }
}
