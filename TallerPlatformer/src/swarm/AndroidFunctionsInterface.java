package swarm;

public interface AndroidFunctionsInterface {
	public boolean IsSwarmInitiated();
	public void SwarmInitiate();
	public void SwarmPreload();
	public void SubmitScore(float credits);
	public void ShowLeaderboardSwarm();
	//Encription
	public String encript(int num,int level);
	public int decript(String encriptada,int level);
	public void showInterstitial();
}