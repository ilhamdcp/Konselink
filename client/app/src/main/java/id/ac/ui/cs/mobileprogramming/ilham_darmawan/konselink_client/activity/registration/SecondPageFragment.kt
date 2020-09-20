package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.FormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.FragmentRegistrationSecondPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.TextFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModelFactory
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.RegistrationService

class SecondPageFragment : Fragment() {
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
        return DataBindingUtil.inflate<FragmentRegistrationSecondPageBinding>(
            inflater,
            R.layout.fragment_registration_second_page,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = registrationViewModel    // Attach your view model here
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViewModel()
        assignElements()
    }

    private fun initializeViewModel() {
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            Log.d("observeable", it.toString())
            it?.apply {
                registrationViewModel.mutableLiveData.value = it
            }
        }
    }

    fun assignElements() {
        val previousButton = view?.findViewById<ImageView>(R.id.button_fragment_second_previous)
        previousButton?.setOnClickListener {
            registrationViewModel.saveCurrentRegistration()
            val firstPageFragment = FirstPageFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, firstPageFragment).addToBackStack(null)
                .commit()
        }

        val nextButton = view?.findViewById<ImageView>(R.id.button_fragment_second_next)
        nextButton?.setOnClickListener {
            if (registrationViewModel.secondPageIsValid()) {
                registrationViewModel.saveCurrentRegistration()
                val thirdPageFragment = ThirdPageFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, thirdPageFragment)
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Mohon isi data secara lengkap", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val kindergartenAddressForm = view?.findViewById<EditText>(R.id.form_kindergarten_address)
        kindergartenAddressForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.KINDERGARTEN_ADDRESS.typeName
            )
        )

        val elementaryAddressForm = view?.findViewById<EditText>(R.id.form_elementary_address)
        elementaryAddressForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.ELEMENTARY_ADDRESS.typeName
            )
        )

        val juniorAddressForm = view?.findViewById<EditText>(R.id.form_junior_address)
        juniorAddressForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.JUNIOR_ADDRESS.typeName
            )
        )

        val seniorAddressForm = view?.findViewById<EditText>(R.id.form_senior_address)
        seniorAddressForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SENIOR_ADDRESS.typeName
            )
        )

        val collegeAddressForm = view?.findViewById<EditText>(R.id.form_college_address)
        collegeAddressForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.COLLEGE_ADDRESS.typeName
            )
        )
    }
}