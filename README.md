
# MangaUpdates  API.
MangaUpdates is a free and open source Kotlin API to get manga INFO.

This repository contains the API to grape some manga INFO from the [MangaUpdate](https://api.mangaupdates.com/) API.

# Usage

just clone the repository and have fun with it. you can copy some code or just add the class file to your project and don't forget to add the `dependencies` in your `build.gradle.kts`

# Usage in tachiyomi-extensions 
**example :**

        override fun mangaDetailsParse(document: Document): SManga = SManga.create().apply {
        val infoElement = document.select("div.row.mb-5").first()
        title = infoElement.select("div.author-info-title.mb-3 > h1").text().removeSurrounding("(", ")")
        val manga =  = MangaUpDates(title)
        description =manga.getDescription()
        author = manga.getAuthor()
        artist = manga.getArtist()
        status = manga.getStatus()
        genre =manga.getGenres()
    }

- you only need to grape the title of manga and passed as parameter 
- you can also add the type and category to genre by just adding true as parameter in `getStatus(ture)`

# Contributing

Contributions are welcome!


## License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Disclaimer

The developer of this application does not have any affiliation with the content providers available.
