package ru.blackbull.eatogether.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.StartActivity

class ProfileFragment : Fragment() {
    private val firebaseViewModel: ProfileViewModel by viewModels()

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

        firebaseViewModel.currentUser.observe(viewLifecycleOwner , Observer { user ->
            profile_photo.load(user._imageUri) {
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
        firebaseViewModel.getCurrentUser()
    }
}

