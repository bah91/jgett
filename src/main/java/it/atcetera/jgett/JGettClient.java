package it.atcetera.jgett;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Client used to interact with <a href="http://www.ge.tt" target="_blank">Ge.tt</a> services thru REST API
 * 
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 *
 */
public class JGettClient {
	
	/**
	 * Timeout, in millisecond, for an http request
	 */
	private static final int HTTP_REQUEST_TIMEOUT = 1000;
	
	/**
	 * Logging Facility
	 */
	private static final Logger logger = LoggerFactory.getLogger(JGettClient.class);
	
	/**
	 * Base URL for Ge.tt API requests
	 */
	private static final String GETT_BASE_URL = "https://open.ge.tt";
	
	/**
	 * Login URL for Ge.tt API
	 */
	private static final String GETT_LOGIN_URL = "/1/users/login";
	
	/**
	 * Ge.tt API User information URL 
	 */
	private static final String GETT_ME_URL = "/1/users/me";
	
	/**
	 * Access token obtained after authentication
	 */
	private String accessToken;
	
	/**
	 * Expiration date for this access
	 */
	private Date expirationDate;

	/**
	 * Java to JSON mapper
	 */
	private Gson gson = new Gson();
	
	/**
	 * Http client used to interact with Ge.tt API
	 */
	private HttpClient httpClient = null;
	
	/**
	 * Refresh token obtained after authentication
	 */
	private String refreshToken;
	
	/**
	 * Check if a method that calls Ge.tt API could be invoked (i.e. user is authenticated and not exipred).<br>
	 * If the authentication is expired, this method invoke the reauthentication mechanism
	 * @return <code>true</code> if the invocation could be performed, <code>false</code> otherwise
	 * @throws IOException In case of problem duting comunication with Ge.tt Services
	 */
	private boolean checkPreconditions() throws IOException{
		if (!this.isAuthenticated()){
			String message = "You must be authenticated on Ge.tt before using this method. Check \"authenticate\" method.";
			if (logger.isErrorEnabled()){
				logger.error(message);
			}
			return false;
		}
		try{
			if (this.isExpired()){
				this.reAuthenticateUser();
			}
		}catch(AuthenticationException ae){
			if (logger.isErrorEnabled()){
				logger.error("Unable to reauthenticate the current user. Check log for details.");
			}
			return false;
		}
		
		return true;
	}
	
	/**
	 * Get an instance of a http client used to interact with Ge.tt API.<br>
	 * This client is created using singleton pattern and it has all the common request parameters set. 
	 * 
	 * @return A {@link HttpClient} used to interact with Ge.tt API with all parameters set
	 */
	private HttpClient getHttpClient(){
		if (this.httpClient == null){
			this.httpClient = new DefaultHttpClient();
			this.httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
			this.httpClient.getParams().setParameter("http.socket.timeout", new Integer(HTTP_REQUEST_TIMEOUT));
			this.httpClient.getParams().setParameter("http.protocol.content-charset", "utf-8");
			
			// Library version
			Properties p = new Properties();
			try {
				p.load(this.getClass().getResourceAsStream("/version.properties"));
				StringBuilder sb = new StringBuilder();
				sb.append(p.get("jgett.version.name"));
				sb.append(" ");
				sb.append(p.get("jgett.version.number"));
				sb.append(" ");
				sb.append(p.get("jgett.version.build"));
				if (logger.isDebugEnabled()){
					logger.debug("Library version used as http user-agent: [{}]", sb.toString());
				}
				this.httpClient.getParams().setParameter("http.useragent", sb.toString());
			} catch (IOException e) {
				if (logger.isWarnEnabled()){
					logger.warn("Unable to get library version information. Version will be not set into http requests.");
				}
			}
		}
		return this.httpClient;
	}
	
	/**
	 * Make a GET HTTP 1.1 request to an HTTP Server
	 * 
	 * @param url A {@link String} containing the URL where to post data
	 * @param params A {@link Map} of name - value parameters that will be encoded into the post string as GET parameters. 
	 * It can be <code>null</code> if no parameters are necessary
	 * @return A {@link String} containing the response body or <code>null</code> if HTTP response != 200
	 * @throws ClientProtocolException When there is a protocol mismatch on HTTP
	 * @throws IOException In case of generic error
	 */
	private String makeGetRequest(String url, Map<String, String> params) throws ClientProtocolException, IOException{
		HttpClient c = this.getHttpClient();
		if (params != null){
			url = url + this.toQueryString(params);
		}
		HttpGet get = new HttpGet(url);
		if (logger.isDebugEnabled()){
			logger.debug("Make a GET call to URL [{}]", url);
		}

		HttpResponse response = c.execute(get);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
			String message = MessageFormat.format("Unable to obtain the result from URL [{0}]. The server response with status code [{1}]", 
					url,  
					response.getStatusLine().getStatusCode());
			if (logger.isWarnEnabled()){
				logger.warn(message);
			}
			// Deallocate connection
			EntityUtils.consume(response.getEntity());
			return null;
		}
		String responseBody = EntityUtils.toString(response.getEntity());
		if (logger.isDebugEnabled()){
			logger.debug("Obtained response body: [{}]", responseBody);
		}
		
