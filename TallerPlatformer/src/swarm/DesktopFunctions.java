package swarm;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import rosalila.taller.platformer.Global;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class DesktopFunctions implements AndroidFunctionsInterface
{

	public static BASE64Encoder base64encoder;
	public static BASE64Decoder base64decoder;
	public static SecretKey key;
	
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
//		Gdx.app.log("DesktopFunctions", "Is Swarm inititated.");
		return true;
	}

	public void SwarmPreload()
	{
		Gdx.app.log("DesktopFunctions", "Swarm preload.");
//		//Encripcion
		base64encoder = new BASE64Encoder();
		base64decoder = new BASE64Decoder();
		try
		{
			System.out.print(getKey("keys/scores.key"));
			DESKeySpec keySpec = new DESKeySpec(getKey("keys/score1.key").getBytes("UTF8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			this.key = keyFactory.generateSecret(keySpec);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
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
	
	//Encription
	public String encript(int num,int level)
	{
//		return ""+num;
		//Fuente http://stackoverflow.com/questions/5220761/fast-and-simple-string-encrypt-decrypt-in-java
		String no_encriptada = ""+num;
		String encriptada = "";
		
		try {
			// ENCODE plainTextPassword String
			byte[] cleartext = no_encriptada.getBytes("UTF8");
			Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
			cipher.init(Cipher.ENCRYPT_MODE, this.key);
			encriptada = base64encoder.encode(cipher.doFinal(cleartext));
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("Score no encontrado.");
		}      
		
		return encriptada;
	}
	
	public int decript(String encriptada,int level)
	{
//		return 0;
		// DECODE encryptedPwd String
		int decodificada = 0;
		try
		{
			byte[] encrypedPwdBytes = base64decoder.decodeBuffer(encriptada);
	
			Cipher cipher = Cipher.getInstance("DES");// cipher is not thread safe
			cipher.init(Cipher.DECRYPT_MODE, this.key);
			byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));
			String str_decodificada = new String(plainTextPwdBytes, "UTF-8");
			decodificada = Integer.parseInt(str_decodificada);
		}catch(Exception e)
		{
//			e.printStackTrace();
			System.out.println("Score no encontrado.");
		}
		return decodificada;
	}
	
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