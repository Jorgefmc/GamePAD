package gamepad.pad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registro");

        //TODO: PARA VOLVER A LA ACTIVIDAD ANTERIOR UTILIZA EL SIGUIENTE CODIGO!!!!;
        Button save = (Button) findViewById(R.id.register_register);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterClicked();
            }
        });
        /*Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();*/
    }

    private void onRegisterClicked () {
        TextView email = findViewById(R.id.register_email);
        TextView uname = findViewById(R.id.register_username);
        TextView pw = findViewById(R.id.register_pw);
        TextView rpw = findViewById(R.id.register_rpt_pw);

        email.setError(null);
        uname.setError(null);
        pw.setError(null);
        rpw.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (!checkRepPass(pw.getText().toString(), rpw.getText().toString())) {
            rpw.setError("Las contraseñas son distintas.");
            focusView = rpw;
            cancel = true;
        }

        if (!checkPass(pw.getText().toString())) {
            pw.setError("Contraseña muy corta.");
            focusView = pw;
            cancel = true;
        }

        if (!checkName(uname.getText().toString())) {
            uname.setError("Nombre de usuario muy corto.");
            focusView = uname;
            cancel = true;
        }

        if (!checkEmail(email.getText().toString())) {
            email.setError("Email no valido.");
            focusView = email;
            cancel = true;
        }

        try {
            DBConnection db = DBConnection.db(getApplicationContext());
            db.createUser(email.getText().toString(), uname.getText().toString(), pw.getText().toString());
        }catch (LoginException e) {
            if (e.isUsedName()) {
                rpw.setError("Nombre de usuario ya en uso.");
                focusView = uname;
                cancel = true;
            }
            else if (e.isUsedEmail()) {
                email.setError("Email no valido.");
                focusView = email;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else {
            Intent intent = new Intent();
            intent.putExtra("email", email.getText().toString());
            intent.putExtra("pw", pw.getText().toString());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private boolean checkEmail (String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean checkName (String name) {
        return name.length() > 4;
    }

    private boolean checkPass (String pass) {
        return pass.length() > 4;
    }

    private boolean checkRepPass (String pass, String rpt_pass) {
        return pass.equals(rpt_pass);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();

    }

}
