package ru.blackbull.eatogether.ui.fragments

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
import androidx.lifecycle.Observer
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.extensions.shortToast
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.ui.ProfileActivity
import ru.blackbull.eatogether.ui.StartActivity
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class EditProfileFragment : Fragment() {

    private val PICK_IMAGE: Int = 100

    private lateinit var firebaseViewModel: FirebaseViewModel

    private var savedUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        firebaseViewModel = (activity as ProfileActivity).firebaseViewModel

        firebaseViewModel.user.observe(viewLifecycleOwner , Observer { newUser ->
            // Костыль, чтобы изображение сбрасывалось,
            // когда выходишь из фрагмента, и сохранялось,
            // когда нажимаешь сохранить
            if (savedUri == null) {
                savedUri = newUser._imageUri
            }
            Log.d("ImageDebug" , "savedUri in user: $savedUri")
            updateUserInfo(newUser)
        })
        return inflater.inflate(R.layout.fragment_edit_profile , container , false)
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        edit_profile_save_btn.setOnClickListener { onClickSaveButton() }
        btn_sign_out.setOnClickListener {
            firebaseViewModel.signOut()
            val intent = Intent(activity , StartActivity::class.java)
            startActivity(intent)
        }
        profile_change_photo_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent , PICK_IMAGE)
        }
        edit_profile_back_btn.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.profile_fragment_container ,
                    ProfileFragment()
                )
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!firebaseViewModel.isAuthenticated()) {
            val intent = Intent(context , StartActivity::class.java)
            startActivity(intent)
        }
        firebaseViewModel.getCurrentUser()
    }

    private fun onClickSaveButton() {
        val user = User()
        user.firstName = first_name_input.text.toString()
        user.lastName = last_name_input.text.toString()
        user.description = description_input.text.toString()
        user.email = email_input.text.toString()

        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern , Locale.US)
        val date: Date?
        try {
            date = simpleDateFormat.parse(birthday_input.text.toString())
        } catch (e: ParseException) {
            birthday_input.error = "Введено неправильное значение"
            return
        }
        user.birthday = Timestamp(date)
        user._imageUri = savedUri
        firebaseViewModel.updateUser(user)
        shortToast("Профиль успешно обновлен")
    }

    private fun updateUserInfo(user: User) {
        first_name_input.setText(user.firstName)
        last_name_input.setText(user.lastName)
        email_input.setText(user.email)
        description_input.setText(user.description)

        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern , Locale.US)
        birthday_input.setText(simpleDateFormat.format(user.birthday?.toDate()!!))
        Log.d("EditProfile" , "updateUserInfo: ${user.imageUri}")
        profile_photo.load(savedUri) {
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