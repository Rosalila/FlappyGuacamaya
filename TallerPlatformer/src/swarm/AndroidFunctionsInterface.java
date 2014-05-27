package swarm;

public interface AndroidFunctionsInterface {
	public boolean IsSwarmInitiated();
	public void SwarmInitiate();
	public void SwarmPreload();
	public void SubmitScore(float credits);
	public void ShowLeaderboardSwarm();
}