package ru.blackbull.eatogether.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.extensions.shortToast
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.ui.StartActivity
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private val PICK_IMAGE: Int = 100

    private val firebaseViewModel: ProfileViewModel by viewModels()

    private var savedUri: Uri? = null

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        subscribeToObservers()

        edit_profile_save_btn.setOnClickListener { onClickSaveButton() }
        btnEditProfileSignOut.setOnClickListener {
            firebaseViewModel.signOut()
            findNavController().popBackStack()
        }
        btnEditProfileChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent , PICK_IMAGE)
        }
        btnEditProfileBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun subscribeToObservers() {
        firebaseViewModel.currentUser.observe(viewLifecycleOwner , Observer { newUser ->
            if (newUser == null) {
                findNavController().popBackStack()
            }
            // Костыль, чтобы изображение сбрасывалось,
            // когда выходишь из фрагмента, и сохранялось,
            // когда нажимаешь сохранить
            if (savedUri == null) {
                savedUri = newUser!!._imageUri
            }
            Timber.d("savedUri in user: $savedUri")
            updateUserInfo(newUser!!)
        })
    }

    override fun onStart() {
        super.onStart()
        firebaseViewModel.getCurrentUser()
    }

    private fun onClickSaveButton() {
        val user = User()
        user.firstName = etEditProfileFirstName.text.toString()
        user.lastName = etEditProfileLastName.text.toString()
        user.description = etEditProfileDescription.text.toString()
        user.email = etEditProfileEmail.text.toString()

        val pattern = "dd.MM.yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern , Locale.US)
        val date: Date?
        try {
            date = simpleDateFormat.parse(etEditProfileBirthday.text.toString())
        } catch (e: ParseException) {
            etEditProfileBirthday.error = "Введено неправильное значение"
            return
        }
        user.birthday = Timestamp(date)
        user._imageUri = savedUri
        firebaseViewModel.updateUser(user)
        shortToast("Профиль успешно обновлен")
    }

    private fun updateUserInfo(user: User) {
        etEditProfileFirstName.setText(user.firstName)
        etEditProfileLastName.setText(user.lastName)
        etEditProfileEmail.setText(user.email)
        etEditProfileDescription.setText(user.description)

        val pattern = "dd.MM.yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern , Locale.US)
        etEditProfileBirthday.setText(simpleDateFormat.format(user.birthday?.toDate()!!))
        ivEditProfileImage.load(savedUri) {
            transformations(CircleCropTransformation())
        }
    }

    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            val imageUri = data?.data ?: return
            savedUri = imageUri
        }
    }
}