		// Deallocate connection
		EntityUtils.consume(response.getEntity());

		return responseBody;
	}	
	
	/**
	 * Make a POST HTTP 1.1 request to an HTTP Server
	 * 
	 * @param url A {@link String} containing the URL where to post data
	 * @param body A {@link String} containing the body to post
	 * @return A {@link String} containing the response body or <code>null</code> if HTTP response != 200
	 * @throws ClientProtocolException When there is a protocol mismatch on HTTP
	 * @throws IOException In case of generic error
	 */
	private String makePostRequest(String url, String body) throws ClientProtocolException, IOException{
		return this.makePostRequest(url, body, null);
	}
	/**
	 * Make a POST HTTP 1.1 request to an HTTP Server
	 * 
	 * @param url A {@link String} containing the URL where to post data
	 * @param body A {@link String} containing the body to post
	 * @param params A {@link Map} of name - value parameters that will be encoded into the post string as GET parameters (as Ge.tt required this strange behavior). 
	 * It can be <code>null</code> if no parameters are necessary
	 * @return A {@link String} containing the response body or <code>null</code> if HTTP response != 200
	 * @throws ClientProtocolException When there is a protocol mismatch on HTTP
	 * @throws IOException In case of generic error
	 */
	private String makePostRequest(String url, String body, Map<String, String> params) throws ClientProtocolException, IOException{
		HttpClient c = this.getHttpClient();
		if (params != null){
			url = url + this.toQueryString(params);
		}
		HttpPost post = new HttpPost(url);
		if (logger.isDebugEnabled()){
			logger.debug("Make a POST call to URL [{}]", url);
		}
		StringEntity se = new StringEntity(body, "application/json", "utf-8");
		post.setEntity(se);
		HttpResponse response = c.execute(post);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
			String message = MessageFormat.format("Unable to obtain the result from URL [{0}] and body [{1}]. The server response with status code [{2}]", 
					url, 
					body, 
					response.getStatusLine().getStatusCode());
			if (logger.isWarnEnabled()){
				logger.warn(message);
			}
			// Deallocate connection
			EntityUtils.consume(response.getEntity());
			return null;
		}
		String responseBody = EntityUtils.toString(response.getEntity());
		if (logger.isDebugEnabled()){
			logger.debug("Obtained response body: [{}]", responseBody);
		}
		
		// Deallocate connection
		EntityUtils.consume(response.getEntity());
		
		return responseBody;
	}
	
	private UserInfo reAuthenticateUser() throws IOException, AuthenticationException{
		if (!this.isAuthenticated()){
			String message = "Unable to reauth a user that is not yet authenticated.";
			if (logger.isErrorEnabled()){
				logger.error(message);
			}
			throw new IllegalAccessError(message);
		}
		if (logger.isDebugEnabled()){
			logger.debug("Reauthenticating user with refresh token [{}]", this.refreshToken);
		}
		String loginURL = JGettClient.GETT_BASE_URL + JGettClient.GETT_LOGIN_URL;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("refreshtoken", this.refreshToken);
		
		// JSON data used into the request body
		String request = this.gson.toJson(parameters);
		String body = this.makePostRequest(loginURL, request);
		if (body == null){
			throw new AuthenticationException(MessageFormat.format(
					"Unable to authenticate user with refresh token [{0}].", 
					this.refreshToken)
				);
		}
		
		AuthenticationResponse ar = this.gson.fromJson(body, AuthenticationResponse.class);
		// Save tokens into the client
		this.accessToken = ar.getAccessToken();
		this.refreshToken = ar.getRefreshToken();
		// Set the expiration date
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, (new Long(ar.getExpires()).intValue()));
		this.expirationDate = c.getTime();
		if (logger.isDebugEnabled()){
			logger.debug("Authentication expiration date: [{}]", this.expirationDate);
			logger.debug("Authentication succeded for user [{}]", ar.getUser().getFullName());
		}
		return ar.getUser();

	}
	
	/**
	 * Convert a {@link Map} of parameters into a standard Query String
	 * @param params A {@link Map} of name value parameters needed to be converted
	 * @return A {@link String} that contains the correctly encoded params
	 */
	private String toQueryString(Map<String, String> params){
		ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : params.entrySet()){
			p.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return "?" + URLEncodedUtils.format(p, "utf-8");
	}
	
	/**
	 * Authenticate a Ge.tt user into the Ge.tt system
	 * 
	 * @param apiKey A {@link String} that contains the unique Ge.tt API Key associated with the consumer app
	 * @param eMail A {@link String} that contains the Ge.tt User E - Mail
	 * @param password A {@link String} that contains the Ge.tt User password
	 * @return An {@link UserInfo} object with the information of Ge.tt user
	 * @throws IOException In case of generic IO Error on HTTP communication
	 * @throws AuthenticationException If the authentication of this user fails
	 */
	public UserInfo authenticate(
			String apiKey,
			String eMail,
			String password) throws IOException, AuthenticationException{
		String loginURL = JGettClient.GETT_BASE_URL + JGettClient.GETT_LOGIN_URL;
		if (logger.isDebugEnabled()){
			logger.debug(MessageFormat.format(
					"Authenticating user [{0}] using URL [{1}] and Ge.tt API key [{2}]...",
					eMail,
					loginURL,
					apiKey
				)
			);
		}
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("apikey", apiKey);
		parameters.put("email", eMail);
		parameters.put("password", password);
		
		// JSON data used into the request body
		String request = this.gson.toJson(parameters);
		String body = this.makePostRequest(loginURL, request);
		if (body == null){
			throw new AuthenticationException(MessageFormat.format(
					"Unable to authenticate user [{0}] with API key [{1}], is the password and the API key correct?", 
					eMail, 
					apiKey)
				);
		}
		
		AuthenticationResponse ar = this.gson.fromJson(body, AuthenticationResponse.class);
		// Save tokens into the client
		this.accessToken = ar.getAccessToken();
		this.refreshToken = ar.getRefreshToken();
		// Set the expiration date
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, (new Long(ar.getExpires()).intValue()));
		this.expirationDate = c.getTime();
		if (logger.isDebugEnabled()){
			logger.debug("Authentication expiration date: [{}]", this.expirationDate);
			logger.debug("Authentication succeded for user [{}]", ar.getUser().getFullName());
		}
		return ar.getUser();
	}
	
	/**
	 * Retrieve Current Ge.tt logged in user information
	 * @return A {@link UserInfo} implementation containing the data of the current logged in user
	 * @throws IOException 
	 */
	public UserInfo getUserInformation() throws IOException{
		if (!this.checkPreconditions()){
			throw new IllegalAccessError("Unable to perform the request to Ge.tt service. Check if the user is correctly authenticated.");
		}
		String meUrl = JGettClient.GETT_BASE_URL + JGettClient.GETT_ME_URL;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("accesstoken", this.accessToken);
		String body = this.makeGetRequest(meUrl, parameters);
		if (body == null){
			String message = MessageFormat.format("Unable to retrieve user information using access token [{0}].", this.accessToken);
			if (logger.isErrorEnabled()){
				logger.error(message);
			}
			throw new IOException(message);
		}
		return this.gson.fromJson(body, UserInfoImpl.class);
	}
	
	/**
	 * States if this client is authenticated against Ge.tt services
	 * @return <code>true</code> if the client is authenticated, <code>false</code> otherwise
	 */
	public boolean isAuthenticated(){
		return ((this.accessToken != null) && (this.refreshToken != null) && (this.expirationDate != null));
	}
	
	/**
	 * States if this client session over Ge.tt services is expired 
	 * @return <code>true</code> if the client is expired, <code>false</code> otherwise
	 */
	public boolean isExpired(){
		if (!this.isAuthenticated()){
			return true;
		}
		
		if (this.expirationDate == null){
			return true;
		}
		
		Date now = new Date();
		return now.after(this.expirationDate);
	}

}

