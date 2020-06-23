package com.example.flip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FilterAdapter.OnClickFilter {
    static {
        System.loadLibrary("NativeImageProcessor");
    }


    private ImageView img;
    private Button btnFlip;
    private Matrix matrix = new Matrix();
    private HorizontalProgressWheelView progress;
    float[] v = new float[9];
    private DisplayMetrics displayMetrics;
    private FilterAdapter adapter;
    private List<ThumbnailItem> thumbnailItemList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView rvFilter;
    int drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initRecycle();
        drawable = R.drawable.dog;
        displayMetrics = getResources().getDisplayMetrics();

        Glide.with(this).load(R.drawable.blend5).into(img);
        bindDataToAdapter();

//
//      matrix = img.getImageMatrix();
//        float scaleFactor = img.getWidth()/(float)img.getDrawable().getIntrinsicWidth();
//        matrix.setScale(scaleFactor, scaleFactor, 0, 0);
//        img.setImageMatrix(matrix);

        btnFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMatrixAngle(matrix);
            }
        });

        progress.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScrollStart() {

            }

            @Override
            public void onScroll(float delta, float totalDistance) {
                rotation(-delta / 42);
            }

            @Override
            public void onScrollEnd() {

            }
        });

    }

    private void initRecycle() {
        thumbnailItemList = new ArrayList<>();

    }

    private void initView() {
        img = (ImageView) findViewById(R.id.img);
        btnFlip = (Button) findViewById(R.id.btn_flip);
        progress = (HorizontalProgressWheelView) findViewById(R.id.progress);
        rvFilter = (RecyclerView) findViewById(R.id.rv_filter);
    }

    private void rotation(float angle) {
        matrix.postRotate(angle, img.getWidth() / 2, img.getHeight() / 2);
        img.setImageMatrix(matrix);
        img.invalidate();
    }

    private void getMatrixAngle(Matrix matrix) {
        matrix.getValues(v);
        float rAngle = Math.round(Math.atan2(v[Matrix.MSKEW_X], v[Matrix.MSCALE_X]) * (180 / Math.PI));
        Toast.makeText(MainActivity.this, rAngle + "", Toast.LENGTH_SHORT).show();
    }

    private void bindDataToAdapter() {
        final Context context = this.getApplication();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), drawable), 640, 640, false);
                ThumbnailsManager.clearThumbs();
                List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

                for (Filter filter : filters) {
                    ThumbnailItem thumbnailItem = new ThumbnailItem();
                    thumbnailItem.image = thumbImage;
                    thumbnailItem.filter = filter;
                    ThumbnailsManager.addThumb(thumbnailItem);
                }
                //List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);
                thumbnailItemList = ThumbnailsManager.processThumbs(context);
                adapter = new FilterAdapter(MainActivity.this, thumbnailItemList);
                adapter.setOnClickFilter(MainActivity.this);
                linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false);
                rvFilter.setLayoutManager(linearLayoutManager);
                rvFilter.setAdapter(adapter);
            }
        };
        handler.post(r);
    }

    @Override
    public void onClickFilter(Filter filter) {
        img.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getApplicationContext().getResources(), drawable), img.getWidth(), img.getHeight(), false)));
    }


}