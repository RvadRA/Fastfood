package com.example.greatingcard.screen.main.profile
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.greatingcard.R
import com.example.greatingcard.model.MainViewModel
import com.example.greatingcard.screen.auth.LoginActivity
import com.example.greatingcard.utils.ProfileSharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {


    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var profileSharedPreferences: ProfileSharedPreferences

    private lateinit var userImageView: ImageView
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var editButton: Button
    private lateinit var cancelButton: Button
    private lateinit var logoutButton: Button
    private var isEditMode = false
    private val PICK_IMAGE = 1
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        profileSharedPreferences = ProfileSharedPreferences(requireContext())

        userImageView = view.findViewById(R.id.userImageView)
        usernameEditText = view.findViewById(R.id.usernameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        phoneEditText = view.findViewById(R.id.phoneNumberEditText)
        addressEditText = view.findViewById(R.id.addressEditText)
        editButton = view.findViewById(R.id.editButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        logoutButton = view.findViewById(R.id.logoutButton)

        // Disable EditText fields initially
        disableEditMode()
        loadUserProfile()
        loadGoogleUserProfile()

        userImageView.setOnClickListener {
            if (isEditMode) {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE)

            }
        }


        editButton.setOnClickListener {
            if (isEditMode) {
                if (isAnyFieldChanged()) {
                    showSaveConfirmationDialog()

                } else {
                    revertToViewMode()

                }
            } else {
                enterEditMode()
            }
        }

        cancelButton.setOnClickListener {
            showCancelConfirmationDialog()
        }
        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        return view
    }

    private fun enterEditMode() {
        editButton.text = "Save"
        cancelButton.visibility = View.VISIBLE
        enableEditMode()
        isEditMode = true
    }

    private fun revertToViewMode() {
        editButton.text = "Edit"
        cancelButton.visibility = View.GONE
        disableEditMode()
        isEditMode = false
        loadUserProfile() // Revert changes
        loadGoogleUserProfile()
    }

    private fun isAnyFieldChanged(): Boolean {
        val currentUser = auth.currentUser
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val address = addressEditText.text.toString()

        return (currentUser?.displayName != username
                || currentUser?.email != email
                || password.isNotEmpty() // Assuming password is not displayed in EditText
                || currentUser?.phoneNumber != phone // Assuming you have phone number in user profile
              //  || currentUser?.address !=address
                ||/* Check for any other changes in address, etc. */ false)

    }

   /* private fun showSaveConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Save Changes")
            .setMessage("Are you sure you want to save the changes?")
            .setPositiveButton("Yes") { _, _ ->
                saveUserProfile()
                revertToViewMode()
            }
            .setNegativeButton("No") { _, _ ->
                revertToViewMode()
            }
            .show()
    }*/
   private fun showSaveConfirmationDialog() {
       val currentUser = auth.currentUser
       val username = usernameEditText.text.toString()
       val email = emailEditText.text.toString()
       val password = passwordEditText.text.toString()
       val phone = phoneEditText.text.toString()
       val address = addressEditText.text.toString()

       val isAnyFieldChanged = (currentUser?.displayName != username
               || currentUser?.email != email
               || password.isNotEmpty() // Assuming password is not displayed in EditText
               || currentUser?.phoneNumber != phone // Assuming you have phone number in user profile
               || false) // Add conditions for any other changes in address, etc.

       val isImageAdded = imageUri != null

       if (!isAnyFieldChanged && !isImageAdded) {
           revertToViewMode()
           return
       }

       AlertDialog.Builder(requireContext())
           .setTitle("Save Changes")
           .setMessage("Are you sure you want to save the changes?")
           .setPositiveButton("Yes") { _, _ ->
                   saveUserProfile()
                   revertToViewMode()
           }
           .setNegativeButton("No") { _, _ ->
               revertToViewMode()
           }
           .show()
   }


    private fun showCancelConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Editing")
            .setMessage("Are you sure you want to cancel editing? Any unsaved changes will be lost.")
            .setPositiveButton("Yes") { _, _ ->
                revertToViewMode()
            }
            .setNegativeButton("No", null)
            .show()
    }


    private fun enableEditMode() {
        usernameEditText.isEnabled = true
        emailEditText.isEnabled = true
        passwordEditText.isEnabled = true
        phoneEditText.isEnabled = true
        addressEditText.isEnabled = true
    }

    private fun disableEditMode() {
        usernameEditText.isEnabled = false
        emailEditText.isEnabled = false
        passwordEditText.isEnabled = false
        phoneEditText.isEnabled = false
        addressEditText.isEnabled = false
    }

    private fun loadGoogleUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.email != null) {
            val email = currentUser.email
            val username = currentUser.displayName // Display name is equivalent to username in this context

            Log.d("ProfileFragment", "Email: $email, Username: $username")

            // Update the user data in the ViewModel
            if (email != null) {
                viewModel.setUserEmail(email)
            }
            if (username != null) {
                viewModel.setUsername(username)
            }

            // Now, the Fragment can observe changes to this data and update the UI accordingly
        }
    }



    private fun loadUserProfile() {
        if (!isAdded) return
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = document.data
                    if (userData != null) {
                        val username = userData["username"] as? String ?: ""
                        usernameEditText.setText(username)
                        // usernameEditText.setText(userData["username"] as? String ?: "")
                        emailEditText.setText(userData["email"] as? String ?: "")
                        passwordEditText.setText(userData["password"] as? String ?: "")
                        phoneEditText.setText(userData["phone"] as? String ?: "")
                        addressEditText.setText(userData["address"] as? String ?: "")
                        val imageUrl = userData["imageUrl"] as? String
                        // Update SharedViewModel
                        viewModel.setUsername(username)
                        if (!imageUrl.isNullOrEmpty()) {
                            val uri = Uri.parse(imageUrl)
                            viewModel.setUserImageUri(uri)
                            Glide.with(requireContext())
                                .load(uri)
                                .into(userImageView)
                        } else {
                            // If imageUrl is null or empty, display a placeholder image
                            userImageView.setImageResource(R.drawable.user)
                        }
                    }
                }
            }
        }
    }

    private fun saveUserProfile() {
        val currentUser = auth.currentUser
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val address = addressEditText.text.toString()

        if (currentUser != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()

            // Update display name
            currentUser.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
                if (profileTask.isSuccessful) {
                    Toast.makeText(activity, "Display name updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Failed to update display name", Toast.LENGTH_SHORT).show()
                }

                // Update email address
                currentUser.updateEmail(email).addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        Toast.makeText(activity, "Email updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "Failed to update email", Toast.LENGTH_SHORT).show()
                    }

                    // Update password if provided
                    if (password.isNotEmpty()) {
                        currentUser.updatePassword(password).addOnCompleteListener { passwordTask ->
                            if (passwordTask.isSuccessful) {
                                Toast.makeText(activity, "Password updated", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity, "Failed to update password", Toast.LENGTH_SHORT).show()
                            }
                            handleProfileImageUpdate(currentUser.uid, username, email, password, phone, address)
                        }
                    } else {
                        handleProfileImageUpdate(currentUser.uid, username, email, password, phone, address)
                    }
                    // Update SharedViewModel
                    viewModel.setUsername(username)
                    imageUri?.let {
                        viewModel.setUserImageUri(it)
                    }
                }
            }
        }
    }

    private fun handleProfileImageUpdate(userId: String, username: String, email: String, password: String, phone: String, address: String) {
        if (imageUri != null) {
            val storageRef = storage.reference.child("userImages/$userId")
            val uploadTask = storageRef.putFile(imageUri!!)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { imageUploadTask ->
                if (imageUploadTask.isSuccessful) {
                    val downloadUri = imageUploadTask.result
                    val userMap = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "password" to password,
                        "phone" to phone,
                        "address" to address,
                        "imageUrl" to downloadUri.toString()
                    )
                    saveUserDataToFirestore(userId, userMap)
                } else {
                    Toast.makeText(activity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Fetch existing imageUrl from Firestore and include it in the update
            firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
                val existingImageUrl = document.getString("imageUrl") ?: ""
                val userMap = hashMapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password,
                    "phone" to phone,
                    "address" to address,
                    "imageUrl" to existingImageUrl // Ensure the existing imageUrl is retained
                )
                saveUserDataToFirestore(userId, userMap)
            }
        }
    }

    private fun saveUserDataToFirestore(userId: String, userMap: Map<String, Any>) {
        if (activity != null) {
            Toast.makeText(activity, "Profile updated", Toast.LENGTH_SHORT).show()
        }
        firestore.collection("users").document(userId).set(userMap)
            .addOnCompleteListener { firestoreTask ->
                if (activity != null) {
                    if (firestoreTask.isSuccessful) {
                        // Profile data saved to Firestore successfully
                        Toast.makeText(activity, "Profile updated", Toast.LENGTH_SHORT).show()
                        disableEditMode()
                    } else {
                        // Failed to save profile data to Firestore
                        Toast.makeText(activity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }

            }
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
        loadGoogleUserProfile()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe changes in the SharedViewModel
        viewModel.username.observe(viewLifecycleOwner) { username ->
            usernameEditText.setText(username)
        }

        viewModel.userImageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                Glide.with(requireContext())
                    .load(uri)
                    .into(userImageView)
            } else {
                userImageView.setImageResource(R.drawable.user)
            }
        }

        // Call loadUserProfile function when the view is created
        loadUserProfile()
        loadGoogleUserProfile()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            userImageView.setImageURI(imageUri)
        }
    }
}
