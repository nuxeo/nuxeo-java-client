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

String getDefaultNuxeoVersionToTest() {
  return '2023'
}

def getNuxeoVersionsToTest() {
  return [
    '10.10',
    '2021',
    getDefaultNuxeoVersionToTest()
  ]
}

String getFunctionalTestDockerImageTag(nuxeoVersion) {
  return "${nuxeoVersion}-${VERSION}"
}

def buildFunctionalDockerImages() {
  def stages = [:]
  for (nuxeoVersion in getNuxeoVersionsToTest()) {
    stages["Build functional tests image ${nuxeoVersion}"] = buildFunctionalTestDockerBuildStage(nuxeoVersion)
  }
  parallel stages
}

Closure buildFunctionalTestDockerBuildStage(nuxeoVersion) {
  return {
    container("maven") {
      script {
        // TODO for 10.10 retrieve the last HF version
        def nuxeoDockerVersion =  nuxeoVersion
        if ("${nuxeoDockerVersion}"  == "10.10" ) {
          nuxeoDockerVersion = '10.10-HF63'
        }
        nxDocker.build(skaffoldFile: 'ci/docker/nuxeo/skaffold.yaml',
            envVars: ["FTESTS_VERSION=${getFunctionalTestDockerImageTag(nuxeoVersion)}", "NUXEO_VERSION=${nuxeoDockerVersion}"])
      }
    }
  }
}

def runFunctionalTests() {
  def runStages = [:]
  for (nuxeoVersion in getNuxeoVersionsToTest()) {
    runStages["Against Nuxeo ${nuxeoVersion}"] = buildFunctionalTestStage(nuxeoVersion)
  }
  parallel runStages
}

Closure buildFunctionalTestStage(nuxeoVersion) {
  def nuxeoVersionSlug = nuxeoVersion.replaceAll('\\..*', '')
  def testNamespace = "$CURRENT_NAMESPACE-java-client-ftests-$BRANCH_NAME-$BUILD_NUMBER-nuxeo-${nuxeoVersionSlug}".toLowerCase()

  String mvnCustomEnv = "nuxeo-${nuxeoVersionSlug}"
  return {
    container("maven") {
      nxWithHelmfileDeployment(namespace: testNamespace, environment: "functional-tests-${nuxeoVersion}",
          envVars: ["NUXEO_VERSION=${getFunctionalTestDockerImageTag(nuxeoVersion)}"]) {
        script {
          try {
            echo """
            ----------------------------------------
            Run Java Client functional tests
            ---------------------------------------- 
            Nuxeo version: ${nuxeoVersion}
            """
            sh """
              mvn -pl :nuxeo-java-client-test \
                ${MAVEN_ARGS} \
                -Dcustom.environment=${mvnCustomEnv} \
                -Dnuxeo.server.url=http://nuxeo.${testNamespace}.svc.cluster.local/nuxeo \
                verify
            """
          } finally {
            junit testResults: "**/target-${mvnCustomEnv}/failsafe-reports/*.xml"
          }
        }
      }
    }
  }
}

return this
