package se.lisaannica.stopmotion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

/**
 * List of stop motion movies.
 * @author Annica Lindstrom and Lisa Ring
 *
 */
public class MainActivity extends ListActivity {
	private List<String> movies;
	private File movieStorageDir;
	private ArrayAdapter<String> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		movieStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "StopMotionMovies");

		movies = setMovieList(movieStorageDir);
		
		//TODO should we create our own adapter?
		//If we have time we can prettyfy it.
		adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, movies);
		setListAdapter(adapter);

		registerForContextMenu(getListView());
	}
	
	/**
	 * Sets the list with all the movie names.
	 * @param files
	 * @return
	 */
	private List<String> setMovieList(File files)
	{
		List<String> movieList = new ArrayList<String>();
		if (files.exists()) {
			File[] fileList = files.listFiles();
			for (File file: fileList) {
				if ((file.getName()).endsWith(".gif")) {
					movieList.add(file.getName().replaceAll(".gif", ""));
				}
			}
		}
		return movieList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_create_new) {
			Intent intent = new Intent(MainActivity.this, MovieCreator.class);
			this.startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		// TODO maybe we should remove the option to remove and edit?
		super.onCreateContextMenu(menu, view, menuInfo);

		if (view.getId() == getListView().getId()) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(movies.get(info.position).toString());
			menu.add(0, 0, 0, getResources().getString(R.string.main_play));
			menu.add(0, 1, 0, getResources().getString(R.string.main_remove));
			menu.add(0, 2, 0, getResources().getString(R.string.main_share));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		String movieName = movies.get((info.position));
		
		if(item.getItemId() == 0) { //Play movie
			Log.d("main", "MainActivity, onContextItemSelected, item: " + item.getItemId());

			Intent intent = new Intent(MainActivity.this, MoviePlayer.class);
			intent.putExtra("gifName", movieName);
			this.startActivity(intent);
		} else if (item.getItemId() == 1) { //Remove movie
			deleteMovie(movieName);
		} else if (item.getItemId() == 2) {
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			Uri screenshotUri = Uri.parse(movieStorageDir + File.separator + movieName + ".gif");
			
			shareIntent.setType("image/gif");
			shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, screenshotUri);
			
			//For sending text
			//shareIntent.setType("text/plain");
			//shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Testing");		 
			
			//For starting Twitter
		    /*final PackageManager pm = this.getPackageManager();
		    final List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
		    for (final ResolveInfo app : activityList) {
		      if ("com.twitter.android.PostActivity".equals(app.activityInfo.name)) {
		        final ActivityInfo activity = app.activityInfo;
		        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
		        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		        shareIntent.setComponent(name);
		        startActivity(shareIntent);
		        break;
		      }
		    }*/
			
			//Comment this part if you just want to open Twitter.
			startActivity(Intent.createChooser(shareIntent, "Share image using"));
		} 
		return super.onContextItemSelected(item);
	}
	
	/**
	 * Deletes the selected movie.
	 * @param movieName
	 */
	private void deleteMovie(String movieName)
	{
		File gif = new File(movieStorageDir.getPath() + File.separator +
				movieName + ".gif");
		gif.delete();
		
		for(int i = 0; i < movies.size(); i++) {
			if(movieName.equals(movies.get(i))) {
				movies.remove(i);
			}	
		}
		adapter.remove(movieName);
	}
}
