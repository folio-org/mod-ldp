buildMvn {
  publishModDescriptor = true
  doApiDoc = true
  mvnDeploy = true
  doApiLint = true
  doKubeDeploy = false
  buildNode = 'jenkins-agent-java11'

  doDocker = {
    buildJavaDocker {
      publishMaster = true
      healthChk = false
    }
  }
}
