package com.example.viewandroidapp.Model


import com.example.viewandroidapp.dao.AppLocalDb

class Model private constructor() {

    private val database = AppLocalDb.db

    companion object {
        val instance: Model = Model()
    }

    interface GetAllUsersListener {
        fun onComplete(users: List<User>)
    }
}