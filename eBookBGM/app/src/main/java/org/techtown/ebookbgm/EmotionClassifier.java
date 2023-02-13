package org.techtown.ebookbgm;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.support.v4.os.IResultReceiver;
import android.util.Log;
import android.util.Pair;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmotionClassifier {
    private static final String MODEL_NAME = "emo_class_keras_model.tflite";
    Context context;
    Interpreter interpreter = null;
    int modelInputWidth, modelInputHeight, modelInputChannel;
    int modelOutputClasses;
    String[] emotion = {"anger", "boredom", "empty", "enthusiasm", "fear", "fun", "happiness",
            "hate" ,"love", "neutral", "relief", "sadness", "surprise", "worry"};


    public EmotionClassifier(Context context) {
        this.context = context;
    }

    public void init() throws IOException{
        ByteBuffer model = loadModelFile(MODEL_NAME);
        model.order(ByteOrder.nativeOrder());
        interpreter = new Interpreter(model);

        initModelShape();
    }

    private ByteBuffer loadModelFile(String modelName) throws IOException{
        AssetManager assetManager = context.getAssets();
        AssetFileDescriptor assetFileDescriptor = assetManager.openFd(modelName);
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long declaredLength = assetFileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

    }

    private void initModelShape(){
        Tensor inputTensor = interpreter.getInputTensor(0);
        int[] inputShape = inputTensor.shape();

        for(int i=0;i<inputShape.length;i++){
            Log.d("MyModel", "Input " + i + ": " + inputShape[i]);
        }
        modelInputChannel = inputShape[0];
        modelInputWidth = inputShape[1];

        Tensor outputTensor = interpreter.getOutputTensor(0);
        int[] outputShape = outputTensor.shape();
        for(int i=0;i<outputShape.length;i++){
            Log.d("MyModel", "Output" + i + ": " + outputShape[i]);
        }
        modelOutputClasses = outputShape[1];

    }

    public Map<String, Float> classify(String str){
        ByteBuffer buffer = convertStringToByteBuffer(str);
        float [][] result = new float[1][modelOutputClasses];
        interpreter.run(buffer, result);
        return argmax(result[0]);
    }

    private ByteBuffer convertStringToByteBuffer(String str){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1600);
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.put(str.getBytes(StandardCharsets.UTF_8));

        return byteBuffer;
    }

    private Map<String, Float> argmax(float[] array){

        Map<String, Float> outputResult = new HashMap<>();
        for(int i=0;i<emotion.length;i++){
            outputResult.put(emotion[i], array[i]);
        }
        return outputResult;

    }
}
