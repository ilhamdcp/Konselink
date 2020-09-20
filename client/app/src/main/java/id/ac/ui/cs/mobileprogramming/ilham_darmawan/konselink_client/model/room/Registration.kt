package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registrations")
data class Registration(
    @Transient @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Int = 0,

    var name: String? = null,

    var gender: String? = null,

    var birthPlace: String? = null,

    var birthDay: Int? = null,
    var birthMonth: Int? = null,
    var birthYear: Int? = null,

    var age: Int? = null,

    var religion: String? = null,
    @ColumnInfo(name = "religion_spinner_id") @Transient var religionSpinnerId: Int? = null,

    var address: String? = null,

    var phoneNumber: String? = null,

    var email: String? = null,

    var currentEducation: String? = null,
    @ColumnInfo(name = "education_spinner_id") @Transient var currentEducationSpinnerId: Int? = null,

    var currentProfession: String? = null,

    var hasConsultedBefore: Boolean? = null,

    var monthConsulted: Int? = null,

    var yearConsulted: Int? = null,

    var placeConsulted: String? = null,

    var complaint: String? = null,
    var solution: String? = null,

    var problem: String? = null,

    var effortDone: String? = null,

    var kindergartenData: String? = null,
    var elementaryData: String? = null,
    var juniorData: String? = null,
    var seniorData: String? = null,
    var collegeData: String? = null,

    var fatherName: String? = null,
    var fatherAge: Int? = null,
    var fatherReligion: String? = null,

    @ColumnInfo(name = "father_religion_spinner_id") @Transient var fatherReligionSpinnerId: Int? = null,
    var fatherTribe: String? = null,
    var fatherEducation: String? = null,
    @ColumnInfo(name = "father_education_spinner_id") @Transient var fatherEducationSpinnerId: Int? = null,
    var fatherOccupation: String? = null,
    var fatherAddress: String? = null,

    var motherName: String? = null,
    var motherAge: Int? = null,
    var motherReligion: String? = null,
    @ColumnInfo(name = "mother_religion_spinner_id") @Transient var motherReligionSpinnerId: Int? = null,
    var motherTribe: String? = null,
    var motherEducation: String? = null,
    @ColumnInfo(name = "mother_education_spinner_id") @Transient var motherEducationSpinnerId: Int? = null,
    var motherOccupation: String? = null,
    var motherAddress: String? = null,

    var sibling1Name: String? = null,
    var sibling1Gender: String? = null,
    var sibling1Age: Int? = null,
    var sibling1Education: String? = null,
    @ColumnInfo(name = "sibling1_education_spinner_id") @Transient var sibling1EducationSpinnerId: Int? = null,

    var sibling2Name: String? = null,
    var sibling2Gender: String? = null,
    var sibling2Age: Int? = null,
    var sibling2Education: String? = null,
    @ColumnInfo(name = "sibling2_education_spinner_id") @Transient var sibling2EducationSpinnerId: Int? = null,

    var sibling3Name: String? = null,
    var sibling3Gender: String? = null,
    var sibling3Age: Int? = null,
    var sibling3Education: String? = null,
    @ColumnInfo(name = "sibling3_education_spinner_id") @Transient var sibling3EducationSpinnerId: Int? = null,

    var sibling4Name: String? = null,
    var sibling4Gender: String? = null,
    var sibling4Age: Int? = null,
    var sibling4Education: String? = null,
    @ColumnInfo(name = "sibling4_education_spinner_id") @Transient var sibling4EducationSpinnerId: Int? = null,

    var sibling5Name: String? = null,
    var sibling5Gender: String? = null,
    var sibling5Age: Int? = null,
    var sibling5Education: String? = null,
    @ColumnInfo(name = "sibling5_education_spinner_id") @Transient var sibling5EducationSpinnerId: Int? = null
) {
    override fun toString(): String {
        return "Registration(id=$id, name=$name, gender=$gender, birthPlace=$birthPlace, birthDay=$birthDay, birthMonth=$birthMonth, birthYear=$birthYear, age=$age, religion=$religion, religionSpinnerId=$religionSpinnerId, address=$address, phoneNumber=$phoneNumber, email=$email, currentEducation=$currentEducation, currentEducationSpinnerId=$currentEducationSpinnerId, currentProfession=$currentProfession, hasConsultedBefore=$hasConsultedBefore, monthConsulted=$monthConsulted, yearConsulted=$yearConsulted, placeConsulted=$placeConsulted, complaint=$complaint, solution=$solution, problem=$problem, effortDone=$effortDone, kindergartenData=$kindergartenData, elementaryData=$elementaryData, juniorData=$juniorData, seniorData=$seniorData, collegeData=$collegeData, fatherName=$fatherName, fatherAge=$fatherAge, fatherReligion=$fatherReligion, fatherReligionSpinnerId=$fatherReligionSpinnerId, fatherTribe=$fatherTribe, fatherEducation=$fatherEducation, fatherEducationSpinnerId=$fatherEducationSpinnerId, fatherOccupation=$fatherOccupation, fatherAddress=$fatherAddress, motherName=$motherName, motherAge=$motherAge, motherReligion=$motherReligion, motherReligionSpinnerId=$motherReligionSpinnerId, motherTribe=$motherTribe, motherEducation=$motherEducation, motherEducationSpinnerId=$motherEducationSpinnerId, motherOccupation=$motherOccupation, motherAddress=$motherAddress, sibling1Name=$sibling1Name, sibling1Gender=$sibling1Gender, sibling1Age=$sibling1Age, sibling1Education=$sibling1Education, sibling1EducationSpinnerId=$sibling1EducationSpinnerId, sibling2Name=$sibling2Name, sibling2Gender=$sibling2Gender, sibling2Age=$sibling2Age, sibling2Education=$sibling2Education, sibling2EducationSpinnerId=$sibling2EducationSpinnerId, sibling3Name=$sibling3Name, sibling3Gender=$sibling3Gender, sibling3Age=$sibling3Age, sibling3Education=$sibling3Education, sibling3EducationSpinnerId=$sibling3EducationSpinnerId, sibling4Name=$sibling4Name, sibling4Gender=$sibling4Gender, sibling4Age=$sibling4Age, sibling4Education=$sibling4Education, sibling4EducationSpinnerId=$sibling4EducationSpinnerId, sibling5Name=$sibling5Name, sibling5Gender=$sibling5Gender, sibling5Age=$sibling5Age, sibling5Education=$sibling5Education, sibling5EducationSpinnerId=$sibling5EducationSpinnerId)"
    }
};