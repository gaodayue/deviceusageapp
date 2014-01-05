package bugbuster.deviceusage.component;

import bugbuster.deviceusage.R;
import bugbuster.deviceusage.access.AppStatistics;
import bugbuster.deviceusage.access.GoogleAnalyticsUploader;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	Button startBtn;
	Button stopBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startBtn = (Button) this.findViewById(R.id.btn_startservice);
		stopBtn = (Button) this.findViewById(R.id.btn_stopservice);

		startBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent service = new Intent(MainActivity.this,
						DeviceUsageService.class);
				startService(service);
			}
		});

		stopBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent service = new Intent(MainActivity.this,
						DeviceUsageService.class);
				stopService(service);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
