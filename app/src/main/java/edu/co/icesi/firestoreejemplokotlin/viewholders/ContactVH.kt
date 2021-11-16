package edu.co.icesi.firestoreejemplokotlin.viewholders

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import edu.co.icesi.firestoreejemplokotlin.R
import edu.co.icesi.firestoreejemplokotlin.activites.ChatActivity
import edu.co.icesi.firestoreejemplokotlin.models.User

class ContactVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    //State
    var contact:User? = null

    //UI
    val contactImg: ImageView = itemView.findViewById(R.id.contactImg)
    val contactNameTV: TextView = itemView.findViewById(R.id.contactNameTV)
    val contactEmailTV:TextView= itemView.findViewById(R.id.contactEmailTV)
    val actionBtn: Button= itemView.findViewById(R.id.actionBtn)

    init {
        actionBtn.setOnClickListener{ openChat() }
    }


    fun bindUser(user:User){
        this.contact = user
        contactNameTV.text = user.name
        contactEmailTV.text = user.email

        //Download image
        if(user.photoID != null){
            Firebase.storage.reference.child("profile").child(user.photoID!!).downloadUrl.addOnSuccessListener {
                Glide.with(contactImg).load(it).into(contactImg)
            }
        }
    }


    fun openChat(){
        val contact = this.contact
        val user = loadUser()

        val intent = Intent(actionBtn.context, ChatActivity::class.java).apply {
            putExtra("contact", contact)
            putExtra("user", user)
        }
        actionBtn.context.startActivity(intent)
    }

    fun loadUser():User?{
        val sp = actionBtn.context.getSharedPreferences("appmoviles", AppCompatActivity.MODE_PRIVATE)
        val json = sp.getString("user", "NO_USER")
        if(json == "NO_USER"){
            return null
        }else{
            return Gson().fromJson(json, User::class.java)
        }
    }

}