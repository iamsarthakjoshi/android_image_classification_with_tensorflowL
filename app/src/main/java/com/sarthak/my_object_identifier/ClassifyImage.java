package com.sarthak.my_object_identifier;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class ClassifyImage extends AppCompatActivity {

    /** Tag for the {@link Log}. */
    private static final String TAG = "ClassifyImage.Class";

    // presets for rgb conversion
    private static final int RESULTS_TO_SHOW = 3;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    // initiate bitmap objects
    private Bitmap bitmap = null;
    private Bitmap resizedBitmap = null;

    // options for model interpreter
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
    // tensorflow lite graph interpreter
    private Interpreter tflite;
    // holds all the possible labels for model
    private List<String> labelList;
    // holds the selected image data as bytes
    private ByteBuffer imgData = null;
    // holds the probabilities of each label for non-quantized graphs
    private float[][] labelProbArray = null;
    // holds the probabilities of each label for quantized graphs
    private byte[][] labelProbArrayB = null;
    // array that holds the labels with the highest probabilities
    private String[] topLables = null;
    // array that holds the highest probabilities
    private String[] topConfidence = null;

    // selected classifier information received from extras
    private String chosen = "inception_quant.tflite";
    private boolean quant;
    private Uri imageUri;

    // input image dimensions for the Inception Model
    private int DIM_IMG_SIZE_X = 299;
    private int DIM_IMG_SIZE_Y = 299;
    private int DIM_PIXEL_SIZE = 3;

    // int array to hold image data
    private int[] intValues;

    // imageview to display captured image
    private ImageView selected_image;

    // button to execute classification task
    private Button classify_button;

    // button to return back to camera fragment
    private Button backtoCam_button;

    // Instantiate iOutput obj to store prediction results
    InferenceOutput iOutput = new InferenceOutput();

    // priority queue that will hold the top results from the CNN
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_and_classify);

        // get all selected classifier data from classifiers
        quant = getIntent().getBooleanExtra("quant", false);
        chosen = quant ? "inception_quant.tflite" : "inception_float.tflite";
        chosen = "assets/" + chosen;
        imageUri = getIntent().getParcelableExtra("resID_uri");

        // initialize array that holds image data
        intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

        // initialize graph
        try{
            tflite = new Interpreter(loadModelFile(), tfliteOptions);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        // initialize byte array. The size depends if the input data needs to be quantized or not
        if(quant){
            imgData =
                    ByteBuffer.allocateDirect(
                            DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        } else {
            imgData =
                    ByteBuffer.allocateDirect(
                            4 * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        }
        imgData.order(ByteOrder.nativeOrder());

        // labels that hold top three results of CNN
        iOutput.setLabel1((TextView) findViewById(R.id.label1));
        iOutput.setLabel2((TextView) findViewById(R.id.label2));
        iOutput.setLabel3((TextView) findViewById(R.id.label3));

        // displays the probabilities of top labels
        iOutput.setConfidence1((TextView) findViewById(R.id.Confidence1));
        iOutput.setConfidence2((TextView) findViewById(R.id.Confidence2));
        iOutput.setConfidence3((TextView) findViewById(R.id.Confidence3));

        // initialize imageView that displays selected image to the user
        selected_image = findViewById(R.id.image);

        // initialize classify and back button
        classify_button = findViewById(R.id.classify_image_btn);
        backtoCam_button = findViewById(R.id.btn_backTo_cam);

        // initialize array to hold top labels
        topLables = new String[RESULTS_TO_SHOW];
        // initialize array to hold top probabilities
        topConfidence = new String[RESULTS_TO_SHOW];

        // get image from previous activity to show in the imageView
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(),
                    imageUri);
            selected_image.setImageBitmap(bitmap);
            selected_image.setRotation(selected_image.getRotation() + 90);

//            Bitmap bitmap_orig = ((BitmapDrawable) selected_image.getDrawable()).getBitmap();
            // resize the bitmap to the required input size to the CNN
            resizedBitmap = getResizedBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // classify current captured image in a task
        final AsyncTask<Bitmap, Integer, Bitmap> classifyBtnTask = new AsyncTask<Bitmap, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Bitmap... b) {
                Bitmap bitmap = null;
                try {
                    // load list of labels from file
                    labelList = loadLabelList();

                    // initialize probabilities array. the datatypes that array holds
                    // depends if the input data needs to be quantized or not
                    if(quant){
                        labelProbArrayB= new byte[1][labelList.size()];
                    } else {
                        labelProbArray = new float[1][labelList.size()];
                    }

                    // convert bitmap to byte array
                    convertBitmapToByteBuffer(resizedBitmap);

                    // pass byte data to the graph
                    if(quant){
                        tflite.run(imgData, labelProbArrayB);
                    } else {
                        tflite.run(imgData, labelProbArray);
                    }
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                // display the results
                printTopKLabels();
                // endClassifyBtn
                classify_button.setText("Classified!");
                classify_button.setEnabled(false);
                classify_button.setTextColor(Color.MAGENTA);
                super.onPostExecute(bitmap);
            }
        };

        // classify button to execute classification task
        classify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // busyClassifyBtn
                classify_button.setText("Classifing...");
                classify_button.setEnabled(false);

                // execute the task
                classifyBtnTask.execute();
            }
        });

        // button to return back to camera fragment
        backtoCam_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ClassifyImage.this, ClassificationActivity.class);
                startActivity(i);
            }
        });
    }

    // loads tflite grapg from file
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd(chosen);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // converts bitmap to byte array which is passed in the tflite graph
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // loop through all pixels
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                // get rgb values from intValues where each int holds the rgb values for a pixel.
                // if quantized, convert each rgb value to a byte, otherwise to a float
                if(quant){
                    imgData.put((byte) ((val >> 16) & 0xFF));
                    imgData.put((byte) ((val >> 8) & 0xFF));
                    imgData.put((byte) (val & 0xFF));
                } else {
                    imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                }
            }
        }
    }

    // loads the labels from the label txt file in assets into a string array
    private List<String> loadLabelList() throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open("assets/labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    // print the top labels and respective confidences
    private void printTopKLabels() {
        // add all results to priority queue
        for (int i = 0; i < labelList.size(); ++i) {
            if(quant){
                sortedLabels.add(
                        new AbstractMap.SimpleEntry<>(labelList.get(i), (labelProbArrayB[0][i] & 0xff) / 255.0f));
            } else {
                sortedLabels.add(
                        new AbstractMap.SimpleEntry<>(labelList.get(i), labelProbArray[0][i]));
            }
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }

        // get top results from priority queue
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            topLables[i] = label.getKey();
            topConfidence[i] = String.format("%.0f%%",label.getValue()*100);
        }

        // set the corresponding text eh views with the results
        iOutput.getLabel1().setText("1. "+topLables[2]);
        iOutput.getLabel2().setText("2. "+topLables[1]);
        iOutput.getLabel3().setText("3. "+topLables[0]);
        iOutput.getConfidence1().setText(topConfidence[2]);
        iOutput.getConfidence2().setText(topConfidence[1]);
        iOutput.getConfidence3().setText(topConfidence[0]);
    }


    // resizes bitmap to given dimensions
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}