/**
 * Internal class used to implement {@link StorageInfo} interface
 * 
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 */
class StorageInfoImpl implements StorageInfo{
	
	/**
	 * Space used by the user
	 */
	@SerializedName("used")
	private long usedSpace = 0;
	/**
	 * Max space available by the user
	 */
	@SerializedName("limit")
	private long limitSpace = 0;
	
	/**
	 * Extra space graned to the user (already present in limitSpace)
	 */
	@SerializedName("extra")
	private long extraSpace = 0;

	@Override
	public long getUsedSpace() {
		return usedSpace;
	}

	@Override
	public long getLimitSpace() {
		return limitSpace;
	}

	@Override
	public long getExtraSpace() {
		return extraSpace;
	}
	
	/**
	 * Set the Ge.tt user used space
	 * @param usedSpace a long that contains the used space
	 */
	public void setUsedSpace(long usedSpace) {
		this.usedSpace = usedSpace;
	}

	/**
	 * Set the Ge.tt user limit space
	 * @param limitSpace a long that contains the limit space
	 */
	public void setLimitSpace(long limitSpace) {
		this.limitSpace = limitSpace;
	}

	/**
	 * Set the Ge.tt user extra space
	 * @param extraSpace a long that contains the extra earned space by the user
	 */
	public void setExtraSpace(long extraSpace) {
		this.extraSpace = extraSpace;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	/**
	 * Default constructor
	 */
	public StorageInfoImpl() {}
	
}

/**
 * Represents a correct Authentication response obtained by the Ge.tt services
 * 
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 *
 */
class AuthenticationResponse{
	/**
	 * Time after this authorization will expire
	 */
	private long expires;
	
