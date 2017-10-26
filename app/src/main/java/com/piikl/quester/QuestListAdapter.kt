package com.piikl.quester

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.piikl.quester.api.Quest

class QuestListAdapter : RecyclerView.Adapter<QuestListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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