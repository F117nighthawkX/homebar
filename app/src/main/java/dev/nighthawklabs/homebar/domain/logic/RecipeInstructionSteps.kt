package dev.nighthawklabs.homebar.domain.logic

fun parseInstructionSteps(instructions: String): List<String> =
    instructions.lines()
        .map(String::trim)
        .filter(String::isNotBlank)

fun formatInstructionSteps(steps: List<String>): String =
    steps
        .map(String::trim)
        .filter(String::isNotBlank)
        .joinToString(separator = "\n")
