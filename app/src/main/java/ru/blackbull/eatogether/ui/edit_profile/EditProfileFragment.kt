package ru.blackbull.eatogether.ui.edit_profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.blackbull.data.models.firebase.User
import ru.blackbull.data.models.firebase.toUser
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.ImageAdapter
import ru.blackbull.eatogether.core.BaseFragmentV2
import ru.blackbull.eatogether.databinding.FragmentEditProfileBinding
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.ui.onTextChanged
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : BaseFragmentV2<EditProfileViewModel>(
    R.layout.fragment_edit_profile, EditProfileViewModel::class
) {

    private val binding by viewBinding<FragmentEditProfileBinding>()

    private val PICK_IMAGE: Int = 100

    private var user: User? = null

    @Inject
    lateinit var imageAdapter: ImageAdapter

    private var currentImage: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeToObservers()
        Timber.d("onViewCreated")

        binding.vpImages.adapter = imageAdapter

        val callback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentImage = position
            }
        }
        binding.vpImages.registerOnPageChangeCallback(callback)

        binding.btnSignOut.setOnClickListener {
            viewModel.signOut()
        }
        binding.etFirstName.onTextChanged(viewModel::handleFirstName)
    }

    private fun subscribeToObservers() {
        viewModel.deleteStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
                hideLoadingBar()
            },
            onLoading = {
                showLoadingBar()
            }
        ) { user ->
            hideLoadingBar()
            imageAdapter.images = user.images.map { Uri.parse(it) }
            this.user = user.toUser()
            Timber.d("Delete status success: $user")
        })
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::handleState)
            }
        }
    }

    private fun handleState(state: EditProfileState) {
        binding.progressBar.isVisible = state.isLoading

        binding.etFirstName.setText(state.firstName)
        binding.etLastName.setText(state.lastName)
        imageAdapter.images = state.images
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSave -> viewModel.onSave()
            R.id.actionMakeMain ->
                viewModel.makeImageMain(imageAdapter.images[currentImage])
            R.id.actionAddNew -> {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent, PICK_IMAGE)
            }
            R.id.actionDelete ->
                viewModel.deleteImage(imageAdapter.images[currentImage])
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            val imageUri = data?.data ?: return
            user!!.images += imageUri.toString()
            imageAdapter.images += imageUri
            viewModel.updateUser(user!!)
        }
    }
}