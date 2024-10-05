package classes

class Bookmark (
    private val href: String,
    private val text: String,
    private val addDate: String ?= null,
    private val lastModified: String ?=null,
    private val icon: String ?=null,
) {
    fun getHref(): String {
        return href
    }

    fun toHTMLString(): String {
        return """
            <DT><A HREF="$href" ADD_DATE="$addDate" LAST_MODIFIED="$lastModified" ICON="$icon">${text}</A>
        """.trimIndent()
    }
}