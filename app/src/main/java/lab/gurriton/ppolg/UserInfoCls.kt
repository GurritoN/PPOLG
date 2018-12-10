package lab.gurriton.ppolg

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserInfo(
    var email: String = "",
    var firstName: String? = "",
    var lastName: String? = "",
    var phone: String? = ""
)