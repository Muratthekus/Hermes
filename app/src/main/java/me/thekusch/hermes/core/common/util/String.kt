package me.thekusch.hermes.core.common.util

fun String.shortenString(): String {

    val parts = this.split(" ")
    return if (parts.size == 2) {
        val firstName = parts[0]
        val lastName = parts[1]
        "${firstName.getOrNull(1)}${lastName.first()}"
    } else {
        this
    }

}