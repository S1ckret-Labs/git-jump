package com.s1ckret.labs.gitj.interfaces.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


actual fun exitProcess(exitCode: Int) {
    kotlin.system.exitProcess(exitCode)
}

actual fun executeCommand(
    command: List<String>,
    timeout: Long
): ExecutionResult {
    return runBlocking {
        withContext(Dispatchers.IO) {
            ProcessBuilder()
                .command(command)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
                .onExit()
                .get(timeout, TimeUnit.SECONDS)
        }.run {
            return@run ExecutionResult(
                exitCode = exitValue(),
                stdout = inputStream.bufferedReader().readText(),
                stderr = errorStream.bufferedReader().readText(),
            )
        }
    }
}
