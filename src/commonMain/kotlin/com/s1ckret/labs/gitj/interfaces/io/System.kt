package com.s1ckret.labs.gitj.interfaces.io

data class ExecutionResult(val exitCode: Int, val stdout: String, val stderr: String)

expect fun executeCommand(command: List<String>, timeout: Long): ExecutionResult

expect fun exitProcess(exitCode: Int)
