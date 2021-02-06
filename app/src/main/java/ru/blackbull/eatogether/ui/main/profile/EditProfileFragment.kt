package ru.blackbull.eatogether.ui.main.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.extensions.shortToast
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.auth.AuthActivity
import ru.blackbull.eatogether.ui.main.snackbar
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private val PICK_IMAGE: Int = 100

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        subscribeToObservers()

        edit_profile_save_btn.setOnClickListener { onClickSaveButton() }
        btnEditProfileSignOut.setOnClickListener {
            viewModel.signOut()
            Intent(requireContext() , AuthActivity::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        }
        btnEditProfileChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent , PICK_IMAGE)
        }
        btnEditProfileBack.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.getCurrentUser()
    }

    private fun subscribeToObservers() {
        viewModel.currentUser.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
            } ,
            onLoading = {

            }
        ) { user ->
            // Костыль, чтобы изображение сбрасывалось,
            // когда выходишь из фрагмента, и сохранялось,
            // когда нажимаешь сохранить
            if (viewModel.currentPhoto.value == null) {
                viewModel.setPhoto(Uri.parse(user?.imageUri))
            }
            Timber.d("currentUser: ${viewModel.currentPhoto.value}")
            updateUserInfo(user!!)
        })
        viewModel.currentPhoto.observe(viewLifecycleOwner , { uri ->
            Timber.d("currentPhoto: $uri")
            ivEditProfileImage.load(uri) {
                transformations(CircleCropTransformation())
            }
        })
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
        Timber.d(viewModel.currentPhoto.value.toString())
        user.imageUri = viewModel.currentPhoto.value.toString()
        viewModel.updateUser(user , viewModel.currentPhoto.value!!)
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
//        ivEditProfileImage.load(savedUri) {
//            transformations(CircleCropTransformation())
//        }
    }

    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            val imageUri = data?.data ?: return
            viewModel.setPhoto(imageUri)
        }
    }
}