package com.example.approtest.fragments;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.Loader;

import android.content.Intent;


import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;

import android.util.Base64;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.approtest.R;
import com.example.approtest.models.ClusterMarker;
import com.example.approtest.models.Event;
import com.example.approtest.models.User;
import com.example.approtest.utilities.CustomClusterRenderer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {
    private boolean markerMoveEnabled = false;
    Boolean isAdmin;
    private static int RESULT_LOAD_IMAGE = 1;

    public static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =1;

    private ViewGroup layoutContainer; // Container for the layout to be displayed
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    HashMap<String, Event> events;
    FirebaseFirestore db;
    LatLng place;

    GoogleMap map;

    private ClusterManager<ClusterMarker> mClusterManager;
    private CustomClusterRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    User current;






    Bitmap image;
    private ClusterManager clusterManager;


    ImageView eventImage;
    protected HashMap<String, Marker> markers;
    public MapFragment(HashMap<String,Event> events, User current)
    {
        this.events = events;
        this.current = current;
        Log.d("onsucc", current.getFullName()+current.getEmail()+current.getToken());
        place = new LatLng(0,0);
    }

    private void updateCurrent()
    {

        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                current.setUser(user);
            }
        });
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            // updateCurrent();
            updateEvents();
            markers = new HashMap<String,Marker>();
            LatLng sydney = new LatLng(31, 35);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            MarkerOptions markerOptions = new MarkerOptions().position(sydney).title("current");
            Marker tempMarker = googleMap.addMarker(markerOptions);
            tempMarker.setVisible(false);

            addMapMarkers();

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng)
                {
                    if (markerMoveEnabled) {
                        place =new LatLng(latLng.latitude,latLng.longitude);
                        tempMarker.setVisible(true);
                        tempMarker.setPosition(latLng);
                        showDialog(tempMarker,false);
                    }
                    else
                    {
                        tempMarker.setVisible(false);
                    }
                }
            });

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String mName = marker.getTitle();
                    View rootView = getView(); // Get the root view of your activity/fragment

                    // Check if rootView is null before proceeding
                    if (rootView == null) {
                        return false;
                    }
                    // Create and show a Snackbar with the marker title
                    Snackbar snackbar = Snackbar.make(rootView, mName, Snackbar.LENGTH_SHORT);

                    // Add an action to the Snackbar for the click event
                    snackbar.setAction("Show Details", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create and show a custom dialog here
                            showDialogWithMarkerTitle(mName);
                        }
                    });

                    snackbar.show();

                    // Return true to indicate the event has been consumed
                    return true;
                }

                // Method to show a custom dialog with the marker title
            });
        }
    };


    private void addMapMarkers(){

        if(map != null){

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), map);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new CustomClusterRenderer(
                        getActivity(),
                        map,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            for(Event event: events.values()){
                try{
                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(event.latitude, event.longitude),
                            event.eventName,
                            event.date,
                            event.encodedImage
                    );
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);

                }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                }

            }
            mClusterManager.cluster();
        }
    }


    private void addMapMarker(Event event){

        if(map != null){

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), map);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new CustomClusterRenderer(
                        getActivity(),
                        map,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            try{
                ClusterMarker newClusterMarker = new ClusterMarker(
                        new LatLng(event.latitude, event.longitude),
                        event.eventName,
                        event.date,
                        event.encodedImage
                );
                mClusterManager.addItem(newClusterMarker);
                mClusterMarkers.add(newClusterMarker);

            }catch (NullPointerException e){
                Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
            }
            mClusterManager.cluster();
        }
    }
