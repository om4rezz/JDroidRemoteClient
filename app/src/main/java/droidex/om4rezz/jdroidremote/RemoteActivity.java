package droidex.om4rezz.jdroidremote;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class RemoteActivity extends AppCompatActivity {

    ImageView ivMonitor;

    Button btnRight, btnLeft, btnKeyboard;

    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;

    Thread thReceiveMonitor;

    InputMethodManager imm;

    String ipAddress = "";

    int connectedFlag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);

        ipAddress = getIpAddress();

        initViews();
        Log.w("aaaaaaaaaaaa", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        connectServer();
        Log.w("bbbbbbbbbbbb", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        initEventDriven();

    }



    private void initEventDriven() {
        btnKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dos.writeUTF("left_click");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dos.writeUTF("right_click");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getIpAddress() {
        Intent i = getIntent();
        String ip = i.getExtras().getString("server_ip");
        return ip;
    }

    private void connectServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ipAddress, 5005);
                    Log.w("Connected","I am connected.!");

                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());

                    Log.w("cccccc","ccccccccccccccccccccccccccccccc");

                    connectedFlag = 1;

                    // start recieving monitor after init socket
//                    thReceiveMonitor.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initViews() {
        ivMonitor = (ImageView) findViewById(R.id.iv_monitor);
        btnLeft = (Button) findViewById(R.id.btn_left);
        btnRight = (Button) findViewById(R.id.btn_right);
        btnKeyboard = (Button) findViewById(R.id.btn_keyboard);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_UP:
//        }

        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();

        double screenWidth = d.getWidth();
        double screenHeight = d.getHeight();

        int relativeX = (int) ((x / screenWidth) * 100);
        int relativeY = (int) ((y / screenHeight) * 100);

        Log.w("(x,y)", "( " + x + " , " + y + ")");
        Log.w("Relative (x,y)", "( " + relativeX + " % , " + relativeY + " %)");

        try {
            dos.writeUTF(relativeX + ":" + relativeY + ": coords");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int keycode = event.getKeyCode();
        int keyunicode = event.getUnicodeChar(event.getMetaState());
        char character = (char) keyunicode;

        String SendData = keycode + "-" + character;

        try {
            if (connectedFlag == 1) {
                if (event.getAction() == KeyEvent.ACTION_UP) {

                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        SendData = "-" + "@enter";
                        Log.e("data sent", "enter");
                    } else if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                        SendData = "-" + "@backspace";
                        Log.e("data sent", "backspace");
                    } else {
                        Log.e("data sent" + keyunicode, "" + character);
                    }
                    dos.writeUTF(SendData);
                }
            } else {
                Log.e("data sent_E", " Sorry,Not Connected ,please try again");
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("data not sent", "");
        }

        return super.dispatchKeyEvent(event);
    }
}
