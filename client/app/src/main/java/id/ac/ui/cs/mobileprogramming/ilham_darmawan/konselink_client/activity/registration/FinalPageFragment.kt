package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.SHARED_PREF
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.VerificationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.FragmentRegistrationFinalPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.CheckboxListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModel

class FinalPageFragment(private val registrationViewModel: RegistrationViewModel) : Fragment() {
    private var sharedPref: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        assignElements()
        initializeViewModel()
    }

    private fun initializeViewModel() {
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            it?.apply {
                registrationViewModel.mutableLiveData.value = this
            }
        }

        registrationViewModel.postRegistrationResponseCode.observe(viewLifecycleOwner) {
            it.apply {
                if (it == 200 && registrationViewModel.postIpipAndSrqStatusCode.value == 200) {
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

        registrationViewModel.postIpipAndSrqStatusCode.observe(viewLifecycleOwner) {
            if (it == 200 && registrationViewModel.postRegistrationResponseCode.value == 200) {
                val verificationIntent = Intent(activity?.applicationContext, VerificationActivity::class.java)
                startActivity(verificationIntent)
                activity?.finish()
            } else if (it > 0 && it != 200) {
                val loading = view?.findViewById<ConstraintLayout>(R.id.layout_loading)
                loading?.visibility = View.GONE
                Toast.makeText(activity?.applicationContext, "Gagal mengupload survey IPIP dan SRQ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun assignElements() {
        val previousButton = view?.findViewById<ImageView>(R.id.button_fragment_final_previous)
        previousButton?.setOnClickListener {
            val srqFragment = SrqFragment(registrationViewModel)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, srqFragment).addToBackStack(null)
                .commit()
        }

        val continueButton = view?.findViewById<Button>(R.id.button_continue)
        continueButton?.setOnClickListener {
            val loading = view?.findViewById<ConstraintLayout>(R.id.layout_loading)
            loading?.visibility = View.VISIBLE
            val token = sharedPref?.getString(TOKEN, "")
            if (registrationViewModel.postRegistrationResponseCode.value != 200) {
                registrationViewModel.postRegistrationData(token!!)
            }

            if (registrationViewModel.postIpipAndSrqStatusCode.value != 200) {
                registrationViewModel.postIpipAndSrqData(token!!)
            }
        }


        val agreeCheckbox = view?.findViewById<CheckBox>(R.id.checkbox_continue)
        agreeCheckbox?.setOnCheckedChangeListener(CheckboxListener(registrationViewModel))
    }
}