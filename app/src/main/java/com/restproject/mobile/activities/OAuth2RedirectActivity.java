package com.restproject.mobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.restproject.mobile.BuildConfig;
import com.restproject.mobile.R;
import com.restproject.mobile.fragments.LoginFragment;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.CryptoService;

import java.util.Map;

public class OAuth2RedirectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_oauth2);
        Intent intent = this.getIntent();
        Uri uri = intent.getData();

        if (uri != null) {
            String scheme = uri.getScheme();
            String host = uri.getHost();
            String path = uri.getPath();
            Toast.makeText(this, "Scheme: " + scheme + "\nHost: " + host, Toast.LENGTH_LONG).show();
            String encryptedData = uri.getQueryParameter("encryptedData");
            String[] tokens = CryptoService.decrypt(encryptedData).split(BuildConfig.DATA_SEPARATOR);
            boolean result = APIUtilsHelper.saveTokensAfterAuthentication(this.getBaseContext(), Map.of(
                "refreshToken", tokens[0],
                "accessToken", tokens[1]
            ));
            if (result) {
                Intent mainIntent = new Intent(this, MainActivity.class);
                this.startActivity(mainIntent);
                this.finish();
            }
        } else {
            Toast.makeText(this, "No URI data received", Toast.LENGTH_SHORT).show();
        }
        this.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.main, new LoginFragment())
            .commit();
    }
}