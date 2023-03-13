package com.redrock.sso

import com.google.gson.annotations.SerializedName

data class SsoAccount(
    val acr: String,
    @SerializedName("allowed-origins")
    val allowed_origins: List<String>,
    val aud: String,
    val auth_time: Int,
    val azp: String,
    val email: String,
    val email_verified: Boolean,
    val exp: Int,
    val family_name: String,
    val given_name: String,
    val iat: Int,
    val iss: String,
    val jti: String,
    val name: String,
    val preferred_username: String,
    val realm_access: RealmAccess,
    val resource_access: ResourceAccess,
    val scope: String,
    val session_state: String,
    val sid: String,
    val sub: String,
    val typ: String
) {
    data class RealmAccess(
        val roles: List<String>
    )

    data class ResourceAccess(
        val account: Account
    ) {
        data class Account(
            val roles: List<String>
        )
    }
}