package edu.co.icesi.firestoreejemplokotlin.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import edu.co.icesi.firestoreejemplokotlin.databinding.ActivityMainBinding
import edu.co.icesi.firestoreejemplokotlin.models.User
import edu.co.icesi.firestoreejemplokotlin.util.NotificationUtil
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NotificationUtil.showNotification(this, "Nuevo","Mensaje")


        Firebase.messaging.subscribeToTopic("promo")


        binding.loginBtn.setOnClickListener {
            val email = binding.usernameET.text.toString()
            val pass = binding.passET.text.toString()

            Firebase.auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener {

                val fbuser = Firebase.auth.currentUser
                if(fbuser!!.isEmailVerified){
                    //Le damos acceso

                    //1. Pedir el usuario almacenado en firestore
                    Firebase.firestore.collection("users").document(fbuser.uid).get().addOnSuccessListener {
                        val user = it.toObject(User::class.java)
                        //2. Salvar al usuario en las SP
                        saveUser(user!!)
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }


                }else{
                    Toast.makeText(this, "Su email no está verificado", Toast.LENGTH_LONG).show()
                }

            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }



        }


        binding.noaccountTV.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.forgotpassTV.setOnClickListener {
            Firebase.auth.sendPasswordResetEmail(binding.usernameET.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this, "Revise su correo "+binding.usernameET.text.toString(), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun saveUser(user: User){
        val sp = getSharedPreferences("appmoviles", MODE_PRIVATE)
        val json = Gson().toJson(user)
        sp.edit().putString("user", json).apply()
    }

}