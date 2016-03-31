package droidex.om4rezz.jdroidremote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText etIpAddress;
    Button btnConnect, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initEventDriven();
    }

    private void initEventDriven() {
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goRemote = new Intent(MainActivity.this, RemoteActivity.class);
                goRemote.putExtra("server_ip", etIpAddress.getText().toString());
                startActivity(goRemote);
            }
        });
    }



    private void initViews() {
        etIpAddress = (EditText)findViewById(R.id.et_ip);
        btnExit = (Button)findViewById(R.id.btn_exit);
        btnConnect= (Button)findViewById(R.id.btn_connect_server);
    }
}