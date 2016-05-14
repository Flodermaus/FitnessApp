package com.example.florian.Fitness_Calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class Exercise extends AppCompatActivity {

    ArrayList<String> instance = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        convertFile();
    }


    private void convertFile(){
        try {
            File file = new File(getExternalFilesDir(null), "Exercise.arff");
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            String attributeStr = "";
            char c;

            while ((c = (char) inputReader.read()) != '!') {

                    if (c == ','){
                        instance.add(attributeStr);
                        attributeStr = "";
                    }else{
                        attributeStr += c;
                    }

            }
            System.out.println(instance);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }


        //Instance instance = new DenseInstance(data);

        //classify(data);
    }

    public static void classify(String data){

    }
}

