package com.s1ckret.labs

import com.github.ajalt.clikt.core.subcommands
import com.s1ckret.labs.commands.GitJump
import com.s1ckret.labs.commands.Status

fun main(args: Array<String>) =
    GitJump().subcommands(Status()).main(args)
