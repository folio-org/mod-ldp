buildMvn {
  publishModDescriptor = 'no'
  publishAPI = 'no'
  mvnDeploy = 'no'
  runLintRamlCop = 'no'
  doKubeDeploy = false

  doDocker = {
    buildJavaDocker {
      publishMaster = 'yes'
      healthChk = 'yes'
      healthChkCmd = 'curl -sS --fail -o /dev/null  http://localhost:8081/apidocs/ || exit 1'
    }
  }
}
