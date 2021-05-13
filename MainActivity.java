package com.example.forestfire;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText oxy, temp, hum;
    //Button pred_button;
    TextView output;
    Interpreter tflite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        oxy = (EditText) findViewById(R.id.oxygen);
        temp = (EditText) findViewById(R.id.temperature);
        hum = (EditText) findViewById(R.id.humidity);
        Button pred_button = (Button) findViewById(R.id.button);
        output = (TextView) findViewById(R.id.prediction);


        try {
            tflite = new Interpreter(loadModelFile());

        }catch (Exception e){
            e.printStackTrace();
        }

        pred_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                float prediction = inference(oxy.getText().toString(),temp.getText().toString(),hum.getText().toString());
                output.setText(Float.toString(prediction) + "%  chance of fire occurrence");
            }
        });
}

public float inference(String o, String t, String h){

        float [] inputValues = new float[3];
        inputValues[0] = Float.valueOf(o);
        inputValues[1] = Float.valueOf(t);
        inputValues[2] = Float.valueOf(h);

        float[][] outputValue = new float[1][1];
        tflite.run(inputValues, outputValue);
        float returnvalue = outputValue[0][0] *100;
        return returnvalue;
    }

private MappedByteBuffer loadModelFile() throws IOException{
    AssetFileDescriptor fileDescriptor = this.getAssets().openFd("forestfire.tflite");
    FileInputStream fileInputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
    FileChannel fileChannel = fileInputStream.getChannel();
    long stratOffSets = fileDescriptor.getStartOffset();
    long declaredLength = fileDescriptor.getDeclaredLength();
    return  fileChannel.map(FileChannel.MapMode.READ_ONLY,stratOffSets,declaredLength);

}
}