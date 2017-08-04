package com.sieae.jamaicaobserver;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Mohammad Arshi Khan
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
	
	// Class for store/retrieve data in database.
	private static Context ourContext;
	private static final String DATABASE_TABLE = GlobalVariables.DATABASE_TABLE;
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "rssarticlestatus.db";
	private SQLiteDatabase ourDatabase;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.ourContext=context;
		// TODO Auto-generated constructor stub
	}
		
	public void createDatabase() throws IOException{
		boolean dbExist = checkDatabase();
		if(dbExist){
			//do nothing - database already exist
		}else{
			 //By calling this method an empty database will be created into the default system path
			//of application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();
			try{
			copyDatabase();
			}catch(Exception e){
				Log.d("DBError", "Copy DB error");
			}
			
		}
	}
	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */
	private void copyDatabase() throws IOException{
		//Open your local db as the input stream
		InputStream myInput = ourContext.getAssets().open(DATABASE_NAME);
		
		// Path to the just created empty db
		String outFileName = GlobalVariables.DB_PATH + DATABASE_NAME;
		
		//Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		
		//transfer bytes from the inputfile to the outputfile
		byte buffer[] = new byte[1024];
		
		int len;
		while ((len = myInput.read(buffer))>0) {
			myOutput.write(buffer, 0, len);
			
		}
		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
		
	}
	
	 /**
	  * Check if the database already exist to avoid re-copying the file each time you open the application.
	  * @return true if it exists, false if it doesn't
	  */
	private boolean checkDatabase(){
		
		SQLiteDatabase checkDB = null;
		try{
//		String myPath = DB_PATH + DATABASE_NAME;
//		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		File dbFile = new File(GlobalVariables.DB_PATH + DATABASE_NAME);
	    return dbFile.exists();
	    
		}catch(Exception e){
			Log.d("DBError", "check existing db " + e.toString());
		}
		if(checkDB != null){
			checkDB.close();
		}
//		return checkDB != null ? true : false;
		return false;
		
	}
	
	public void openDatabase() throws SQLException{
		//Open the database
		String myPath = GlobalVariables.DB_PATH + DATABASE_NAME;
		ourDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}
	
	public void close() 
	{
		if(ourDatabase != null){
			ourDatabase.close();
		}
		
		super.close();
		 
	}
	
	public long adddata(String dbtable, String[] Columns, String[] ColumnsValue) 
	{
		// TODO Auto-generated method stub
		// add the custom Image Gallery Image Path to Data Base
			
		ourDatabase = getWritableDatabase();
		
		if (ourDatabase.isOpen()) 
		{
	
			ContentValues cv = new ContentValues();
			
			for (int i = 0; i < Columns.length; i++) {  // i indexes each element successively.
			    cv.put(Columns[i], ColumnsValue[i]);
			    
			}
	
			return ourDatabase.insert(dbtable, null, cv);
		}
		else
		{
		  return 0;
		}  
	}
	
	public long updatedata(String dbtable, String[] Columns, String[] ColumnsValue, String where) 
	{
		// TODO Auto-generated method stub
		// add the custom Image Gallery Image Path to Data Base
			
		ourDatabase = getWritableDatabase();
		
		if (ourDatabase.isOpen()) 
		{
	
			ContentValues cv = new ContentValues();
			
			for (int i = 0; i < Columns.length; i++) {  // i indexes each element successively.
			    cv.put(Columns[i], ColumnsValue[i]);
			    
			}
	
			
			return ourDatabase.update(dbtable, cv, where, null);
		}
		else
		{
		  return 0;
		}  
	}
	
	public Cursor getAlldata(String dbtable, String[] Columns, String Condition, String[] args, String groupby, String having, String orderby) 
	{
		Cursor details = null;
		if (ourDatabase.isOpen() == false)
		ourDatabase = getWritableDatabase();
		
		if (ourDatabase.isOpen()) 
		{
			details = ourDatabase.query(dbtable, Columns, Condition, args, groupby, having, orderby);
		}
		
		return details;
	}
	
	public long deleteRecord(String dbtable, String condition) 
	{
		
	    ourDatabase = getWritableDatabase();
	
	    if (ourDatabase.isOpen()) 
		{
			return ourDatabase.delete(dbtable, condition, null);
		}
		return 0;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		onCreate(db);

	}

}
