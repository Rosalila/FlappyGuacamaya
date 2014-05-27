package rosalila.taller.platformer;

import java.util.Map;

import swarm.AndroidFunctionsInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmAchievement;
import com.swarmconnect.SwarmAchievement.GotAchievementsMapCB;
import com.swarmconnect.SwarmActiveUser;
import com.swarmconnect.SwarmActiveUser.GotCloudDataCB;
import com.swarmconnect.SwarmLeaderboard;
import com.swarmconnect.SwarmLeaderboard.GotLeaderboardCB;
import com.swarmconnect.SwarmLeaderboard.GotScoreCB;
import com.swarmconnect.SwarmLeaderboardScore;
import com.swarmconnect.delegates.SwarmLoginListener;

public class MainActivity extends AndroidApplication implements AndroidFunctionsInterface{
	
	int loaded_score = 0;
	float readed_barcode;
	boolean loaded_score_ready = false;
	boolean swarm_preloaded = false;
	
	int LEADERBOARD_ID = 16298;
	int GAME_ID = 11396;
	String GAME_KEY = "";
	
	public static SwarmLeaderboard leaderboards;
	public static String yourGameCloudData = "0,1";
	
	Map<Integer, SwarmAchievement> achievements;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        
        Swarm.setActive(this);
        
//        if ( Swarm.isEnabled() ) {
//        	SwarmPreload();
//            Swarm.init(this, GAME_ID, GAME_KEY);
//        }
        
        initialize(new TallerPlatformer(this), cfg);
    }
    
    
	// Swarm
	@Override
	public void onResume() {
		super.onResume();
		try {
			Swarm.setActive(this);
		} catch (Exception e) {
		}
	}

	// Swarm
	@Override
	public void onPause() {
		super.onPause();
		try {
			Swarm.setInactive(this);
		} catch (Exception e) {
		}
	}

	// Swarm
	private SwarmLoginListener mySwarmLoginListener = new SwarmLoginListener() {
		// This method is called when the login process has started
		// (when a login dialog is displayed to the user).
		public void loginStarted() {
		}

		// This method is called if the user cancels the login process.
		public void loginCanceled() {

		}

		GotAchievementsMapCB callback = new GotAchievementsMapCB() {
			public void gotMap(Map<Integer, SwarmAchievement> achievements) {
				// Store the map of achievements somewhere to be used later.
				MainActivity.this.achievements = achievements;
			}
		};
		
		// This method is called when the user has successfully logged in.
		public void userLoggedIn(SwarmActiveUser user) {
			// Load our Leaderboard
			try {
				SwarmLeaderboard.getLeaderboardById(LEADERBOARD_ID,
						new GotLeaderboardCB() {
							public void gotLeaderboard(SwarmLeaderboard lb) {
								leaderboards = lb;

								// Load previous score
								if (loaded_score == -1) {
									// position=22;
									GotScoreCB got_score = new GotScoreCB() {
										@Override
										public void gotScore(
												SwarmLeaderboardScore arg0) {
											// TODO Auto-generated method stub
											if (arg0 != null)
												loaded_score = (int) arg0.score;
											else
												loaded_score = 0;
										}
									};
									if (Swarm.user != null)
										leaderboards.getScoreForUser(
												Swarm.user, got_score);
								}
							}
						});
			} catch (Exception e) {
			}

			try {
				SwarmAchievement.getAchievementsMap(callback);
			} catch (Exception e) {
			}
		}

		// This method is called when the user logs out.
		public void userLoggedOut() {

		}

	};

	// Swarm
	public boolean IsSwarmInitiated() {
		return Swarm.isInitialized();
	}
	
	// Swarm
	public void SwarmPreload()
	{		
		GAME_KEY = getKey("crypt_keys/test.cript");
//		Swarm.preload(this, GAME_ID, GAME_KEY);
		swarm_preloaded=true;
//		System.out.print("Testa"+GAME_KEY);
//		Toast.makeText(getBaseContext(), GAME_KEY, Toast.LENGTH_LONG).show();
	}

	// Swarm
	public void SwarmInitiate() {
		// Ensure it runs on UI thread
		MainActivity.this.runOnUiThread(new Runnable()
		{
			public void run()
			{
				if(!swarm_preloaded)
					SwarmPreload();
				Swarm.setLeaderboardNotificationsEnabled(true);
				try {
					if (!Swarm.isInitialized()) {
						Swarm.init(MainActivity.this, GAME_ID,  GAME_KEY,
								mySwarmLoginListener);
					}
				} catch (Exception e) {
				}
			}
		});
	}

	
	 final Handler handler = new Handler() {
	        public void handleMessage(Message msg) {
	              if(msg.arg1 == 1)
	            	  	Toast.makeText(getBaseContext(), "C", Toast.LENGTH_LONG).show();
	        }
	    };
	
	// Swarm
	public void SubmitScore(float score) {
		// Swarm submit score – uses an interface AndroidFunctionsInterface
		// that allows calls from the main project.
		



		 
		 
		try {
			if (MainActivity.leaderboards != null) {
				// Leaderboards come in 3 types, INTEGER, FLOAT, and TIME
				// TIME Leaderboards are submitted in seconds.
				// We can optionally pass in String data which can be retrieved
				// from
				// a score at a later time to implement things like replays.
				MainActivity.leaderboards.submitScore(score, null, null);
			}else
			{
				 Message msg = handler.obtainMessage();
				 msg.arg1 = 1;
				 handler.sendMessage(msg);
			}
		} catch (Exception e) {
		}
	}

	// Swarm
	public void ShowLeaderboardSwarm() {
		// Swarm Leader board show – uses an interface AndroidFunctionsInterface
		// that allows calls from the main project.

		// Ensure it runs on UI thread
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				try {
					Swarm.showLeaderboards();
				} catch (Exception e) {
				}
			}
		});
	}

	// // Swarm
	public void SwarmYourGameCloudDataSave(String theYourGameCloudData) {
		// Save to the cloud
		if (Swarm.isLoggedIn()) {
			Swarm.user.saveCloudData("YourGameCloudData", theYourGameCloudData);
		}
	}

	// Swarm
	GotCloudDataCB callback1 = new GotCloudDataCB() {
		public void gotData(String data) {
			// Did our request fail (network offline, and uncached)?
			if (data == null) {
				// Handle failure case.
				MainActivity.yourGameCloudData = "";
				return;
			}

			// Has this key never been set? Default it to a value…
			if (data.length() == 0) {
				data = "0,1";
			}

			// Parse the level data for later use
			MainActivity.yourGameCloudData = data;
		}
	};
	
	public static String getKey(String path)
	{
		try
		{
			FileHandle file = Gdx.files.internal(path);
			String str = file.readString();
			System.out.println(str);
			//Removing \n at the end
			return str.substring(0, str.length()-1);
		}catch(Exception e)
		{
			System.out.print("Flappy error: " + path + " not found");
		}
		return "error";
	}
}