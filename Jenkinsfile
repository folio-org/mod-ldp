buildMvn {
  publishModDescriptor = true
  mvnDeploy = true
  doKubeDeploy = false
  buildNode = 'jenkins-agent-java11'

  doDocker = {
    buildJavaDocker {
      publishMaster = true
      healthChk = false
    }
  }
}
