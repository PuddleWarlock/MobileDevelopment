package ru.mirea.reznikap.data.ml;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;
import java.util.Map;

public class BirdClassifier {

    private static final String MODEL_FILENAME = "birds_model.tflite";
    private static final String LABELS_FILENAME = "labels.txt";
    private static final int INPUT_SIZE = 224;

    private final Context context;
    private Interpreter interpreter;
    private List<String> labels;

    public BirdClassifier(Context context) {
        this.context = context;
        initInterpreter();
    }

    private void initInterpreter() {
        try {
            // Загрузка модели и меток из assets
            MappedByteBuffer modelFile = FileUtil.loadMappedFile(context, MODEL_FILENAME);
            labels = FileUtil.loadLabels(context, LABELS_FILENAME);

            Interpreter.Options options = new Interpreter.Options();
            interpreter = new Interpreter(modelFile, options);

            Log.d("BirdClassifier", "Model loaded successfully");
        } catch (IOException e) {
            Log.e("BirdClassifier", "Error loading model", e);
        }
    }

    public String classify(Bitmap bitmap) {
        if (interpreter == null) {
            return "Ошибка: Модель не загружена";
        }

        try {
            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                    .build();

            TensorImage tensorImage = new TensorImage(DataType.UINT8);
            tensorImage.load(bitmap);
            tensorImage = imageProcessor.process(tensorImage);

            TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, labels.size()}, DataType.UINT8);

            interpreter.run(tensorImage.getBuffer(), outputBuffer.getBuffer().rewind());

            Map<String, Float> labeledProbability = new TensorLabel(labels, outputBuffer).getMapWithFloatValue();

            String maxLabel = "Неизвестно";
            float maxProbability = 0.0f;

            for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
                if (entry.getValue() > maxProbability) {
                    maxProbability = entry.getValue();
                    maxLabel = entry.getKey();
                }
            }

            Log.d("BirdClassifier", "Result: " + maxLabel + " (" + maxProbability + ")");


            if (maxProbability < 0.3f) {

                return "Птица не распознана";
            }

            return maxLabel;

        } catch (Exception e) {
            Log.e("BirdClassifier", "Classification failed", e);
            return "Ошибка классификации";
        }
    }
}
