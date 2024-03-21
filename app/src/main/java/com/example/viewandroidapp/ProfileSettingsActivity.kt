package com.example.viewandroidapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.viewandroidapp.Model.FireBaseModel
import com.example.viewandroidapp.databinding.ActivityProfileSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingsBinding
    private lateinit var fireBaseModel: FireBaseModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_profile_settings)

        fireBaseModel = FireBaseModel()
        auth = FirebaseAuth.getInstance()
    }

    fun onIconCloseClick(view: View) {
        finish()
    }

    fun onIconLogoutClick(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
            // logout action

            // dismiss window and go back to login page
            dialogInterface.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.logout))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black))
        }
        alertDialog.show()
    }

    fun changeProfilePictureClick(view: View) {
        // Open image picker
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Retrieve the selected image URI
            val selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                // Update ImageView with selected image
                binding.profilePicture.setImageURI(uri)
                // Upload the selected image to Firebase Storage
                val userEmail = auth.currentUser?.email ?: ""
                selectedImageUri?.let { uri ->
                    fireBaseModel.uploadPhoto(uri, userEmail,
                        onSuccess = {
                            // Profile picture uploaded successfully
                            // You may want to update UI or show a message here
                                    Log.e("ProfileSettingsActivity", "Profile picture uploaded successfully")
                        },
                        onFailure = { exception ->
                            // Handle failure
                            // You may want to show an error message or log the exception
                            Log.e("ProfileSettingsActivity", "Error uploading profile picture: $exception")
                        }
                    )
                }            }
        }
    }
    fun onIconCheckClick(view: View) {
        // Change name in the database
        val newName = findViewById<EditText>(R.id.userName).text.toString()
        if (newName.isNotEmpty()) { // Check if the newName is not empty
            val userEmail = auth.currentUser?.email ?: ""

            fireBaseModel.updateUserName(userEmail, newName,
                onSuccess = {
                    // Name updated successfully
                    Log.e("ProfileSettingsActivity", "Name updated successfully")
                    // Show a success message or perform any other action if needed
                    Toast.makeText(
                        baseContext, "Name updated successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
                    startActivity(intent)
                    finish()
                },
                onFailure = { exception ->
                    // Handle failure
                    Log.e("ProfileSettingsActivity", "Error updating name: $exception")
                    Toast.makeText(
                        baseContext, "Error updating name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        } else {
            // Show an error message or perform any other action if the new name is empty
            Toast.makeText(
                baseContext, "Please enter a non-empty name.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }







}