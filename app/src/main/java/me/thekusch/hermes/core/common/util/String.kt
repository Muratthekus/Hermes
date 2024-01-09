package me.thekusch.hermes.core.common.util

fun String.shortenString(): String {
    val formatted = this.removeSurrounding("\"")
    val parts = formatted.split(" ")
    return if (parts.size == 2) {
        val firstName = parts[0]
        val lastName = parts[1]
        "${firstName.first()}${lastName.first()}"
    } else {
        this
    }

}