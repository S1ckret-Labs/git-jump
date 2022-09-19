package com.s1ckret.labs.commands;

import com.github.ajalt.clikt.core.CliktCommand


class Status : CliktCommand() {
    override fun run() {
        println("status")
    }
}
