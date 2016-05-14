package com.example.florian.Fitness_Calculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button start, remove, end;
    private Spinner dropDown, exerciseChooser;
    private static final String[] modi = {"Data Collection", "Exercising"}, exercises = {"PushUp", "PushUpErhoeht", "PullUpVorhand", "PullUpRueckhand", "Crunch", "Squats", "RollOut"};
    private int mode = 1;

    String exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dropDown = (Spinner)findViewById(R.id.dropDown);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, modi);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDown.setAdapter(adapter1);
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_SHORT).show();

                if (dropDown.getSelectedItem().toString().equals("Data Collection")) {
                    mode = 1;
                    exerciseChooser.setVisibility(View.VISIBLE);
                } else if (dropDown.getSelectedItem().toString().equals("Exercising")) {
                    mode = 2;
                    exerciseChooser.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing to do
            }
        });

        exerciseChooser = (Spinner) findViewById(R.id.exercise);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, exercises);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseChooser.setAdapter(adapter2);
        exerciseChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_SHORT).show();

                exercise = exerciseChooser.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing to do
            }
        });

        start = (Button)findViewById(R.id.start);
        start.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        Intent intent = new Intent(getApplicationContext(), DataCollection.class);
                        intent.putExtra("Exercise", exercise);

                        if (mode == 1) {
                            startActivity(intent);
                        } else if (mode == 2) {
                            startActivity(new Intent(getApplicationContext(), Exercise.class));
                        }
                        finish();
                    }
                }
        );

        remove = (Button) findViewById(R.id.remove);
        remove.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        System.out.println(exercise);
                        File file = new File(getExternalFilesDir(null), "Exercise.arff");
                        File fileTime = new File(getExternalFilesDir(null), "time" + exercise + ".txt");
                        File fileAccRotData = new File(getExternalFilesDir(null), "AccRotData" + exercise + ".txt");

                        file.delete();
                        fileTime.delete();
                        fileAccRotData.delete();

                    }
                }
        );

        end = (Button) findViewById(R.id.end);
        end.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }
}