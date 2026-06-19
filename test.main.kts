fun String.stripMarkdown(): String {
    val lines = this.lines()
    val result = StringBuilder()
    var inCodeBlock = false

    for (line in lines) {
        when {
            line.startsWith("```") -> inCodeBlock = !inCodeBlock
            inCodeBlock -> result.appendLine(line) // keep code content as plain text
            line.startsWith("#") -> result.appendLine(line.dropWhile { it == '#' }.trim())
            line.trim().startsWith(">") -> result.appendLine(line.dropWhile { it == '>' }.trim())
            line.matches(Regex("^([\\-*]\\s*)?\\[[ xX]]( .*)?")) -> {
                val isChecked = line.contains(Regex("\\[[Xx]]"))
                val text = line.replace(Regex("^([\\-*]\\s*)?\\[[ xX]] ?"), "").trim()
                result.appendLine((if (isChecked) "[x] " else "[ ] ") + text)
            }
            line.trim().startsWith("- ") || line.trim().startsWith("+ ") || line.trim().startsWith("* ") ->
                result.appendLine(
                    "• " + line.trim().drop(2),
                )
            line.matches(Regex("^\\d+\\. .*")) -> result.appendLine(line.substringAfter(". "))
            line.trim() == "---" -> {} // skip horizontal rules
            line.trim().startsWith("!(") -> {} // skip images
            else ->
                result.appendLine(
                    line
                        .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
                        .replace(Regex("\\*(.+?)\\*"), "$1")
                        .replace(Regex("~~(.+?)~~"), "$1")
                        .replace(Regex("`(.+?)`"), "$1")
                        .replace(Regex("\\[(.+?)\\]\\(.*?\\)"), "$1"),
                )
        }
    }

    return result.toString().trim()
}

println("#hi".stripMarkdown())
println("**bold**".stripMarkdown())
println("Some text\n# heading\n**bold**".stripMarkdown())
