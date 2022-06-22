package com.dinesh.demoforinvoy.datamodels.user

data class User(
    val name: String,
    val userId: String,
    val isACoach: Boolean = false
) {
    companion object {
        val UNKNOWN_USER = User("", "", true)
    }
}
