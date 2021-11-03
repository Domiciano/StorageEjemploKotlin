package edu.co.icesi.firestoreejemplokotlin.models

import java.io.Serializable

data class User(
    var id:String = "",
    var username:String = "",
    var pass:String = ""
) : Serializable{
    override fun toString(): String {
        return username
    }
}