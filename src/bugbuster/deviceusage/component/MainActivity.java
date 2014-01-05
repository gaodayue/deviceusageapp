package bugbuster.deviceusage.component;

import java.util.List;

import bugbuster.deviceusage.R;
import bugbuster.deviceusage.access.AppStatistics;
import bugbuster.deviceusage.access.GoogleAnalyticsUploader;
import bugbuster.deviceusage.access.LocalStoreService;
import bugbuster.deviceusage.access.SQLiteLocalStore;
import bugbuster.deviceusage.access.UploaderService;
import bugbuster.deviceusage.utils.AsycExecutor;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	Button startBtn;
	Button stopBtn;
	Button uploadBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startBtn = (Button) this.findViewById(R.id.btn_startservice);
		stopBtn = (Button) this.findViewById(R.id.btn_stopservice);
		uploadBtn = (Button) this.findViewById(R.id.btn_upload);
		
		startBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent service = new Intent(MainActivity.this, DeviceUsageService.class);
				startService(service);
			}
		});
		
		stopBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent service = new Intent(MainActivity.this, DeviceUsageService.class);
				stopService(service);
			}
		});
		
		uploadBtn.setOnClickListener(new UploaderListener(getApplicationContext()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	class UploaderListener implements OnClickListener {
		Context context;
		
		AsycExecutor executor;
		
		List<AppStatistics> allStats;
		
		public UploaderListener(Context context) {
			this.context = context;
		}

		@Override
		public void onClick(View v) {
			executor = new AsycExecutor();
			
			executor.asyncRun(new Runnable() {
				public void run() {
					LocalStoreService localStore = new SQLiteLocalStore(context);
					localStore.startService();
					allStats = localStore.listAllAppStatistics();
				}
			});
			
			executor.asyncRun(new Runnable() {
				public void run() {
					UploaderService uploader = new GoogleAnalyticsUploader(context);
					uploader.startService();
					for (AppStatistics stat : allStats) {
						Log.v("UploaderListener", "upload " + stat);
						uploader.uploadAppStatistics(stat);
					}
					uploader.stopService();
				}
			});
			
			executor.quit();
		}
		
	}

}
