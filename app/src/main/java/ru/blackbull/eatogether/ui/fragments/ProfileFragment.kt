package ru.blackbull.eatogether.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.extensions.shortToast
import ru.blackbull.eatogether.ui.ProfileActivity
import ru.blackbull.eatogether.ui.StartActivity
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel

class ProfileFragment : Fragment() {
    private lateinit var firebaseViewModel: FirebaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout: View = inflater.inflate(R.layout.fragment_profile , container , false)
        val editProfileBtn = layout.findViewById<ImageButton>(R.id.profile_settings)
        editProfileBtn.setOnClickListener(context as View.OnClickListener?)
        val menuBtn = layout.findViewById<ImageButton>(R.id.profile_menu_btn)
        menuBtn.setOnClickListener(context as View.OnClickListener?)

        firebaseViewModel = (activity as ProfileActivity).firebaseViewModel
        firebaseViewModel.currentUserPhoto.observe(viewLifecycleOwner , Observer { photoUri ->
            profile_photo.load(photoUri) {
                transformations(CircleCropTransformation())
            }
        })

        return layout
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(context , StartActivity::class.java)
            startActivity(intent)
        }
        firebaseViewModel.getCurrentUserPhotoUri()
    }
}

