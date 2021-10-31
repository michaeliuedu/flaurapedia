package gg.mic.vanguard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.snackbar.Snackbar;
import com.soundcloud.android.crop.Crop;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserMenu extends AppCompatActivity {

    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private HorizontalBarChart mBarChart;

    private  int imageSizeX;
    private  int imageSizeY;
    private  TensorBuffer outputProbabilityBuffer;
    private  TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Bitmap bitmap;
    private List<String> labels;

    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntriesArrayList;
    ImageView imageView;
    Uri imageuri;
    Button buclassify;
    ImageButton back;
    WebView wiki;
    TextView prediction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        imageView=(ImageView)findViewById(R.id.image);
        buclassify=(Button)findViewById(R.id.classify);
        prediction=(TextView)findViewById(R.id.predictions);
        back = (ImageButton) findViewById(R.id.back);

        wiki = (WebView) findViewById(R.id.wiki);

        wiki.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        mBarChart = findViewById(R.id.chart);
        mBarChart.setNoDataText("");
        mBarChart.setNoDataTextTypeface(Typeface.SANS_SERIF);
        imageView.setImageResource(R.drawable.flower);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prompt_media(findViewById(android.R.id.content).getRootView());
            }
        });

        try{
            tflite=new Interpreter(loadmodelfile(UserMenu.this));
        }catch (Exception e) {
            e.printStackTrace();
        }

        buclassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bitmap != null) {
                    int imageTensorIndex = 0;
                    int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
                    imageSizeY = imageShape[1];
                    imageSizeX = imageShape[2];
                    DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

                    int probabilityTensorIndex = 0;
                    int[] probabilityShape =
                            tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
                    DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

                    inputImageBuffer = new TensorImage(imageDataType);
                    outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                    probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

                    inputImageBuffer = loadImage(bitmap);

                    System.out.println(bitmap.getWidth() + "x" + bitmap.getHeight());
                    System.out.println(inputImageBuffer.getWidth() + "x" + inputImageBuffer.getHeight());

                    tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());
                    showresult();
                }
                else{
                    Snackbar fail = Snackbar.make(findViewById(android.R.id.content).getRootView(), "Please select an image by clicking the flower icon before processing it!", 2000);
                    fail.show();
                }
            }

        });

    }

    private void prompt_media(View view) {



        View mediaView = null;

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);

        try {
            mediaView = inflater.inflate(R.layout.select_media, null);
        } catch (InflateException e) {
        }

        int width = view.getWidth();
        int height = view.getHeight();
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(mediaView, (width), height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        ImageButton camera = (ImageButton) mediaView.findViewById(R.id.take_camera);
        ImageButton media = (ImageButton) mediaView.findViewById(R.id.media_button);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCamera();
                popupWindow.dismiss();
            }
        });

        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestMedia();
                popupWindow.dismiss();
            }
        });
    }




    public void requestCamera(){
        if (!Handler.hasPermissions(this, android.Manifest.permission.CAMERA)){
            Snackbar fail = Snackbar.make(findViewById(android.R.id.content).getRootView(), "You restricted access to your camera from Vanguard! Please change this in your settings", 2000);
            fail.show();
        }
        else{
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

            imageuri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            startActivityForResult(intent, 13);
        }
    }

    public void requestMedia(){
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        if (!Handler.hasPermissions(this, PERMISSIONS)){
            Snackbar fail = Snackbar.make(findViewById(android.R.id.content).getRootView(), "You restricted access to your camera roll from Vanguard! Please change this in your settings", 2000);
            fail.show();
        }
        else{
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),12);
        }

    }


    public void back(){
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    public static void barchart(BarChart barChart, ArrayList<BarEntry> arrayList, final ArrayList<String> xAxisValues) {
        barChart.setDrawBarShadow(false);
        barChart.setFitBars(true);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(25);
        barChart.setPinchZoom(true);

        barChart.setDrawGridBackground(true);
        BarDataSet barDataSet = new BarDataSet(arrayList, "Plant Types");
        barDataSet.setColors(new int[]{Color.parseColor("#03A9F4"), Color.parseColor("#FF9800"),
                Color.parseColor("#76FF03"), Color.parseColor("#E91E63"), Color.parseColor("#2962FF")});
        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
        barData.setValueTextSize(0f);

        barChart.setBackgroundColor(Color.TRANSPARENT); //set whatever color you prefer
        barChart.setDrawGridBackground(false);
        barChart.animateY(2000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(13f);
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setDrawGridLines(false);

        barChart.setData(barData);

    }


    private TensorImage loadImage(final Bitmap bitmap) {

        inputImageBuffer.load(bitmap);

        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();

        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("plant_model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }




    private void showresult(){

        try{
            labels = FileUtil.loadLabels(UserMenu.this,"labels-plants.txt");


        }catch (Exception e){
            e.printStackTrace();
        }

        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();

        float maxValueInMap =(Collections.max(labeledProbability.values()));

        List<Float> list = new ArrayList<Float>(labeledProbability.values());
        Collections.sort(list, Collections.reverseOrder());
        List<Float> top5 = list.subList(0, 5);

        Map<String, Float> barValues = new HashMap<>();

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            for(Float f : top5){
                if (entry.getValue()==f) {
                    barValues.put(entry.getKey(), entry.getValue());
                }
            }
        }

        Map.Entry<String, Float> maxEntry = null;

        for (Map.Entry<String, Float> entry : barValues.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }
        System.out.println("Largest Probability: " + maxEntry.getKey());

        if(maxEntry.getKey().toString().equals("background")){
            Snackbar fail = Snackbar.make(findViewById(android.R.id.content).getRootView(), "No plants were detected! The majority of the image consists of background", 2000);
            fail.show();
        }else {
            wiki.loadUrl("https://en.m.wikipedia.org/wiki/".concat(maxEntry.getKey().toString()));
        }


        final WebSettings webSettings = wiki.getSettings();
        Resources res = getResources();
        float fontSize = res.getDimension(R.dimen.txtSize);
        webSettings.setDefaultFontSize((int)fontSize);

        for (Map.Entry<String, Float> entry : barValues.entrySet()) {
            String[] label = barValues.keySet().toArray(new String[0]);
            Float[] label_probability = barValues.values().toArray(new Float[0]);

            mBarChart.getXAxis().setDrawGridLines(false);
            mBarChart.getAxisLeft().setDrawGridLines(false);

            ArrayList<BarEntry> barEntries = new ArrayList<>();
            for(int i=0; i<label_probability.length; i++)
            {
                barEntries.add(new BarEntry(i, label_probability[i]*100));
            }

            ArrayList<String> xAxisName = new ArrayList<>();
            for(int i=0;i<label.length; i++)
            {
                xAxisName.add(label[i]);
            }
            barchart(mBarChart,barEntries,xAxisName);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==12 && resultCode==RESULT_OK && data!=null) {
            imageuri = data.getData();
            try {
                imageView.setImageURI(data.getData());
                Uri source_uri = imageuri;
                Uri dest_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(source_uri, dest_uri).asSquare().start(this);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), source_uri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 13 && resultCode == RESULT_OK && data!=null) {
            try {
                imageView.setImageURI(imageuri);
                Uri source_uri = imageuri;
                Uri dest_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(source_uri, dest_uri).asSquare().start(this);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            imageView.setImageURI(Crop.getOutput(data));
        }
    }
}

