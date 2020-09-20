package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.EditProfileActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.LoginActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.FragmentMainProfilePageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.UserService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ProfileViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ProfileViewModelFactory

class ProfileFragment : Fragment() {
    val profileViewModel by lazy {
        ViewModelProvider(
            this, ProfileViewModelFactory(
                requireContext(),
                UserService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        ).get(ProfileViewModel::class.java)
    }
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil
            .inflate<FragmentMainProfilePageBinding>(
                inflater, R.layout.fragment_main_profile_page,
                container, false
            ).apply {
                lifecycleOwner = viewLifecycleOwner
                vm = profileViewModel
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assignElements()
    }

    override fun onResume() {
        super.onResume()
        initializeViewModel()
    }

    private fun initializeViewModel() {
        sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)!!
        profileViewModel.getUserData(sharedPref.getString(TOKEN, "")!!)
    }

    private fun assignElements() {
        val editProfileButton = view?.findViewById<TextView>(R.id.edit_profile)
        editProfileButton?.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }



        val logoutButton = view?.findViewById<Button>(R.id.button_logout)
        logoutButton?.setOnClickListener {
            with(sharedPref.edit()) {
                remove("cookie")
                remove(TOKEN)
                remove(USER_ID)
                remove(USER_ROLE)
                commit()
            }

            val loginIntent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(loginIntent)
            activity?.finish()
        }
    }
}
