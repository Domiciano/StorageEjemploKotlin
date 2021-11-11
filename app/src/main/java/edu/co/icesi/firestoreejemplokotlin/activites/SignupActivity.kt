package edu.co.icesi.firestoreejemplokotlin.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.co.icesi.firestoreejemplokotlin.R
import edu.co.icesi.firestoreejemplokotlin.databinding.ActivitySignupBinding
import edu.co.icesi.firestoreejemplokotlin.models.User

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ihaveacountTV.setOnClickListener {
            finish()
        }


        binding.signupBtn.setOnClickListener(::register)


    }

    fun register(view: View) {
        //1. Registrar usuario en la db de auth
        Firebase.auth.createUserWithEmailAndPassword(
            binding.emailTI.editText?.text.toString(),
            binding.passTI.editText?.text.toString()
        ).addOnSuccessListener {
            //2. Registrar todos los datos del user en firestore
            val id = Firebase.auth.currentUser?.uid

            val user = User(id!!,
                binding.nameTI.editText?.text.toString()
                , binding.emailTI.editText?.text.toString())

            Firebase.firestore.collection("users").document(id).set(user).addOnSuccessListener {
                sendVerificationEmail()
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }
    }

    fun sendVerificationEmail(){
        Firebase.auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
            Toast.makeText(this, "Verifique su email antes de entrar", Toast.LENGTH_LONG).show()
        }?.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }
    }
}


