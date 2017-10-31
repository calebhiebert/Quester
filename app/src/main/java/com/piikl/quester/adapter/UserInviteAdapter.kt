package com.piikl.quester.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import com.piikl.quester.R
import com.piikl.quester.api.User

class UserInviteAdapter : RecyclerView.Adapter<UserInviteAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.txtNameView)
        val loader: ProgressBar = itemView.findViewById(R.id.ldgLoader)
        val check: CheckBox = itemView.findViewById(R.id.checkbox)

        fun bind(user: User) {
            nameView.text = user.name

//            loader.visibility =
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}