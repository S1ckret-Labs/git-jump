package com.s1ckret.labs.gitj.interfaces.cli.subcommands

import com.github.ajalt.clikt.core.CliktCommand
import com.s1ckret.labs.gitj.domain.subcommands.StatusDomainLogic
import com.s1ckret.labs.gitj.interfaces.io.executeCommand
import com.s1ckret.labs.gitj.interfaces.io.exitProcess

class StatusCommand : CliktCommand() {
    override fun run() {
        val (exitCode, stdout, stderr) = executeCommand(listOf("git", "status", "--porcelain=v2"), 2)

        if (exitCode == 0) {
            StatusDomainLogic.parseGitStatusPorcelainV2(stdout).forEach {
                println(it)
            }
        } else {
            println(stderr)
        }

        exitProcess(exitCode)
    }
}
