package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.Education
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.FormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.FragmentRegistrationFourthPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.SpinnerFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.TextFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModelFactory
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.RegistrationService

class FourthPageFragment: Fragment() {
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
        return DataBindingUtil.inflate<FragmentRegistrationFourthPageBinding>(
            inflater,
            R.layout.fragment_registration_fourth_page,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = registrationViewModel    // Attach your view model here
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateSpinner()
        assignElements()
        initializeViewModel()
    }

    private fun initializeViewModel() {
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            Log.d("observeable", it.toString())
            it?.apply {
                registrationViewModel.mutableLiveData.value = this
            }
        }
    }

    private fun assignElements() {
        val previousButton = view?.findViewById<ImageView>(R.id.button_fragment_fourth_previous)
        previousButton?.setOnClickListener {
            registrationViewModel.saveCurrentRegistration()
            val thirdPageFragment = ThirdPageFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, thirdPageFragment).addToBackStack(null)
                .commit()
        }

        val nextButton = view?.findViewById<ImageView>(R.id.button_fragment_fourth_next)
        nextButton?.setOnClickListener {
            if (registrationViewModel.fourthPageIsValid()) {
                registrationViewModel.saveCurrentRegistration()
                val fifthPageFragment = FifthPageFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, fifthPageFragment)
                    .commit()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Mohon isi data secara lengkap",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        val sibling1NameForm = view?.findViewById<EditText>(R.id.form_first_sibling_name)
        sibling1NameForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SIBLING1_NAME.typeName
            )
        )

        val sibling1AgeForm = view?.findViewById<EditText>(R.id.form_first_sibling_age)
        sibling1AgeForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SIBLING1_AGE.typeName
            )
        )

        val sibling1EducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_first_sibling)
        sibling1EducationSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.SIBLING1_EDUCATION.typeName)

        val sibling2NameForm = view?.findViewById<EditText>(R.id.form_second_sibling_name)
        sibling2NameForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SIBLING2_NAME.typeName
            )
        )

        val sibling2AgeForm = view?.findViewById<EditText>(R.id.form_second_sibling_age)
        sibling2AgeForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SIBLING2_AGE.typeName
            )
        )

        val sibling2EducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_second_sibling)
        sibling2EducationSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.SIBLING2_EDUCATION.typeName)

        val sibling3NameForm = view?.findViewById<EditText>(R.id.form_third_sibling_name)
        sibling3NameForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SIBLING3_NAME.typeName
            )
        )

        val sibling3AgeForm = view?.findViewById<EditText>(R.id.form_third_sibling_age)
        sibling3AgeForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SIBLING3_AGE.typeName
            )
        )

        val sibling3EducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_third_sibling)
        sibling3EducationSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.SIBLING3_EDUCATION.typeName)

        val sibling4NameForm = view?.findViewById<EditText>(R.id.form_fourth_sibling_name)
        sibling4NameForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SIBLING4_NAME.typeName
            )
        )

        val sibling4AgeForm = view?.findViewById<EditText>(R.id.form_fourth_sibling_age)
        sibling4AgeForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SIBLING4_AGE.typeName
            )
        )

        val sibling4EducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_fourth_sibling)
        sibling4EducationSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.SIBLING4_EDUCATION.typeName)

        val sibling5NameForm = view?.findViewById<EditText>(R.id.form_fifth_sibling_name)
        sibling5NameForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SIBLING5_NAME.typeName
            )
        )

        val sibling5AgeForm = view?.findViewById<EditText>(R.id.form_fifth_sibling_age)
        sibling5AgeForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SIBLING5_AGE.typeName
            )
        )

        val sibling5EducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_fifth_sibling)
        sibling5EducationSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.SIBLING5_EDUCATION.typeName)
    }

    private fun populateSpinner() {
        // spinner is a special case for data binding where it requires much effort to implement
        // especially to retain previous selected value

        val sibling1EducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_first_sibling)
        val sibling1EducationSpinnerAdapter = ArrayAdapter<Education>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Education.values()
        )
        sibling1EducationSpinner?.adapter = sibling1EducationSpinnerAdapter
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            if (it.sibling1EducationSpinnerId != null) sibling1EducationSpinner?.setSelection(it.sibling1EducationSpinnerId!!)
        }

        val sibling2EducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_second_sibling)
        val sibling2EducationSpinnerAdapter = ArrayAdapter<Education>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Education.values()
        )
        sibling2EducationSpinner?.adapter = sibling2EducationSpinnerAdapter
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            if (it.sibling2EducationSpinnerId != null) sibling2EducationSpinner?.setSelection(it.sibling2EducationSpinnerId!!)
        }

        val sibling3EducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_third_sibling)
        val sibling3EducationSpinnerAdapter = ArrayAdapter<Education>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Education.values()
        )
        sibling3EducationSpinner?.adapter = sibling3EducationSpinnerAdapter
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            if (it.sibling3EducationSpinnerId != null) sibling3EducationSpinner?.setSelection(it.sibling3EducationSpinnerId!!)
        }

        val sibling4EducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_fourth_sibling)
        val sibling4EducationSpinnerAdapter = ArrayAdapter<Education>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Education.values()
        )
        sibling4EducationSpinner?.adapter = sibling4EducationSpinnerAdapter
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            if (it.sibling4EducationSpinnerId != null) sibling4EducationSpinner?.setSelection(it.sibling4EducationSpinnerId!!)
        }

        val sibling5EducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_fifth_sibling)
        val sibling5EducationSpinnerAdapter = ArrayAdapter<Education>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Education.values()
        )
        sibling5EducationSpinner?.adapter = sibling5EducationSpinnerAdapter
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            if (it.sibling5EducationSpinnerId != null) sibling5EducationSpinner?.setSelection(it.sibling5EducationSpinnerId!!)
        }
    }
}