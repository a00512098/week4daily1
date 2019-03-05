package com.example.week4daily1;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText userInput;
    TextView threadOutput, asyncOutput, looperOutput;
    Button threadButton, asyncButton, looperButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        userInput = findViewById(R.id.userInput);
        threadOutput = findViewById(R.id.threadOutput);
        threadButton = findViewById(R.id.threadButton);
        asyncOutput = findViewById(R.id.asyncOutput);
        asyncButton = findViewById(R.id.asyncButton);
        looperOutput = findViewById(R.id.looperOutput);
        looperButton = findViewById(R.id.looperButton);

        threadButton.setOnClickListener(this);
        asyncButton.setOnClickListener(this);
        looperButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String input = userInput.getText().toString();
        switch (v.getId()) {
            case R.id.threadButton:
                countStringLength(input);
                break;
            case R.id.asyncButton:
                reverseString(input);
                break;
            case R.id.looperButton:
                findDuplicatedChars(input);
                break;
        }
    }

    private void findDuplicatedChars(String input) {
        MyLooper looper;
        looper = new MyLooper(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                looperOutput.setText(bundle.getString("key"));
            }
        }, input);
        looper.start();
        looper.workerThreadHandler.sendMessage(new Message());
    }

    private void reverseString(String input) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String original = strings[0];
                StringBuilder reverse = new StringBuilder();
                for (int i = original.length() - 1; i > -1; i--) {
                    reverse.append(original.charAt(i));
                }
                return reverse.toString();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                asyncOutput.setText("Reversed String: " + s);
            }
        };
        task.execute(input);
    }

    private void countStringLength(String toString) {
        Runnable runnable = countStringLengthWithRunnable(toString);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private Runnable countStringLengthWithRunnable(final String toString) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final int stringLength = toString.length();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        threadOutput.setText("String size: " + String.valueOf(stringLength));
                    }
                });
            }
        };

        return runnable;
    }

    private class MyLooper extends Thread {
        Handler workerThreadHandler;
        Handler mainThreadHandler;

        public MyLooper(Handler handler, final String string) {
            super();
            mainThreadHandler = handler;
            workerThreadHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    Message message = new Message();
                    message.what = msg.what;
                    Bundle bundle = new Bundle();
                    String str = findDuplicates(string);
                    bundle.putString("key", str);
                    message.setData(bundle);
                    mainThreadHandler.sendMessage(message);
                }
            };
        }

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            Looper.loop();
        }

        public String findDuplicates(String str) {
            StringBuilder duplicatesString = new StringBuilder();
            HashMap<Character, Integer> duplicates = new HashMap<>();
            for (int i = 0; i < str.length(); i++) {
                if (!duplicates.containsKey(str.charAt(i))){
                    duplicates.put(str.charAt(i), 1);
                } else {
                    duplicates.put(str.charAt(i), duplicates.get(str.charAt(i)) + 1);
                }
            }

            duplicatesString.append("\nDuplicates: | ");
            for (Character key : duplicates.keySet()) {
                if (duplicates.get(key) > 1) {
                    duplicatesString.append(key);
                    duplicatesString.append(": ");
                    duplicatesString.append(duplicates.get(key));
                    duplicatesString.append(" | ");
                }
            }
            return duplicatesString.toString();
        }
    }
}
