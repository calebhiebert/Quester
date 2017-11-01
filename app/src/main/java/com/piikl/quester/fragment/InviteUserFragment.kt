package com.piikl.quester.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.piikl.quester.R
import com.piikl.quester.activity.MainActivity
import com.piikl.quester.adapter.UserInviteAdapter
import com.piikl.quester.api.ErrorHandler
import com.piikl.quester.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [InviteUserFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [InviteUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InviteUserFragment : Fragment() {

    private var campaignId: Long = 0

    private lateinit var userSearchBox: EditText
    private lateinit var usersDisplay: RecyclerView

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            campaignId = arguments.getLong("campaign_id")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_invite_user, container, false)

        userSearchBox = view.findViewById(R.id.txtSearchUser)
        usersDisplay = view.findViewById(R.id.recUserDisplay)

        usersDisplay.layoutManager = LinearLayoutManager(context)
        usersDisplay.adapter = UserInviteAdapter(context, campaignId)

        return view
    }

    fun doInvite(campaignId: Long, userId: Long) {
        MainActivity.questerService!!.inviteUser(campaignId, userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>?, response: Response<User>) {
                Snackbar.make(view!!, "Invited user ${response.body()?.name}", Snackbar.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<User>?, t: Throwable) {
                ErrorHandler.handleErrors(context, t)
            }
        })
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment InviteUserFragment.
         */
        fun newInstance(campaignId: Long): InviteUserFragment {
            val fragment = InviteUserFragment()
            val args = Bundle()
            args.putLong("campaign_id", campaignId)
            fragment.arguments = args
            return fragment
        }
    }
}
