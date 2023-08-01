pipeline {
    agent any

    tools {
        // Install the Maven version configured as "Maven" and add it to the path.
        maven "Maven"
    }

    parameters {
        choice(name: 'PLATFORM', choices: ['android', 'ios'], description: 'Mobile Platform to use for automated tests')
    }

    stages {
        stage('Stage 1 - Checkout Code') {
            steps {
                // Get some code from a GitHub repository
                git ([
                        branch: 'main',
                        changelog: true,
                        credentialsId: 'PERSONAL_GIT_CREDENTIALS_ID',
                        poll: false,
                        url: 'https://github.com/itkhanz/AppiumTDDFramework-OC.git'
                ])

                echo 'Code is checked out'
            }
        }
        stage('Stage 2 - Compile Code') {
            steps {
                echo 'Compiling Code'
                sh "mvn compile"
            }
        }
        stage('Stage 3 - Run Tests') {
            steps {
                echo "Running tests for platform: ${params.PLATFORM}"
                sh "mvn clean test -Dsurefire.suiteXmlFiles='${params.PLATFORM}.xml'"
            }
        }
        stage('Stage 4 - Archive Logs') {
            steps {

                echo 'Archiving the appium server and test logs'
                // Zip the logs folder
                sh 'zip -r logs.zip logs'

                // Archive the zip file as a build artifact
                archiveArtifacts artifacts: 'logs.zip', fingerprint: true
            }
        }
    }

    post {

        always {
            //Publish TestNG Results
            echo 'Publishing the TestNG Results'
            testNG()

            //Publish the HTML report using the HTML Publisher plugin
            echo 'Publishing the Extent Report'
            publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: false,
                    reportDir: 'test-report/extent',
                    reportFiles: '**/extent.html',
                    reportName: 'Extent Report',
                    reportTitles: 'Appium TDD Sauce Labs Demo App Test Report',
                    useWrapperFileDirectly: true
            ])

            // Send an email with the attached HTML report using the email-ext plugin
            echo 'Sending EMAIL using email-ext plugin'
            emailext ([
                    attachLog: true,
                    attachmentsPattern: '**/extent.html, **/emailable-report.html, logs.zip',
                    mimeType: 'text/html',
                    postsendScript: '$DEFAULT_POSTSEND_SCRIPT',
                    presendScript: '$DEFAULT_PRESEND_SCRIPT',
                    subject: '$DEFAULT_SUBJECT',
                    to: '$DEFAULT_RECIPIENTS',
                    body: '''<!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="utf-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1">
                            <title></title>
                        </head>
                        <body>
                        <h4 style="font-size: 20px" class="value" align="left">${JOB_DESCRIPTION}</h4>
                        <div>Jenkins Job: <b>${JOB_NAME}</b></div>
                        <div>Build Initiated by: <b>${BUILD_USER}</b></div>
                        <div>GIT Branch: <b>${GIT_BRANCH}</b></div>
                        <div>Test Platform: <b>${PLATFORM}</b></div>
                        <div>Tests Summary: <b>${FAILED_TESTS, showStack=false}</b></div>
                        <div>Check Build details (incl. TestNG Results, Console Output) at <a href="$BUILD_URL">BUILD URL</a> to view the detailed results.</div>
                        <div>View the HTML report: <a href="${JENKINS_URL}job/${JOB_NAME}/Extent_20Report/">Extent Report</a></div>
                        <table class="container" align="center" style="padding-top: 20px;">
                            <tr class="center">
                                <td colspan="4">
                                    <h2>Appium TDD Automation Test Reports</h2>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <table style="background: #67c2ef;width: 120px;">
                                        <tr>
                                            <td style="font-size: 36px" class="value" align="center">${TEST_COUNTS, var="total"}</td>
                                        </tr>
                                        <tr>
                                            <td style="center">Total</td>
                                        </tr>
                                    </table>
                                </td>
                                <td>
                                    <table style="background: #79c447;width: 120px;">
                                        <tr>
                                            <td style="font-size: 36px" class="value" align="center">${TEST_COUNTS, var="pass"}</td>
                                        </tr>
                                        <tr>
                                            <td style="center">Passed</td>
                                        </tr>
                                    </table>
                                </td>
                                <td>
                                    <table style="background: #ff5454;width: 120px;">
                                        <tr>
                                            <td style="font-size: 36px" class="value" align="center">${TEST_COUNTS, var="fail"}</td>
                                        </tr>
                                        <tr>
                                            <td style="center">Failed</td>
                                        </tr>
                                    </table>
                                </td>
                                <td>
                                    <table style="background: #fabb3d;width: 120px;">
                                        <tr>
                                            <td style="font-size: 36px" class="value" align="center">${TEST_COUNTS, var="skip"}</td>
                                        </tr>
                                        <tr>
                                            <td style="center">Skipped</td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                       </body>
                        </html>'''
            ])

        }
        success {
            echo 'Build Successful'
        }

        failure {
            echo 'Build Failed'
        }

        unstable {
            echo 'Build unstable'
        }
    }
}
