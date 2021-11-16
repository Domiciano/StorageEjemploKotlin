package edu.co.icesi.firestoreejemplokotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.co.icesi.firestoreejemplokotlin.R
import edu.co.icesi.firestoreejemplokotlin.models.User
import edu.co.icesi.firestoreejemplokotlin.viewholders.ContactVH

class ContactAdapter : RecyclerView.Adapter<ContactVH>(){

    private val users = ArrayList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactVH {
        val inflater = LayoutInflater.from(parent.context)
        //XML -> View
        val view = inflater.inflate(R.layout.contactrow, parent, false)
        val contactVH = ContactVH(view)
        return contactVH
    }

    override fun onBindViewHolder(holder: ContactVH, position: Int) {
        val user = users[position]
        holder.bindUser(user)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun addUser(user: User) {
        users.add(user)
        notifyItemInserted(users.size-1)
    }

    fun clear() {
        val size = users.size
        users.clear()
        notifyItemRangeRemoved(0, size)
    }

}