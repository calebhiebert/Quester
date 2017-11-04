package com.piikl.quester.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.piikl.quester.R
import com.piikl.quester.api.Quest


class QuestChecklistAdapter(private val selectionUpdated: (MutableList<Quest>) -> Unit, private val selectedQuests: MutableList<Quest>) : RecyclerView.Adapter<QuestChecklistAdapter.ViewHolder>() {

    class ViewHolder(itemView: View, private val selectionUpdated: (MutableList<Quest>) -> Unit, private val selectedQuests: MutableList<Quest>) : RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.findViewById(R.id.txtQuestName)
        private val check: CheckBox = itemView.findViewById(R.id.chkQuestCheckbox)

        fun bind(quest: Quest) {
            nameView.text = quest.name
            check.isChecked = selectedQuests.contains(quest)

            check.setOnClickListener {
                if(check.isChecked)
                    selectedQuests.add(quest)
                else
                    selectedQuests.remove(quest)

               selectionUpdated(selectedQuests)
            }
        }
    }

    var data: List<Quest>? = null
        set(value) { field = value; notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.view_quest_check_list, parent, false)
        return ViewHolder(v, selectionUpdated, selectedQuests)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(data != null) {
            val quest: Quest = data!![position]
            holder.bind(quest)
        }
    }

    override fun getItemCount(): Int {
        return if(data != null)
            data!!.size
        else
            0
    }
}