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
import com.hendiware.hendienger.models.LoginResponse;
import com.hendiware.hendienger.models.User;
import com.hendiware.hendienger.utils.Session;
import com.hendiware.hendienger.webservices.WebService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";
    
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_dont_have_account)
    TextView tvDontHaveAccount;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.rllt_body)
    RelativeLayout rlltBody;
    @BindView(R.id.rllt_loading)
    RelativeLayout rlltLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            String email = getIntent().getStringExtra("email");
            String pass = getIntent().getStringExtra("pass");
            etEmail.setText(email);
            etPassword.setText(pass);
        }
    }

    @OnClick({R.id.tv_dont_have_account, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_dont_have_account:
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                // override default transation of activity
                this.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);

                break;
            case R.id.btn_login:
                if (!FUtilsValidation.isEmpty(etEmail, getString(R.string.enter_email))
                        && FUtilsValidation.isValidEmail(etEmail, getString(R.string.enter_valid_email))
                        && !FUtilsValidation.isEmpty(etPassword, getString(R.string.enter_password))
                        ) {
                    setLoadingMode();
                    // create new user
                    final User user = new User();
                    user.email = etEmail.getText().toString();
                    user.password = etPassword.getText().toString();
                    // login User using Retrofit
                    WebService.getInstance().getApi().loginUser(user).enqueue(new Callback<LoginResponse>() {
                        
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            // check for status value comming from server (response of login-user.php file status)
                            if (response.body().status == 2) {
                                Toast.makeText(LoginActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            } else if (response.body().status == 1) {
                                Toast.makeText(LoginActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                user.username = response.body().user.user_name;
                                user.id = Integer.parseInt(response.body().user.id);
                                user.isAdmin = response.body().user.is_user_admin.equals("1");
                                Session.getInstance().loginUser(user);
                                Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(goToMain);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            }
                            setNormalMode();
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            // print error message in logcat
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
