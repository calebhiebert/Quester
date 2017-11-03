package com.piikl.quester.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import com.piikl.quester.R
import com.piikl.quester.activity.MainActivity
import com.piikl.quester.api.ErrorHandler
import com.piikl.quester.api.SearchUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInviteAdapter(private val ctx: Context, val campaignId: Long) : RecyclerView.Adapter<UserInviteAdapter.ViewHolder>() {

    class ViewHolder(itemView: View, val ctx: Context, val campaignId: Long) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.txtNameView)
        val loader: ProgressBar = itemView.findViewById(R.id.ldgLoader)
        val check: CheckBox = itemView.findViewById(R.id.chkInviteCheckbox)

        fun bind(user: SearchUser) {
            nameView.text = user.name
            loader.visibility = View.INVISIBLE
            check.isChecked = user.isPartOfCampaign!!
            check.isEnabled = !user.isOwnerOfCampaign!!

            check.setOnClickListener {
                loader.visibility = View.VISIBLE
                check.visibility = View.INVISIBLE

                if(check.isChecked) {
                    MainActivity.questerService!!.inviteUser(campaignId, user.id).enqueue(object : Callback<SearchUser> {
                        override fun onFailure(call: Call<SearchUser>?, t: Throwable) {
                            ErrorHandler.handleErrors(ctx, t)
                        }

                        override fun onResponse(call: Call<SearchUser>?, response: Response<SearchUser>) {
                            when (response.code()) {
                                200 -> {
                                    check.isChecked = response.body()!!.isPartOfCampaign!!
                                    check.visibility = View.VISIBLE
                                    loader.visibility = View.INVISIBLE
                                }

                                else -> ErrorHandler.handleErrors(ctx, response.errorBody()!!)
                            }
                        }
                    })
                } else {
                    MainActivity.questerService!!.uninviteUser(campaignId, user.id).enqueue(object : Callback<SearchUser> {
                        override fun onFailure(call: Call<SearchUser>?, t: Throwable) {
                            ErrorHandler.handleErrors(ctx, t)
                        }

                        override fun onResponse(call: Call<SearchUser>?, response: Response<SearchUser>) {
                            when (response.code()) {
                                200 -> {
                                    check.isChecked = response.body()!!.isPartOfCampaign!!
                                    check.visibility = View.VISIBLE
                                    loader.visibility = View.INVISIBLE
                                }

                                else -> ErrorHandler.handleErrors(ctx, response.errorBody()!!)
                            }
                        }
                    })
                }
            }
        }
    }

    var data: List<SearchUser>? = null
        set(value) { field = value; notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.view_user_invite_list_item, parent, false)
        return ViewHolder(v, ctx, campaignId)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = data!![position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        if(data != null)
            return data!!.count()
        else
            return 0
    }
}