package it.atcetera.jgett;

import java.io.IOException;
import java.util.List;

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
	
	/**
	 * Test the retrieval of Ge.tt user information
	 */
	@Test(dependsOnGroups = { "auth" })
	public void testUserInfoRetrieval(){
		try{
			UserInfo user = client.getUserInformation();
			Assert.assertNotNull(user, "Something went wrong with authentication. The UserInfo data is missing!");
		}catch(Exception e){
			Assert.fail("Unable to retrieve user info from an authenticated user", e);
		}
	}
	
	/**
	 * Test the creation of a Ge.tt Share
	 */
	@Test(dependsOnGroups = { "auth" }, groups = { "share" })
	public void testShares(){
		ShareInfo si = null;
		try{
			si = client.createShare("The Test");
			Assert.assertEquals(si.getTitle(), "The Test", "Something went wrong with share creation. The share title is mismatching!");
		}catch(Exception e){
			Assert.fail("Unable to create a new Ge.tt share", e);
			return;
		}
		
		// Next retrieve all the Shares
		try{
			List<ShareInfo> shareList = client.getShares();
			Assert.assertNotEquals(shareList.size(), 0, "Ge.tt Share number mismatch while retrieving all shares");
		}catch(Exception e){
			Assert.fail("Unable to retrieve all Ge.tt shares associated to an user", e);
			return;
		}
		
		// Retrieve a single share
		try{
			ShareInfo ssi = client.getShare(si.getShareName());
			Assert.assertEquals(ssi.getTitle(), "The Test", "Something went wrong with share retrieval. The share title is mismatching!");
		}catch(Exception e){
			Assert.fail("Unable to retrieve a Ge.tt share associated to an user", e);
			return;
		}
		
		// Retrieve a wrong single share
		try{
			ShareInfo ssi = client.getShare("ndknvdlvnd");
			Assert.assertNull(ssi, "Retrieved an invalid Ge.tt share!");
		}catch(Exception e){
			Assert.fail("Unable to retrieve a wrong Ge.tt share associated to an user", e);
			return;
		}
	}
	
}
