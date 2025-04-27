package com.restproject.mobile.fragments;



import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.BuildConfig;
import com.restproject.mobile.R;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.models.OptionItem;
import com.restproject.mobile.utils.APIResponseObject;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GenerateSchedulesFragment extends Fragment {

    private Spinner spinnerAim, spinnerGender, spinnerLevel, spinnerRatio;
    private Button btnUploadImage, btnSubmit, btnDecreaseWeight, btnIncreaseWeight, btnDecreaseAge, btnIncreaseAge, btnDecreaseSession, btnIncreaseSesison, btnDecreaseWeightAim, btnIncreaseWeightAim;
    private EditText editTextAge, editTextWeight, editTextSession, editTextWeightAim;
    private ImageView imageView;
    private Uri imageUri;
    private TextView tvHint, tvRatio, tvWeightAim;
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isCaculated;
    private String bodyFatRatio;
    private LinearLayout linearLayoutWeightAim;

    ArrayList<OptionItem> aimList = new ArrayList<>();
    ArrayList<OptionItem> genderList = new ArrayList<>();
    ArrayList<OptionItem> levelList = new ArrayList<>();
    ArrayList<OptionItem> weightDownAimRatioOptions = new ArrayList<>();
    ArrayList<OptionItem> weightUpAimRatioOptions = new ArrayList<>();
    OptionItem selectedGender;
    OptionItem selectedAim;
    OptionItem selectedLevel;
    OptionItem selectedRaito;


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
        setupValueButtons(btnDecreaseWeightAim, btnIncreaseWeightAim, editTextWeightAim);
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
            linearLayoutWeightAim.setVisibility(View.GONE);
            spinnerRatio.setVisibility(View.VISIBLE);
            tvRatio.setVisibility(View.VISIBLE);
        } else if (aimValue == 0) { // weight maintain
            spinnerRatio.setVisibility(View.GONE);
            tvRatio.setVisibility(View.GONE);
            linearLayoutWeightAim.setVisibility(View.GONE);
            tvWeightAim.setVisibility(View.GONE);
        } else { //weight down
            updatedRatioOptions.addAll(weightDownAimRatioOptions);
            linearLayoutWeightAim.setVisibility(View.VISIBLE);
            tvWeightAim.setVisibility(View.VISIBLE);
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
        aimList.add(new OptionItem(-1, "Weight down"));
        aimList.add(new OptionItem(0, "Maintain weight"));
        aimList.add(new OptionItem(1, "Weight up"));

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

        btnDecreaseWeightAim = view.findViewById(R.id.btnDecreaseWeightAim);
        btnIncreaseWeightAim = view.findViewById(R.id.btnIncreaseWeightAim);
        editTextWeightAim = view.findViewById(R.id.textValueWeightAim);

        tvHint = view.findViewById(R.id.textHintUploadImage);

        isCaculated = false;
        bodyFatRatio = "";
        tvRatio = view.findViewById(R.id.textRatio);
        tvWeightAim = view.findViewById(R.id.textWeightAim);
        linearLayoutWeightAim = view.findViewById(R.id.layoutWeightAim);

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
        var reqUrl = BuildConfig.FASTAPI_ENDPOINT + "/api" + BuildConfig.PRIVATE_USER_DIR + "/v1/cal-body-fat-detection";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                reqUrl,
                response -> {
                    // response là JSONObject
                    var res = APIUtilsHelper.mapVolleySuccessStringTime(response);
                    bodyFatRatio = res.getData().get("bodyFatRatio").toString();
                    tvHint.setText(bodyFatRatio);
                    isCaculated = true;
                    Toast.makeText(context, res.getMessage(), Toast.LENGTH_SHORT).show();
                },
                error -> {
                    tvHint.setText(getString(R.string.upload_image_hint));
                    imageView.setImageResource(R.drawable.body_template);
                    APIResponseObject<Void> response = APIUtilsHelper.readErrorFromVolleyStringTime(error);
                    Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                },
                RequestInterceptor.getPrivateHeadersFastApi(context, "multipart/form-data")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                OptionItem selectedGender = (OptionItem) spinnerGender.getSelectedItem();
                params.put("gender", selectedGender.getValueString());
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
            openFileChooser();
        });
    }

    private void requestSchedule(JSONObject request) {
        var context = this.requireContext();
        var reqUrl = BuildConfig.FASTAPI_ENDPOINT + "/api" + BuildConfig.PRIVATE_USER_DIR + "/v1/decide-schedule-id";
        var jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, reqUrl, request,
                success -> {
                    var res = APIUtilsHelper.mapVolleySuccessStringTime(success);
                    int scheduleId = ((Double) res.getData().get("scheduleId")).intValue();
                    Log.d("Request", "Request: " + scheduleId);
                },
                error -> {
                    Log.d("Request", "Request: " + error.toString());
                    APIResponseObject<Void> response = APIUtilsHelper.readErrorFromVolleyStringTime(error);
                    Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPrivateHeaders(context);
            }
        };
        Volley.newRequestQueue(context)
                .add(APIUtilsHelper.setVolleyRequestTimeOut(jsonObjectRequest, 30_000));
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(v -> {
            if (isCaculated) {
                selectedAim = (OptionItem) spinnerAim.getSelectedItem();
                selectedLevel = (OptionItem) spinnerLevel.getSelectedItem();
                selectedRaito = (OptionItem) spinnerRatio.getSelectedItem();
                String age = editTextAge.getText().toString();
                String weight = editTextWeight.getText().toString();
                String session = editTextSession.getText().toString();
                try {
                    requestSchedule(new JSONObject()
                            .put("age", Integer.parseInt(age))
                            .put("gender", selectedGender.getValue())
                            .put("weight", Integer.parseInt(weight))
                            .put("bodyFat", Float.parseFloat(bodyFatRatio))
                            .put("session", Integer.parseInt(session))
                    );
                } catch (JSONException e) {
                    e.fillInStackTrace();
                    Toast.makeText(requireContext(), "Error request schedule", Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(requireContext(), "Must caculate first!!!!!!!", Toast.LENGTH_SHORT).show();
        });
    }
}
