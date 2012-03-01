package com.culblueswan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.util.*;

public class TweakAIOActivity extends Activity {
    
    private static final String FILE_NAME = "/data/tweakaio/tweakaio.conf";
    
    private LinearLayout linearLayout;
    private Button saveButton;
    List<String> textData;
    private Map<String, View> valueNameVsComponent = new HashMap<String, View>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        textData = new ArrayList<String>();
        showFile();

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFile();
            }
        });
    }


    private void showFile() {
        readTweakAIOFile();
        for (String text : textData) {
            if (text.equals("") || text.startsWith("#")) {
                addCommentInTextView(text);
            } else {
                addEditableWidget(text);
            }

        }
    }

    private void readTweakAIOFile() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
            try {
                readToEndOfFile(bufferedReader);
            } finally {
                bufferedReader.close();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void readToEndOfFile(BufferedReader bufferedReader) throws IOException {
        String s = bufferedReader.readLine();
        do {
            textData.add(s);
            s = bufferedReader.readLine();
        } while (s != null);
    }

    private void addCommentInTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        linearLayout.addView(textView);
    }

    private void addEditableWidget(String text) {
        String[] values = text.split("=");
        values[1] = values[1].replace("\"", "");
        if (values[1].equalsIgnoreCase("ON") || values[1].equalsIgnoreCase("OFF")) {
            addCheckBoxToLayout(values);
        } else {
            addTextBoxWithLabel(values);
        }
    }

    private void addTextBoxWithLabel(String[] values) {
        TextView textView = new TextView(this);
        textView.setText(values[0]);
        EditText editText = new EditText(this);
        editText.setText(values[1]);
        linearLayout.addView(textView);
        linearLayout.addView(editText);
        valueNameVsComponent.put(values[0], editText);
    }

    private void addCheckBoxToLayout(String[] values) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(values[0]);
        checkBox.setChecked(values[1].equalsIgnoreCase("ON"));
        linearLayout.addView(checkBox);
        valueNameVsComponent.put(values[0], checkBox);
    }

    private void updateFile() {
        try {

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME));
            try {
                writeAllDataToFile(bufferedWriter);
                bufferedWriter.flush();
            } finally {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            finish();
            throw new IllegalStateException(e);
        }
        finish();
    }

    private void writeAllDataToFile(BufferedWriter bufferedWriter) throws IOException {
        for (String text : textData) {
            String valueToWrite;
            if (text.equals("") || text.startsWith("#")) {
                valueToWrite = text;
            } else {
                valueToWrite = getValueFromWidget(text);
            }
            bufferedWriter.write(valueToWrite);
            bufferedWriter.newLine();
        }
    }

    private String getValueFromWidget(String text) throws IOException {
        String[] values = text.split("=");
        String valueToWrite = values[0] + "=";

        View view = valueNameVsComponent.get(values[0]);
        if (view instanceof EditText) {
            valueToWrite += ((EditText) view).getText().toString();
        } else if (view instanceof CheckBox) {
            boolean checked = ((CheckBox) view).isChecked();
            valueToWrite += checked ? "on" : "off";
        }
        return valueToWrite;
    }
}
