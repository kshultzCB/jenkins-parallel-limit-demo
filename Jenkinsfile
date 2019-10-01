@Library('jenkins-parallel-limit-demo') _

pipeline {
    parameters {
        string(
            name: "DEMO_NODE_LABEL",
            defaultValue: "prod",
            description: "The label filter to be used when creating dynamic branches/stages"
        )
        text(
            name: "TOTAL_BRANCHES",
            defaultValue: "20",
            description: "Total number of dynamic branches/stages to created"
        )
        string(
            name: "MAX_PARALLEL_BRANCHES",
            defaultValue: "4",
            description: "The maximum number of parallel branches/stages that will be executing at any given time."
        )
        string(
            name: "SECONDS_TO_SLEEP_IN_BRANCH",
            defaultValue: "5",
            description: "The amount of time to sleep in branch/stage to simulate work."
        )
    }
    agent {
        label 'prod'
    }
    stages {
        stage("RunParallel") {
            steps {
                runParallelLimitedBranches()
            }
        }
    }
}
