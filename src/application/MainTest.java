package application;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bridj.ann.Template;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MainTest {

	 private ProfilePicHandler profilePicHandler;
	 private static final String USER_PROFILE_PIC_FILE = "saved_users_profile_pics.properties";

	    Path tempDir;

	    @Before
	    void setUp() {
	        //set up the ProfilePicHandler with a temporary directory for testing
	        System.setProperty("user.dir", tempDir.toString());
	        profilePicHandler = new ProfilePicHandler();
	    }

	    @Test
	    void testInitializeCreatesFile() {
	        //check that the properties file is created
	        File propertiesFile = new File(USER_PROFILE_PIC_FILE);
	        assertTrue(propertiesFile.exists());
	    }
	    
	  //~~~DATABASE PORTION~~~
	 //test to ensure the SQL connection can be established
	    @Test
	    public void testSqlConnection() {
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "passywassy")) {
	            assertNotNull(conn);
	        } catch (SQLException e) {
	            fail("Connection failed: " + e.getMessage());
	        }
	    }

	    //test to check if the ArticleDB and required tables exist
	    @Test
	    public void testDatabaseExists() {
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ArtFaceDB", "root", "passywassy");
	             ResultSet rsDB = conn.getMetaData().getCatalogs()) {

	            boolean dbExists = false;
	            while (rsDB.next()) {
	                if ("ArtFaceDB".equals(rsDB.getString(1))) {
	                    dbExists = true;
	                    break;
	                }
	            }
	            assertTrue(dbExists, "Database ArtFaceDB does not exist.");

	        } catch (SQLException e) {
	            fail("Database check failed: " + e.getMessage());
	        }
	    }
	    
	  //~~~API PORTION~~~
	    //test to see if txt2img api is online
	    @Test
	    public void testIMGTWOIMGAPI() {
	        JSONObject body = new JSONObject();
	        body.put("text", "TESTIMP" + "TESTSTYLE");

	        HttpResponse<InputStream> response;
			try {
				response = Unirest.post("https://open-ai21.p.rapidapi.com/texttoimage2")
				        .header("content-type", "application/json")
				        .header("X-RapidAPI-Key", "8b2bd64aa5msh34f679538ef2433p1e4a2djsn927a54490a26")
				        .header("X-RapidAPI-Host", "open-ai21.p.rapidapi.com")
				        .body(body.toString())
				        .asBinary();
				assertTrue((response.getStatus() == 200), "TXT2IMG API IS OFFLINE OR NONFUNCTIONAL");
			} catch (UnirestException e) {
				e.printStackTrace();
			}
	    }

	    /*
	    @Test
	    void testGetProfilePicReturnsDefaultForNewUser() {
	        // Check that a new user gets the default profile picture
	        Image defaultPic = profilePicHandler.getProfilePic("newuser");
	        assertNotNull(defaultPic);
	        // Further checks can be added to verify the default image content
	    }

	    @Test
	    void testSaveProfilePicAndRetrieve() {
	        // Save a profile picture for a user
	        String username = "testuser";
	        File testImageFile = new File(tempDir.toFile(), "testimage.png");
	        // Create a test image file in the temp directory
	        // ... (code to create a test image file) ...

	        assertTrue(profilePicHandler.saveProfilePic(username, testImageFile));

	        // Retrieve the saved profile picture
	        Image retrievedPic = profilePicHandler.getProfilePic(username);
	        assertNotNull(retrievedPic);
	        // Further checks can be added to verify the retrieved image content
	    }
	*/
	
}
