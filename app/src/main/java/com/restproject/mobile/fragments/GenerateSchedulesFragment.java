package com.restproject.mobile.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.BuildConfig;
import com.restproject.mobile.R;
import com.restproject.mobile.activities.MainActivity;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.models.OptionItem;
import com.restproject.mobile.utils.APIResponseObject;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.CryptoService;
import com.restproject.mobile.utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GenerateSchedulesFragment extends Fragment {

    private Spinner spinnerAim, spinnerGender, spinnerLevel, spinnerRatio;
    private Button btnUploadImage, btnSubmit,btnDecreaseWeight,btnIncreaseWeight, btnDecreaseAge, btnIncreaseAge, btnDecreaseSession, btnIncreaseSesison;
    EditText editTextAge, editTextWeight, editTextSession;
    private ImageView imageView;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;


    ArrayList<OptionItem> aimList = new ArrayList<>();
    ArrayList<OptionItem> genderList = new ArrayList<>();
    ArrayList<OptionItem> levelList = new ArrayList<>();
    ArrayList<OptionItem> weightDownAimRatioOptions = new ArrayList<>();
    ArrayList<OptionItem> weightUpAimRatioOptions = new ArrayList<>();
    OptionItem selectedGender;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generate_schedules, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupAdapter();
        setupSpinners();
        setupValueButtons(btnDecreaseAge, btnIncreaseAge, editTextAge);
        setupValueButtons(btnDecreaseWeight, btnIncreaseWeight, editTextWeight);
        setupValueButtons(btnDecreaseSession, btnIncreaseSesison, editTextSession);
        setupUploadButton();
        setupSubmitButton();

        spinnerAim.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                OptionItem selectedAim = (OptionItem) parentView.getItemAtPosition(position);
                updateSpinnerRatio(selectedAim.value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

    }
    private void updateSpinnerRatio(int aimValue) {
        ArrayList<OptionItem> updatedRatioOptions = new ArrayList<>();

        if (aimValue == 1) {  // Weight up
            updatedRatioOptions.addAll(weightUpAimRatioOptions);
        } else {
            updatedRatioOptions.addAll(weightDownAimRatioOptions);
        }

        ArrayAdapter<OptionItem> ratioAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                updatedRatioOptions
        );
        ratioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRatio.setAdapter(ratioAdapter);
    }

    private void setupAdapter() {
        // handle call api get this data in the future :)
        //aim
        aimList.add(new OptionItem(-1,"Weight down"));
        aimList.add(new OptionItem(0,"Maintain weight"));
        aimList.add(new OptionItem(1,"Weight up"));

        //gender
        genderList.add(new OptionItem(0, "Female"));
        genderList.add(new OptionItem(1, "Male"));

        // level
        levelList.add(new OptionItem(3, "Original Level (100% level)"));
        levelList.add(new OptionItem(2, "Average (90% level)"));
        levelList.add(new OptionItem(1, "Easy (80% level)"));

        // Weight Down Aim Ratio options
        weightDownAimRatioOptions.add(new OptionItem(-10, "Slowly"));
        weightDownAimRatioOptions.add(new OptionItem(-20, "Normally"));
        weightDownAimRatioOptions.add(new OptionItem(-30, "Fast"));
        weightDownAimRatioOptions.add(new OptionItem(-40, "Crash"));

        // Weight Up Aim Ratio options
        weightUpAimRatioOptions.add(new OptionItem(5, "Slowly"));
        weightUpAimRatioOptions.add(new OptionItem(10, "Normally"));
        weightUpAimRatioOptions.add(new OptionItem(15, "Fast"));

    }

    private void setupViews(View view) {
        spinnerAim = view.findViewById(R.id.spinnerAim);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        spinnerLevel = view.findViewById(R.id.spinnerLevel);
        spinnerRatio = view.findViewById(R.id.spinnerRaito);

        imageView = view.findViewById(R.id.imageView);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        btnSubmit = view.findViewById(R.id.btnSumbit);

        btnDecreaseAge = view.findViewById(R.id.btnDecreaseAge);
        btnIncreaseAge = view.findViewById(R.id.btnIncreaseAge);
        editTextAge = view.findViewById(R.id.textValueAge);

        btnDecreaseWeight = view.findViewById(R.id.btnDecreaseWeight);
        btnIncreaseWeight = view.findViewById(R.id.btnIncreaseWeight);
        editTextWeight = view.findViewById(R.id.textValueWeight);

        btnDecreaseSession = view.findViewById(R.id.btnDecreaseSession);
        btnIncreaseSesison = view.findViewById(R.id.btnIncreaseSession);
        editTextSession = view.findViewById(R.id.textValueSession);
    }

    private void setupSpinners() {

        ArrayAdapter<OptionItem> aimAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, aimList);
        spinnerAim.setAdapter(aimAdapter);

        ArrayAdapter<OptionItem> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, genderList);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<OptionItem> levelAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, levelList);
        spinnerLevel.setAdapter(levelAdapter);

    }


    private void setupValueButtons(Button btnDecrease, Button btnIncrease, EditText editText) {

        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        final Handler handler = new Handler();
        final int[] value = {0};

        Runnable increaseRunnable = new Runnable() {
            @Override
            public void run() {
                value[0]++;
                editText.setText(String.valueOf(value[0]));
                handler.postDelayed(this, 100);  // Lặp sau 100ms
            }
        };

        Runnable decreaseRunnable = new Runnable() {
            @Override
            public void run() {
                if (value[0] > 0) value[0]--;
                editText.setText(String.valueOf(value[0]));
                handler.postDelayed(this, 100);  // Lặp sau 100ms
            }
        };

        btnIncrease.setOnClickListener(v -> {
            String currentValue = editText.getText().toString();
            value[0] = currentValue.isEmpty() ? 0 : Integer.parseInt(currentValue);
            value[0]++;
            editText.setText(String.valueOf(value[0]));
        });

        btnDecrease.setOnClickListener(v -> {
            String currentValue = editText.getText().toString();
            value[0] = currentValue.isEmpty() ? 0 : Integer.parseInt(currentValue);
            if (value[0] > 0) value[0]--;
            editText.setText(String.valueOf(value[0]));
        });

        btnIncrease.setOnTouchListener((v, event) -> {
            String currentValue = editText.getText().toString();
            value[0] = currentValue.isEmpty() ? 0 : Integer.parseInt(currentValue);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.post(increaseRunnable);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(increaseRunnable);
                    break;
            }
            return false;
        });

        btnDecrease.setOnTouchListener((v, event) -> {
            String currentValue = editText.getText().toString();
            value[0] = currentValue.isEmpty() ? 0 : Integer.parseInt(currentValue);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.post(decreaseRunnable);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(decreaseRunnable);
                    break;
            }
            return false;
        });

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String input = editText.getText().toString();
                try {
                    int valid = Integer.parseInt(input);
                    if (valid < 0) {
                        editText.setText("0");
                    }
                } catch (NumberFormatException e) {
                    editText.setText("0");
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); // hoặc Intent.ACTION_PICK
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Log để kiểm tra
            Log.d("ImagePick", "Image selected: " + imageUri.toString());  // Log thành công

            imageView.setImageURI(imageUri);
            selectedGender = (OptionItem) spinnerGender.getSelectedItem();

            requestFastApi();
        } else {
            Log.d("ImagePick", "Image selection failed or canceled.");
        }
    }

    private void requestFastApi() {
        var context = this.requireContext();
        var reqUrl = BuildConfig.FASTAPI_ENDPOINT + BuildConfig.PRIVATE_USER_DIR+ "/v1/cal-body-fat-detection";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                reqUrl,
                response -> {
                    // response là JSONObject
                    Log.d("UPLOAD", "Response: " + response.toString());
                },
                error -> {
                    Log.d("UPLOAD", "ERROR full: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.d("UPLOAD", "Status Code: " + error.networkResponse.statusCode);
                        Log.d("UPLOAD", "Response Data: " + new String(error.networkResponse.data));
                    }

                },
                RequestInterceptor.getPrivateHeadersFastApi(context, "multipart/form-data")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                OptionItem selectedGender = (OptionItem) spinnerGender.getSelectedItem();
                params.put("gender",    selectedGender.getValueString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> data = new HashMap<>();
                byte[] imageBytes = readBytesFromUri(imageUri);
                data.put("image", new DataPart(getFileName(imageUri), imageBytes, "image/jpeg"));
                return data;
            }
        };
        Volley.newRequestQueue(context).add(APIUtilsHelper.setVolleyMultipartRequestTimeOut(multipartRequest, 30_000));
    }

    String getFileName(Uri uri) {
        Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            cursor.close();
            return name;
        }
        return "unknown.jpg";
    }

    private byte[] readBytesFromUri(Uri uri) {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int byteRead;
            while ((byteRead = inputStream.read()) != -1) {
                byteArrayOutputStream.write(byteRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void setupUploadButton() {
        btnUploadImage.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Upload...", Toast.LENGTH_SHORT).show();
            openFileChooser();
        });
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Submit successful!", Toast.LENGTH_SHORT).show();
        });
    }
}
