package com.example.florian.Fitness_Calculator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class DataCollection extends AppCompatActivity {

    long start = 0, current;

    Button showGraph, restartDataCollection, save, end;

    TextView xAccData, yAccData, zAccData, xRotData, yRotData, zRotData;

    double[] xAccDataArray = new double[0];
    double[] yAccDataArray = new double[0];
    double[] zAccDataArray = new double[0];

    double[] xRotDataArray = new double[0];
    double[] yRotDataArray = new double[0];
    double[] zRotDataArray = new double[0];

    //double[] time = new double[0];
    double[] accRotData = new double[0];

    private SensorManager sm;
    private SensorEventListener listener;

    GraphView graph;

    private LineGraphSeries<DataPoint> seriesAccX, seriesAccY, seriesAccZ, seriesRotX, seriesRotY, seriesRotZ;

    private int count = 0;
    private boolean graphSwitch = false;
    private boolean collect = true;

    String data, data4Reading = "", dataTime = "";
    String exercise;

    Calendar cal;
    SimpleDateFormat sdf;
    String t;
    ArrayList<String> time = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_collection_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        exercise = getIntent().getStringExtra("Exercise");

        showGraph = (Button) findViewById(R.id.showGraph);
        restartDataCollection = (Button) findViewById(R.id.restartDataCollection);
        save = (Button) findViewById(R.id.save);
        end = (Button) findViewById(R.id.end);

        xAccData = (TextView) findViewById(R.id.xAccData);
        yAccData = (TextView) findViewById(R.id.yAccData);
        zAccData = (TextView) findViewById(R.id.zAccData);
        /*xRotData = (TextView) findViewById(R.id.xRotData);
        yRotData = (TextView) findViewById(R.id.yRotData);
        zRotData = (TextView) findViewById(R.id.zRotData);*/

        save.setVisibility(View.INVISIBLE);

        sm = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        listener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                if (collect && sensor.getType() == sensor.TYPE_LINEAR_ACCELERATION) {

                    xAccData.setText("X-Acceleration: " + event.values[0]);
                    yAccData.setText("Y-Acceleration: " + event.values[1]);
                    zAccData.setText("Z-Acceleration: " + event.values[2]);

                    xAccDataArray = addData(xAccDataArray, event.values[0]);
                    yAccDataArray = addData(yAccDataArray, event.values[1]);
                    zAccDataArray = addData(zAccDataArray, event.values[2]);

                    cal = Calendar.getInstance();
                    sdf = new SimpleDateFormat("S");
                    t = sdf.format(cal.getTime());
                    time.add(t);
                    System.out.println(t);
                }

               /* if (collect && sensor.getType() == sensor.TYPE_GYROSCOPE) {

                    xRotData.setText("X-Rotation: " + event.values[0]);
                    yRotData.setText("Y-Rotation: " + event.values[1]);
                    zRotData.setText("Z-Rotation: " + event.values[2]);

                    xRotDataArray = addData(xRotDataArray, event.values[0]);
                    yRotDataArray = addData(yRotDataArray, event.values[1]);
                    zRotDataArray = addData(zRotDataArray, event.values[2]);

                }*/
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sm.registerListener(listener, sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(listener, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);

        graph = (GraphView) findViewById(R.id.graph);

        showGraph.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        graph.removeAllSeries();
                        count = 0;
                        collect = false;

                        showGraph.setText("Switch Graph");

                        restartDataCollection.setVisibility(View.VISIBLE);
                        end.setVisibility(View.VISIBLE);

                        if(!graphSwitch){
                            graphSwitch = true;
                        }else if(graphSwitch){
                            graphSwitch = false;
                        }

                        initiateGraph();
                        startGraph();

                        for(int i = 0; i<time.size(); i++){
                            dataTime += time.get(i) + "\n";
                        }

                        save.setVisibility(View.VISIBLE);
                    }
                }
        );

        restartDataCollection.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
        );

        save.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        saveFile();
                    }
                }
        );

        end.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        Toast.makeText(getApplicationContext(), "Exercise: " + exercise , Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static double[] addData (double[] array, double elem) {
        array = Arrays.copyOf(array, array.length + 1);
        array[array.length - 1] = elem;
        return array;
    }

    private void initiateGraph() {
        seriesAccX = new LineGraphSeries<DataPoint>();
        seriesAccY = new LineGraphSeries<DataPoint>();
        seriesAccZ = new LineGraphSeries<DataPoint>();
        /*seriesRotX = new LineGraphSeries<DataPoint>();
        seriesRotY = new LineGraphSeries<DataPoint>();
        seriesRotZ = new LineGraphSeries<DataPoint>();*/

        if(graphSwitch) {
            graph.addSeries(seriesAccX);
            graph.addSeries(seriesAccY);
            graph.addSeries(seriesAccZ);
        }else if(!graphSwitch) {
            /*graph.addSeries(seriesRotX);
            graph.addSeries(seriesRotY);
            graph.addSeries(seriesRotZ);*/
        }

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-11);
        viewport.setMaxY(11);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(xAccDataArray.length);

        seriesAccX.setColor(Color.RED);
        seriesAccY.setColor(Color.GREEN);
        seriesAccZ.setColor(Color.BLUE);
        /*seriesRotX.setColor(Color.YELLOW);
        seriesRotY.setColor(Color.BLACK);
        seriesRotZ.setColor(Color.DKGRAY);*/

        //graph.getViewport().setScrollable(true);
        //graph.getViewport().setScalable(true);
    }

    protected void startGraph() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < xAccDataArray.length; i++) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntryInDataArray();
                        }
                    });

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
    }

    private void addEntryInDataArray() {
        seriesAccX.appendData(new DataPoint(count, xAccDataArray[count]), true, xAccDataArray.length);
        seriesAccY.appendData(new DataPoint(count, yAccDataArray[count]), true, yAccDataArray.length);
        seriesAccZ.appendData(new DataPoint(count, zAccDataArray[count]), true, zAccDataArray.length);

      /*  seriesRotX.appendData(new DataPoint(count, xRotDataArray[count]), true, xRotDataArray.length);
        seriesRotY.appendData(new DataPoint(count, yRotDataArray[count]), true, yRotDataArray.length);
        seriesRotZ.appendData(new DataPoint(count, zRotDataArray[count]), true, zRotDataArray.length);*/

        count++;
    }

    public void saveFile(){

        data =  "@RELATION fittness              \n" +
                "                                \n" +
                "@ATTRIBUTE translate_xAxis REAL \n" +
                "@ATTRIBUTE translate_yAxis REAL \n" +
                "@ATTRIBUTE translate_zAxis REAL \n" +
                "@ATTRIBUTE rotate_xAxis REAL    \n" +
                "@ATTRIBUTE rotate_yAxis REAL    \n" +
                "@ATTRIBUTE rotate_zAxis REAL    \n" +
                "@ATTRIBUTE class {PushUp, PullUp, Crunch, Squat} \n" +
                "                                \n" +
                "@data                           \n";

        for(int i = 0; i < xAccDataArray.length; i++){
            data += xAccDataArray[i] + ", ";
            data += yAccDataArray[i] + ", ";
            data += zAccDataArray[i] + ", ";
           /* data += xRotDataArray[i] + ", ";
            data += yRotDataArray[i] + ", ";
            data += zRotDataArray[i] + ", ";*/
            data += exercise + " \n";
        }

        for(int i = 0; i < xAccDataArray.length; i++){
            data4Reading += xAccDataArray[i] + "\n";
            data4Reading += yAccDataArray[i] + "\n";
            data4Reading += zAccDataArray[i] + "\n";
            /*data4Reading += xRotDataArray[i] + "\n";
            data4Reading += yRotDataArray[i] + "\n";
            data4Reading += zRotDataArray[i] + "\n";*/
        }

            try
        {

            FileOutputStream fos = openFileOutput("Exercise.arff", Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();

            FileOutputStream fosTime = openFileOutput("time" + exercise + ".txt", Context.MODE_PRIVATE);
            fosTime.write(dataTime.getBytes());
            fosTime.close();

            FileOutputStream fosAccRotData = openFileOutput("AccRotData" + exercise + ".txt", Context.MODE_PRIVATE);
            fosAccRotData.write(data4Reading.getBytes());
            fosAccRotData.close();

            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)){

                File file = new File(getExternalFilesDir(null), "Exercise.arff");
                File fileTime = new File(getExternalFilesDir(null), "time" + exercise + ".txt");
                File fileAccRotData = new File(getExternalFilesDir(null), "AccRotData" + exercise + ".txt");

                if (fileTime.exists()){
                    BufferedReader inputReader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(file)));
                    String inputString;
                    StringBuffer stringBuffer = new StringBuffer();
                    while((inputString = inputReader.readLine()) != null){
                        stringBuffer.append(inputString + "\n");
                    }

                    BufferedReader inputReaderAccRot = new BufferedReader(
                            new InputStreamReader(new FileInputStream(fileAccRotData)));
                    String inputStringAccRot;
                    StringBuffer stringBufferAccRot = new StringBuffer();
                    while((inputStringAccRot = inputReaderAccRot.readLine()) != null){
                        stringBufferAccRot.append(inputStringAccRot + "\n");
                    }

                    BufferedReader inputReaderTime = new BufferedReader(
                            new InputStreamReader(new FileInputStream(fileTime)));
                    String inputStringTime;
                    StringBuffer stringBufferTime = new StringBuffer();
                    while((inputStringTime = inputReaderTime.readLine()) != null){
                        stringBufferTime.append(inputStringTime + "\n");
                    }

                    String data = "";
                    for(int i = 0; i < xAccDataArray.length; i++){
                        data += xAccDataArray[i] + ", ";
                        data += yAccDataArray[i] + ", ";
                        data += zAccDataArray[i] + ", ";
                       /* data += xRotDataArray[i] + ", ";
                        data += yRotDataArray[i] + ", ";
                        data += zRotDataArray[i] + ", ";*/
                        data += exercise + " \n";
                    }


                    String data4Reading = "";
                    for(int i = 0; i < xAccDataArray.length; i++){
                        data4Reading += xAccDataArray[i] + "\n";
                        data4Reading += yAccDataArray[i] + "\n";
                        data4Reading += zAccDataArray[i] + "\n";
                       /* data4Reading += xRotDataArray[i] + "\n";
                        data4Reading += yRotDataArray[i] + "\n";
                        data4Reading += zRotDataArray[i] + "\n";*/
                    }

                    inputString = stringBuffer.toString() + data;
                    inputStringAccRot = stringBufferAccRot.toString() + data4Reading;
                    inputStringTime = stringBufferTime.toString() + dataTime;

                    FileOutputStream fos2 = new FileOutputStream(file);
                    fos2.write(inputString.getBytes());
                    fos2.close();

                    FileOutputStream fosTime2 = new FileOutputStream(fileTime);
                    fosTime2.write(inputStringTime.getBytes());
                    fosTime2.close();

                    FileOutputStream fosAccRotData2 = new FileOutputStream(fileAccRotData);
                    fosAccRotData2.write(inputStringAccRot.getBytes());
                    fosAccRotData2.close();
                    Toast.makeText(getApplicationContext(), "Data was appended", Toast.LENGTH_SHORT).show();

                }else if(file.exists()){
                    BufferedReader inputReader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(file)));
                    String inputString;
                    StringBuffer stringBuffer = new StringBuffer();
                    while((inputString = inputReader.readLine()) != null){
                        stringBuffer.append(inputString + "\n");
                    }

                    String data = "";
                    for(int i = 0; i < xAccDataArray.length; i++){
                        data += xAccDataArray[i] + ", ";
                        data += yAccDataArray[i] + ", ";
                        data += zAccDataArray[i] + ", ";
                       /* data += xRotDataArray[i] + ", ";
                        data += yRotDataArray[i] + ", ";
                        data += zRotDataArray[i] + ", ";*/
                        data += exercise + " \n";
                    }

                    inputString = stringBuffer.toString() + data;

                    FileOutputStream fos2 = new FileOutputStream(file);
                    fos2.write(inputString.getBytes());
                    fos2.close();

                    FileOutputStream fosTime2 = new FileOutputStream(fileTime);
                    fosTime2.write(dataTime.getBytes());
                    fosTime2.close();

                    FileOutputStream fosAccRotData2 = new FileOutputStream(fileAccRotData);
                    fosAccRotData2.write(data4Reading.getBytes());
                    fosAccRotData2.close();

                    Toast.makeText(getApplicationContext(), "Data was appended", Toast.LENGTH_SHORT).show();



                }else {

                    FileOutputStream fos2 = new FileOutputStream(file);
                    fos2.write(data.getBytes());
                    fos2.close();

                    FileOutputStream fosTime2 = new FileOutputStream(fileTime);
                    fosTime2.write(dataTime.getBytes());
                    fosTime2.close();

                    FileOutputStream fosAccRotData2 = new FileOutputStream(fileAccRotData);
                    fosAccRotData2.write(data4Reading.getBytes());
                    fosAccRotData2.close();

                    Toast.makeText(getApplicationContext(), "File saved", Toast.LENGTH_SHORT).show();
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
