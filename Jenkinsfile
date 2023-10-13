buildMvn {
  publishModDescriptor = true
  mvnDeploy = true
  doKubeDeploy = false
  buildNode = 'jenkins-agent-java17'

  doDocker = {
    buildJavaDocker {
      publishMaster = true
      healthChk = false
    }
  }
}
