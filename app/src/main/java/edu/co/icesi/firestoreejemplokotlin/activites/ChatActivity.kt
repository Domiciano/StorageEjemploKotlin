package edu.co.icesi.firestoreejemplokotlin.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import edu.co.icesi.firestoreejemplokotlin.databinding.ActivityChatBinding
import edu.co.icesi.firestoreejemplokotlin.fcm.FCMMessage
import edu.co.icesi.firestoreejemplokotlin.models.Chat
import edu.co.icesi.firestoreejemplokotlin.models.Message
import edu.co.icesi.firestoreejemplokotlin.models.User
import edu.co.icesi.firestoreejemplokotlin.util.HTTPSWebUtilDomi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var contact: User

    private lateinit var binding: ActivityChatBinding

    private lateinit var chat: Chat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)

        setContentView(binding.root)


        binding.messagesTV.movementMethod = ScrollingMovementMethod()


        user = intent.extras?.get("user") as User
        contact = intent.extras?.get("contact") as User


        Firebase.firestore.collection("users").document(user.id).collection("chats")
            .whereEqualTo("contactID", contact.id).get().addOnCompleteListener { task ->
                if(task.result?.size() == 0){
                    createChat()
                }else{
                    for(document in task.result!!){
                        chat = document.toObject(Chat::class.java)
                        break
                    }
                }
                //Podemos leer los mensajes entre ambos usuarios
                getMessages();
            }

        binding.sendBtn.setOnClickListener {
            val message = Message(UUID.randomUUID().toString(), binding.messageET.text.toString(), user.id, Date().time)
            Firebase.firestore.collection("chats").document(chat.id).collection("messages").document(message.id).set(message)

            //Notificar al contacto
            lifecycleScope.launch(Dispatchers.IO){
                val obj = FCMMessage("/topics/${contact.id}",message)
                val json = Gson().toJson(obj)
                HTTPSWebUtilDomi().POSTtoFCM(json)
            }

        }


    }

    private fun getMessages() {
        Firebase.firestore.collection("chats").document(chat.id).collection("messages").orderBy("date").limitToLast(10).addSnapshotListener { value, error ->

            for(change in value!!.documentChanges){
                when(change.type){
                    DocumentChange.Type.ADDED->{
                        val message = change.document.toObject(Message::class.java)
                        binding.messagesTV.append("${message.message}\n\n")
                    }
                }
            }

        }
    }

    private fun createChat() {
        val id = UUID.randomUUID().toString()
        chat = Chat(id, contact.id)
        val foreingChat = Chat(id, user.id)
        Firebase.firestore.collection("users").document(user.id).collection("chats").document(id)
            .set(chat)
        Firebase.firestore.collection("users").document(contact.id).collection("chats").document(id)
            .set(foreingChat)
    }
}