	/**
	 * Access token obtained from authentication
	 */
	@SerializedName("accesstoken")
	private String accessToken;
	
	/**
	 * Refresh token obtained from authentication
	 */
	@SerializedName("refreshtoken")
	private String refreshToken;
	
	/**
	 * User information obtained from authentication
	 */
	@SerializedName("user")
	private UserInfoImpl user;

	/**
	 * After how many seconds this authentication will expire
	 * @return A long that represents after how many seconds this authentication will expire
	 */
	public long getExpires() {
		return expires;
	}

	/**
	 * Access token obtained from this authentication
	 * @return A {@link String} that contains the Ge.tt access token
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * Refresh token obtained from this authentication
	 * @return A {@link String} that contains the Ge.tt refresh token
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * Information on Ge.tt authenticated user
	 * @return A {@link UserInfo} object with the information about the user
	 */
	public UserInfo getUser() {
		return user;
	}

	/**
	 * Set the expiration login time
	 * @param expires A long that represents after how many seconds this authentication will expire 
	 */
	public void setExpires(long expires) {
		this.expires = expires;
	}

	/**
	 * Set the Ge.tt access token
	 * @param accessToken A {@link String} that contains the Ge.tt access token
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Set the Ge.tt refresh token
	 * @param refreshToken  A {@link String} that contains the Ge.tt refresh token
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * Set the Ge.tt user information
	 * @param user A {@link UserInfoImpl} that contains the Ge.tt user information
	 */
	public void setUser(UserInfoImpl user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	
}

/**
 * Internal class used to implement {@link UserInfo} interface.
 * 
 * @author Gian Luca Dalla Torre <g.dallatorre@atcetera.it>
 * @version $Id$
 *
 */
class UserInfoImpl implements UserInfo{
	
	/**
	 * Ge.tt unique user identification
	 */
	@SerializedName("userid")
	private String userId;
	/**
	 * Ge.tt User Full Name
	 */
	@SerializedName("fullname")
	private String fullName;
	/**
	 * Ge.tt User e - mail
	 */
	@SerializedName("email")
	private String eMail;
	/**
	 * Information on User storage
	 */
	@SerializedName("storage")
	private StorageInfoImpl storageInfo;

	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public String getFullName() {
		return this.fullName;
	}

	@Override
	public String getEMail() {
		return this.eMail;
	}

	@Override
	public StorageInfo getStorageInfo() {
		return this.storageInfo;
	}
	
	/**
	 * Set the Ge.tt user unique identifier
	 * @param userId A {@link String} that contains Ge.tt User Unique identifier
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Set the Ge.tt user full name as registered into the service
	 * @param fullName A {@link String} containing the User full name
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Set the Ge.tt user email as registered into the service
	 * @param eMail A {@link String} containing the User email
	 */
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	/**
	 * Set the Ge.tt user {@link StorageInfo}
	 * @param storageInfo a {@link StorageInfoImpl} structure used to 
	 */
	public void setStorageInfo(StorageInfoImpl storageInfo) {
		this.storageInfo = storageInfo;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	

	/**
	 * Default constructor
	 */
	public UserInfoImpl() {}
	
}
