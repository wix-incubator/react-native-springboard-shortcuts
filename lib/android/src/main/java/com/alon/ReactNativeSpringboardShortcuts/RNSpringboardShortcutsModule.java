package com.alon.ReactNativeSpringboardShortcuts;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Icon;
import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

import androidx.annotation.Nullable;

public class RNSpringboardShortcutsModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private final String SHORTCUT_NOT_EXIST = "SHORTCUT_NOT_EXIST";
    private final String DEFAULT_ACTIVITY = "MainActivity";
    private final String ID_KEY = "id";
    private final String SHORT_LABEL_KEY = "shortLabel";
    private final String LONG_LABEL_KEY = "longLabel";
    private final String INTENT_URI_KEY = "intentUri";
    private final String IMAGE_URL_KEY = "imageUrl";


    public RNSpringboardShortcutsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNSpringboardShortcuts";
    }

    @ReactMethod
    public void isShortcutServiceAvailable(Callback callback) {
        callback.invoke(isServiceAvailable());
    }

    @ReactMethod
    public void handleShortcut(Callback successCallback) {
        Activity currentActivity = this.reactContext.getCurrentActivity();
        String shortCutId = currentActivity.getIntent().getStringExtra("shortcutId");
        if (shortCutId != null) {
            successCallback.invoke(shortCutId);
        }
    }

    @ReactMethod
    public void removeShortcut(String id) {
        if (!isServiceAvailable()) {
            return;
        }

        ShortcutManager shortcutManager = (ShortcutManager)getShortCutManager();
        shortcutManager.removeDynamicShortcuts(Arrays.asList(id));
    }

    @ReactMethod
    public void removeAllShortcuts() {
        if (!isServiceAvailable()) {
            return;
        }

        ShortcutManager shortcutManager = (ShortcutManager)getShortCutManager();
        shortcutManager.removeAllDynamicShortcuts();
    }

    @ReactMethod
    public void exists(String id, Promise promise) {

        if (isShortcutExist(id)) {
            promise.resolve(null);
        } else {
            promise.reject(SHORTCUT_NOT_EXIST, "Not found this app shortcut");
        }
    }

    @ReactMethod
    public void addShortcut(ReadableMap shortcutDetails) {
        if (!isServiceAvailable()) {
            return;
        }

        if (isShortcutExist(shortcutDetails.getString(ID_KEY))) return;

        try {
            ShortcutInfo shortcut = (ShortcutInfo)initShortcut(shortcutDetails);
            ShortcutManager shortcutManager = (ShortcutManager)getShortCutManager();
            shortcutManager.addDynamicShortcuts(Arrays.asList(shortcut));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                shortcutManager.requestPinShortcut(shortcut, null);
            }
        } catch (Exception e) {
            Log.d("Alon", "Failed to add a shortcut");
        }
    }

    @ReactMethod
    public void updateShortcut(ReadableMap shortcutDetail) {
        if (!isServiceAvailable()) {
            return;
        }

        if (isShortcutExist(shortcutDetail.getString(ID_KEY))) {

            try {
                ShortcutInfo shortcut = (ShortcutInfo)initShortcut(shortcutDetail);
                ShortcutManager shortcutManager = (ShortcutManager)getShortCutManager();
                shortcutManager.updateShortcuts(Arrays.asList(shortcut));
            } catch (Exception e) {
                Log.d("Alon", "Failed to update a shortcut");
            }

        } else {
            return;
        }
    }

    @Nullable
    private Object initShortcut(ReadableMap shortcutDetail) throws IOException {
        if (!isServiceAvailable()) {
            throw new IOException();
        }


        Activity currentActivity = this.reactContext.getCurrentActivity();
        String intentUri = shortcutDetail.getString(INTENT_URI_KEY);
        Intent intent;

        if (intentUri != null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(intentUri));
        } else {
            intent = new Intent(currentActivity.getApplicationContext(), currentActivity.getClass());
            intent.putExtra("shortcutId", shortcutDetail.getString(ID_KEY));
            intent.setAction(Intent.ACTION_VIEW);
        }

        String imageUrl = shortcutDetail.getString(IMAGE_URL_KEY);
        Bitmap bitmap = drawableFromUrl(imageUrl);

        ShortcutInfo shortcut = new ShortcutInfo.Builder(currentActivity, shortcutDetail.getString(ID_KEY))
                .setShortLabel(shortcutDetail.getString(SHORT_LABEL_KEY))
                .setLongLabel(shortcutDetail.getString(LONG_LABEL_KEY))
                .setIcon(Icon.createWithBitmap(bitmap))
                .setIntent(intent)
                .build();

        return shortcut;
    }

    private boolean isShortcutExist(String id) {
        if (!isServiceAvailable()) {
            return false;
        }

        ShortcutManager shortcutManager = (ShortcutManager)getShortCutManager();
        List<ShortcutInfo> shortcutInfoList = shortcutManager.getDynamicShortcuts();
        for (ShortcutInfo shortcutInfo : shortcutInfoList) {
            if (shortcutInfo.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private Bitmap drawableFromUrl(String url) throws IOException {
        Bitmap bitmap;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-agent","Mozilla/4.0");

        connection.connect();
        InputStream input = connection.getInputStream();

        bitmap = BitmapFactory.decodeStream(input);

        return getCircularBitmap(bitmap);
    }

    private Bitmap getCircularBitmap(Bitmap srcBitmap) {
        int squareBitmapWidth = Math.min(srcBitmap.getWidth(), srcBitmap.getHeight());

        Bitmap dstBitmap = Bitmap.createBitmap(
                squareBitmapWidth, // Width
                squareBitmapWidth, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        Canvas canvas = new Canvas(dstBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Rect rect = new Rect(0, 0, squareBitmapWidth, squareBitmapWidth);
        RectF rectF = new RectF(rect);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        float left = (squareBitmapWidth-srcBitmap.getWidth())/2;
        float top = (squareBitmapWidth-srcBitmap.getHeight())/2;
        canvas.drawBitmap(srcBitmap, left, top, paint);

        srcBitmap.recycle();

        return dstBitmap;
    }

    @Nullable
    private Object getShortCutManager() {
        if (!isServiceAvailable()) {
            return null;
        }
        Activity currentActivity = this.reactContext.getCurrentActivity();
        ShortcutManager shortcutManager = currentActivity.getSystemService(ShortcutManager.class);

        return shortcutManager;
    }

    private boolean isServiceAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;
    }

}