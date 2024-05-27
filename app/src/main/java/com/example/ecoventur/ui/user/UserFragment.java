package com.example.ecoventur.ui.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.databinding.FragmentUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserFragment extends Fragment {

    Activity context;
    ImageView profilepic;
    String personPhone;
    String personName;
    Long personEcocoin;
    private FragmentUserBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db;
    FirebaseUser user;
    TextView name;
    Button email, phone, Ecocoins;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        new ViewModelProvider(this).get(com.example.ecoventur.ui.user.UserViewModel.class);
        context = getActivity();
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
    public void onStart() {
        super.onStart();

        Button logout = (Button) context.findViewById(R.id.logout);
        Button editProfile = (Button) context.findViewById(R.id.editprofile);
        name = context.findViewById(R.id.name);
        email = context.findViewById(R.id.displayemail);
        phone = context.findViewById(R.id.phoneNumber);
        Ecocoins = context.findViewById(R.id.EcoCoin);
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user == null) {
            Intent intent = new Intent(context, LoginPageActivity.class);
            startActivity(intent);
        } else {
            String personEmail = user.getEmail();
            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Access the profilePicUrl field
                                    personPhone = document.getString("phone");
                                    personName = document.getString("username");
                                    personEcocoin = document.getLong("ecocoin");
                                    String personEcocoins = (personEcocoin != null) ? String.valueOf(personEcocoin) : "N/A";

                                    if(personPhone==null){
                                        callphonedialog();
                                    }
                                    phone.setText(personPhone);
                                    name.setText(personName);
                                    Ecocoins.setText(personEcocoins + " EcoCoins");
                                }
                            } else {
                                // Handle failures
                                Exception exception = task.getException();
                                if (exception != null) {
                                    // Log or display the exception details
                                }
                            }
                        }
                    });
            email.setText(personEmail);
        }


        // proile pic
        profilepic = context.findViewById(R.id.ProfilePicture);

        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // Access the profilePicUrl field
                            if (document.contains("profilePicUrl")){
                                String profilePicUrl = document.getString("profilePicUrl");
                                if(profilePicUrl!=null && !profilePicUrl.isEmpty()){
                                    Glide.with(profilepic.getContext()).load(profilePicUrl).into(profilepic);
                                }
                            }
                        }
                    }
                });

        //edit profile
        String strEmail = email.getText().toString().trim();
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.editpassword).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        auth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Reset password link has been sent", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss(); // Dismiss the current dialog
                    }
                });

                //edit phone number
                dialogView.findViewById(R.id.editphone).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callphonedialog();

                    }
                });

                // Edit username
                dialogView.findViewById(R.id.editusername).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                        View dialogView2 = getLayoutInflater().inflate(R.layout.dialog_edit_username, null);
                        builder2.setView(dialogView2);
                        AlertDialog dialog2 = builder2.create();

                        dialogView2.findViewById(R.id.changeUsername).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                if (firebaseAuth.getCurrentUser() != null) {
                                    EditText usernameEditText = dialogView2.findViewById(R.id.newUsername);
                                    String newusername = usernameEditText.getText().toString();
                                    Map<String, Object> NewUser = new HashMap<>();
                                    NewUser.put("username", newusername);

                                    db.collection("users").document(user.getUid())
                                            .update(NewUser)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    name.setText(newusername);
                                                    dialog2.dismiss();
                                                }
                                            });
                                }
                            }
                        });

                        dialog2.show(); // Show the new dialog
                        dialog.dismiss(); // Dismiss the current dialog
                    }
                });

                dialog.show();
            }
        });


        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditProfilePictureActivity.class);
                startActivity(intent);
            }
        });

        //logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null); // Use R.layout.dialoglogout
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // if yes
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(context, LoginPageActivity.class);
                        startActivity(intent);
                        dialog.dismiss(); // Dismiss the dialog instead of trying to finalize
                    }
                });
                dialogView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


    }
    private String callphonedialog(){
        AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
        View dialogView3 = getLayoutInflater().inflate(R.layout.dialog_edit_phonenumber, null);
        builder3.setView(dialogView3);
        AlertDialog dialog3 = builder3.create();
        dialogView3.findViewById(R.id.updatephone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    EditText usernameEditText = dialogView3.findViewById(R.id.newphone);
                    personPhone = usernameEditText.getText().toString();
                    Map<String, Object> NewUser = new HashMap<>();
                    NewUser.put("phone", personPhone);

                    db.collection("users").document(user.getUid())
                            .update(NewUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    phone.setText(personPhone);
                                    dialog3.dismiss();
                                }
                            });
                    dialog3.dismiss();
                }
            }
        });

        dialog3.show();
        return personPhone;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}