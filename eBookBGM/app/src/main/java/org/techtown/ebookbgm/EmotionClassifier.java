package org.techtown.ebookbgm;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.support.v4.os.IResultReceiver;
import android.util.Log;
import android.util.Pair;
import android.widget.MultiAutoCompleteTextView;

import com.google.mediapipe.tasks.components.containers.Embedding;
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder;
import com.google.mediapipe.tasks.text.textembedder.TextEmbedderResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.TensorFlowLite;
import org.tensorflow.lite.support.metadata.MetadataExtractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;

public class EmotionClassifier {
    private static final String MODEL_NAME = "emo_class_keras_model.tflite";
    private static final String START = "<START>";
    private static final String PAD = "<PAD>";
    private static final String UNKNOWN = "<UNKNOWN>";
    private static final int SENTENCE_LEN = 400;
    private static final String SIMPLE_SPACE_OR_PUNCTUATION = " |\\,|\\.|\\!|\\?|\n";
    private final Map<String, Integer> dic = new HashMap<>();
    private final List<String> labels = new ArrayList<>();
    Context context;
    Interpreter interpreter = null;
    int modelInputWidth, modelInputHeight, modelInputChannel;
    int modelOutputClasses;
    String[] emotion = {"anger", "boredom", "empty", "enthusiasm", "fear", "fun", "happiness",
            "hate" ,"love", "neutral", "relief", "sadness", "surprise", "worry"};
    TextEmbedder textEmbedder;


    public EmotionClassifier(Context context) {
        this.context = context;
    }

    public void init() throws IOException, JSONException {
        ByteBuffer model = loadModelFile(MODEL_NAME);
        loadLabel();
        model.order(ByteOrder.nativeOrder());
        interpreter = new Interpreter(model);
        initModelShape();

        // Use metadata extractor to extract the dictionary and label files.
        MetadataExtractor metadataExtractor = new MetadataExtractor(model);

        // Extract and load the dictionary file.
        AssetManager assetManager = context.getAssets();
        InputStream dictionaryFile = assetManager.open("word_dict.json");
        loadDictionaryFile(dictionaryFile);
        Log.v("MyModel", "Dictionary loaded.");
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
    private void loadDictionaryFile(InputStream ins) throws IOException, JSONException {
        int size = ins.available();
        byte[] buffer = new byte[size];
        ins.read(buffer);
        ins.close();
        String json = new String(buffer, "UTF-8");
        // Each line in the dictionary has two columns.
        // First column is a word, and the second is the index of this word.
        JSONObject jsonObject = new JSONObject(json);
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            dic.put(key, jsonObject.getInt(key));
        }
    }

    private void loadLabel() {
        for(int i=0;i<emotion.length;i++){
            labels.add(emotion[i]);
        }
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

    public Pair<String, Float>[] classify(String text){
        int [][] input = tokenizeInputText(text);
        float [][] inputConverted = new float[input.length][input[0].length];
        for(int i=0;i<input[0].length;i++){
            inputConverted[0][i] = (float)((byte)input[0][i]);
        }


        float[][] result = new float[1][modelOutputClasses];
        interpreter.run(inputConverted, result);

        Pair[] pairs = new Pair[14];
        for(int i=0;i<pairs.length;i++){
            pairs[i] = new Pair(emotion[i], result[0][i]);
        }
        return pairs;
    }

    private ByteBuffer convertStringToByteBuffer(String str){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1600);
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.put(str.getBytes());

        Log.d("MyModel", "" + str.getBytes(StandardCharsets.UTF_8));

        return byteBuffer;
    }

    private Map<String, Float> mapping(float[] array){

        Map<String, Float> outputResult = new HashMap<>();
        for(int i=0;i<emotion.length;i++){
            outputResult.put(emotion[i], array[i]);
        }
        return outputResult;

    }

    int[][] tokenizeInputText(String text) {
        String new_text = text.toLowerCase(Locale.ROOT);
        int[] tmp = new int[SENTENCE_LEN];
        List<String> array = Arrays.asList(new_text.split(SIMPLE_SPACE_OR_PUNCTUATION));

        int index = 0;
        // Prepend <START> if it is in vocabulary file.

        for (String word : array) {
            if (index >= SENTENCE_LEN) {
                break;
            }
            if(dic.containsKey(word)){
                tmp[index++] = dic.get(word);
            }

        }
        // Padding and wrapping.
        Arrays.fill(tmp, index, SENTENCE_LEN - 1, 0);
        int[][] ans = {tmp};
        return ans;
    }



}

