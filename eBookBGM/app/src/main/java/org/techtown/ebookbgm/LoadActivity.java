package org.techtown.ebookbgm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import com.google.mediapipe.tasks.text.textclassifier.TextClassifier;
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder;

import org.json.JSONException;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadActivity extends AppCompatActivity {

    EmotionClassifier emotionClassifier;
    String str1 = "it gets better. without explanation; you just wake up one morning and youâ€™re just happy, totally and utterly elated.";
    String str2 = "i am not at all happy";
    String str = "";
    String[] emotion = {"anger", "boredom", "empty", "enthusiasm", "fear", "fun", "happiness",
            "hate" ,"love", "neutral", "relief", "sadness", "surprise", "worry"};
    Interpreter interpreter;
    @Override
    public void finish() {
        super.finish();
        interpreter.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        emotionClassifier = new EmotionClassifier(this);
        try {
            emotionClassifier.init();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


//        try {
//            FirebaseSetting();
//        } catch (FirebaseMLException e) {
//            e.printStackTrace();
//            Log.d("MyModel", "exception 1");
//        }

        //emotion analysis button
        Button button_analysis = findViewById(R.id.button_analysis);
        TextView textView_analysis = findViewById(R.id.text_analysis);

        button_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair[] emotion_result = emotionClassifier.classify(str);
                String str = "";
                float max_emotion = 0;
                int max_position = 0;
                for(int i=0;i<emotion_result.length;i++){
                    str = str + emotion_result[i].first + " : " + emotion_result[i].second + "\n";
                    if(max_emotion < (float)emotion_result[i].second){
                        max_position = i;
                        max_emotion = (float)emotion_result[i].second;
                    }
                }
                str = str + "MAX = " + emotion_result[max_position].first + " : " + emotion_result[max_position].second;
                textView_analysis.setText(str);
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();


            }
        });

        //set test String
        TextView example = findViewById(R.id.example);

        Button example1 = findViewById(R.id.example1);
        example1.setText(str1);
        example1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = str1;
                example.setText(str);
            }
        });
        Button example2 = findViewById(R.id.example2);
        example2.setText(str2);
        example2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = str2;
                example.setText(str);
            }
        });

    }

    private void FirebaseSetting() throws FirebaseMLException {
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel("emotion_analysis", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        // Download complete. Depending on your app, you could enable the ML
                        // feature, or switch from the local model to the remote model, etc.

                        // The CustomModel object contains the local path of the model file,
                        // which you can use to instantiate a TensorFlow Lite interpreter.
                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                        }
                    }
                });

    }
    private String FirebaseInput(String text){
        ByteBuffer input = ByteBuffer.allocateDirect(1*400*4).order(ByteOrder.nativeOrder());
        ByteBuffer modelOutput = ByteBuffer.allocateDirect(1 * 400 * 4).order(ByteOrder.nativeOrder());
        interpreter.run(input, modelOutput);
        String ans = "";
        for(int i=0;i<emotion.length;i++){
            ans = ans + emotion[i] + " : " + modelOutput.get(i) + "\n";
        }

        Log.d("MyModel", "exception2");
        return ans;
    }


}