package com.example.barcodedetect;


// FACE DETECTION : https://codelabs.developers.google.com/codelabs/face-detection/index.html?index=..%2F..%2Findex#1
// 이미지, 영상에서 눈, 코, 입도 검출 가능. 하지만 recognition은 아님. recognition은 누구의 것인지 까지.
// 얼굴이 field of view에서 벗어났다 다시 들어오면 이전에 검출됐던 것으로 인식하지 못한다.
// Important: This is not a face recognition API.
// Instead, the new API simply detects areas in the image or video that are human faces.
// It also infers from changes in the position frame to frame that faces in consecutive frames of video are the same face.
// If a face leaves the field of view, and re-enters, it isn't recognized as a previously detected face.

// Face Detection에 쓸 API

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 버튼 클릭 시, 이미지를 load하고 faces 위해 precess하고 찾아진 얼굴들에 빨간 네모가 씌워진다.

                // 얼굴에 빨간 네모 씌우기 위해 bitmap이 mutable 해야 한다.

                ImageView myImageView = (ImageView) findViewById(R.id.imgview);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable=true;
                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.test2,
                        options);

                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(5); // 굵기 5
                myRectPaint.setColor(Color.RED); // 빨간색
                myRectPaint.setStyle(Paint.Style.STROKE); // 막대기 스타일

               // 결과물을 저장할 이미지 저장소(Canvas). myBitmap 위에 그림 그리는거니 그과 동일한 사이즈로 만든다.
                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null); // Canvas에 myBitmap 넣음

                // FaceDector
                FaceDetector faceDetector = new
                        FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                        .build();
                if(!faceDetector.isOperational()){ // detector 실행 가능한지. 실행 못 하면 메세지 창 띄워줌.
                    new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                    return;
                }


                // 여러 개 얼굴 가져올 수 있으므로 array로 받아옴.

                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Face> faces = faceDetector.detect(frame);

                // 찾을 수 있는 모든 얼굴에 대해서 사각형 표시를 해준다.
                for(int i=0; i<faces.size(); i++) { // faces.size()는 검출된 얼굴의 숫자이다.
                    Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x; // 얼굴 위치 x좌표
                    float y1 = thisFace.getPosition().y; // 얼굴 위치 y좌표
                    float x2 = x1 + thisFace.getWidth(); // 얼굴 너비
                    float y2 = y1 + thisFace.getHeight(); // 얼굴 높이
                    tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                }

                myImageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap)); // 얼굴 화면에 보여줌 & 그에 맞게 사각형 그려줌

            }

        });


    }
}

/* Barcode 코딩

// Barcode 관련해서 쓸 API들
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btn = (Button) findViewById(R.id.button); // btn과 이미 만들어놓은 UI의 button 연결
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 클릭 하면 your code goes here
                TextView txtView = (TextView) findViewById(R.id.txtContent);


                ImageView myImageView = (ImageView) findViewById(R.id.imgview); // myImageView와 이미 만들어놓은 UI의 imgview 연결
                Bitmap myBitmap = BitmapFactory.decodeResource( // puppy의 확장자를 bitmap으로 변환
                        getApplicationContext().getResources(),
                        R.drawable.puppy);
                myImageView.setImageBitmap(myBitmap); // bitmap으로 변환된 puppy 이미지를 myImageView에 담음


                // QR코드 형식이나 data matrices 형식 바코드 detect
                // So we need to check if our detector is operational before we use it.
                // If it isn't, we may have to wait for a download to complete,
                // or let our users know that they need to find an internet connection
                // or clear some space on their device.

                BarcodeDetector detector =
                        new BarcodeDetector.Builder(getApplicationContext())
                                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                                .build();
                if(!detector.isOperational()){ // 만드는데 실패하면 에러메세지 띄움
                    txtView.setText("Could not set up the detector!");
                    return;
                }

                // 여러 개의 바코드를 array로 세트로 받는다.

                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Barcode> barcodes = detector.detect(frame);

                // 여러 개의 바코드 중 첫 번재 바코드를 불러와서 textView에 넣는다.
                // Typically in this step you would iterate through the SparseArray, and process each bar code independently.
                // Usually, we need to allow for the possibility that there won't be any barcodes,or there might be several.
                // Because for this sample, I know I have 1 and only 1 bar code, I can hard code for it.
                // To do this, I take the Barcode called ‘thisCode' to be the first element in the array.
                // I then assign it's rawValue to the textView -- and that's it -- it's that simple!

                Barcode thisCode = barcodes.valueAt(0);
                txtView.setText(thisCode.rawValue);
            }
        });
    }
}
*/
