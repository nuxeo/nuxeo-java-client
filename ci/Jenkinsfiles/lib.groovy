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

def setPodsLabel() {
  echo """
  ----------------------------------------
  Set Kubernetes resource labels
  ----------------------------------------
  """
  echo "Set label 'branch: ${BRANCH_NAME}' on pod ${NODE_NAME}"
  sh "kubectl label pods ${NODE_NAME} branch=${BRANCH_NAME}"
}

String getHelmfileCommand() {
  return "helmfile --file ci/helm/helmfile.yaml --helm-binary /usr/bin/helm3"
}

void helmfileSync(namespace, environment) {
  def helmfileCommand = getHelmfileCommand()
  withEnv(["NAMESPACE=${namespace}"]) {
    sh """
      ${helmfileCommand} deps
      ${helmfileCommand} --namespace=${namespace} --environment ${environment} sync
    """
  }
}

void helmfileDestroy(namespace, environment) {
  def helmfileCommand = getHelmfileCommand()
  withEnv(["NAMESPACE=${namespace}"]) {
    sh """
      ${helmfileCommand} --namespace=${namespace} --environment ${environment} destroy
    """
  }
}

def runFunctionalTests() {
  def testNuxeoVersions = [
    '10.10',
    '2021'
  ]
  def stages = [:]
  for (nuxeoVersion in testNuxeoVersions) {
    stages["Against Nuxeo ${nuxeoVersion}"] = buildFunctionalTestStage(nuxeoVersion);
  }
  parallel stages
}

def buildFunctionalTestStage(nuxeoVersion) {
  def environment = "functional-tests-${nuxeoVersion}"
  def testNamespace = "$CURRENT_NAMESPACE-nuxeo-java-client-ftests-$BRANCH_NAME-$BUILD_NUMBER-${nuxeoVersion.replace('.', '-')}".toLowerCase()

  def ftestsVersion = "${nuxeoVersion}-${VERSION}"
  return {
    stage("Against Nuxeo ${nuxeoVersion}") {
      container("maven") {
        script {
          try {
            // TODO for 10.10 retrieve the last HF version
            def nuxeoDockerVersion =  nuxeoVersion
            if ("${nuxeoDockerVersion}"  == "10.10" ) {
              nuxeoDockerVersion = '10.10-HF59'
            }
            echo """
              ----------------------------------------
              Build Nuxeo functional tests image
              ----------------------------------------
              Nuxeo version: ${nuxeoDockerVersion}
              Image tag: ${ftestsVersion}
              Registry: ${DOCKER_REGISTRY}
            """
            // push images to the Jenkins internal Docker registry
            withEnv(["FTESTS_VERSION=${ftestsVersion}", "NUXEO_VERSION=${nuxeoDockerVersion}"]) {
              sh '''
                envsubst < ci/docker/nuxeo/skaffold.yaml > skaffold.yaml~gen
                skaffold build -f skaffold.yaml~gen
              '''
            }
            echo "Create ftests namespace for ${nuxeoVersion}"
            sh "kubectl create namespace ${testNamespace}"

            echo "Copy image pull secret to ${testNamespace} namespace"
            sh "kubectl --namespace=platform get secret kubernetes-docker-cfg -ojsonpath='{.data.\\.dockerconfigjson}' | base64 --decode > /tmp/config.json"
            sh """kubectl create secret generic kubernetes-docker-cfg \
                --namespace=${testNamespace} \
                --from-file=.dockerconfigjson=/tmp/config.json \
                --type=kubernetes.io/dockerconfigjson --dry-run -o yaml | kubectl apply -f -"""

            echo """
              ----------------------------------------
              Deploy Nuxeo
              ---------------------------------------- 
            """
            withEnv(["NUXEO_VERSION=${ftestsVersion}"]) {
              helmfileSync("${testNamespace}", "${environment}")
            }

            echo """
              ----------------------------------------
              Run Java Client functional tests
              ---------------------------------------- 
            """
            sh """mvn -pl :nuxeo-java-client-test \
                  ${MAVEN_ARGS} \
                  -Dcustom.environment=${nuxeoVersion} \
                  -Dnuxeo.server.url=http://nuxeo.${testNamespace}.svc.cluster.local/nuxeo \
                  verify
            """
          } finally {
            try {
              sh "kubectl logs -n ${testNamespace} \
                  \$(kubectl get pods -n ${testNamespace} --selector=app=nuxeo --output=jsonpath='{.items[*].metadata.name}') \
                > nuxeo-java-client-test/target-${nuxeoVersion}/nuxeo-server.log || true"
              archiveArtifacts allowEmptyArchive: true, artifacts: "**/target-${nuxeoVersion}/*.log"
              junit testResults: "**/target-${nuxeoVersion}/failsafe-reports/*.xml"
            } finally {
              try {
                echo "${nuxeoVersion} ftests: clean up test namespace"
                helmfileDestroy("${testNamespace}", "${environment}")
              } finally {
                // clean up test namespace
                sh "kubectl delete namespace ${testNamespace} --ignore-not-found=true"
              }
            }
          }
        }
      }
    }
  }
}

return this
