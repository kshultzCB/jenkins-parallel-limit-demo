package com.mycorp.ci

import org.jenkinsci.plugins.workflow.cps.CpsScript
import java.util.concurrent.LinkedBlockingDeque

class Demo {

    static void run(CpsScript currentJob) {

        String demoNodeLabel = currentJob.params.DEMO_NODE_LABEL ?: "prod"

        int totalBranches = currentJob.params.TOTAL_BRANCHES.toInteger()
        int maxParallelBranches = currentJob.params.MAX_PARALLEL_BRANCHES.toInteger()
        int secondsToSleep = currentJob.params.SECONDS_TO_SLEEP_IN_BRANCH.toInteger()

        currentJob.echo("Using demoNodeLabel : ${demoNodeLabel}")
        currentJob.echo("Using totalBranches : ${totalBranches}")
        currentJob.echo("Using maxParallelBranches : ${maxParallelBranches}")
        currentJob.echo("Using secondsToSleep : ${secondsToSleep}")

        List<String> stages = (1..totalBranches).collect { "Stage-${it}" as String }

        parallelLimitedBranches(currentJob, stages, maxParallelBranches, false) { String stageName ->
            currentJob.stage(stageName) {
                currentJob.node(demoNodeLabel) {
                    currentJob.echo("Running stage ${stageName}, sleeping for 5 seconds")
                    currentJob.sleep(secondsToSleep)
                }
            }
        }
    }

    static def parallelLimitedBranches(
            CpsScript currentJob,
            List<String> items,
            Integer maxConcurrentBranches,
            Boolean failFast = false,
            Closure body) {

        def branches = [:]
        Deque latch = new LinkedBlockingDeque(maxConcurrentBranches)
        maxConcurrentBranches.times {
            latch.offer("$it")
        }

        items.each {
            branches["${it}"] = {
                def queueSlot = null
                while (true) {
                    queueSlot = latch.pollFirst();
                    if (queueSlot != null) {
                        break;
                    }
                }
                try {
                    body(it)
                }
                finally {
                    latch.offer(queueSlot)
                }
            }
        }

        currentJob.parallel(branches)
    }
}