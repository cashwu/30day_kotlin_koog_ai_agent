package com.cashwu

/**
 *
 * @author cash.wu
 * @since 2025/08/21
 *
 */
class SemanticChunker {
    fun chunkByParagraphs(text: String): List<TextChunk> {
        return text.split("\n\n")
            .filter { it.trim().isNotEmpty() }
            .mapIndexed { index, paragraph ->
                TextChunk(
                    id = "paragraph_$index",
                    content = paragraph.trim(),
                    startIndex = index,
                    endIndex = index + 1
                )
            }
    }

    fun chunkByHeaders(markdownText: String): List<TextChunk> {
        val sections = mutableListOf<TextChunk>()
        val lines = markdownText.lines()

        var currentSection = StringBuilder()
        var currentTitle = "intro"
        var sectionId = 0

        for (line in lines) {
            if (line.startsWith("#")) {
                // 儲存前一個區段
                if (currentSection.isNotEmpty()) {
                    sections.add(
                        TextChunk(
                            id = "section_${sectionId++}",
                            content = currentSection.toString().trim(),
                            startIndex = sectionId,
                            endIndex = sectionId + 1
                        )
                    )
                }

                // 開始新區段
                currentTitle = line.removePrefix("#").trim()
                currentSection = StringBuilder(line + "\n")
            } else {
                currentSection.append(line + "\n")
            }
        }

        // 加入最後一個區段
        if (currentSection.isNotEmpty()) {
            sections.add(
                TextChunk(
                    id = "section_${sectionId++}",
                    content = currentSection.toString().trim(),
                    startIndex = sectionId,
                    endIndex = sectionId + 1
                )
            )
        }

        return sections
    }
}