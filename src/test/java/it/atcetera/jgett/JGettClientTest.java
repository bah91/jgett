package it.atcetera.jgett;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Test Case for JGett Client
 * 
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 *
 */
public class JGettClientTest {
	
	/**
	 * Object that had to be tested
	 */
	private JGettClient client = new JGettClient();

	/**
	 * Test client authentication 
	 * @param gettApiKey Ge.tt test API key
	 * @param gettEmail Ge.tt test User e - mail
	 * @param gettPassword Ge.tt test User password
	 */
	@Test
	@Parameters({"gettApiKey", "gettEmail", "gettPassword"})
	public void testAuthentication(String gettApiKey, String gettEmail, String gettPassword){
		UserInfo user = client.authenticate(gettApiKey, gettEmail, gettPassword);
		Assert.assertNotNull(user, "Something went wrong with authentication. The UserInfo data is missing!");
	}

}
