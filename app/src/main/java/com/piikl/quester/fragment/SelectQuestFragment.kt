package com.piikl.quester.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.piikl.quester.R
import com.piikl.quester.adapter.QuestChecklistAdapter
import com.piikl.quester.api.Quest


/**
 * A simple [Fragment] subclass.
 * Use the [SelectQuestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectQuestFragment : DialogFragment() {

    private lateinit var questListView: RecyclerView
    private lateinit var questListAdapter: QuestChecklistAdapter
    private lateinit var sListener: QuestSelectionListener

    private lateinit var selectedQuests: MutableList<Quest>
    private lateinit var quests: List<Quest>

    interface QuestSelectionListener {
        fun selectionUpdated(selected: MutableList<Quest>)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        sListener = try {
            context as QuestSelectionListener
        } catch (e: ClassCastException) {
            throw ClassCastException("${context.toString()} must implement QuestSelectionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            quests = arguments.getParcelableArrayList("quests")
            selectedQuests = arguments.getParcelableArrayList("selectedQuests")

            questListAdapter = QuestChecklistAdapter({ sListener.selectionUpdated(it) }, selectedQuests)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quest_list, container, false)
        questListView = view.findViewById(R.id.recQuestList)
        questListView.layoutManager = LinearLayoutManager(context)
        questListView.adapter = questListAdapter

        questListAdapter.data = quests

        return view
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SelectQuestFragment.
         */
        fun newInstance(quests: List<Quest>, selected: List<Quest>): SelectQuestFragment {
            val fragment = SelectQuestFragment()
            val args = Bundle()

            args.putParcelableArrayList("selectedQuests", selected as ArrayList<Quest>)
            args.putParcelableArrayList("quests", quests as ArrayList<Quest>)
            fragment.arguments = args
            return fragment
        }
    }
}
