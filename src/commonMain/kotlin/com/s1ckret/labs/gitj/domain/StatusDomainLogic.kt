package com.s1ckret.labs.gitj.domain

import arrow.core.zip
import com.s1ckret.labs.gitj.domain.validation.Validation
import com.s1ckret.labs.gitj.domain.validation.ValidationError
import com.s1ckret.labs.gitj.domain.validation.err
import com.s1ckret.labs.gitj.domain.validation.ok

fun parseGitStatusPorcelainV2(text: String): List<Any> {
    return text.trim().split("\n").map { line ->
        val tokens = line.trim().split(" ")
        when (tokens[0]) {
            // 0  1     2    3    4    5    6    7     8
            // 1 <XY> <sub> <mH> <mI> <mW> <hH> <hI> <path>
            "1" -> {
                parseEntryStatus(tokens[1]).fold(
                    { validationErrors -> Entry.SkippedEntry(reason = "$line : ${validationErrors.all}") },
                    { entryStatus -> Entry.ChangedEntry(status = entryStatus, path = tokens[8]) }
                )
            }
            // 0  1     2    3    4    5    6    7       8              9
            // 2 <XY> <sub> <mH> <mI> <mW> <hH> <hI> <X><score> <path><sep><origPath>
            "2" -> {
                parseEntryStatus(tokens[1]).fold(
                    { validationErrors -> Entry.SkippedEntry(reason = "$line : ${validationErrors.all}") },
                    { entryStatus ->
                        val (targetPath, originPath) = tokens[9].trim().split("\t")
                        when (entryStatus.index) {
                            StatusCharacter.COPIED -> Entry.CopiedEntry(entryStatus, targetPath, originPath)
                            StatusCharacter.RENAMED -> Entry.RenamedEntry(entryStatus, targetPath, originPath)
                            else -> Entry.SkippedEntry(reason = "$line : Index status must be C or R!")
                        }
                    }
                )
            }
            // 0  1     2    3    4    5    6    7    8    9     10
            // u <XY> <sub> <m1> <m2> <m3> <mW> <h1> <h2> <h3> <path>
            "u" -> {
                parseEntryStatus(tokens[1]).fold(
                    { validationErrors -> Entry.SkippedEntry(reason = "$line : ${validationErrors.all}") },
                    { entryStatus -> Entry.UnmergedEntry(status = entryStatus, path = tokens[10]) }
                )
            }
            // 0   1
            // ? <path>
            "?" -> Entry.UntrackedEntry(path = tokens[1])
            // 0   1
            // ! <path>
            "!" -> Entry.IgnoredEntry(path = tokens[1])
            else -> Entry.SkippedEntry(reason = "$line : Unknown first character")
        }
    }
}

private fun parseEntryStatus(xy: String): Validation<EntryStatus> =
    StatusCharacter.of(xy[0]).zip(StatusCharacter.of(xy[1])) { x, y -> EntryStatus(index = x, workingTree = y) }

data class UnknownStatusCharacter(val msg: String) : ValidationError(msg)
enum class StatusCharacter(val char: Char) {
    NOT_UPDATED('.'),
    IGNORED('!'),
    UNTRACKED('?'),
    MODIFIED('M'),
    FILE_TYPE_CHANGED('T'),
    ADDED('A'),
    DELETED('D'),
    RENAMED('R'),
    COPIED('C'),
    UNMERGED('U');

    companion object {
        private val allowedCharacters = StatusCharacter.values().map { it.char }

        fun of(char: Char): Validation<StatusCharacter> {
            return if (char in allowedCharacters)
                values().first { char == it.char }.ok()
            else
                UnknownStatusCharacter("$char doesn't match any of $allowedCharacters").err()
        }
    }
}

data class EntryStatus(val index: StatusCharacter, val workingTree: StatusCharacter)
sealed class Entry {
    data class ChangedEntry(val status: EntryStatus, val path: String)
    data class RenamedEntry(val status: EntryStatus, val targetPath: String, val originalPath: String)
    data class CopiedEntry(val status: EntryStatus, val targetPath: String, val originalPath: String)
    data class UnmergedEntry(val status: EntryStatus, val path: String)
    data class UntrackedEntry(val path: String)
    data class IgnoredEntry(val path: String)
    data class SkippedEntry(val reason: String)
}
