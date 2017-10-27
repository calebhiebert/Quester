package com.piikl.quester

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.piikl.quester.api.Quest

class QuestListAdapter : RecyclerView.Adapter<QuestListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.txtQuestListItemName)
        var description: TextView = itemView.findViewById(R.id.txtQuestListItemDescription)
        var icon: ImageView = itemView.findViewById(R.id.imgQuestListItemStatusIcon)

        fun bind(quest: Quest) {
            name.text = quest.name
            description.text = quest.details.substring(0, 150)
            icon.setImageResource(R.drawable.ic_flag)
        }
    }

    var questList: List<Quest>? = null

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return if(questList == null) 0
        else questList!!.count()
    }
}