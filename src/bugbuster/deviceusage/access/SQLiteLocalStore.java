package bugbuster.deviceusage.access;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import bugbuster.deviceusage.access.AppStatistics;

public class SQLiteLocalStore extends LocalStoreService {

	private static class DeviceUsageDBOpenHelper extends SQLiteOpenHelper {
		
		private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "bugbusters_db";
        
        private static final String TABLE_NAME = "app_statistics";
        private static final String COLNAME_PKGNAME	= "pkgname";
        private static final String COLNAME_VERSION	= "version";
        private static final String COLNAME_FGTIME	= "fgtime";	// how long the app's activity is in foreground
        private static final String COLNAME_FGCOUNT	= "fgnum";	// how many times user starts the app's activity
        private static final String COLNAME_BGTIME	= "bgtime";	// how long the app runs services in background
        private static final String COLNAME_BGCOUNT	= "bgnum";	// how many times the app runs services in background
        private static final String COLNAME_TRAFFIC	= "traffics";	// total network traffics
        private static final String COLNAME_DISK	= "diskusage";	// total app data's disk usage
        // private static final String COLNAME_NUM_UPDATES	= "num_updates";
        
        private static final String SQL_CREATE_TABLE =
        		"CREATE TABLE '" + TABLE_NAME + "' (" +
        		COLNAME_PKGNAME + " TEXT," +
        		COLNAME_VERSION + " TEXT," +
        		COLNAME_FGTIME	+ " INTEGER," +	// in second
        		COLNAME_FGCOUNT	+ " INTEGER," +
        		COLNAME_BGTIME	+ " INTEGER," +	// in second
        		COLNAME_BGCOUNT	+ " INTEGER," +
        		COLNAME_TRAFFIC + " INTEGER," +	// in KB
        		COLNAME_DISK	+ " INTEGER);";
        
        private static final String SQL_DROP_TABLE =
        		"DROP TABLE IF EXIST '" + TABLE_NAME + "';";
        		
        public DeviceUsageDBOpenHelper(Context context) {
        	super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(SQL_DROP_TABLE);
			this.onCreate(db);
		}
	}
	
	private DeviceUsageDBOpenHelper dbhelper;
	
	public SQLiteLocalStore(Context context) {
		super(context);
		dbhelper = new DeviceUsageDBOpenHelper(context);
	}

	@Override
	public void startService() {
		// nothing to do
	}

	@Override
	public boolean putAppStatistics(AppStatistics app) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<AppStatistics> listAllAppStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopService() {
		dbhelper.close();
	}
}
