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
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.Religion
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.FragmentRegistrationThirdPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.SpinnerFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.TextFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModelFactory
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.RegistrationService

class ThirdPageFragment : Fragment() {
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
        return DataBindingUtil.inflate<FragmentRegistrationThirdPageBinding>(
            inflater,
            R.layout.fragment_registration_third_page,
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
        val previousButton = view?.findViewById<ImageView>(R.id.button_fragment_third_previous)
        previousButton?.setOnClickListener {
            registrationViewModel.saveCurrentRegistration()
            val secondPageFragment = SecondPageFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, secondPageFragment).addToBackStack(null)
                .commit()
        }

        val nextButton = view?.findViewById<ImageView>(R.id.button_fragment_third_next)
        nextButton?.setOnClickListener {
            if (registrationViewModel.thirdPageIsValid()) {
                registrationViewModel.saveCurrentRegistration()
                val fourthPageFragment = FourthPageFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, fourthPageFragment)
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

        val fatherNameForm = view?.findViewById<EditText>(R.id.form_father_name)
        fatherNameForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.FATHER_NAME.typeName
            )
        )

        val fatherAgeForm = view?.findViewById<EditText>(R.id.form_father_age)
        fatherAgeForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.FATHER_AGE.typeName
            )
        )

        val fatherTribeForm = view?.findViewById<EditText>(R.id.form_father_tribe)
        fatherTribeForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.FATHER_TRIBE.typeName
            )
        )

        val fatherOccupationForm = view?.findViewById<EditText>(R.id.form_father_occupation)
        fatherOccupationForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.FATHER_OCCUPATION.typeName
            )
        )

        val fatherAddressForm = view?.findViewById<EditText>(R.id.form_father_address)
        fatherAddressForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.FATHER_ADDRESS.typeName
            )
        )

        val fatherReligionSpinner = view?.findViewById<Spinner>(R.id.spinner_religion_father)
        fatherReligionSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.FATHER_RELIGION.typeName)

        val fatherEducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_father)
        fatherEducationSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.FATHER_EDUCATION.typeName)


        val motherNameForm = view?.findViewById<EditText>(R.id.form_mother_name)
        motherNameForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.MOTHER_NAME.typeName
            )
        )

        val motherAgeForm = view?.findViewById<EditText>(R.id.form_mother_age)
        motherAgeForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.MOTHER_AGE.typeName
            )
        )

        val motherTribeForm = view?.findViewById<EditText>(R.id.form_mother_tribe)
        motherTribeForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.MOTHER_TRIBE.typeName
            )
        )

        val motherOccupationForm = view?.findViewById<EditText>(R.id.form_mother_occupation)
        motherOccupationForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.MOTHER_OCCUPATION.typeName
            )
        )

        val motherAddressForm = view?.findViewById<EditText>(R.id.form_mother_address)
        motherAddressForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.MOTHER_ADDRESS.typeName
            )
        )

        val motherReligionSpinner = view?.findViewById<Spinner>(R.id.spinner_religion_mother)
        motherReligionSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.MOTHER_RELIGION.typeName)

        val motherEducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_mother)
        motherEducationSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.MOTHER_EDUCATION.typeName)
    }

    fun populateSpinner() {
        // spinner is a special case for data binding where it requires much effort to implement
        // especially to retain previous selected value

        val fatherReligionSpinner = view?.findViewById<Spinner>(R.id.spinner_religion_father)
        val fatherReligionSpinnerAdapter = ArrayAdapter<Religion>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Religion.values().filter { it.religionName != Religion.OTHER.religionName }
        )
        fatherReligionSpinner?.adapter = fatherReligionSpinnerAdapter
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            if (it.fatherReligionSpinnerId != null) fatherReligionSpinner?.setSelection(it.fatherReligionSpinnerId!!)
        }

        val fatherEducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_father)
        val fatherEducationSpinnerAdapter = ArrayAdapter<Education>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Education.values()
        )
        fatherEducationSpinner?.adapter = fatherEducationSpinnerAdapter
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            if (it.fatherEducationSpinnerId != null) fatherEducationSpinner?.setSelection(it.fatherEducationSpinnerId!!)
        }

        val motherReligionSpinner = view?.findViewById<Spinner>(R.id.spinner_religion_mother)
        val motherReligionSpinnerAdapter = ArrayAdapter<Religion>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Religion.values().filter { it.religionName != Religion.OTHER.religionName }
        )
        motherReligionSpinner?.adapter = motherReligionSpinnerAdapter
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            if (it.motherReligionSpinnerId != null) motherReligionSpinner?.setSelection(it.motherReligionSpinnerId!!)
        }

        val motherEducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education_mother)
        val motherEducationSpinnerAdapter = ArrayAdapter<Education>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Education.values()
        )
        motherEducationSpinner?.adapter = motherEducationSpinnerAdapter
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            if (it.motherEducationSpinnerId != null) motherEducationSpinner?.setSelection(it.motherEducationSpinnerId!!)
        }
    }
}