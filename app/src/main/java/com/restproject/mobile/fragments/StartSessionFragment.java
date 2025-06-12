package com.restproject.mobile.fragments;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.restproject.mobile.BuildConfig.BACKEND_ENDPOINT;
import static com.restproject.mobile.BuildConfig.PRIVATE_USER_DIR;
import static com.restproject.mobile.activities.RequestEnums.GET_SESSION_DETAIL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.restproject.mobile.R;
import com.restproject.mobile.activities.MainActivity;
import com.restproject.mobile.activities.PrivateUIObject;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.models.Exercise;
import com.restproject.mobile.models.ExerciseDetailOfSession;
import com.restproject.mobile.models.Muscle;
import com.restproject.mobile.models.Session;
import com.restproject.mobile.utils.APIBuilderForGET;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.VolleyErrorHandler;
import com.restproject.mobile.activities.RequestEnums;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StartSessionFragment extends Fragment {

    private final Long scheduleId;
    private final int ordinal;
    private Long sessionId;
    private Session sessionData = null;
    private ExerciseDetailOfSession exercisesData = null;
    private List<ExerciseDetailOfSession> listExerciseDetailOfSession;

    // UI elements
    private TextView tvSessionTitle, tvSessionLevel, tvTargetMuscles, tvProgressPercent, tvTotalTime, loading;
    private TextView tvExerciseName, tvExerciseMuscles, tvUnit, tvQuan, tvIterations, tvCurrentIteration, tvTimeType, tvCountdownTimer;
    private ImageView imgExercise;
    private ProgressBar progressBar;
    private Button btnAction;
    private LinearLayout data, vCountdown;
    private boolean isSessionStarted = false;
    private boolean needSwitchExerciseDelay = false;
    private boolean isResting = true;
    private int currentIteration = 1;
    private int currentExerciseIndex = 0;
    private int totalTime = 0;
    private int slackTime = 0;
    private CountDownTimer countDownTimer;
    private double downRepRatio = 0;
    private int basicReps = 0;
    private int iteration = 0;
    private int progress = 0, total = 0;
    private Runnable onCountDownFinishRunnable;
    public StartSessionFragment(Long scheduleId, int ordinal) {
        this.scheduleId = scheduleId;
        this.ordinal = ordinal;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_session, container, false);

        data = view.findViewById(R.id.data);
        loading = view.findViewById(R.id.page_home_dialog_explainTxtForEmptyData);
        // Initialize UI elements
        tvSessionTitle = view.findViewById(R.id.tv_session_title);
        tvSessionLevel = view.findViewById(R.id.tv_session_level);
        tvTargetMuscles = view.findViewById(R.id.tv_target_muscle_groups);
        tvProgressPercent = view.findViewById(R.id.tv_progress_percent);
        tvTotalTime = view.findViewById(R.id.tv_total_time);

        tvExerciseName = view.findViewById(R.id.tv_exercise_name);
        tvExerciseMuscles = view.findViewById(R.id.tv_target_muscle_groups_of_exercise);
        tvUnit = view.findViewById(R.id.tv_unit);
        tvQuan = view.findViewById(R.id.tv_quan);
        tvIterations = view.findViewById(R.id.tv_iterations);
        tvCurrentIteration = view.findViewById(R.id.tv_current_iteration);
        tvTimeType = view.findViewById(R.id.tv_time_type);
        tvCountdownTimer = view.findViewById(R.id.tv_countdown_timer);

        imgExercise = view.findViewById(R.id.img_exercise);
        progressBar = view.findViewById(R.id.progress_bar);
        btnAction = view.findViewById(R.id.btn_action);
        vCountdown = view.findViewById(R.id.exercise_inform_5);

        // Gọi API lấy thông tin phiên tập
        this.requestSessionData(new JSONObject(Map.of("scheduleId", scheduleId, "ordinal", ordinal)));
        this.checkAndShowExplainTagIfEmptyData();

        btnAction.setOnClickListener(v -> {
            isSessionStarted = true;
            startTotalTimeClock();
            setBtnAction();
        });

        return view;
    }

    public void checkAndShowExplainTagIfEmptyData() {
        if (Objects.isNull(this.exercisesData)) {
            this.data.setVisibility(View.GONE);
            this.loading.setVisibility(View.VISIBLE);
        } else {
            this.data.setVisibility(View.VISIBLE);
            this.loading.setVisibility(View.GONE);
        }
    }


    private void requestSessionData(JSONObject requestData) {
        var context = this;
        var jsonReq = new JsonObjectRequest(
                Request.Method.GET,
                APIBuilderForGET.parseFromJsonObject(requestData,
                        BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/get-sessions-of-subscribed-schedule-of-user"),
                null,
                success -> {
                    var response = APIUtilsHelper.mapVolleySuccess(success).getData();
                    if (response.containsKey("session")) {
                        LinkedTreeMap sessionDataMap = (LinkedTreeMap) response.get("session");
                        this.sessionData = Session.mapping(sessionDataMap);
                        // Gán sessionId trực tiếp ngay sau khi mapping
                        this.sessionId = sessionData.getSessionId();
//                        listExerciseDetailOfSession = generateMockExerciseDetailOfSession();
                        initializeSessionData();
                    }
                    // Phải chắc chắn sessionId có rồi mới request tiếp
                    if (this.sessionId != null) {
                        requestExercisesData(new JSONObject(Map.of("id", sessionId)));
                    } else {
                        Log.e("SessionId", "Session ID is null, cannot request exercises.");
                    }
                }
                , error -> {
                    Log.e("SessionDataError", error.toString());
                    APIUtilsHelper.handlePrivateVolleyRequestError(VolleyErrorHandler.builder()
                            .activity((AppCompatActivity) requireActivity())
                            .context(getContext())
                            .app((PrivateUIObject) this)
                            .requestData(requestData)
                            .requestEnum(RequestEnums.GET_SESSION_DETAIL)
                            .error(error));
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPrivateHeaders(getContext());
            }
        };
        Volley.newRequestQueue(requireContext()).add(APIUtilsHelper.setVolleyRequestTimeOut(jsonReq, 30_000));
    }

    private void initializeSessionData() {
        if (sessionData != null) {
            // Gán tên session vào TextView tvSessionTitle
            this.tvSessionTitle.setText(sessionData.getName());
            // Gán level của session vào TextView tvSessionLevel
            this.tvSessionLevel.setText(sessionData.getLevelEnum());
            // Gán mô tả session vào TextView tvTargetMuscles
            this.tvTargetMuscles.setText(sessionData.getDescription());
            // Gán các cơ bắp vào UI
            this.tvTargetMuscles.setText(sessionData.getMusclesStr());
//            this.sessionId = sessionData.getSessionId();
        } else {
            Log.e("SessionData", "Session data is null");
        }
    }
    private void requestExercisesData(JSONObject requestData) {
        var context = this;
        var url = APIBuilderForGET.parseFromJsonObject(requestData,BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/get-exercises-in-session-of-subscribed-schedule-of-user");
        var jsonReq = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                success -> {
                    try {
                        // Chuyển đổi response thành LinkedTreeMap
                        var response = APIUtilsHelper.mapListVolleySuccess(success).getData();
//                        Log.d("response", response.toString());
                        List<?> rawList = response;
                        // Chuyển đổi từng phần tử từ LinkedTreeMap sang ExerciseDetailOfSession
                        listExerciseDetailOfSession = rawList.stream()
                                .map(obj -> ExerciseDetailOfSession.mapping((LinkedTreeMap) obj))
                                .collect(Collectors.toList());

                        if (listExerciseDetailOfSession != null) {
                            setUpCurrentExercise();
                            for (ExerciseDetailOfSession exercise:listExerciseDetailOfSession){
                                total += exercise.getIteration();
                            }
                            this.checkAndShowExplainTagIfEmptyData();
                        } else {
                            Toast.makeText(getContext(), "No exercises found!", LENGTH_SHORT).show();
                        }

                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "An error occurred. Please restart the app.", LENGTH_SHORT).show();
                    }
                }, error -> {
            // Log lỗi trả về nếu có
            Log.e("ExerciseDataError", error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPrivateHeaders(getContext());
            }
        };
        Volley.newRequestQueue(requireContext()).add(APIUtilsHelper.setVolleyRequestTimeOut(jsonReq, 30_000));
    }

    private void startTotalTimeClock() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSessionStarted) {
                    totalTime++;
                    tvTotalTime.setText(formatTime(totalTime));
                    startTotalTimeClock(); // Gọi lại để tiếp tục đếm
                }
            }
        }, 1000);
    }

    private void countDown(int seconds, Runnable onFinish) {
        if (countDownTimer != null) countDownTimer.cancel();
        onCountDownFinishRunnable = onFinish;
        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int sec = (int) (millisUntilFinished / 1000);
                tvCountdownTimer.setText(formatTime(sec));
            }
            @Override
            public void onFinish() {
                onFinish.run();
            }
        };
        countDownTimer.start();
    }

    private String formatTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void setBtnAction() {
        isResting = false;
        doingExercise();
        btnAction.setOnClickListener(v -> {
            if (isResting) {
                finishRest();
            } else {
                completeIteration();
            }
        });
    }

    private void updateProgress() {
        progress += 1;
        int percent = (int) ((progress / (float) total) * 100);
        progressBar.setProgress(percent);
        tvProgressPercent.setText(percent + "%");
    }

    private void finishSession() {
        // Ngăn không cho đếm tiếp thời gian
        isSessionStarted = false;
        progressBar.setProgress(100);
        tvProgressPercent.setText("100%");
        // Hủy mọi countdown đang chạy
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        // Ẩn nút hành động
        btnAction.setText("Back");
        btnAction.setBackgroundResource(R.drawable.btn_blue);

        btnAction.setOnClickListener(v -> {
            this.getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainLayout_dialogContainer,
                            new DetailScheduleForHomeFragment(scheduleId))
                    .commit();
            ((MainActivity) this.requireActivity()).openDialog();
        });
        Toast.makeText(getContext(), "You have finished your session!", Toast.LENGTH_LONG).show();
    }

    private void setUpCurrentExercise() {
        if (currentExerciseIndex >= listExerciseDetailOfSession.size()) return;

        exercisesData = listExerciseDetailOfSession.get(currentExerciseIndex);
        basicReps = exercisesData.getExercise().getBasicReps();
        downRepRatio = exercisesData.getDownRepsRatio();
        int adjustedReps = (int) Math.round(basicReps * (1 - downRepRatio));
        iteration = exercisesData.getIteration();
        currentIteration = 1;
        slackTime = exercisesData.getSlackInSecond();
        needSwitchExerciseDelay = exercisesData.getNeedSwitchExerciseDelay();
        // Gán thông tin bài tập
        tvExerciseName.setText(exercisesData.getExercise().getName());
        tvExerciseMuscles.setText(exercisesData.getExercise().getMusclesStr());
        tvQuan.setText(String.valueOf(adjustedReps));
        tvIterations.setText(String.valueOf(iteration));
        tvCurrentIteration.setText("1/" + iteration);
        // Load ảnh
        String imageUrl = exercisesData.getExercise().getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imgExercise.setImageResource(R.drawable.no_image); // ảnh mặc định
        }
        else {
            Glide.with(getContext()).load(imageUrl).into(imgExercise);
        }
    }

    private void doingExercise() {
        isResting = false;
        if (isHoldExercise()) {
            doHoldExercise();
        } else {
            doRepExercise();
        }
    }

    private boolean isHoldExercise() {
        String exerciseName = exercisesData.getExercise().getName().toLowerCase();
        if (exerciseName.contains("hold")) {
            return true;
        }
        return false;
    }

    private void doRepExercise() {
        btnAction.setText("Complete");
        btnAction.setBackgroundResource(R.drawable.btn_green);
        btnAction.setEnabled(true);
        tvUnit.setText("Reps: ");
        vCountdown.setVisibility(INVISIBLE);
    }

    private void doHoldExercise() {
        btnAction.setVisibility(INVISIBLE);
        tvUnit.setText("Time: ");
        tvTimeType.setText("Hold: ");
        vCountdown.setVisibility(VISIBLE);
        int adjustedReps = (int) Math.round(basicReps * (1 - downRepRatio));
        countDown(adjustedReps, new Runnable() {
            @Override
            public void run() {
                vCountdown.setVisibility(INVISIBLE);
                btnAction.setVisibility(VISIBLE);
                completeIteration();
            }
        });
    }

    private void completeIteration() {
        updateProgress();
        resting();
    }

    private void resting() {
        isResting = true;
        tvTimeType.setText("Rest Time: ");
        btnAction.setBackgroundResource(R.drawable.btn_blue);
        if (currentIteration < iteration) {
            currentIteration += 1;
            restTimeBetweenIterations();
        } else {
            if (++currentExerciseIndex < listExerciseDetailOfSession.size()) {
                setUpCurrentExercise();
                restTimeBetweenExercises();
            } else {
                finishSession(); // hoàn thành toàn bộ
            }
        }
    }

    private void restTimeBetweenIterations() {
        btnAction.setText("Next");
        vCountdown.setVisibility(VISIBLE);
        countDown(slackTime, new Runnable() {
            @Override
            public void run() {
                if (currentIteration != iteration) {
                    slackTime += exercisesData.getRaiseSlackInSecond();
                }
                tvCurrentIteration.setText(currentIteration + "/" + iteration);
                vCountdown.setVisibility(INVISIBLE);
                continueExercise();
            }
        });
    }

    private void restTimeBetweenExercises() {
        if (needSwitchExerciseDelay)
        {
            btnAction.setText("Next");
            vCountdown.setVisibility(VISIBLE);
            countDown(300, new Runnable() {
                @Override
                public void run() {
                    vCountdown.setVisibility(INVISIBLE);
                    continueExercise();
                }
            });
        } else
        {
            vCountdown.setVisibility(INVISIBLE);
            continueExercise();
        }
    }

    private void finishRest() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            vCountdown.setVisibility(View.INVISIBLE);

            if (onCountDownFinishRunnable != null) {
                onCountDownFinishRunnable.run(); // Gọi thủ công phần kết thúc
                onCountDownFinishRunnable = null;
            }
        }
    }

    private void continueExercise() {
        doingExercise();
    }
}