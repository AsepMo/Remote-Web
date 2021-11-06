package com.ftp.client.engine.app.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ftp.client.R;
import com.ftp.client.application.WebListActivity;
import com.ftp.client.engine.app.config.Constants;
import com.ftp.client.engine.app.settings.Settings;
import com.ftp.client.engine.widget.MaterialEditText;
import com.ftp.client.engine.widget.SwitchMultiButton;
import com.ftp.client.engine.widget.CircleImageView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileDescriptor;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class Register extends Fragment {
    private static final String TAG = Register.class.getSimpleName();

    public Register() {
        // Required empty public constructor
    }
    // A request code's purpose is to match the result of a "startActivityForResult" with
    // the type of the original request.  Choose any value.
    private static final int READ_REQUEST_CODE = 1337;

    private CircleImageView imageView;
    private String name, email, password, age,site_address, image="na";
    private Integer gender;
    private MaterialEditText nameET;
    private MaterialEditText emailET;
    private MaterialEditText passwordET;
    private MaterialEditText ageET;
    private MaterialEditText siteAddressET;
    private SwitchMultiButton genderSwitch;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), WebListActivity.class));
                }
            });

        imageView = getView().findViewById(R.id.registerProfileImage);
        
        nameET = getView().findViewById(R.id.registerName);
        emailET = getView().findViewById(R.id.registerEmail);
        passwordET = getView().findViewById(R.id.registerPassword);
        ageET = getView().findViewById(R.id.registerAge);
        siteAddressET = getView().findViewById(R.id.registerSiteAddress);

        genderSwitch = getView().findViewById(R.id.registerGender);

        genderSwitch.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
                @Override
                public void onSwitch(int position, String tabText) {
                    gender = position;                  
                }
            });

        imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchImagePicker();
                }
            });

        getView().findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name = nameET.getText().toString();
                    email = emailET.getText().toString();
                    password = passwordET.getText().toString();
                    age = ageET.getText().toString();
                    site_address = siteAddressET.getText().toString();
                    gender = genderSwitch.getSelectedTab();
                    if(validate())
                    {
                        if(new Settings(getContext()).saveDetals(name, email, password, age, site_address, gender, image))
                        {
                            new Settings(getContext()).setLoginStatus(true);
                            Toast.makeText(getContext(),"Welcome "+name,Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), WebListActivity.class));
                            getActivity().finish();
                        }

                    }
                }
            });

    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void launchImagePicker() {

        // BEGIN_INCLUDE (use_open_document_intent)
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a file (as opposed to a list
        // of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers, it would be
        // "*/*".
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
        // END_INCLUDE (use_open_document_intent)
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.i(TAG, "Received an \"Activity Result\"");
        // BEGIN_INCLUDE (parse_open_document_response)
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                image=  uri.toString();
                // showImage(uri);
                // BEGIN_INCLUDE (show_image)
                // Loading the image is going to require some sort of I/O, which must occur off the UI
                // thread.  Changing the ImageView to display the image must occur ON the UI thread.
                // The easiest way to divide up this labor is with an AsyncTask.  The doInBackground
                // method will run in a separate thread, but onPostExecute will run in the main
                // UI thread.
                AsyncTask<Uri, Void, Bitmap> imageLoadAsyncTask = new AsyncTask<Uri, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Uri... uris) {
                        dumpImageMetaData(uris[0]);
                        return getBitmapFromUri(uris[0]);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                };
                imageLoadAsyncTask.execute(uri);
                // END_INCLUDE (show_image)

            }
            // END_INCLUDE (parse_open_document_response)
        }
    }

    public boolean validate()
    {
        try{

            if(name.equals("") || email.equals("") || password.equals("") || age.equals(""))
            {
                Toast.makeText(getContext(),"All fields are mandatory",Toast.LENGTH_SHORT).show();
                return false;
            }

            if(image.equalsIgnoreCase("na"))
            {
                Toast.makeText(getContext(),"Please select profile image",Toast.LENGTH_SHORT).show();
                return false;
            }

            Pattern pattern;
            Matcher matcher;

            pattern = Pattern.compile(Constants.EMAIL_PATTERN);
            matcher = pattern.matcher(email);
            if(!matcher.matches())
            {
                emailET.setError("Invalid email");
                return false;
            }

            if(password.length()<8)
            {
                passwordET.setError("Atleast 8 characters required");
                return false;
            }

            pattern = Pattern.compile(Constants.PASSWORD_PATTERN);
            matcher = pattern.matcher(password);
            if(!matcher.matches())
            {
                passwordET.setError("Password should contain uppercase, lowercase, special character and digit");
                return false;
            }

            int a = Integer.parseInt(age);
            if(a<=0 || a>120)
            {
                ageET.setError("Invalid age");
                return false;
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(),"Some value is not as per our expectation",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    /** Create a Bitmap from the URI for that image and return it.
     *
     * @param uri the Uri for the image to return.
     */
    private Bitmap getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }


    /**
     * Grabs metadata for a document specified by URI, logs it to the screen.
     *
     * @param uri The uri for the document whose metadata should be printed.
     */
    public void dumpImageMetaData(Uri uri) {
        // BEGIN_INCLUDE (dump_metadata)

        // The query, since it only applies to a single document, will only return one row.
        // no need to filter, sort, or select fields, since we want all fields for one
        // document.
        Cursor cursor = getActivity().getContentResolver()
            .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is provider-specific, and
                // might not necessarily be the file name.
                String displayName = cursor.getString(
                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an int can't be
                // null in java, the behavior is implementation-specific, which is just a fancy
                // term for "unpredictable".  So as a rule, check if it's null before assigning
                // to an int.  This will happen often:  The storage API allows for remote
                // files, whose size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString will do the
                    // conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // END_INCLUDE (dump_metadata)
    }
}
