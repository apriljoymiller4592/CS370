package application;
import static org.junit.jupiter.api.Assertions.*;



import org.junit.jupiter.api.Test;

import com.mashape.unirest.http.exceptions.UnirestException;

class JUnit_Tests {

	@Test
	void testAPI() {
		Main mainTest = new Main();
		try {
			mainTest.generateImageFromText(null, null);
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

}
