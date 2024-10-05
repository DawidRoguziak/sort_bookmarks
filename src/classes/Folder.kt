package classes

class Folder(
    val name: String,
    val links: ArrayList<Bookmark> = ArrayList<Bookmark>()
) {

    private fun linksToString(): String {
        val linksAsString = buildString {
            for (link in links) {
                append(link.toHTMLString())
                append("\n")
            }
        }

        return linksAsString;
    }

    fun toHTMLString(): String {
        val linksAsString = linksToString();
        return """
               <DT><H3 ADD_DATE="0" LAST_MODIFIED="0">$name</H3>
               <DL><p>
                    $linksAsString
               </DL><p>
        """.trimIndent()

    }
}
