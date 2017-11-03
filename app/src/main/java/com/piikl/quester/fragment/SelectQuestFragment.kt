package com.piikl.quester.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.piikl.quester.R


/**
 * A simple [Fragment] subclass.
 * Use the [SelectQuestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectQuestFragment : Fragment() {

    private var campaignId: Long = 0

    private lateinit var questListView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            campaignId = arguments.getLong("campaign_id")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quest_list, container, false)
        questListView = view.findViewById(R.id.recQuestList)
        return view
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SelectQuestFragment.
         */
        fun newInstance(campaignId: Long): SelectQuestFragment {
            val fragment = SelectQuestFragment()
            val args = Bundle()
            args.putLong("campaign_id", campaignId)
            fragment.arguments = args
            return fragment
        }
    }

}
