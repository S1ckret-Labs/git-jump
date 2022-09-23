package com.s1ckret.labs.gitj.interfaces.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.s1ckret.labs.gitj.interfaces.cli.subcommands.StatusCommand

class GitJump : CliktCommand() {
    override fun run() {
    }

    companion object {
        fun run(args: Array<String>) =
            GitJump().subcommands(StatusCommand()).main(args)
    }
}
