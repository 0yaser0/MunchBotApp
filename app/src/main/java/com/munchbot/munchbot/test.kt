package com.munchbot.munchbot

data class User(val name: String,val age:Int , val id: Int)
fun main() {
//sampleStart
    val user = User("Alex", 19, 1)
    println(user.copy(name = "Max",id = 2))
//sampleEnd
}