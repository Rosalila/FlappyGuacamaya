package swarm;

import com.badlogic.gdx.Gdx;

public class DesktopFunctions implements AndroidFunctionsInterface
{
	public void SubmitScore(float score)
	{
		Gdx.app.log("DesktopFunctions", "Submit Score would have been score: " + score);
	}
	
	public void ShowLeaderboardSwarm()
	{
		Gdx.app.log("DesktopFunctions", "Show Leaderboard Swarm");
	}
	
	public boolean IsSwarmInitiated()
	{
		Gdx.app.log("DesktopFunctions", "Is Swarm inititated.");
		return true;
	}

	public void SwarmInitiate()
	{
		Gdx.app.log("DesktopFunctions", "Swarm inititate.");
	}
	
	public void SwarmUnlockAchievement(int AchievementID)
	{
		Gdx.app.log("DesktopFunctions", "Swarm Unlock Achievement ID:" + AchievementID);
	}
	
	public void SwarmYourGameCloudDataSave(String theYourGameCloudData)
	{
		Gdx.app.log("DesktopFunctions", "Swarm YourGame Cloud Data  Save:" + theYourGameCloudData);
	}
	
	public void SwarmYourGameCloudDataLoad()
	{
		Gdx.app.log("DesktopFunctions", "Swarm YourGame Cloud Data Load");
	}

	public String SwarmYourGameCloudDataGet()
	{
		Gdx.app.log("DesktopFunctions", "Swarm YourGame Cloud Data Get");
		return "405,1";
	}
	
	public float getScore()
	{
		Gdx.app.log("DesktopFunctions", "Swarm YourGame Get Score");
		return 1;
	}
	
	public float getReadedBarcode()
	{
		Gdx.app.log("DesktopFunctions", "Requesting readed barcode");
		return 1;
	}
	
	public void readBarCode()
	{
		Gdx.app.log("DesktopFunctions", "Reading BarCode");
	}
}