package com.my.anthonymamode.go4lunch.utils

fun String.addChar(ch: Char, position: Int): String {
    val sb = StringBuilder(this)
    sb.insert(position, ch)
    return sb.toString()
}
