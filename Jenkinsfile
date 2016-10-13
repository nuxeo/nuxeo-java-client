node('SLAVE') {
    try {
        wrap([$class: 'TimestamperBuildWrapper']) {
            stage 'checkout'
            checkout([$class: 'GitSCM', branches: [[name: '*/master']], browser: [$class: 'GithubWeb', repoUrl: 'https://github.com/nuxeo/nuxeo-java-client'], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'nuxeo-java-client']], submoduleCfg: [], userRemoteConfigs: [[url: 'git@github.com:nuxeo/nuxeo-java-client.git']]])
            checkout([$class: 'GitSCM', branches: [[name: '*/master']], browser: [$class: 'GithubWeb', repoUrl: 'https://github.com/nuxeo/nuxeo-java-client-test'], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'nuxeo-java-client-test']], submoduleCfg: [], userRemoteConfigs: [[url: 'git@github.com:nuxeo/nuxeo-java-client-test.git']]])

            def mvnHome = tool name: 'maven-3.3', type: 'hudson.tasks.Maven$MavenInstallation'

            dir('nuxeo-java-client') {
                stage 'build-client'
                sh "${mvnHome}/bin/mvn clean deploy"
            }

            dir('nuxeo-java-client-test') {
                stage 'run-test'
                sh "${mvnHome}/bin/mvn clean deploy"
            }
        }
    } catch (e) {
        currentBuild.result = 'FAILURE'
        step([$class: 'ClaimPublisher'])
        throw e
    }
}
