package application;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javafx.scene.image.Image;

public class ProfilePicHandler 
{
	//define and instantiate variables
	private static final String PROFILE_PIC_DIR = "profile_pics";
    private static final String USER_PROFILE_PIC_FILE = "saved_users_profile_pics.properties";
    private Properties userProfilePics = new Properties();
    
	//constructor
    public ProfilePicHandler()
    {
    	initialize();
    	loadUserProfiles();
    }
    
	//create the directory for the profile pics if it doesn't exist
    public void initialize()
    {
    	File propertiesFile = new File(USER_PROFILE_PIC_FILE); //create File object
    	if (!propertiesFile.exists()) { //if the properties file doesn't exist, create it
    		try {
    			boolean exists = propertiesFile.createNewFile(); //verify if file was created
    			if (exists)
    				System.out.println("ProfilePicHandler: userProfilePics properties file created!");
    			else
    				System.out.println("ProfilePicHandler: userProfilePics properties FAILED to be file created :(");
    		}
    		catch (Exception ex)
        	{
        		System.out.println(ex);
        	}
    	}
    }
    
	//load the user profiles from the properties file
    private void loadUserProfiles() 
    {
    	try (FileInputStream inp = new FileInputStream(USER_PROFILE_PIC_FILE)){	//load the properties file
    		userProfilePics.load(inp);
    		System.out.println("ProfilePicHandler: userProfilePics loaded!");
    	}
    	catch (Exception ex)
    	{
    		System.out.println(ex);
    	}
    }
    
	//get the profile pic of the current user
    public Image getProfilePic(String username)
    {
    	String profilePicDir = userProfilePics.getProperty(username);	//get the profile pic directory
    	if (profilePicDir != null && Files.exists(Paths.get(profilePicDir))) {	//if the profile pic directory exists, return the image
    		System.out.println("ProfilePicHandler: current User profile pic found!");
            return new Image(new File(profilePicDir).toURI().toString());
        } else {	//if the profile pic directory doesn't exist, return the default image
        	System.out.println("ProfilePicHandler: current user doesnt have a profile pic, default loaded.");
            return new Image("application/icon.jpeg");
        }
    }
    
	//save the profile pic of the current user
    public boolean saveProfilePic(String username, File uploadedProfilePic)
    {
    	try {
    		Files.createDirectories(Paths.get(PROFILE_PIC_DIR));	//create the profile pic directory if it doesn't exist
    		
    		File destFile = new File(PROFILE_PIC_DIR, username + "_profile.png");	//create the destination file
    		Files.copy(uploadedProfilePic.toPath(), destFile.toPath()); //copy the uploaded file to the destination file
    		
    		userProfilePics.setProperty(username, destFile.getAbsolutePath()); //set the property of the current user to the destination file
    		try (FileOutputStream out = new FileOutputStream(USER_PROFILE_PIC_FILE)) { //save the properties file
    			userProfilePics.store(out, null);
    			System.out.println("ProfilePicHandler: profile pic updated!");
    		}
    		catch (Exception ex) {
    			System.out.println(ex);
    			return false;
    		}
    	}
    	catch (Exception ex) {
			System.out.println(ex);
			return false;
		}
		return true;
    }
}
