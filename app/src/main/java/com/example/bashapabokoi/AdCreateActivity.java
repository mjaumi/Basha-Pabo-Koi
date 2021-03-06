package com.example.bashapabokoi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.bashapabokoi.Helper.LocaleHelper;
import com.example.bashapabokoi.Models.CreateAd;
import com.example.bashapabokoi.Models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.paperdb.Paper;

@SuppressWarnings("unchecked")
public class AdCreateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private int REQUEST_CAMERA;
    private int SELECT_FILE;

    File imageFile;
    Uri outputImageUri;
    String currentImagePath = null;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String randomKey;

    LottieAnimationView liftBox, generatorBox, parkingBox, securityBox, gasBox, wifiBox, termBox, markerAddedBox;
    boolean isLiftChecked = false, isGeneratorChecked = false, isParkingChecked = false, isSecurityChecked = false, isGasChecked = false, isWifiChecked = false, isTermChecked = false;


    Spinner thanaSpinner, washSpinner, bedSpinner, religionSpinner, flatTypeSpinner, verandaSpinner, floorSpinner, genreSpinner;
    TextView langState, modeState, vacantText, titleText, title, rent, addressTitle, basicInfo, thanaTitle, vacFromTitle, flatTypeTitle, washTitle, verandaTitle, bedTitle, floorTitle, religionTitle, genreTitle, otherServicesTitle, currentBillTitle, waterBillTitle, gasBillTitle, otherTitle, lift, generator, parking, security, gas, wifi, desTitle, addPicTitle, acceptTitle, markerTitle;

    ImageView img1, img2, img3, img4, img5;

    String longitude, latitude;
    String image_url1 = "no_image", image_url2 = "no_image", image_url3 = "no_image", image_url4 = "no_image", image_url5 = "no_image";

    Button createButton;
    EditText titleTextBox, addressTextBox, currentBillTextBox, waterBillTextBox, gasBillTextBox, otherChargeTextBox, descriptionTextBox, rentTextBox;

    Calendar calendar;

    Intent previousIntentData;
    Date date = new Date();

    String alertTitle, alertMsg, yes, no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){

            setTheme(R.style.Theme_BashaPaboKoi_Dark);
        } else{

            setTheme(R.style.Theme_BashaPaboKoi);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_create);

        Paper.init(this);

        String language = Paper.book().read("language");
        if(language == null){

            Paper.book().write("language", "en");
        }

        randomKey = database.getReference().push().getKey();

        FloatingActionButton returnFromAd = findViewById(R.id.return_from_ad);

        returnFromAd.setOnClickListener(v -> this.finish());

        langState = findViewById(R.id.switch_lang_state);
        modeState = findViewById(R.id.switch_mode_state);

        title = findViewById(R.id.title);
        basicInfo = findViewById(R.id.basic_info);
        rent = findViewById(R.id.rent);
        addressTitle = findViewById(R.id.address_title);
        thanaTitle = findViewById(R.id.thana_title);
        vacFromTitle = findViewById(R.id.vac_from_title);
        flatTypeTitle = findViewById(R.id.flat_type_title);
        washTitle = findViewById(R.id.washroom_title);
        verandaTitle = findViewById(R.id.verandas_title);
        bedTitle = findViewById(R.id.bedroom_title);
        floorTitle = findViewById(R.id.floor_title);
        religionTitle = findViewById(R.id.religion_title);
        genreTitle = findViewById(R.id.genre_title);
        otherServicesTitle = findViewById(R.id.other_services_title);
        currentBillTitle = findViewById(R.id.current_bill_title);
        waterBillTitle = findViewById(R.id.water_bill_title);
        gasBillTitle = findViewById(R.id.gas_bill_title);
        otherTitle = findViewById(R.id.other_charges_title);
        lift = findViewById(R.id.lift_box);
        generator = findViewById(R.id.generator_box);
        parking = findViewById(R.id.parking_box);
        security = findViewById(R.id.security_box);
        gas = findViewById(R.id.gas_box);
        wifi = findViewById(R.id.wifi_box);
        desTitle = findViewById(R.id.description_ad_title);
        addPicTitle = findViewById(R.id.add_photos_title);
        acceptTitle = findViewById(R.id.terms_agree_box);
        markerTitle = findViewById(R.id.marker_box);

        liftBox = findViewById(R.id.lift);
        generatorBox = findViewById(R.id.generator);
        parkingBox = findViewById(R.id.parking);
        securityBox = findViewById(R.id.security);
        gasBox = findViewById(R.id.gas);
        wifiBox = findViewById(R.id.wifi);
        termBox = findViewById(R.id.terms_agreed);
        markerAddedBox = findViewById(R.id.marker_added);

        LinearLayout markerLayout = findViewById(R.id.is_marker_added);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){

            liftBox.setAnimation("tick_dark.json");
            generatorBox.setAnimation("tick_dark.json");
            parkingBox.setAnimation("tick_dark.json");
            securityBox.setAnimation("tick_dark.json");
            gasBox.setAnimation("tick_dark.json");
            wifiBox.setAnimation("tick_dark.json");
            termBox.setAnimation("tick_dark.json");
            markerAddedBox.setAnimation("tick_dark.json");

            Context context = LocaleHelper.setLocale(this, language);
            Resources resources = context.getResources();

            modeState.setText(resources.getString(R.string.on));

        } else{

            Context context = LocaleHelper.setLocale(this, language);
            Resources resources = context.getResources();

            modeState.setText(resources.getString(R.string.off));
        }

        checkTheBoxes();

        thanaSpinner = findViewById(R.id.thana);
        washSpinner = findViewById(R.id.washroom_spinner);
        bedSpinner = findViewById(R.id.bedroom_spinner);
        religionSpinner = findViewById(R.id.religion_spinner);
        flatTypeSpinner = findViewById(R.id.flat_type_spinner);
        verandaSpinner = findViewById(R.id.verandas_spinner);
        floorSpinner = findViewById(R.id.floor_spinner);
        genreSpinner = findViewById(R.id.genre_spinner);

        titleText = findViewById(R.id.create_title);
        vacantText = findViewById(R.id.vacant_text);
        vacantText.setOnClickListener(this);

        setSpinnerList(Paper.book().read("language"));

        titleTextBox = findViewById(R.id.ad_title);
        addressTextBox = findViewById(R.id.address);
        currentBillTextBox = findViewById(R.id.current_bill);
        waterBillTextBox = findViewById(R.id.water_bill);
        gasBillTextBox = findViewById(R.id.gas_bill);
        otherChargeTextBox = findViewById(R.id.service_charge);
        descriptionTextBox = findViewById(R.id.description);
        rentTextBox = findViewById(R.id.ad_rent);

        img1 = findViewById(R.id.image_1);
        img2 = findViewById(R.id.image_2);
        img3 = findViewById(R.id.image_3);
        img4 = findViewById(R.id.image_4);
        img5 = findViewById(R.id.image_5);

        img1.setOnClickListener(v -> {

            REQUEST_CAMERA = 1;
            SELECT_FILE = 6;
            selectImage();
        });

        img2.setOnClickListener(v -> {

            REQUEST_CAMERA = 2;
            SELECT_FILE = 7;
            selectImage();
        });

        img3.setOnClickListener(v -> {

            REQUEST_CAMERA = 3;
            SELECT_FILE = 8;
            selectImage();
        });

        img4.setOnClickListener(v -> {

            REQUEST_CAMERA = 4;
            SELECT_FILE = 9;
            selectImage();
        });

        img5.setOnClickListener(v -> {

            REQUEST_CAMERA = 5;
            SELECT_FILE = 10;
            selectImage();
        });

        createButton = findViewById(R.id.but_ad_done);

        drawerLayout = findViewById(R.id.drawer_ad);
        navigationView = findViewById(R.id.nav_view_ad);

        updateView(Paper.book().read("language"));

        createButton.setOnClickListener(v -> {

            String title = titleTextBox.getText().toString();
            String address = addressTextBox.getText().toString();
            String currentBill = currentBillTextBox.getText().toString();
            String waterBill = waterBillTextBox.getText().toString();
            String gasBill = gasBillTextBox.getText().toString();
            String otherCharge = otherChargeTextBox.getText().toString();
            String description = descriptionTextBox.getText().toString();
            String rent = rentTextBox.getText().toString();

            String vacFrom = vacantText.getText().toString();
            String thana = thanaSpinner.getSelectedItem().toString();

            String washroom = washSpinner.getSelectedItem().toString();
            String bedroom = bedSpinner.getSelectedItem().toString();
            String religion = religionSpinner.getSelectedItem().toString();
            String flatType = flatTypeSpinner.getSelectedItem().toString();
            String veranda = verandaSpinner.getSelectedItem().toString();
            String floor = floorSpinner.getSelectedItem().toString();
            String genre = genreSpinner.getSelectedItem().toString();

            if (isTermChecked) {

                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(rent) && !TextUtils.isEmpty(vacFrom) && !thanaSpinner.getSelectedItem().toString().matches("-") && !washSpinner.getSelectedItem().toString().matches("-") && !bedSpinner.getSelectedItem().toString().matches("-") && !religionSpinner.getSelectedItem().toString().matches("-") && !flatTypeSpinner.getSelectedItem().toString().matches("-") && !verandaSpinner.getSelectedItem().toString().matches("-") && !floorSpinner.getSelectedItem().toString().matches("-") && !genreSpinner.getSelectedItem().toString().matches("-")){

                    AlertDialog.Builder markerPlacementDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                    markerPlacementDialog.setTitle(alertTitle);
                    markerPlacementDialog.setMessage(alertMsg);

                    markerPlacementDialog.setPositiveButton(yes, (dialog, which) -> {

                        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }

                        @SuppressWarnings("rawtypes")
                        Task location = fusedLocationProviderClient.getLastLocation();

                        location.addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                Location currentLocation = (Location) task.getResult();
                                longitude = Double.toString(currentLocation.getLongitude());
                                latitude = Double.toString(currentLocation.getLatitude());


                                CreateAd newAd = new CreateAd(date.getTime(), auth.getUid()+randomKey, title, address, thana, vacFrom, flatType, washroom, veranda, bedroom, floor, religion, genre, currentBill, waterBill, gasBill, otherCharge, Boolean.toString(isLiftChecked), Boolean.toString(isGeneratorChecked), Boolean.toString(isParkingChecked), Boolean.toString(isSecurityChecked), Boolean.toString(isGasChecked), Boolean.toString(isWifiChecked), description, rent, image_url1,image_url2,image_url3,image_url4,image_url5, longitude, latitude);

                                database.getReference().child("All_ad").child(auth.getUid()+randomKey).setValue(newAd)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(AdCreateActivity.this,"Ad created successfully!", Toast.LENGTH_SHORT).show());

                            }
                        });

                        finish();
                    });

                    markerPlacementDialog.setNegativeButton(no, (dialog, which) -> {
                        CreateAd newAd = new CreateAd(date.getTime(), auth.getUid()+randomKey, title, address, thana, vacFrom, flatType, washroom, veranda, bedroom, floor, religion, genre, currentBill, waterBill, gasBill, otherCharge, Boolean.toString(isLiftChecked), Boolean.toString(isGeneratorChecked), Boolean.toString(isParkingChecked), Boolean.toString(isSecurityChecked), Boolean.toString(isGasChecked), Boolean.toString(isWifiChecked), description, rent, image_url1,image_url2,image_url3,image_url4,image_url5, "no_long", "no_lat");

                        database.getReference().child("All_ad").child(auth.getUid()+randomKey).setValue(newAd)
                                .addOnSuccessListener(aVoid -> Toast.makeText(AdCreateActivity.this,"Ad created successfully!", Toast.LENGTH_SHORT).show());


                    });

                    markerPlacementDialog.show();
                } else{

                    Toast.makeText(this, "Please fill up the basic information first", Toast.LENGTH_LONG).show();
                }

            } else{

                Toast.makeText(this, "Please accept our terms and agreements", Toast.LENGTH_LONG).show();
            }

        });

        previousIntentData = getIntent();

        if(previousIntentData.getStringExtra("FROM_ACTIVITY").equals("AdDescriptionActivity")){

            editAd(Paper.book().read("language"));
        } else{

            ((ViewGroup) markerLayout.getParent()).removeView(markerLayout);
        }

        View header =  navigationView.getHeaderView(0);
        TextView headerProfileName = header.findViewById(R.id.profile_name_header);
        RoundedImageView headerProPic = header.findViewById(R.id.pro_pic_header);

        FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                headerProfileName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());

                User u = snapshot.getValue(User.class);
                assert u != null;
                Glide.with(getApplicationContext()).load(u.getProfileImage()).placeholder(R.drawable.user).into(headerProPic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.nav_ad);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //TODO need to add menu items
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.nav_out:

                FirebaseAuth.getInstance().signOut();

                SharedPreferences.Editor editor = SplashActivity.sharedPref.edit();
                editor.putBoolean("keepMeLoggedIn", false);
                editor.apply();

                Intent intentLogOut = new Intent(AdCreateActivity.this, LoginActivity.class);
                startActivity(intentLogOut);
                break;

            case R.id.nav_settings:

                Intent intentSettings = new Intent(AdCreateActivity.this, SettingsActivity.class);
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(intentSettings);
                break;
        }

        return true;
    }

    private void checkTheBoxes(){

        liftBox.setOnClickListener(v -> {

            if(isLiftChecked){

                liftBox.setSpeed(-1f);
                isLiftChecked = false;

            } else{

                liftBox.setSpeed(1f);
                isLiftChecked = true;
            }
            liftBox.playAnimation();
        });

        generatorBox.setOnClickListener(v -> {

            if(isGeneratorChecked){

                generatorBox.setSpeed(-1f);
                isGeneratorChecked = false;

            } else{

                generatorBox.setSpeed(1f);
                isGeneratorChecked = true;
            }
            generatorBox.playAnimation();
        });

        parkingBox.setOnClickListener(v -> {

            if(isParkingChecked){

                parkingBox.setSpeed(-1f);
                isParkingChecked = false;

            } else {

                parkingBox.setSpeed(1f);
                isParkingChecked = true;
            }
            parkingBox.playAnimation();
        });

        securityBox.setOnClickListener(v -> {

            if(isSecurityChecked){

                securityBox.setSpeed(-1f);
                isSecurityChecked = false;

            } else{

                securityBox.setSpeed(1f);
                isSecurityChecked = true;
            }
            securityBox.playAnimation();
        });

        gasBox.setOnClickListener(v -> {

            if(isGasChecked){

                gasBox.setSpeed(-1f);
                isGasChecked = false;

            } else{

                gasBox.setSpeed(1f);
                isGasChecked = true;
            }
            gasBox.playAnimation();
        });

        wifiBox.setOnClickListener(v -> {

            if(isWifiChecked){

                wifiBox.setSpeed(-1f);
                isWifiChecked = false;

            } else{

                wifiBox.setSpeed(1f);
                isWifiChecked = true;
            }
            wifiBox.playAnimation();
        });

        termBox.setOnClickListener(v -> {

            if(isTermChecked){

                termBox.setSpeed(-1f);
                isTermChecked = false;

            } else{

                termBox.setSpeed(1f);
                isTermChecked = true;
            }
            termBox.playAnimation();
        });
    }

    void setSpinnerList(String language){

        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();

        List<String> thanaCategories = new ArrayList<>();
        List<String> washBedVerandaCategories = new ArrayList<>();
        List<String> religionCategories = new ArrayList<>();
        List<String> flatTypeCategories = new ArrayList<>();
        List<String> floorCategories = new ArrayList<>();
        List<String> genreCategories = new ArrayList<>();

        thanaCategories.add(resources.getString(R.string.none));
        thanaCategories.add(resources.getString(R.string.adabar));
        thanaCategories.add(resources.getString(R.string.zimpur));
        thanaCategories.add(resources.getString(R.string.badda));
        thanaCategories.add(resources.getString(R.string.bangshal));
        thanaCategories.add(resources.getString(R.string.bimanbandar));
        thanaCategories.add(resources.getString(R.string.cantonment));
        thanaCategories.add(resources.getString(R.string.chowkbazar));
        thanaCategories.add(resources.getString(R.string.darus_salam));
        thanaCategories.add(resources.getString(R.string.demra));
        thanaCategories.add(resources.getString(R.string.dhanmondi));
        thanaCategories.add(resources.getString(R.string.gendaria));
        thanaCategories.add(resources.getString(R.string.gulshan));
        thanaCategories.add(resources.getString(R.string.hazaribagh));
        thanaCategories.add(resources.getString(R.string.kadamtali));
        thanaCategories.add(resources.getString(R.string.kafrul));
        thanaCategories.add(resources.getString(R.string.kalabagan));
        thanaCategories.add(resources.getString(R.string.kamrangirchar));
        thanaCategories.add(resources.getString(R.string.khilgaon));
        thanaCategories.add(resources.getString(R.string.khilkhet));
        thanaCategories.add(resources.getString(R.string.kotwali));
        thanaCategories.add(resources.getString(R.string.lalbagh));
        thanaCategories.add(resources.getString(R.string.mirpur_model));
        thanaCategories.add(resources.getString(R.string.mohammadpur));
        thanaCategories.add(resources.getString(R.string.motijheel));
        thanaCategories.add(resources.getString(R.string.new_market));
        thanaCategories.add(resources.getString(R.string.pallabi));
        thanaCategories.add(resources.getString(R.string.paltan));
        thanaCategories.add(resources.getString(R.string.panthapath));
        thanaCategories.add(resources.getString(R.string.ramna));
        thanaCategories.add(resources.getString(R.string.rampura));
        thanaCategories.add(resources.getString(R.string.sabujbagh));
        thanaCategories.add(resources.getString(R.string.shah_ali));
        thanaCategories.add(resources.getString(R.string.shahbag));
        thanaCategories.add(resources.getString(R.string.sher_e_bangla));
        thanaCategories.add(resources.getString(R.string.shyampur));
        thanaCategories.add(resources.getString(R.string.sutrapur));
        thanaCategories.add(resources.getString(R.string.tejgaon_industrial_area));
        thanaCategories.add(resources.getString(R.string.tejgaon));
        thanaCategories.add(resources.getString(R.string.turag));
        thanaCategories.add(resources.getString(R.string.uttar_khan));
        thanaCategories.add(resources.getString(R.string.uttara));
        thanaCategories.add(resources.getString(R.string.vatara));
        thanaCategories.add(resources.getString(R.string.wari));

        washBedVerandaCategories.add(resources.getString(R.string.none));
        washBedVerandaCategories.add(resources.getString(R.string.one));
        washBedVerandaCategories.add(resources.getString(R.string.two));
        washBedVerandaCategories.add(resources.getString(R.string.three));
        washBedVerandaCategories.add(resources.getString(R.string.four));
        washBedVerandaCategories.add(resources.getString(R.string.five));
        washBedVerandaCategories.add(resources.getString(R.string.five_plus));

        religionCategories.add(resources.getString(R.string.none));
        religionCategories.add(resources.getString(R.string.any));
        religionCategories.add(resources.getString(R.string.muslim));
        religionCategories.add(resources.getString(R.string.hindu));
        religionCategories.add(resources.getString(R.string.christian));
        religionCategories.add(resources.getString(R.string.buddhism));

        flatTypeCategories.add(resources.getString(R.string.none));
        flatTypeCategories.add(resources.getString(R.string.flat));
        flatTypeCategories.add(resources.getString(R.string.seat));
        flatTypeCategories.add(resources.getString(R.string.sublet));

        floorCategories.add(resources.getString(R.string.none));
        floorCategories.add(resources.getString(R.string.one));
        floorCategories.add(resources.getString(R.string.two));
        floorCategories.add(resources.getString(R.string.three));
        floorCategories.add(resources.getString(R.string.four));
        floorCategories.add(resources.getString(R.string.five));
        floorCategories.add(resources.getString(R.string.six));
        floorCategories.add(resources.getString(R.string.seven));
        floorCategories.add(resources.getString(R.string.eight));
        floorCategories.add(resources.getString(R.string.nine));
        floorCategories.add(resources.getString(R.string.ten));
        floorCategories.add(resources.getString(R.string.eleven));
        floorCategories.add(resources.getString(R.string.twelve));
        floorCategories.add(resources.getString(R.string.thirteen));
        floorCategories.add(resources.getString(R.string.fourteen));
        floorCategories.add(resources.getString(R.string.fifteen));
        floorCategories.add(resources.getString(R.string.sixteen));
        floorCategories.add(resources.getString(R.string.seventeen));
        floorCategories.add(resources.getString(R.string.eighteen));
        floorCategories.add(resources.getString(R.string.nineteen));
        floorCategories.add(resources.getString(R.string.twenty));
        floorCategories.add(resources.getString(R.string.twenty_one));
        floorCategories.add(resources.getString(R.string.twenty_two));
        floorCategories.add(resources.getString(R.string.twenty_three));
        floorCategories.add(resources.getString(R.string.twenty_four));
        floorCategories.add(resources.getString(R.string.twenty_five));

        genreCategories.add(resources.getString(R.string.none));
        genreCategories.add(resources.getString(R.string.family));
        genreCategories.add(resources.getString(R.string.small_family));
        genreCategories.add(resources.getString(R.string.only_student_male));
        genreCategories.add(resources.getString(R.string.only_student_female));
        genreCategories.add(resources.getString(R.string.bachelor));
        genreCategories.add(resources.getString(R.string.only_male));
        genreCategories.add(resources.getString(R.string.only_female));
        genreCategories.add(resources.getString(R.string.male_service_holder));
        genreCategories.add(resources.getString(R.string.female_service_holder));

        ArrayAdapter<String> thanaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, thanaCategories);
        ArrayAdapter<String> washBedVerandaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, washBedVerandaCategories);
        ArrayAdapter<String> religionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, religionCategories);
        ArrayAdapter<String> flatTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, flatTypeCategories);
        ArrayAdapter<String> floorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, floorCategories);
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genreCategories);

        thanaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        washBedVerandaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        religionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        flatTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        thanaSpinner.setAdapter(thanaAdapter);
        washSpinner.setAdapter(washBedVerandaAdapter);
        bedSpinner.setAdapter(washBedVerandaAdapter);
        verandaSpinner.setAdapter(washBedVerandaAdapter);
        religionSpinner.setAdapter(religionAdapter);
        flatTypeSpinner.setAdapter(flatTypeAdapter);
        floorSpinner.setAdapter(floorAdapter);
        genreSpinner.setAdapter(genreAdapter);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String format = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        vacantText.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void onClick(View v) {

        calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(AdCreateActivity.this, R.style.DialogTheme , this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @SuppressLint({"IntentReset", "QueryPermissionsNeeded"})
    private void selectImage(){

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AdCreateActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, (dialog, which) -> {

            if(items[which].equals("Camera")){

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager()) != null){

                    imageFile = null;

                    try {
                        imageFile = getImageFile();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(imageFile != null){

                        outputImageUri = FileProvider.getUriForFile(this, "com.example.android.fileProvider", imageFile);

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputImageUri);
                        startActivityForResult(intent, REQUEST_CAMERA);

                    }
                }

            } else if(items[which].equals("Gallery")){

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);


            } else if (items[which].equals("Cancel")) {

                dialog.dismiss();
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){

            if(requestCode == REQUEST_CAMERA){

                if(REQUEST_CAMERA == 1){

                    Bitmap image = BitmapFactory.decodeFile(currentImagePath);
                    img1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img1.setImageBitmap(image);

                    StorageReference reference = storage.getReference().child("Ad_pictures").child(Objects.requireNonNull(auth.getUid())).child(randomKey).child("Image_1");
                    reference.putFile(getImageUri(image, Bitmap.CompressFormat.JPEG, 100)).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(uri -> image_url1 = uri.toString());

                        }

                    });


                } else if(REQUEST_CAMERA == 2){

                    Bitmap image = BitmapFactory.decodeFile(currentImagePath);
                    img2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img2.setImageBitmap(image);

                    StorageReference reference = storage.getReference().child("Ad_pictures").child(Objects.requireNonNull(auth.getUid())).child(randomKey).child("Image_2");
                    reference.putFile(getImageUri(image, Bitmap.CompressFormat.JPEG, 100)).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(uri -> image_url2 = uri.toString());

                        }

                    });

                } else if(REQUEST_CAMERA == 3){

                    Bitmap image = BitmapFactory.decodeFile(currentImagePath);
                    img3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img3.setImageBitmap(image);

                    StorageReference reference = storage.getReference().child("Ad_pictures").child(Objects.requireNonNull(auth.getUid())).child(randomKey).child("Image_3");
                    reference.putFile(getImageUri(image, Bitmap.CompressFormat.JPEG, 100)).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(uri -> image_url3 = uri.toString());

                        }

                    });

                } else if(REQUEST_CAMERA == 4){

                    Bitmap image = BitmapFactory.decodeFile(currentImagePath);
                    img4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img4.setImageBitmap(image);

                    StorageReference reference = storage.getReference().child("Ad_pictures").child(Objects.requireNonNull(auth.getUid())).child(randomKey).child("Image_4");
                    reference.putFile(getImageUri(image, Bitmap.CompressFormat.JPEG, 100)).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(uri -> image_url4 = uri.toString());

                        }

                    });

                } else if(REQUEST_CAMERA == 5){

                    Bitmap image = BitmapFactory.decodeFile(currentImagePath);
                    img5.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img5.setImageBitmap(image);

                    StorageReference reference = storage.getReference().child("Ad_pictures").child(Objects.requireNonNull(auth.getUid())).child(randomKey).child("Image_5");
                    reference.putFile(getImageUri(image, Bitmap.CompressFormat.JPEG, 100)).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(uri -> image_url5 = uri.toString());

                        }

                    });

                }


            } else if(requestCode == SELECT_FILE){

                if(SELECT_FILE == 6){

                    assert data != null;
                    Uri selectedImageUri = data.getData();
                    StorageReference reference = storage.getReference().child("Ad_pictures").child(Objects.requireNonNull(auth.getUid())).child(randomKey).child("Image_1");
                    reference.putFile(selectedImageUri).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(uri -> image_url1 = uri.toString());

                        }

                    });

                    img1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img1.setImageURI(selectedImageUri);

                } else if(SELECT_FILE == 7){

                    assert data != null;
                    Uri selectedImageUri = data.getData();
                    StorageReference reference = storage.getReference().child("Ad_pictures").child(Objects.requireNonNull(auth.getUid())).child(randomKey).child("Image_2");
                    reference.putFile(selectedImageUri).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(uri -> image_url2 = uri.toString());

                        }

                    });
                    img2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img2.setImageURI(selectedImageUri);

                } else if(SELECT_FILE == 8){

                    assert data != null;
                    Uri selectedImageUri = data.getData();
                    StorageReference reference = storage.getReference().child("Ad_pictures").child(Objects.requireNonNull(auth.getUid())).child(randomKey).child("Image_3");
                    reference.putFile(selectedImageUri).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(uri -> image_url3 = uri.toString());

                        }

                    });
                    img3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img3.setImageURI(selectedImageUri);

                } else if(SELECT_FILE == 9){

                    assert data != null;
                    Uri selectedImageUri = data.getData();
                    StorageReference reference = storage.getReference().child("Ad_pictures").child(Objects.requireNonNull(auth.getUid())).child(randomKey).child("Image_4");
                    reference.putFile(selectedImageUri).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(uri -> image_url4 = uri.toString());

                        }

                    });
                    img4.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img4.setImageURI(selectedImageUri);

                } else if(SELECT_FILE == 10){

                    assert data != null;
                    Uri selectedImageUri = data.getData();
                    StorageReference reference = storage.getReference().child("Ad_pictures").child(Objects.requireNonNull(auth.getUid())).child(randomKey).child("Image_5");
                    reference.putFile(selectedImageUri).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(uri -> image_url5 = uri.toString());

                        }

                    });
                    img5.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img5.setImageURI(selectedImageUri);
                }

            }

        }

    }

    private File getImageFile()throws IOException{

        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(timeStamp,".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }



    public Uri getImageUri(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), src, "Basha Pabo Koi", null);

        return Uri.parse(path);

    }

    @SuppressLint("SetTextI18n")
    void editAd(String language){

        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();

        titleText.setText(resources.getString(R.string.edit_ad));

        titleTextBox.setText(previousIntentData.getStringExtra("title"));
        rentTextBox.setText(previousIntentData.getStringExtra("rent"));
        addressTextBox.setText(previousIntentData.getStringExtra("address"));

        for(int i= 0; i < thanaSpinner.getAdapter().getCount(); i++)
        {
            if(thanaSpinner.getAdapter().getItem(i).toString().contains(previousIntentData.getStringExtra("thana")))
            {
                thanaSpinner.setSelection(i);
            }
        }

        vacantText.setText(previousIntentData.getStringExtra("vacantFrom"));

        for(int i= 0; i < flatTypeSpinner.getAdapter().getCount(); i++)
        {
            if(flatTypeSpinner.getAdapter().getItem(i).toString().contains(previousIntentData.getStringExtra("flatType")))
            {
                flatTypeSpinner.setSelection(i);
            }
        }

        for(int i= 0; i < washSpinner.getAdapter().getCount(); i++)
        {
            if(washSpinner.getAdapter().getItem(i).toString().contains(previousIntentData.getStringExtra("washroom")))
            {
                washSpinner.setSelection(i);
            }
        }

        for(int i= 0; i < verandaSpinner.getAdapter().getCount(); i++)
        {
            if(verandaSpinner.getAdapter().getItem(i).toString().contains(previousIntentData.getStringExtra("veranda")))
            {
                verandaSpinner.setSelection(i);
            }
        }

        for(int i= 0; i < bedSpinner.getAdapter().getCount(); i++)
        {
            if(bedSpinner.getAdapter().getItem(i).toString().contains(previousIntentData.getStringExtra("bedroom")))
            {
                bedSpinner.setSelection(i);
            }
        }

        for(int i= 0; i < floorSpinner.getAdapter().getCount(); i++)
        {
            if(floorSpinner.getAdapter().getItem(i).toString().contains(previousIntentData.getStringExtra("floor")))
            {
                floorSpinner.setSelection(i);
            }
        }

        for(int i= 0; i < religionSpinner.getAdapter().getCount(); i++)
        {
            if(religionSpinner.getAdapter().getItem(i).toString().contains(previousIntentData.getStringExtra("religion")))
            {
                religionSpinner.setSelection(i);
            }
        }

        for(int i= 0; i < genreSpinner.getAdapter().getCount(); i++)
        {
            if(genreSpinner.getAdapter().getItem(i).toString().contains(previousIntentData.getStringExtra("genre")))
            {
                genreSpinner.setSelection(i);
            }
        }

        currentBillTextBox.setText(previousIntentData.getStringExtra("electricityBill"));
        waterBillTextBox.setText(previousIntentData.getStringExtra("waterBill"));
        gasBillTextBox.setText(previousIntentData.getStringExtra("gasBill"));
        otherChargeTextBox.setText(previousIntentData.getStringExtra("serviceCharge"));

        isLiftChecked = Boolean.parseBoolean(previousIntentData.getStringExtra("lift"));
        isGeneratorChecked = Boolean.parseBoolean(previousIntentData.getStringExtra("generator"));
        isParkingChecked = Boolean.parseBoolean(previousIntentData.getStringExtra("parking"));
        isSecurityChecked = Boolean.parseBoolean(previousIntentData.getStringExtra("security"));
        isGasChecked = Boolean.parseBoolean(previousIntentData.getStringExtra("gas"));
        isWifiChecked = Boolean.parseBoolean(previousIntentData.getStringExtra("wifi"));

        descriptionTextBox.setText(previousIntentData.getStringExtra("details"));

        if(!previousIntentData.getStringExtra("imageUri1").equals("no_image")){
            image_url1 = previousIntentData.getStringExtra("imageUri1");

            Picasso.get().load(previousIntentData.getStringExtra("imageUri1")).resize(300, 200).centerCrop().placeholder(R.drawable.ic_add_image).into(img1);
        }

        if(!previousIntentData.getStringExtra("imageUri2").equals("no_image")){
            image_url2 = previousIntentData.getStringExtra("imageUri2");

            Picasso.get().load(previousIntentData.getStringExtra("imageUri2")).resize(300, 200).centerCrop().placeholder(R.drawable.ic_add_image).into(img2);
        }

        if(!previousIntentData.getStringExtra("imageUri3").equals("no_image")){
            image_url3 = previousIntentData.getStringExtra("imageUri3");

            Picasso.get().load(previousIntentData.getStringExtra("imageUri3")).resize(300, 200).centerCrop().placeholder(R.drawable.ic_add_image).into(img3);
        }

        if(!previousIntentData.getStringExtra("imageUri4").equals("no_image")){
            image_url4 = previousIntentData.getStringExtra("imageUri4");

            Picasso.get().load(previousIntentData.getStringExtra("imageUri4")).resize(300, 200).centerCrop().placeholder(R.drawable.ic_add_image).into(img4);
        }

        if(!previousIntentData.getStringExtra("imageUri5").equals("no_image")){
            image_url5 = previousIntentData.getStringExtra("imageUri5");

            Picasso.get().load(previousIntentData.getStringExtra("imageUri5")).resize(300, 200).centerCrop().placeholder(R.drawable.ic_add_image).into(img5);
        }

        if(isLiftChecked){

            liftBox.setProgress(1f);
        }

        if(isGeneratorChecked){

            generatorBox.setProgress(1f);
        }

        if(isParkingChecked){

            parkingBox.setProgress(1f);
        }

        if(isSecurityChecked){

            securityBox.setProgress(1f);
        }

        if(isGasChecked){

            gasBox.setProgress(1f);
        }

        if(isWifiChecked){

            wifiBox.setProgress(1f);
        }

        if(!previousIntentData.getStringExtra("lat").matches("no_lat") && !previousIntentData.getStringExtra("long").matches("no_long")){

            markerAddedBox.setProgress(0.5f);
        }

        createButton.setText(resources.getString(R.string.update));

        createButton.setOnClickListener(v -> {

            String title = titleTextBox.getText().toString();
            String address = addressTextBox.getText().toString();
            String currentBill = currentBillTextBox.getText().toString();
            String waterBill = waterBillTextBox.getText().toString();
            String gasBill = gasBillTextBox.getText().toString();
            String otherCharge = otherChargeTextBox.getText().toString();
            String description = descriptionTextBox.getText().toString();
            String rent = rentTextBox.getText().toString();

            String vacFrom = vacantText.getText().toString();
            String thana = thanaSpinner.getSelectedItem().toString();

            //Log.d("position", String.valueOf(thanaSpinner.getSelectedItemPosition()));
            //Log.d("position", thanaSpinner.getItemAtPosition(thanaSpinner.getSelectedItemPosition()).toString());

            String washroom = washSpinner.getSelectedItem().toString();
            String bedroom = bedSpinner.getSelectedItem().toString();
            String religion = religionSpinner.getSelectedItem().toString();
            String flatType = flatTypeSpinner.getSelectedItem().toString();
            String veranda = verandaSpinner.getSelectedItem().toString();
            String floor = floorSpinner.getSelectedItem().toString();
            String genre = genreSpinner.getSelectedItem().toString();


            if (isTermChecked) {

                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(rent) && !TextUtils.isEmpty(vacFrom) && !thanaSpinner.getSelectedItem().toString().matches("-") && !washSpinner.getSelectedItem().toString().matches("-") && !bedSpinner.getSelectedItem().toString().matches("-") && !religionSpinner.getSelectedItem().toString().matches("-") && !flatTypeSpinner.getSelectedItem().toString().matches("-") && !verandaSpinner.getSelectedItem().toString().matches("-") && !floorSpinner.getSelectedItem().toString().matches("-") && !genreSpinner.getSelectedItem().toString().matches("-")){

                    AlertDialog.Builder markerPlacementDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                    markerPlacementDialog.setTitle(alertTitle);
                    markerPlacementDialog.setMessage(alertMsg);

                    markerPlacementDialog.setPositiveButton(yes, (dialog, which) -> {

                        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }


                        @SuppressWarnings("rawtypes")
                        Task location = fusedLocationProviderClient.getLastLocation();

                        location.addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                Location currentLocation = (Location) task.getResult();
                                longitude = Double.toString(currentLocation.getLongitude());
                                latitude = Double.toString(currentLocation.getLatitude());


                                CreateAd updatedAd = new CreateAd(date.getTime(), previousIntentData.getStringExtra("ownerKey"), titleTextBox.getText().toString(), addressTextBox.getText().toString(), thanaSpinner.getSelectedItem().toString(), vacantText.getText().toString(), flatTypeSpinner.getSelectedItem().toString(), washSpinner.getSelectedItem().toString(), verandaSpinner.getSelectedItem().toString(), bedSpinner.getSelectedItem().toString(), floorSpinner.getSelectedItem().toString(), religionSpinner.getSelectedItem().toString(), genreSpinner.getSelectedItem().toString(), currentBillTextBox.getText().toString(), waterBillTextBox.getText().toString(), gasBillTextBox.getText().toString(), otherChargeTextBox.getText().toString(), Boolean.toString(isLiftChecked), Boolean.toString(isGeneratorChecked), Boolean.toString(isParkingChecked), Boolean.toString(isSecurityChecked), Boolean.toString(isGasChecked), Boolean.toString(isWifiChecked), descriptionTextBox.getText().toString(), rentTextBox.getText().toString(),
                                        image_url1,
                                        image_url2,
                                        image_url3,
                                        image_url4,
                                        image_url5,
                                        longitude, latitude);

                                database.getReference().child("All_ad").child(previousIntentData.getStringExtra("ownerKey")).setValue(updatedAd).addOnSuccessListener(aVoid -> Toast.makeText(AdCreateActivity.this, "Ad updated successfully!", Toast.LENGTH_LONG).show());

                            }
                        });

                        finish();
                    });

                    markerPlacementDialog.setNegativeButton(no, (dialog, which) -> {
                        CreateAd newAd = new CreateAd(date.getTime(), auth.getUid()+randomKey, title, address, thana, vacFrom, flatType, washroom, veranda, bedroom, floor, religion, genre, currentBill, waterBill, gasBill, otherCharge, Boolean.toString(isLiftChecked), Boolean.toString(isGeneratorChecked), Boolean.toString(isParkingChecked), Boolean.toString(isSecurityChecked), Boolean.toString(isGasChecked), Boolean.toString(isWifiChecked), description, rent, image_url1,image_url2,image_url3,image_url4,image_url5, "no_long", "no_lat");

                        database.getReference().child("All_ad").child(auth.getUid()+randomKey).setValue(newAd)
                                .addOnSuccessListener(aVoid -> Toast.makeText(AdCreateActivity.this,"Ad created successfully!", Toast.LENGTH_SHORT).show());

                    });

                    markerPlacementDialog.show();
                } else{

                    Toast.makeText(this, "Please fill up the basic information first", Toast.LENGTH_LONG).show();
                }

            } else{

                Toast.makeText(this, "Please accept our terms and agreements", Toast.LENGTH_LONG).show();
            }


        });
    }

    private void updateView(String language) {

        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();

        createButton.setText(resources.getString(R.string.create));

        langState.setText(resources.getString(R.string.lang));

        alertMsg = resources.getString(R.string.we_need_location);
        alertTitle = resources.getString(R.string.are_you);
        yes = resources.getString(R.string.yes);
        no = resources.getString(R.string.no);

        titleText.setText(resources.getString(R.string.create_an_ad));
        title.setText(resources.getString(R.string.title));
        basicInfo.setText(resources.getString(R.string.basic_information));
        rent.setText(resources.getString(R.string.rent));
        addressTitle.setText(resources.getString(R.string.address));
        thanaTitle.setText(resources.getString(R.string.thana));
        vacFromTitle.setText(resources.getString(R.string.vacant_from));
        flatTypeTitle.setText(resources.getString(R.string.flat_type));
        washTitle.setText(resources.getString(R.string.washrooms));
        verandaTitle.setText(resources.getString(R.string.verandas));
        bedTitle.setText(resources.getString(R.string.bedrooms));
        floorTitle.setText(resources.getString(R.string.floor));
        religionTitle.setText(resources.getString(R.string.religion));
        genreTitle.setText(resources.getString(R.string.genre));
        otherServicesTitle.setText(resources.getString(R.string.other_services));
        currentBillTitle.setText(resources.getString(R.string.current_bill));
        waterBillTitle.setText(resources.getString(R.string.water_bill));
        gasBillTitle.setText(resources.getString(R.string.gas_bill));
        otherTitle.setText(resources.getString(R.string.other_charges));
        lift.setText(resources.getString(R.string.lift));
        generator.setText(resources.getString(R.string.generator));
        parking.setText(resources.getString(R.string.parking));
        security.setText(resources.getString(R.string.security));
        gas.setText(resources.getString(R.string.gas));
        wifi.setText(resources.getString(R.string.wifi));
        desTitle.setText(resources.getString(R.string.description_of_your_ad));
        addPicTitle.setText(resources.getString(R.string.add_photos));
        acceptTitle.setText(resources.getString(R.string.accept_our_terms_and_agreements));
        markerTitle.setText(resources.getString(R.string.marker_added));

        if(language.matches("bn")){

            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.main_menu_bn);
        } else{

            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.main_menu);
        }
    }
}