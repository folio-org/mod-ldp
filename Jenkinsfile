buildMvn {
  publishModDescriptor = 'yes'
  publishAPI = 'no'
  mvnDeploy = 'yes'
  runLintRamlCop = 'no'
  doKubeDeploy = false
  buildNode = 'jenkins-agent-java11'

  doDocker = {
    buildJavaDocker {
      publishMaster = 'yes'
      healthChk = 'false'
    }
  }
}
