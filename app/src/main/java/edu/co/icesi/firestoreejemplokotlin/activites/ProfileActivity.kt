package edu.co.icesi.firestoreejemplokotlin.activites

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.co.icesi.firestoreejemplokotlin.R
import edu.co.icesi.firestoreejemplokotlin.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding:ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.changepassBtn.setOnClickListener(::changePassword);
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
}