package ru.blackbull.eatogether.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ru.blackbull.eatogether.R;
import ru.blackbull.eatogether.models.firebase.User;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "TagForDebug";
    private static final int PICK_IMAGE = 1;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText birthdayEditText;
    private EditText emailEditText;
    private EditText descriptionEditText;
    private ImageView userImage;

    private Uri imageUri = null;

    private FirebaseUser user;
    private DatabaseReference userRef;

    @Override
    protected void onStart() {
        super.onStart();
        // Проверка авторизации при каждом открытии приложения
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
        }
        userRef = new User(user.getUid()).getRef();
        emailEditText.setText(user.getEmail());

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User dataUser = snapshot.getValue(User.class);
                if (dataUser == null) {
                    Log.d(TAG, "onDataChange(USER==NULL): " + snapshot.getValue(String.class));
                    return;
                }
                firstNameEditText.setText(dataUser.getFirstName());
                lastNameEditText.setText(dataUser.getLastName());
                birthdayEditText.setText(dataUser.getBirthday());
                descriptionEditText.setText(dataUser.getDescription());
                Log.d(TAG, "onDataChange: " + dataUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        };
        userRef.addListenerForSingleValueEvent(userListener);

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(user.getUid());
//        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                userImage.setImageURI(uri);
//                Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(EditProfileActivity.class.getName(), "Downloading image failed: " + e);
//                Toast.makeText(getApplicationContext(), "Ошибка при загрузки фотографии", Toast.LENGTH_LONG).show();
//            }
//        }).getResult();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_profile);

        emailEditText = findViewById(R.id.email_input);

        firstNameEditText = findViewById(R.id.first_name_input);
        lastNameEditText = findViewById(R.id.last_name_input);
        birthdayEditText = findViewById(R.id.birthday_input);
        descriptionEditText = findViewById(R.id.description_input);
        userImage = findViewById(R.id.profile_photo);

        Button signOutBtn = findViewById(R.id.btn_sign_out);
        signOutBtn.setOnClickListener(view -> onClickSignOut(view));

        ImageButton cancelBtn = findViewById(R.id.edit_profile_back_btn);
        cancelBtn.setOnClickListener(view -> finish());

        ImageButton saveBtn = findViewById(R.id.edit_profile_save_btn);
        saveBtn.setOnClickListener(view -> onClickSave(view));

        Button changePhotoBtn = findViewById(R.id.profile_change_photo_btn);
        changePhotoBtn.setOnClickListener(view -> onClickChangePhoto(view));
    }

    private void onClickChangePhoto(View view) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                Toast.makeText(getApplicationContext(), "Activity Result", Toast.LENGTH_SHORT).show();
                imageUri = data.getData();
                userImage.setImageURI(imageUri);
            }
        }
    }

    private void onClickSave(View view) {
        User dataUser = new User(user.getUid());

        String email = emailEditText.getText().toString();
        if (!user.getEmail().equals(email)) {
            user.updateEmail(email);
        }

        dataUser.addFirstName(firstNameEditText.getText().toString());
        dataUser.addLastName(lastNameEditText.getText().toString());
        dataUser.addBirthday(birthdayEditText.getText().toString());
        dataUser.addDescription(descriptionEditText.getText().toString());

        if (imageUri != null) {
            dataUser.addImage(imageUri);
        }

        dataUser.save();
        Toast.makeText(getApplicationContext(), "Вы успешно обновили информацию о себе", Toast.LENGTH_SHORT).show();
    }

    private void onClickSignOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplication(), StartActivity.class);
        startActivity(intent);
    }
}
