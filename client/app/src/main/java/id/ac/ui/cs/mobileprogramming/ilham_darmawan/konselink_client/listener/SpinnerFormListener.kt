package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener

import android.util.Log
import android.view.View
import android.widget.AdapterView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.Education
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.FormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.Religion
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModel

class SpinnerFormListener(val registrationViewModel: RegistrationViewModel, val formType: String) :
    AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
        when(formType) {
            FormType.RELIGION.typeName -> {
                registrationViewModel.handleReligionSpinner(null, -1)
            }

            FormType.CURRENT_EDUCATION.typeName -> {
                registrationViewModel.handleCurrentEducationSpinner(null, -1)
            }

            FormType.FATHER_RELIGION.typeName -> {
                registrationViewModel.handleFatherReligionSpinner(null, -1)
            }

            FormType.FATHER_EDUCATION.typeName -> {
                registrationViewModel.handleFatherEducationSpinner(null, -1)
            }

            FormType.MOTHER_RELIGION.typeName -> {
                registrationViewModel.handleMotherReligionSpinner(null, -1)
            }

            FormType.MOTHER_EDUCATION.typeName -> {
                registrationViewModel.handleMotherEducationSpinner(null, -1)
            }


            FormType.SIBLING1_EDUCATION.typeName -> {
                registrationViewModel.handleSibling1EducationSpinner(null, -1)
            }

            FormType.SIBLING2_EDUCATION.typeName -> {
                registrationViewModel.handleSibling2EducationSpinner(null, -1)
            }

            FormType.SIBLING3_EDUCATION.typeName -> {
                registrationViewModel.handleSibling3EducationSpinner(null, -1)
            }

            FormType.SIBLING4_EDUCATION.typeName -> {
                registrationViewModel.handleSibling4EducationSpinner(null, -1)
            }

            FormType.SIBLING5_EDUCATION.typeName -> {
                registrationViewModel.handleSibling5EducationSpinner(null, -1)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(formType) {
            FormType.RELIGION.typeName -> {
                registrationViewModel.handleReligionSpinner((parent?.selectedItem as Religion).religionName, position)
            }

            FormType.CURRENT_EDUCATION.typeName -> {
                registrationViewModel.handleCurrentEducationSpinner((parent?.selectedItem as Education).educationType, position)
            }

            FormType.FATHER_RELIGION.typeName -> {
                registrationViewModel.handleFatherReligionSpinner((parent?.selectedItem as Religion).religionName, position)
            }

            FormType.FATHER_EDUCATION.typeName -> {
                registrationViewModel.handleFatherEducationSpinner((parent?.selectedItem as Education).educationType, position)
            }

            FormType.MOTHER_RELIGION.typeName -> {
                registrationViewModel.handleMotherReligionSpinner((parent?.selectedItem as Religion).religionName, position)
            }

            FormType.MOTHER_EDUCATION.typeName -> {
                registrationViewModel.handleMotherEducationSpinner((parent?.selectedItem as Education).educationType, position)
            }

            FormType.SIBLING1_EDUCATION.typeName -> {
                registrationViewModel.handleSibling1EducationSpinner((parent?.selectedItem as Education).educationType, position)
            }

            FormType.SIBLING2_EDUCATION.typeName -> {
                registrationViewModel.handleSibling2EducationSpinner((parent?.selectedItem as Education).educationType, position)
            }

            FormType.SIBLING3_EDUCATION.typeName -> {
                registrationViewModel.handleSibling3EducationSpinner((parent?.selectedItem as Education).educationType, position)
            }

            FormType.SIBLING4_EDUCATION.typeName -> {
                registrationViewModel.handleSibling4EducationSpinner((parent?.selectedItem as Education).educationType, position)
            }

            FormType.SIBLING5_EDUCATION.typeName -> {
                registrationViewModel.handleSibling5EducationSpinner((parent?.selectedItem as Education).educationType, position)
            }
        }
    }
}