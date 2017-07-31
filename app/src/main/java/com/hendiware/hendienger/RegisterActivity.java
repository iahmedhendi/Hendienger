package com.hendiware.hendienger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourhcode.forhutils.FUtilsValidation;
import com.hendiware.hendienger.models.MainResponse;
import com.hendiware.hendienger.models.User;
import com.hendiware.hendienger.webservices.WebService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";
    
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_repeat_password)
    EditText etRepeatPassword;
    @BindView(R.id.tv_already_have_account)
    TextView tvAlreadyHaveAccount;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.rllt_body)
    RelativeLayout rlltBody;
    @BindView(R.id.prgs_loading)
    ProgressBar prgsLoading;
    @BindView(R.id.rllt_loading)
    RelativeLayout rlltLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_already_have_account, R.id.btn_signup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_already_have_account:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                break;
                
            case R.id.btn_signup:
                if (!FUtilsValidation.isEmpty(etUsername, getString(R.string.enter_username))
                        && !FUtilsValidation.isEmpty(etEmail, getString(R.string.enter_email))
                        && FUtilsValidation.isValidEmail(etEmail, getString(R.string.enter_valid_email))
                        && !FUtilsValidation.isEmpty(etPassword, getString(R.string.enter_password))
                        && !FUtilsValidation.isEmpty(etRepeatPassword, getString(R.string.enter_password_again))
                        && FUtilsValidation.isPasswordEqual(etPassword, etRepeatPassword, getString(R.string.password_isnt_equal))
                        ) {
                    setLoadingMode();
                    // create new user object and set data from editTexts
                    final User user = new User();
                    user.username = etUsername.getText().toString();
                    user.email = etEmail.getText().toString();
                    user.password = etPassword.getText().toString();
                    // register user with retorfit
                    WebService.getInstance().getApi().registerUser(user).enqueue(new Callback<MainResponse>() {
                        @Override
                        public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                            if (response.body().status == 2) {
                                Toast.makeText(RegisterActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            } else if (response.body().status == 1) {
                                Toast.makeText(RegisterActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                // go to login activity
                                Intent gotToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                                gotToLogin.putExtra("email", user.email);
                                gotToLogin.putExtra("pass", user.password);
                                startActivity(gotToLogin);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            }
                            setNormalMode();
                        }

                        @Override
                        public void onFailure(Call<MainResponse> call, Throwable t) {
                            Log.e(TAG, t.getLocalizedMessage());

                        }
                    });

                }
                break;
        }
    }

    // set loading layout visible and hide body layout
    private void setLoadingMode() {
        rlltLoading.setVisibility(View.VISIBLE);
        rlltBody.setVisibility(View.GONE);
    }

    // set body layout visible and hide loading layout
    private void setNormalMode() {
        rlltLoading.setVisibility(View.GONE);
        rlltBody.setVisibility(View.VISIBLE);
    }
}
