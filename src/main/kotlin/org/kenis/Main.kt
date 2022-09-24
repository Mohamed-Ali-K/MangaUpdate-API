package org.kenis

fun main(args: Array<String>) {

    val listOfMangaTitle = listOf<String>(
        "Tales of Demons and Gods",
        "妖神记",
        "Hunter x Hunter",
        "bleach",
        "one piece",
        "Solo Leveling"
    )

    listOfMangaTitle.forEach {
        val manga = MangaUpDates(it)
        println("Title : " + manga.getTitle())
        println("Description : " + manga.getDescription())
        println("Type : " + manga.getType())
        println("Years : " + manga.getYears())
        println("Author : " + manga.getAuthor())
        println("Artist : " + manga.getArtist())
        println("Publishers : " + manga.getPublishers())
        println("Alternative Name : " + manga.getAlternativeName())
        println("Status : " + manga.getStatus(false))
        println("Genres : " + manga.getGenres())
        println("Category : " + manga.getCategory())
        println("Last chapter : " + manga.getLastChapter())
        println("\n ########################################################\n")
    }


}
