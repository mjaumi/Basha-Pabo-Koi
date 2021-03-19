package com.example.bashapabokoi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.bashapabokoi.Adapters.OwnerAdAdapter;
import com.example.bashapabokoi.Models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    RoundedImageView profilePicture;
    TextView ProfileName, profilePhoneNo, profileAddress, profileMail;

    CardView editPhoneNo, editAddress, editEmail;

    FloatingActionButton returnFromProfile;

    String tempUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePicture = findViewById(R.id.pro_pic);
        ProfileName = findViewById(R.id.profile_name);
        profilePhoneNo = findViewById(R.id.phone_no_profile);
        profileAddress = findViewById(R.id.address_profile);
        profileMail = findViewById(R.id.email_profile);

        returnFromProfile = findViewById(R.id.return_from_profile);

        editPhoneNo = findViewById(R.id.profile_phone_no);
        editEmail = findViewById(R.id.profile_email);
        editAddress = findViewById(R.id.profile_address);

        drawerLayout = findViewById(R.id.drawer_profile);
        navigationView = findViewById(R.id.nav_view_profile);
        View header =  navigationView.getHeaderView(0);
        TextView headerProfileName = header.findViewById(R.id.profile_name_header);
        RoundedImageView headerProPic = header.findViewById(R.id.pro_pic_header);



        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                headerProfileName.setText(snapshot.child("name").getValue().toString());
                ProfileName.setText(snapshot.child("name").getValue().toString());
                profilePhoneNo.setText(snapshot.child("phoneNumber").getValue().toString());

                User u = snapshot.getValue(User.class);

                tempUri = u.getProfileImage();

                Glide.with(getApplicationContext()).load(u.getProfileImage()).placeholder(R.drawable.user).into(headerProPic);
                Glide.with(getApplicationContext()).load(u.getProfileImage()).placeholder(R.drawable.user).into(profilePicture);

                ViewPager2 ownerAd = findViewById(R.id.owner_ad_shower);

                List<OwnerAdShower> ownerAdShowers = new ArrayList<>();

                OwnerAdShower ad1 = new OwnerAdShower();

                ad1.imageUri = "https://t.auntmia.com/nthumbs/2016-03-08/2284861/2284861_12b.jpg";        //"https://t.auntmia.com/nthumbs/2016-03-08/2284861/2284861_12b.jpg"
                ad1.rent = "10000";
                ad1.location = "Adabor";
                ad1.flatType = "Seat";
                ad1.genre = "Male only";
                ad1.starRating = 4.5f;

                ownerAdShowers.add(ad1);

                OwnerAdShower ad2 = new OwnerAdShower();

                ad2.imageUri = "https://t.auntmia.com/nthumbs/2015-05-07/2360911/2360911_10b.jpg";     //"https://t.auntmia.com/nthumbs/2015-05-07/2360911/2360911_10b.jpg"
                ad2.rent = "78000";
                ad2.location = "Motijheel";
                ad2.flatType = "Flat";
                ad2.genre = "Female only";
                ad2.starRating = 4.9f;

                ownerAdShowers.add(ad2);

                OwnerAdShower ad3 = new OwnerAdShower();

                ad3.imageUri = "https://t.auntmia.com/nthumbs/2014-01-26/2697314/2697314_19b.jpg";     //"https://t.auntmia.com/nthumbs/2014-01-26/2697314/2697314_19b.jpg"
                ad3.rent = "6000";
                ad3.location = "Mohammadpur";
                ad3.flatType = "Sublet";
                ad3.genre = "Family";
                ad3.starRating = 3.5f;

                ownerAdShowers.add(ad3);

                ownerAd.setAdapter(new OwnerAdAdapter(ownerAdShowers));

                ownerAd.setClipToPadding(false);
                ownerAd.setClipChildren(false);
                ownerAd.setOffscreenPageLimit(3);
                ownerAd.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(20));
                compositePageTransformer.addTransformer((page, position) -> {

                    float r = 1 - Math.abs(position);
                    page.setScaleY(0.95f + r * 0.05f);
                });

                ownerAd.setPageTransformer(compositePageTransformer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.nav_pro);
        navigationView.setNavigationItemSelectedListener(this);

        editPhoneNo.setOnClickListener(v -> {

            CustomEditDialog editNumber = new CustomEditDialog(ProfileActivity.this, "Edit phone number", "Enter your new phone number", 1);
            editNumber.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            editNumber.show();
        });

        editEmail.setOnClickListener(v -> {

            CustomEditDialog editNumber = new CustomEditDialog(ProfileActivity.this, "Edit email", "Enter your new email", 2);
            editNumber.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            editNumber.show();
        });

        editAddress.setOnClickListener(v -> {

            CustomEditDialog editNumber = new CustomEditDialog(ProfileActivity.this, "Edit address", "Enter your new address", 3);
            editNumber.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            editNumber.show();
        });


        returnFromProfile.setOnClickListener(v -> finish());
    }

    //TODO need to add menu items
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.nav_pro:

                Intent intentProfile = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intentProfile);

            case R.id.nav_out:

                FirebaseAuth.getInstance().signOut();
                Intent intentLogOut = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intentLogOut);
                break;

            case R.id.nav_ad:

                Intent intentAd = new Intent(ProfileActivity.this, AdCreateActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intentAd);
                break;
        }

        return true;
    }

}