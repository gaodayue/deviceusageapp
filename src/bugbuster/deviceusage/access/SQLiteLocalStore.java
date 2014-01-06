package bugbuster.deviceusage.access;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import bugbuster.deviceusage.access.AppStatistics;

public class SQLiteLocalStore extends LocalStoreService {
	
	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bugbusters_db";
    
    private static final String TABLE_NAME = "app_statistics";
    private static final String COLNAME_PKGNAME	= "pkgname";
    private static final String COLNAME_VERSION	= "version";
    private static final String COLNAME_FGTIME	= "fgtime";	// how long the app's activity is in foreground
    private static final String COLNAME_FGCOUNT	= "fgnum";	// how many times user starts the app's activity
    private static final String COLNAME_BGTIME	= "bgtime";	// how long the app runs services in background
    private static final String COLNAME_BGCOUNT	= "bgnum";	// how many times the app runs services in background
    private static final String COLNAME_SEND	= "sendbytes";		// total bytes send to network
    private static final String COLNAME_RECEIVE	= "receivebytes"; 	// total bytes receive from network
    private static final String COLNAME_DISK	= "diskusage";	// total bytes of app's data usage
    
    private static final String SQL_CREATE_TABLE =
    		"CREATE TABLE '" + TABLE_NAME + "' (" +
    		COLNAME_PKGNAME + " TEXT PRIMARY KEY," +
    		COLNAME_VERSION + " TEXT," +
    		COLNAME_FGTIME	+ " INTEGER," +	// in second
    		COLNAME_FGCOUNT	+ " INTEGER," +
    		COLNAME_BGTIME	+ " INTEGER," +	// in second
    		COLNAME_BGCOUNT	+ " INTEGER," +
    		COLNAME_SEND	+ " BIGINT," +
    		COLNAME_RECEIVE	+ " BIGINT," +
    		COLNAME_DISK	+ " BIGINT);";
    
    private static final String SQL_DROP_TABLE =
    		"DROP TABLE IF EXIST '" + TABLE_NAME + "';";
    
    private static final String WHERE_CLAUSE = COLNAME_PKGNAME + "=?";

	private static class DeviceUsageDBOpenHelper extends SQLiteOpenHelper {
		
		private static DeviceUsageDBOpenHelper instance;
		
		public static DeviceUsageDBOpenHelper getInstance(Context context) {
			if (instance == null) {
				instance = new DeviceUsageDBOpenHelper(context);
			}
			return instance;
		}
        		
        private DeviceUsageDBOpenHelper(Context context) {
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
	}

	@Override
	public void startService() {
		dbhelper = DeviceUsageDBOpenHelper.getInstance(context);
	}

	@Override
	public boolean putAppStatistics(AppStatistics newStat) {
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		
		AppStatistics oldStats = this.findAppByPackage(db, newStat.getPackageName());
		
		// insert a new entry or update existed entry
		if (oldStats == null) {
			return db.insert(TABLE_NAME, null, constructRowFromApp(newStat)) != -1;
			
		} else {
			newStat = oldStats.update(newStat);
			return db.update(
					TABLE_NAME,
					constructRowFromApp(newStat),
					WHERE_CLAUSE,
					new String[] { newStat.getPackageName() }) == 1;
		}
	}

	@Override
	public List<AppStatistics> listAllAppStatistics() {
		List<AppStatistics> apps = new ArrayList<AppStatistics>();
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		Cursor c = db.query(TABLE_NAME, 
							null, // return all columns
							/* selection */null, /* selectionArgs */null,
							/* groupBy */null, /* having */null, /* orderBy */null);
		
		while (c.moveToNext()) {
			apps.add(constructAppFromCursor(c));
		}
		
		return apps;
	}

	@Override
	public void stopService() {
		dbhelper.close();
	}
	
	private AppStatistics findAppByPackage(SQLiteDatabase db, String packageName) {
		Cursor c = db.query(TABLE_NAME,
							null, // return all columns
							WHERE_CLAUSE, new String[] { packageName },
							/* groupBy */null, /* having */null, /* orderBy */null);
		if (c.moveToFirst()) {
			return constructAppFromCursor(c);
		}
		return null;
	}
	
	private AppStatistics constructAppFromCursor(Cursor c) {
		return new AppStatistics(
				c.getString(c.getColumnIndex(COLNAME_PKGNAME)),
				c.getString(c.getColumnIndex(COLNAME_VERSION)),
				c.getInt(c.getColumnIndex(COLNAME_FGTIME)),
				c.getInt(c.getColumnIndex(COLNAME_FGCOUNT)),
				c.getInt(c.getColumnIndex(COLNAME_BGTIME)),
				c.getInt(c.getColumnIndex(COLNAME_BGCOUNT)),
				c.getLong(c.getColumnIndex(COLNAME_SEND)),
				c.getLong(c.getColumnIndex(COLNAME_RECEIVE)),
				c.getLong(c.getColumnIndex(COLNAME_DISK)));
	}
	
	private ContentValues constructRowFromApp(AppStatistics app) {
		ContentValues row = new ContentValues();
		row.put(COLNAME_PKGNAME, app.getPackageName());
		row.put(COLNAME_VERSION, app.getVersion());
		row.put(COLNAME_FGTIME, app.getForegroundTime());
		row.put(COLNAME_FGCOUNT, app.getForegroundCount());
		row.put(COLNAME_BGTIME, app.getBackgroundTime());
		row.put(COLNAME_BGCOUNT, app.getBackgroundCount());
		row.put(COLNAME_SEND, app.getSendBytes());
		row.put(COLNAME_RECEIVE, app.getReceiveBytes());
		row.put(COLNAME_DISK, app.getDiskUsage());
		return row;
	}
}
