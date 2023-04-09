package com.sorbonne.daar.models

import java.io.Serializable

class Book : Serializable {
    var id = 0
    var title: String? = null
    var authors: List<String>? = null
    var content: String? = null
    var image: String? = null

    constructor(id: Int, title: String?, authors: List<String>?, content: String?, image: String?) : super() {
        this.id = id
        this.title = title
        this.authors = authors
        this.content = content
        this.image = image
    }

}