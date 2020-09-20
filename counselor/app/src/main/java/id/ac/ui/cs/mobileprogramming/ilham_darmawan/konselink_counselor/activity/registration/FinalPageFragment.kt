package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.registration

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.ACCESS_NETWORK_STATE_REQUEST
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.VerificationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.FragmentRegistrationFinalPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.CheckboxListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.RegistrationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.RegistrationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.RegistrationViewModelFactory

class FinalPageFragment : Fragment() {
    private var sharedPref: SharedPreferences? = null
    private var userId: Int? = -1

    private val registrationViewModel by lazy {
        ViewModelProvider(
            this,
            RegistrationViewModelFactory(
                requireContext(),
                RegistrationService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        )
            .get(RegistrationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPref = activity?.getSharedPreferences(BuildConfig.SHARED_PREF, Context.MODE_PRIVATE)

        userId = sharedPref?.getInt(BuildConfig.USER_ID, -1)

        return DataBindingUtil.inflate<FragmentRegistrationFinalPageBinding>(
            inflater,
            R.layout.fragment_registration_final_page,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = registrationViewModel    // Attach your view model here
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assignElements()
        initializeViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACCESS_NETWORK_STATE_REQUEST && resultCode == Activity.RESULT_OK) {
            registrationViewModel.submitRegistration(userId!!)
        }
    }

    private fun initializeViewModel() {
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            Log.d("observeable", it.toString())
            it?.apply {
                registrationViewModel.mutableLiveData.value = this
            }
        }

        registrationViewModel.postRegistrationResponseCode.observe(viewLifecycleOwner) {
            it.apply {
                if (it == 200) {
                    val verificationIntent = Intent(activity?.applicationContext, VerificationActivity::class.java)
                    startActivity(verificationIntent)
                    activity?.finish()
                } else if (it > 0 && it != 200){
                    val loading = view?.findViewById<ConstraintLayout>(R.id.layout_loading)
                    loading?.visibility = View.GONE
                    Toast.makeText(activity?.applicationContext, "Gagal melakukan registrasi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun assignElements() {
        val previousButton = view?.findViewById<ImageView>(R.id.button_fragment_final_previous)
        previousButton?.setOnClickListener {
            val secondFragment = SecondPageFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, secondFragment).addToBackStack(null)
                .commit()
        }

        val agreeCheckbox = view?.findViewById<CheckBox>(R.id.checkbox_continue)
        agreeCheckbox?.setOnCheckedChangeListener(CheckboxListener(registrationViewModel))

        val continueButton = view?.findViewById<Button>(R.id.button_continue)
        continueButton?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_NETWORK_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                    BuildConfig.ACCESS_NETWORK_STATE_PERMISSION
                )

            } else {
                val loading = view?.findViewById<ConstraintLayout>(R.id.layout_loading)
                loading?.visibility = View.VISIBLE
                val token = sharedPref?.getString(TOKEN,"")
                registrationViewModel.submitRegistration(userId!!)
                registrationViewModel.postRegistrationData(token!!)
            }

        }
    }
}