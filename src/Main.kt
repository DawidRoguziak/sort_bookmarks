import classes.Bookmark
import classes.Folder
import enums.LinkAttributes
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.URI

fun getDomainWithoutTLD(link: String): String {
    val uri = URI(link)
    val domain = uri.host ?: return "unsorted"

    // Split the domain by "." and remove the last part (TLD)
    val parts = domain.split(".")

    // Check if there are at least 2 parts (e.g., "example" and "com")
    return if (parts.size > 1) parts[parts.size - 2] else domain
}

fun parseElementsToBookmarks(links: Elements): ArrayList<Bookmark> {
    val unSortedLinks = ArrayList<Bookmark>()

    // fill unSortedLinks
    for (link in links) {
        val addDate = link.attr(LinkAttributes.ADD_DATE.toString());
        val lastModified = link.attr(LinkAttributes.LAST_MODIFIED.toString())
        val icon = link.attr(LinkAttributes.ICON.toString())
        val href = link.attr(LinkAttributes.HREF.toString())
        val text = link.text()

        unSortedLinks.add(
            Bookmark(
                href,
                text,
                icon,
                lastModified,
                addDate
            )
        )
    }

    return unSortedLinks;
}

fun arrangeLinks(unSortedLinks: ArrayList<Bookmark>): Map<String, Folder> {
    val foldersList = mutableMapOf<String, Folder>();
    for (link in unSortedLinks) {
        val domain = getDomainWithoutTLD(link.getHref())

        if (foldersList.contains(domain)) {
            foldersList[domain]?.links?.add(link)
        } else {
            val folder = Folder(
                name = domain,
                links = ArrayList<Bookmark>()
            )

            folder.links.add(link)

            foldersList[domain] = folder
        }
    }

    return foldersList
}

fun foldersToHTMLString(folders: Map<String, Folder>): String {
    val foldersString = buildString {
        for ((key, value) in folders) {
            append(value.toHTMLString())
            append("\n")
        }
    }

    return sortedBookmarksTemplate(foldersString)
}

fun sortedBookmarksTemplate(folders: String): String {
    return """
<!DOCTYPE NETSCAPE-Bookmark-file-1>
<!-- This is an automatically generated file.
     It will be read and overwritten.
     DO NOT EDIT! -->
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<TITLE>Bookmarks</TITLE>
<H1>Bookmarks</H1>
<DL><p>
<DT><H3 PERSONAL_TOOLBAR_FOLDER="true">Bookmarks bar</H3>
<DL><p>
    $folders
</DL><p>
    """.trimIndent()
}

fun main() {
    //---------- For check if path is valid ------------
    val currentDir = System.getProperty("user.dir")
    println("Current Directory: $currentDir")
    //--------------------------------------------------

    try {
        val sourceHtmlFile = File("src/bookmarks.html")
        val document = Jsoup.parse(sourceHtmlFile, "UTF-8")

        val unSortedLinks = parseElementsToBookmarks(document.select("A"))
        val foldersList = arrangeLinks(unSortedLinks)

        val bookmarks: String = foldersToHTMLString(foldersList)

        val writer = FileWriter(File("sorted_bookmarks.html"))
        writer.write(bookmarks)

    } catch (e: IOException) {
        println("An error occurred: ${e.message}")
    }
}