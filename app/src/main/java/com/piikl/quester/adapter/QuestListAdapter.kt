package com.piikl.quester.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.piikl.quester.R
import com.piikl.quester.activity.CampaignView
import com.piikl.quester.api.Quest

class QuestListAdapter : RecyclerView.Adapter<QuestListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.txtQuestListItemName)
        var description: TextView = itemView.findViewById(R.id.txtQuestListItemDescription)
        var icon: ImageView = itemView.findViewById(R.id.imgQuestListItemStatusIcon)

        fun bind(quest: Quest) {
            name.text = quest.name
            description.text = (quest.details?.substring(0, minOf(quest.details!!.length, 150 - 3))) + "..."

            name.isEnabled = !(quest.status == Quest.Status.HIDDEN || quest.status == Quest.Status.LOCKED)

            val iconResourceId = quest.getIconDrawble()

            if(iconResourceId != null) {
                icon.setImageResource(iconResourceId)
            } else {
                icon.visibility = View.INVISIBLE
            }

            itemView.setOnClickListener({
                (itemView.context as CampaignView).onQuestSelected(quest)
            })
        }
    }

    var questList: List<Quest>? = null
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(questList!![position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater
                .from(parent.context)
                .inflate(R.layout.view_quest_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return if(questList == null) 0
        else questList!!.count()
    }
}