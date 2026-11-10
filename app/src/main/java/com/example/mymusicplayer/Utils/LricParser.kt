package com.example.mymusicplayer.Utils

import com.example.mymusicplayer.model.LyricLine

object LrcParser {

    private val timestampRegex =
        Regex("""\[(\d{1,2}):(\d{2})(?:[.:](\d{1,3}))?]""")

    fun parse(lyrics: String): List<LyricLine> {

        return lyrics.lines().flatMap { line ->

            val matches = timestampRegex.findAll(line).toList()

            if (matches.isEmpty()) {
                emptyList()
            } else {

                val text = line.replace(timestampRegex, "").trim()

                matches.map { match ->

                    val minutes = match.groupValues[1].toLong()
                    val seconds = match.groupValues[2].toLong()

                    val milliseconds = match.groupValues[3]
                        .padEnd(3, '0')
                        .take(3)
                        .toLongOrNull() ?: 0L

                    LyricLine(
                        timestampMs = minutes * 60_000 +
                                seconds * 1_000 +
                                milliseconds,
                        text = text
                    )
                }
            }

        }.sortedBy { it.timestampMs }
    }
}