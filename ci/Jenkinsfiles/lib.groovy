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

def getNuxeoTagsToTest() {
  return [
    '2023',
    '2025',
  ]
}

String getFunctionalTestDockerImageTag(nuxeoTag) {
  return "${nuxeoTag}-${VERSION}"
}

String getClidSecret(nuxeoTag) {
  // target connect preprod if nuxeo tag is a build tag or a moving tag
  return nuxeoTag.matches("^\\d+\\.(x|\\d+\\.\\d+)\$") ? 'instance-clid-preprod' : 'instance-clid'
}

def buildFunctionalDockerImages() {
  def stages = [:]
  for (nuxeoTag in getNuxeoTagsToTest()) {
    stages["Build functional tests image ${nuxeoTag}"] = buildFunctionalTestDockerBuildStage(nuxeoTag)
  }
  parallel stages
}

Closure buildFunctionalTestDockerBuildStage(nuxeoTag) {
  return {
    container("maven") {
      script {
        nxDocker.build(skaffoldFile: 'ci/docker/nuxeo/skaffold.yaml',
            envVars: ["FTESTS_VERSION=${getFunctionalTestDockerImageTag(nuxeoTag)}", "NUXEO_VERSION=${nuxeoTag}"])
      }
    }
  }
}

def runFunctionalTests() {
  def runStages = [:]
  for (nuxeoTag in getNuxeoTagsToTest()) {
    runStages["Against Nuxeo ${nuxeoTag}"] = buildFunctionalTestStage(nuxeoTag)
  }
  parallel runStages
}

Closure buildFunctionalTestStage(nuxeoTag) {
  def nuxeoFullVersion = nxDocker.getLabel(image: "${PRIVATE_DOCKER_REGISTRY}/nuxeo/nuxeo:${nuxeoTag}", label: 'org.nuxeo.version')
  def clidSecret = getClidSecret(nuxeoTag)

  def nuxeoTagSlug = nuxeoTag.replaceAll('\\..*', '')
  def testNamespace = "$CURRENT_NAMESPACE-java-client-ftests-$BRANCH_NAME-$BUILD_NUMBER-nuxeo-${nuxeoTagSlug}".toLowerCase()
  def environment = "functional-tests-${nuxeoTagSlug}"

  String mvnCustomEnv = "nuxeo-${nuxeoTagSlug}"
  if (nuxeoFullVersion.startsWith("2023")) {
    nuxeoFullVersion = "2023.36.0"
  }
  return {
    container("maven") {
      nxWithHelmfileDeployment(namespace: testNamespace, environment: environment,
          envVars: ["CONNECT_CLID_SECRET=${clidSecret}", "NUXEO_VERSION=${nuxeoFullVersion}", "VERSION=${getFunctionalTestDockerImageTag(nuxeoTag)}"],
          secrets: [[name: clidSecret, namespace: 'platform']],
          cacheName: environment) {
        script {
          try {
            echo """
            ----------------------------------------
            Run Java Client functional tests
            ---------------------------------------- 
            Nuxeo version: ${nuxeoTag} (${nuxeoFullVersion})
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
