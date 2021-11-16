package edu.co.icesi.firestoreejemplokotlin.activites

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import edu.co.icesi.firestoreejemplokotlin.R
import edu.co.icesi.firestoreejemplokotlin.databinding.ActivityProfileBinding
import edu.co.icesi.firestoreejemplokotlin.models.User
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding:ActivityProfileBinding

    private lateinit var galleryLauncher : ActivityResultLauncher<Intent>

    private lateinit var user:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = loadUser()!!
        binding.profileUserTV.text = user.name
        binding.profileEmailTV.text = user.email


        getUpdatedUser()


        galleryLauncher = registerForActivityResult(StartActivityForResult(), ::onGalleryResult)

        binding.changepassBtn.setOnClickListener(::changePassword)

        binding.profileImg.setOnLongClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
            true
        }

    }

    fun getUpdatedUser(){
        Firebase.firestore.collection("users").document(user.id).get().addOnSuccessListener {
            val updatedUser = it.toObject(User::class.java)
            val photoID = updatedUser?.photoID
            downloadImage(photoID)
        }
    }

    fun downloadImage(photoID:String?){

        if(photoID == null) return

        Firebase.storage.getReference().child("profile").child(photoID!!).downloadUrl.addOnSuccessListener {
            Glide.with(binding.profileImg).load(it).into(binding.profileImg)
        }
    }

    fun onGalleryResult(result : ActivityResult){
        if(result.resultCode == RESULT_OK){
            val uri = result.data?.data
            binding.profileImg.setImageURI(uri)


            //Upload
            val filename = UUID.randomUUID().toString()
            Firebase.storage.getReference().child("profile").child(filename).putFile(uri!!)
            Firebase.firestore.collection("users").document(user.id).update("photoID", filename)

        }
    }

    fun changePassword(view: View){
        val newpass = binding.newpassET.editText?.text.toString()

        if(newpass.isEmpty()){
            binding.newpassET.error = "El campo no puede estar vacío"
            return
        }else binding.newpassET.error = null

        if(newpass.length < 6){
            binding.newpassET.error = "La contraseña está muy corta, mínimo 6 caracteres"
            return
        }else binding.newpassET.error = null

        val builder = AlertDialog.Builder(this)
            .setTitle("Cambio de contraseña")
            .setMessage("¿Desea cambiar la contraseña?")
            .setPositiveButton("Si"){ dialog, _ ->
                Firebase.auth.currentUser?.updatePassword(newpass)?.addOnSuccessListener {
                    Toast.makeText(this, "Contraseña cambiada!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }?.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
            .setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
            }
        builder.show()

    }


    fun loadUser():User?{
        val sp = getSharedPreferences("appmoviles", MODE_PRIVATE)
        val json = sp.getString("user", "NO_USER")
        if(json == "NO_USER"){
            return null
        }else{
            return Gson().fromJson(json, User::class.java)
        }
    }

}