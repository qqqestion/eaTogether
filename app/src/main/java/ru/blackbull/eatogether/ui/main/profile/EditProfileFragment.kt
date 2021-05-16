package ru.blackbull.eatogether.ui.main.profile

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_registration_step_two.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.ImageAdapter
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Constants
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.BaseFragment
import ru.blackbull.eatogether.ui.auth.AuthActivity
import ru.blackbull.eatogether.ui.main.snackbar
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : BaseFragment(R.layout.fragment_edit_profile) {

    private val PICK_IMAGE: Int = 100

    private val viewModel: ProfileViewModel by viewModels()

    private var user: User? = null

    @Inject
    lateinit var imageAdapter: ImageAdapter

    private var currentImage: Int = 0

    private val selectedDate = Calendar.getInstance()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setHasOptionsMenu(true)
        subscribeToObservers()
        Timber.d("onViewCreated")
        viewModel.getCurrentUser()

        viewPager.adapter = imageAdapter

        val callback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentImage = position
            }
        }
        viewPager.registerOnPageChangeCallback(callback)

        etEditProfileBirthday.setOnClickListener {
            displayDatePickerDialog()
        }

        btnEditProfileSignOut.setOnClickListener {
            viewModel.signOut()
            Intent(requireContext() , AuthActivity::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        }
    }

    private fun displayDatePickerDialog() {
        val currentDateTime = Calendar.getInstance()
        currentDateTime.timeInMillis = user!!.birthday!!.toDate().time

        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext() ,
            { _ , year , month , day ->
                selectedDate.set(year , month , day)
                val t = DateUtils.formatDateTime(
                    requireContext() ,
                    selectedDate.timeInMillis ,
                    DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                )
                etEditProfileBirthday.setText(t)
            } ,
            startYear ,
            startMonth ,
            startDay).show()
    }

    private fun subscribeToObservers() {
        viewModel.currentUser.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
                hideLoadingBar()
            } ,
            onLoading = {
                showLoadingBar()
            }
        ) { user ->
            // Костыль, чтобы изображение сбрасывалось,
            // когда выходишь из фрагмента, и сохранялось,
            // когда нажимаешь сохранить
            hideLoadingBar()
            this.user = user
            Timber.d("User triggered $user")
            if (viewModel.currentPhoto.value == null) {
                viewModel.setPhoto(Uri.parse(user?.mainImageUri))
            }
            imageAdapter.images = user!!.images.map { Uri.parse(it) }
            viewPager.currentItem = user.images.indexOf(user.mainImageUri)
            Timber.d("currentUser: ${viewModel.currentPhoto.value}")
            updateUserInfo(user!!)
        })
        viewModel.deleteStatus.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
                hideLoadingBar()
            } ,
            onLoading = {
                showLoadingBar()
            }
        ) { user ->
            hideLoadingBar()
            imageAdapter.images = user.images.map { Uri.parse(it) }
            this.user = user
            Timber.d("Delete status success: $user")
        })
        viewModel.currentPhoto.observe(viewLifecycleOwner , { uri ->
            Timber.d("currentPhoto: $uri")
//            ivEditProfileImage.load(uri) {
//                transformations(CircleCropTransformation())
//            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu , inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile , menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSave -> onClickSaveButton()
            R.id.actionMakeMain ->
                viewModel.makeImageMain(imageAdapter.images[currentImage])
            R.id.actionAddNew -> {
                val intent =
                    Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent , PICK_IMAGE)
            }
            R.id.actionDelete ->
                viewModel.deleteImage(imageAdapter.images[currentImage])
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onClickSaveButton() {
        user?.firstName = etEditProfileFirstName.text.toString()
        user?.lastName = etEditProfileLastName.text.toString()
        user?.description = etEditProfileDescription.text.toString()
//        user.phone = etEditProfilePhoneNumber.text.toString()

        user?.birthday = Timestamp(selectedDate.time)
        user?.mainImageUri = viewModel.currentPhoto.value.toString()
        Timber.d("Adapter: ${imageAdapter.images}")
        Timber.d("User images: ${user?.images}")
        viewModel.updateUser(user!!)
        snackbar("Профиль успешно обновлен")
    }

    private fun updateUserInfo(user: User) {
        etEditProfileFirstName.setText(user.firstName)
        etEditProfileLastName.setText(user.lastName)
//        etEditProfilePhoneNumber.setText(user.phone)
        etEditProfileDescription.setText(user.description)

//        val pattern = "dd.MM.yyyy"
//        val simpleDateFormat = SimpleDateFormat(pattern , Locale.US)
//        etEditProfileBirthday.setText(simpleDateFormat.format(user.birthday?.toDate()!!))
        etEditProfileBirthday.setText(
            DateUtils.formatDateTime(
                requireContext() ,
                user.birthday?.toDate()?.time!! ,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
            )
        )
//        ivEditProfileImage.load(viewModel.currentPhoto.value) {
//            transformations(CircleCropTransformation())
//        }
    }

    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            val imageUri = data?.data ?: return
//            user!!.images -= Constants.DEFAULT_IMAGE_URL
//            imageAdapter.images -= Uri.parse(Constants.DEFAULT_IMAGE_URL)
            user!!.images += imageUri.toString()
            imageAdapter.images += imageUri
            viewModel.updateUser(user!!)
//            viewModel.setPhoto(imageUri)
        }
    }
}