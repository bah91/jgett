package it.atcetera.jgett;

import java.io.IOException;

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
	@Test(groups = {"auth"})
	@Parameters({"gettApiKey", "gettEmail", "gettPassword"})
	public void testAuthentication(String gettApiKey, String gettEmail, String gettPassword){
		try{
			UserInfo user = client.authenticate(gettApiKey, gettEmail, gettPassword);
			Assert.assertNotNull(user, "Something went wrong with authentication. The UserInfo data is missing!");
		}catch(Exception e){
			Assert.fail("Something went wrong with authentication. Exception had been thrown");
		}
	}
	
	/**
	 * Test wrong client authentication
	 * @param gettApiKey Ge.tt test API key
	 * @param gettEmail Ge.tt test User e - mail
	 * @param gettPassword Ge.tt test User password
	 * @throws AuthenticationException In case of authentication failure
	 */
	@Test(expectedExceptions = AuthenticationException.class, groups = {"auth"})
	@Parameters({"gettApiKey", "gettEmail", "gettPasswordWrong"})
	public void testAuthenticationFailure(String gettApiKey, String gettEmail, String gettPassword) throws AuthenticationException{
		try{
			UserInfo user = client.authenticate(gettApiKey, gettEmail, gettPassword);
			Assert.assertNotNull(user, "Something went wrong with authentication. The UserInfo data is missing!");
		}catch(IOException e){
			Assert.fail("Something went wrong with authentication. Exception had been thrown");
		}
	}
	
	@Test(dependsOnGroups = { "auth" })
	public void testUserInfoRetrieval(){
		try{
			UserInfo user = client.getUserInformation();
			Assert.assertNotNull(user, "Something went wrong with authentication. The UserInfo data is missing!");
		}catch(Exception e){
			Assert.fail("Unable to retrieve user info from an authenticated user", e);
		}
	}
}
