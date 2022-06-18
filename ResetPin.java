package ideanity.oceans.antitheftapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static ideanity.oceans.antitheftapp.SetPin.MyPREFERENCES;
import static ideanity.oceans.antitheftapp.SetPin.Password;

public class ResetPin extends AppCompatActivity {
    EditText etOldPin,etSetPin,etConfirmPin;
    Button btnSetPin,btnForgotOldPin;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reset_pin);

        etOldPin = (EditText)findViewById(R.id.etOldPin);
        etSetPin = (EditText)findViewById(R.id.etSetPin);
        etConfirmPin = (EditText)findViewById(R.id.etConfirmPin);
        btnSetPin = (Button)findViewById(R.id.btnSetPin);
        btnForgotOldPin = (Button)findViewById(R.id.btnForgotOldPin);

        btnForgotOldPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPin.this, ForgotPin.class));
                finish();
            }
        });

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String password = sharedpreferences.getString("passwordKey", "");
        btnSetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPin = etOldPin.getText().toString();
                String pin = etSetPin.getText().toString();
                String confirmPin = etConfirmPin.getText().toString();
                if(TextUtils.isEmpty(oldPin) || oldPin.length()<4) {
                    etOldPin.setError("Required! Minimum length 4 digit");
                    etOldPin.requestFocus();
                    return;
                }

                else if(TextUtils.isEmpty(pin)|| pin.length()<4) {
                    etSetPin.setError("Required! Minimum length 4 digit");
                    etSetPin.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(confirmPin) || confirmPin.length()<4) {
                    etConfirmPin.setError("Required! Minimum length 4 digit");
                    etConfirmPin.requestFocus();
                    return;
                }
                if(oldPin.equals(password)){
                    if(pin.equals(confirmPin)){
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(Password, confirmPin);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Password Reset Successful", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(ResetPin.this, MainActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Password Do Not Match", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    etOldPin.getText().clear();
                    etOldPin.setError("Wrong Pin");
                    etOldPin.requestFocus();
                }
            }
        });

    }

}
