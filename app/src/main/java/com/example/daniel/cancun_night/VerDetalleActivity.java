package com.example.daniel.cancun_night;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.synnapps.carouselview.ViewListener;

import static android.R.attr.name;
import static com.example.daniel.cancun_night.R.id.descripcion;

public class VerDetalleActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback {


        private FirebaseAuth firebaseAuth;
        private FirebaseAuth.AuthStateListener firebaseAuthListener;
        private GoogleApiClient googleApiClient;
        private FirebaseDatabase database;
        private String UID_user;

        TextView nombreProducto, precioProducto, descProducto;
        ImageView imgProducto;
        Button add_to_cart;
        private String[] arrayURL;

    String  name,img,img_1,img_2,img_3,imgnew, descripcion;
    Double  x_coor, y_coor;

    int[] sampleImages = {R.mipmap.ic_load,R.mipmap.ic_loading,R.mipmap.ic_loading};
    String[] sampleTitles = {"Orange","Orange","Orange"};


    CarouselView carouselView;
    CarouselView customCarouselView;
    TextView carouselLabel;
    TextView customCarouselLabel;
        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_detalle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            getSupportFragmentManager().findFragmentById(R.id.map);

       carouselView = (CarouselView) findViewById(R.id.carouselView);
        customCarouselView = (CarouselView) findViewById(R.id.customCarouselView);
        carouselLabel = (TextView) findViewById(R.id.carouselLabel);
        customCarouselLabel = (TextView) findViewById(R.id.customCarouselLabel);


        carouselView.setPageCount(sampleImages.length);

        customCarouselView.setPageCount(sampleImages.length);
        customCarouselView.setSlideInterval(4000);

        carouselView.setImageListener(imageListener);
        customCarouselView.setViewListener(viewListener);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Firebase
        //database = FirebaseDatabase.getInstance();

        //Firebase auth

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setUserData(user);
                } else {
                    goLogInScreen();
                }
            }
        };

        nombreProducto = (TextView) findViewById(R.id.TitleName);
        imgProducto = (ImageView) findViewById(R.id.img);
        descProducto = (TextView) findViewById(R.id.descripcion);


            Bundle extras = getIntent().getExtras();
            img = extras.getString("img");
            img_1 = extras.getString("img_1");
            img_2 = extras.getString("img_2");
            img_3 = extras.getString("img_3");


            //  Toast.makeText(getApplicationContext(), getUrlFirebase().length, Toast.LENGTH_SHORT).show();

            name = extras.getString("name");
            descripcion = extras.getString("descripcion");

            x_coor = extras.getDouble("x_coor");
            y_coor = extras.getDouble("y_coor");

            Glide.with(this).load(img).into(imgProducto);
            nombreProducto.setText(name);
            descProducto.setText(descripcion);


            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


    }


    private void setUserData(FirebaseUser user) {

        UID_user = user.getUid();
        //descProducto.setText(UID_user);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng cancun = new LatLng(x_coor,y_coor);
        googleMap.addMarker(new MarkerOptions().position(cancun)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(cancun));
        /*Uri gmmIntentUri = Uri.parse("google.navigation:q=21.0833,-86.85");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);*/
    }


    private void goLogInScreen() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthListener != null){

            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }



    public String[] getUrlFirebase()
    {
        //String[] myList = {img_1, img_2, img_3, 3.5};
        String[] xs = new String [] {img_1,img_2,img_3};

        return xs;

    }
    //agarramos las propiedades de un .xml para el estilo del carrusel y el efecto de las pocisiones
    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {

            View customView = getLayoutInflater().inflate(R.layout.view_custom, null);

            TextView labelTextView = (TextView) customView.findViewById(R.id.labelTextView);
            ImageView fruitImageView = (ImageView) customView.findViewById(R.id.fruitImageView);

            fruitImageView.setImageResource(sampleImages[position]);
            labelTextView.setText(sampleTitles[position]);

            carouselView.setIndicatorGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);

            return customView;
        }
    };

    //Con esta funcion implementamos las imagenes HTTP:/ con acceso a internet y las convertimos a Picasso y los plasmamos por cada position
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {

            String[] strArr = getUrlFirebase();

            Picasso.with(getApplicationContext()).load((strArr[position])).placeholder(sampleImages[0]).error(sampleImages[2]).fit().centerCrop().into(imageView);


            //imageView.setImageResource(sampleImages[position]);
        }
    };



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }










}
