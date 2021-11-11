package edu.co.icesi.firestoreejemplokotlin.models

import java.io.Serializable

data class User(
    var id:String = "",
    var name:String = "",
    var email:String = "",
) : Serializable{
    override fun toString(): String {
        return name
    }
}