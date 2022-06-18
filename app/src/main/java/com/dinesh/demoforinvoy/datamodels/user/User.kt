package com.dinesh.demoforinvoy.datamodels.user

data class User(
    val name: String,
    val userId: String
) {
    companion object {
        val UNKNOWN_USER = User("", "")
    }
}
