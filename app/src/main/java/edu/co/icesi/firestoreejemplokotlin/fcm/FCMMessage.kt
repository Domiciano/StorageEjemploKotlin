package edu.co.icesi.firestoreejemplokotlin.fcm

data class FCMMessage<T:Any>(
    var to: String = "",
    var data : T? = null
)