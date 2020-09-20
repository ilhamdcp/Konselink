package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration

import android.os.Bundle
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
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.FragmentRegistrationFirstPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.SpinnerFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.TextFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModelFactory
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.RegistrationService

class FirstPageFragment() : Fragment() {
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
        val binding = DataBindingUtil.inflate<FragmentRegistrationFirstPageBinding>(
            inflater,
            R.layout.fragment_registration_first_page,
            container,
            false
        )
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = registrationViewModel    // Attach your view model here
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateSpinner()
        assignElements()
        initializeViewModel()
    }

    private fun initializeViewModel() {
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            it?.apply {
                registrationViewModel.mutableLiveData.value = it
            }
        }
    }

    fun assignElements() {
        val nextButton = view?.findViewById<ImageView>(R.id.button_fragment_first_next)
        nextButton?.setOnClickListener {
            if (registrationViewModel.firstPageIsValid()) {
                registrationViewModel.saveCurrentRegistration()
                val secondPageFragment = SecondPageFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, secondPageFragment)
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

        val nameForm = view?.findViewById<EditText>(R.id.form_nickname)
        nameForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.NICKNAME.typeName
            )
        )

        val birthPlaceForm = view?.findViewById<EditText>(R.id.form_birth_place)
        birthPlaceForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.BIRTH_PLACE.typeName
            )
        )

        val birthDayForm = view?.findViewById<EditText>(R.id.form_birth_date)
        birthDayForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.DAY.typeName
            )
        )

        val birthMonthForm = view?.findViewById<EditText>(R.id.form_birth_month)
        birthMonthForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.MONTH.typeName
            )
        )

        val birthYearForm = view?.findViewById<EditText>(R.id.form_birth_year)
        birthYearForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.BIRTH_YEAR.typeName
            )
        )

        val addressForm = view?.findViewById<EditText>(R.id.form_address)
        addressForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.ADDRESS.typeName
            )
        )

        val phoneNumberForm = view?.findViewById<EditText>(R.id.form_phone_number)
        phoneNumberForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.PHONE_NUMBER.typeName
            )
        )

        val religionSpinner = view?.findViewById<Spinner>(R.id.spinner_religion)
        religionSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.RELIGION.typeName)

        val currentEducationSpinner = view?.findViewById<Spinner>(R.id.spinner_education)
        currentEducationSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.CURRENT_EDUCATION.typeName)
    }

    fun populateSpinner() {
        // spinner is a special case for data binding where it requires much effort to implement
        // especially to retain previous selected value

        val religionSpinner = view?.findViewById<Spinner>(R.id.spinner_religion)
        val religionSpinnerAdapter = ArrayAdapter<Religion>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Religion.values()
        )
        religionSpinner?.adapter = religionSpinnerAdapter
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            it?.apply {
                if (religionSpinnerId != null) religionSpinner?.setSelection(religionSpinnerId!!)
            }

        }

        val educationSpinner = view?.findViewById<Spinner>(R.id.spinner_education)
        val educationSpinnerAdapter = ArrayAdapter<Education>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Education.values()
        )
        educationSpinner?.adapter = educationSpinnerAdapter
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            it?.apply {
                if (currentEducationSpinnerId != null) educationSpinner?.setSelection(currentEducationSpinnerId!!)
            }

        }
    }
}