//    private void buildDialog (String markerTitle) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(markerTitle);
//        builder.setMessage(markerTitle);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        // build negativeButton
//
//        return;
//    }

    private boolean checkParticipatingFromDB(String eveName,User curr){
        DocumentReference documentReference = db.collection("events").document(eveName);
        final boolean flag[] = {false};
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Event tempEve = documentSnapshot.toObject(Event.class);
                Log.d("onsucc", "succ");
                flag[0] = tempEve.hasUser(current);
            }
        });
        return flag[0];
    }

    private boolean checkParticipating(String eveName,User curr){
        return events.get(eveName).hasUser(curr);
    }

    /*private void constructNegativeButton(boolean participating){
        if (participating){
            builder.setNegativeButton("Unsubscribe From Event", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Delete event
                    dialog.dismiss();
                }
            });
        }
    } */



    private void showDialogWithMarkerTitle(String markerTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(markerTitle);
        builder.setMessage("Date is: " + events.get(markerTitle).getDate()+"\n");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Log.d("bool check", String.valueOf(checkParticipating(markerTitle,current))); // check
        if (checkParticipating(markerTitle,current)) {
            builder.setNegativeButton("Unsubscribe From Event", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    events.get(markerTitle).participants.remove(current.getToken());
                    update(events.get(markerTitle));
                    dialog.dismiss();
                }
            });
        }
        else{
            builder.setNegativeButton("Subscribe To Event", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    events.get(markerTitle).addUser(current);
                    update(events.get(markerTitle));
                    dialog.dismiss();
                }
            });
        }
        // Create the AlertDialog instance and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        FloatingActionButton add_button_floating=rootView.findViewById(R.id.add_button_floating);
        FloatingActionButton update_button_floating=rootView.findViewById(R.id.add_restart_button);
        add_button_floating.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        eventImage = new ImageView(getActivity());
        DocumentReference userDoc = db.collection("users").document(currentUser.getUid());
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {

                        isAdmin = document.getBoolean("admin");

                        if (isAdmin)
                            add_button_floating.setVisibility(View.VISIBLE);


                    } else {
                    }
                } else {
                }
            }
        });

        update_button_floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvents();
                //Intent intent = new Intent(getActivity(), NewEventActivity.class);
                //startActivity(intent);
            }
        });


        add_button_floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerMoveEnabled=!markerMoveEnabled;
                //Intent intent = new Intent(getActivity(), NewEventActivity.class);
                //startActivity(intent);
            }
        });
        return rootView;

    }

    /*rivate BitmapDescriptor getCustomMarkerIcon(int color) {
        Drawable background = ContextCompat.getDrawable(getContext(), R.drawable.ic_custom_marker);
        background.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(((BitmapDrawable) background).getBitmap());
        return bitmapDescriptor;
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    public void updateEvents()
    {
        int i = 1;
        events.clear();
        map.clear();
        CollectionReference colRef = db.collection("events");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Event event = new Event(document.toObject(Event.class));
                        Log.d("pozenim", String.valueOf(document.toObject(Event.class).getEncodedImage()));
                        events.put(event.getEventName(),event);
                        LatLng pos = new LatLng(event.getLatitude(), event.getLongitude());
                        String name = event.getEventName();
                        String im = document.toObject(Event.class).getEncodedImage();
                        event.setEncodedImage(im);
                        MarkerOptions markerOptions = new MarkerOptions().position(pos).title(name);
                        markerOptions.icon(createDescriptor(String.valueOf(im)));
                        map.addMarker(markerOptions);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });
    }



    public BitmapDescriptor createDescriptor(int i)
    {
        ImageView view = (ImageView)getView().findViewById(R.id.markerImage);
        if (i == 1){
            view.setImageResource(R.drawable.blueskys);}
        else
        {
            view.setImageResource(R.drawable.def_group_img);
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public BitmapDescriptor createDescriptor(String im)
    {
        Bitmap bitmap = Bitmap.createBitmap(BitMapConvert(im));
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888 , true);
        bitmap = getResizedBitmap(bitmap,100,100);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);


        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void update(Event event)
    {
        DocumentReference documentReference = db.collection("events").document(event.eventName);
        documentReference.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Log.d("peepeepoopoo", "here2");
                updateEvents();
            }
        });
    }


    private void saveEvent(String name, String date, LatLng place)
    {
        Log.d("shitpit", current.getToken());
        double latitude = place.latitude;
        double longitude = place.longitude;
        String encodedImage = StringConvert(image);
        Event event = new Event(name,date,latitude,longitude,encodedImage);
        event.addUser(current);
        Log.d("shitpit", event.getEncodedImage());
        DocumentReference documentReference = db.collection("events").document(name);
        documentReference.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("peepeepoopoo", "here2");
                updateEvents();
            }
        });
    }


    private void showDialog(Marker tempMarker, boolean fieldError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Event");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText eventN = new EditText(getActivity());
        eventN.setHint("Event Name");
        layout.addView(eventN);

        /* old text form:
        final EditText eventD = new EditText(getActivity());
        eventD.setHint("Date");
        layout.addView(eventD);
         */

        TextView errorMessage = new TextView(getActivity());
        errorMessage.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
        errorMessage.setVisibility(View.INVISIBLE);
        layout.addView(errorMessage);

        eventImage = new ImageView(getActivity());
        eventImage.setImageResource(R.drawable.def_group_img);

        eventImage.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        layout.addView(eventImage);


        image = ((BitmapDrawable)eventImage.getDrawable()).getBitmap();

//        layout.addView(eventImage);

        Button attachButton = new Button(getActivity());
        attachButton.setText("Attach Picture");
        attachButton.setLayoutParams(new LinearLayout.LayoutParams(100, 50));
        layout.addView(attachButton);

        attachButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                if (ContextCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request the permission
                    Log.d("please","please");

                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    Log.d("aaa","aaa");
                    requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    // Permission is already granted, proceed with accessing the internal storage
                }



                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });



        final DatePicker datePicker = new DatePicker(getActivity());
        layout.addView(datePicker);
        builder.setView(layout);

        if (fieldError){
            errorMessage.setText("Please make sure inputs are not empty!");
            errorMessage.setVisibility(View.VISIBLE);
        }

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = String.valueOf(eventN.getText());
                //String date = String.valueOf(eventD.getText());
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1; // Month starts from 0
                int year = datePicker.getYear();

                // Convert the selected date to the desired format (dd/MM/yyyy)
                String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year);

                if (name.isEmpty() || formattedDate.isEmpty()){
                    Log.d("error message should occur!", "error message");
                    showDialog(tempMarker, true);

                }
                else {
                    saveEvent(name, formattedDate, place);
                    tempMarker.setVisible(false);
                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing or perform any required action
                tempMarker.setVisible(false);
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE  && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.d("pohashit", picturePath);
            eventImage.setMaxHeight(10);
            eventImage.setMaxWidth(10);
            eventImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            image = BitmapFactory.decodeFile(picturePath);
            image = getResizedBitmap(image,100,100);
            image = getCroppedBitmap(image);
            eventImage.setImageBitmap(image);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated(): savedInstanceState = "
                + savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }



    public void onLoadFinished(Loader<Void> id, Void result) {
        Log.d(TAG, "onLoadFinished(): id=" + id);
    }

    public void onLoaderReset(Loader<Void> loader) {
        Log.d(TAG, "onLoaderReset(): id=" + loader.getId());
    }

    public static Bitmap BitMapConvert(String base64Str)
    {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String StringConvert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

}
