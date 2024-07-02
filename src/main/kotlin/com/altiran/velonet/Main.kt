package com.altiran.velonet

import com.github.ajalt.mordant.rendering.TextColors.brightGreen
import com.github.ajalt.mordant.terminal.ExperimentalTerminalApi
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalTerminalApi::class)
fun main() = runBlocking {
    val terminal = Terminal()
    terminal.println(brightGreen("Welcome to the Velonet CLI!"))

    val speedTest = Velonet()
    speedTest.start()
}
