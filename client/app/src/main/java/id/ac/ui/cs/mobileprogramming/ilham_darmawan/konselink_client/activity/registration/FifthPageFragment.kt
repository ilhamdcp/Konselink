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
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.FragmentRegistrationFifthPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.TextFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModelFactory
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.RegistrationService

class FifthPageFragment : Fragment() {
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
        return DataBindingUtil.inflate<FragmentRegistrationFifthPageBinding>(
            inflater,
            R.layout.fragment_registration_fifth_page,
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

    private fun initializeViewModel() {
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            Log.d("observeable", it.toString())
            it?.apply {
                registrationViewModel.mutableLiveData.value = this
            }
        }
    }

    fun assignElements() {
        val previousButton = view?.findViewById<ImageView>(R.id.button_fragment_fifth_previous)
        previousButton?.setOnClickListener {
            registrationViewModel.saveCurrentRegistration()
            val fourthFragment = FourthPageFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fourthFragment).addToBackStack(null)
                .commit()
        }

        val nextButton = view?.findViewById<ImageView>(R.id.button_fragment_fifth_next)
        nextButton?.setOnClickListener {
            if (registrationViewModel.fifthPageIsValid()) {
                registrationViewModel.saveCurrentRegistration()
                val ipipFragment = IpipFragment(registrationViewModel)
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, ipipFragment)
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

        val placeConsultedForm = view?.findViewById<EditText>(R.id.form_place_consulted)
        placeConsultedForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.PLACE_CONSULTED.typeName
            )
        )

        val monthConsultedForm = view?.findViewById<EditText>(R.id.form_consulted_month)
        monthConsultedForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.MONTH_CONSULTED.typeName
            )
        )

        val yearConsultedForm = view?.findViewById<EditText>(R.id.form_consulted_year)
        yearConsultedForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.YEAR_CONSULTED.typeName
            )
        )

        val complaintForm = view?.findViewById<EditText>(R.id.form_complaint)
        complaintForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.COMPLAINT.typeName
            )
        )

        val solutionForm = view?.findViewById<EditText>(R.id.form_solution)
        solutionForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SOLUTION.typeName
            )
        )

        val problemForm = view?.findViewById<EditText>(R.id.form_problem)
        problemForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.PROBLEM.typeName
            )
        )

        val effortDoneForm = view?.findViewById<EditText>(R.id.form_effort_done)
        effortDoneForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.EFFORT_DONE.typeName
            )
        )
    }

}