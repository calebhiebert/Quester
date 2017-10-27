package com.piikl.quester.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.piikl.quester.R
import com.piikl.quester.activity.MainActivity
import com.piikl.quester.api.Campaign

class CampaignListAdapter : RecyclerView.Adapter<CampaignListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var titleView: TextView = itemView.findViewById(R.id.txtCampaignListItemTitle)
        var creatorView: TextView = itemView.findViewById(R.id.txtCampaignListItemCreator)

        fun bind(item: Campaign) {
            titleView.text = item.name
            creatorView.text = "Created by ${item.creator?.name}"

            itemView.setOnClickListener({
                (itemView.context as MainActivity).onCampaignSelected(item)
            })
        }
    }

    var data: List<Campaign>? = null
    set(value) { field = value; notifyDataSetChanged() }

    override fun getItemCount(): Int {
        if(data != null)
            return data!!.count()
        else
            return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val campaign = data!![position]
        holder.bind(campaign)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.view_campaign_list_item, parent, false)
        return ViewHolder(v)
    }
}