package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.FragmentRegistrationIpipPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModel
import kotlinx.android.synthetic.main.fragment_registration_ipip_page.*

class IpipFragment(private val registrationViewModel: RegistrationViewModel) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentRegistrationIpipPageBinding>(
            inflater,
            R.layout.fragment_registration_ipip_page,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = registrationViewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assignElements()
    }

    private fun assignElements() {
        button_fragment_ipip_next.setOnClickListener {
            if (registrationViewModel.ipipPageIsValid()) {
                val srqFragment = SrqFragment(registrationViewModel)
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, srqFragment)
                    .commit()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Mohon isi data secara lengkap",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        button_fragment_ipip_previous.setOnClickListener {
            val fifthPageFragment = FifthPageFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, fifthPageFragment)
                .commit()
        }
    }
}