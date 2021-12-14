/*
 * (C) Copyright 2017-2020 Nuxeo (http://nuxeo.com/) and others.
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

                def jdk = tool name: 'java-11-openjdk'
                if (env.STATUS_CONTEXT_NAME == 'nuxeo/8.10' || env.STATUS_CONTEXT_NAME == 'nuxeo/9.10') {
                  jdk = tool name: 'java-8-oracle'
                }
                env.JAVA_HOME = "${jdk}"

                def sha = stage('checkout') {
                    checkout scm
                    return sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                }

                withBuildStatus("${env.STATUS_CONTEXT_NAME}", 'https://github.com/nuxeo/nuxeo-java-client', sha, "${BUILD_URL}") {

                    stage('build and test') {
                        withMaven(maven: 'maven-3') {
                            def resolveRangeOpts = '-DgenerateBackupPoms=false'
                            def mvnGoals = 'clean install'
                            if (masterBuild) {
                                mvnGoals += ' deploy'
                                resolveRangeOpts += ' -DallowSnapshots=true -DincludeProperties=nuxeo.tested.version'
                            }
                            sh "mvn versions:resolve-ranges -P ${env.TARGET_PLATFORM},qa ${resolveRangeOpts}"
                            sh "mvn ${mvnGoals} -P ${env.TARGET_PLATFORM},qa"
                        }
                    }

                    stage('post build') {
                        step([$class        : 'WarningsPublisher', canComputeNew: false, canResolveRelativePaths: false,
                              consoleParsers: [[parserName: 'Maven']], defaultEncoding: '', excludePattern: '',
                              healthy       : '', includePattern: '', messagesPattern: '', unHealthy: ''])
                        archive 'nuxeo-java-client/target/*.jar, nuxeo-java-client-test/target/tomcat/log/*.log'
                        if (masterBuild) {
                            step([$class: 'JiraIssueUpdater', issueSelector: [$class: 'DefaultIssueSelector'], scm: scm])
                        }
                        if (currentBuild.getPreviousBuild() != null && 'SUCCESS' != currentBuild.getPreviousBuild().getResult()) {
                            mail(to: 'ecm@lists.nuxeo.com', subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) - Back to normal",
                                 body: "Build back to normal: ${env.BUILD_URL}.")
                        }
                    }
                }
            }
        }
    } catch (e) {
        currentBuild.result = "FAILURE"
        step([$class: 'ClaimPublisher'])
        archive 'nuxeo-java-client/target/*.jar, nuxeo-java-client-test/target/tomcat/log/*.log'
        mail(to: 'ecm@lists.nuxeo.com', subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) - Failure!",
             body: "Build failed ${env.BUILD_URL}.")
        throw e
    } finally {
        junit '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml, **/target/failsafe-reports/**/*.xml'
        step([$class : 'CheckStylePublisher', canComputeNew: false, defaultEncoding: '', healthy: '',
              pattern: 'ftest/target/checkstyle-result.xml', unHealthy: ''])
    }
}
