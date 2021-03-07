pipeline {
    agent any
    options {
      ansiColor("xterm")
      timestamps()
      buildDiscarder logRotator(artifactDaysToKeepStr: "", artifactNumToKeepStr: "", daysToKeepStr: "", numToKeepStr: "20")
    }

    stages {
        stage("Setup") {
            steps {
                deleteDir()

                git branch: "develop", url: "git@github.com:nathanryder/SmartHomeHub.git", credentialsId: "ba55b59e-cea4-455e-9d8f-75907fa49d11"
            }
        }

        stage("Build") {
            steps {
                sh("mvn clean package")
            }
        }

        stage("Create Images") {
            steps {
                sh """
                    docker buildx build --platform linux/amd64,linux/arm/v7 -t nathanryder/finalyearproject:latest --push .
                 """
            }
        }

        stage("Merge") {
            steps {
                script {
                    def user = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')
                    if (user.isEmpty()){
                        sh """
                            git fetch origin master
                            git merge origin/master
                            git push origin develop:master
                        """
                    }
                }
            }
        }

        stage("Deploy") {
            steps {
                echo "Deploying to Raspberry Pi.."
                sh """
                    ssh pi@89.124.4.203 -p 1013 << EOL
                        cd /tmp
                        git clone git@github.com:nathanryder/SmartHomeHub.git
                        cd SmartHomeHub/scripts/
                        chmod +x deploy.sh
                        ./deploy.sh
                    EOL

                """
                echo "Done!"
            }
        }
    }

    post {
        always {
            script {
                CHANGES = "*No changes*"
                if (env.CHANGE_TITLE) {
                    CHANGES = "${env.CHANGE_TITLE}"
                }
            }

            discordSend description: "**Build:** [$BUILD_NUMBER](http://ci.nathanryder.ml/job/FinalYearProject/$BUILD_NUMBER)\n**Status:** ${currentBuild.currentResult}\n\n**Changes:**\n\n$CHANGES\n\n\n", footer: "Jenkins v2.164.1, Discord Notifier v1.4.6", link: "https://www.google.com", result: "${currentBuild.currentResult}", title: "FinalYearProject #$BUILD_NUMBER", webhookURL: "https://discord.com/api/webhooks/817398683724939284/dxi5Xeo3nUHmLrqpy2-GtMVG8nI56iQ3v7rN3dYvmONS527fIVEfZzcAdPg0ZJLxP-fU"
        }
        success {
            archiveArtifacts artifacts: "target/*.jar", allowEmptyArchive: "true"
        }
    }
}