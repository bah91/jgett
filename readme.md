# JGett 

JGett is a Client API implementation for Ge.tt ([http://www.ge.tt](http://www.ge.tt)) service.

Thru the client class it is possible to manage, upload and download files to the Ge.tt system to share them into the web.

## Download
This library is built with [Maven](http://maven.apache.org/) and it can be downloaded from the [Sonatype Open Source Repository](https://oss.sonatype.org/content/groups/public/).

To do so you have to include this code into your POM file under the `repositories` section:

	<repositories>
		<repository>
			<id>OSS Sonatype Repository</id>
			<url>https://oss.sonatype.org/content/repositories/releases/</url>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
			<releases>
				<enabled>true</enabled>
			</releases>
		</repository>
	</repositories>
and then add to the `dependencies` section the code:

       <dependency>
			<groupId>it.atcetera</groupId>
			<artifactId>jgett</artifactId>
			<version>1.0</version>
			<scope>compile</scope>
		</dependency>

If you do not use Maven you could download the latest version of JGett [here](#). If you choose to download it manually, please check the Requirement section in order to download the libraries that JGett requires.

## Usage

To manage, upload and download file with Ge.tt system you will use a _JGettClient_ instance.

With this object you can handle all the possibilities that the [Ge.tt Rest API](http://ge.tt/developers/rest) offers.

### Connection initialization and User authentication
To connect to Ge.tt and authenticate a user you use this code:

	:::java
	String apiKey = "yourApiKey";
	String userName = "Ge.tt username";
	String password = "Ge.tt password";
	
	JGettClient client = new JGettClient();
	client.authenticate(apiKey, userName, password);

once your authentication is successful, you could manage your shares thru the client methods.

### Obtaining user and storage information
To obtain information about a Ge.tt account you use this code
	
	::java
	UserInfo me = client.getUserInformation();
	System.out.println("User email" + me.getEMail());
	System.out.println("Available size: " + me.getStorageInfo().getLimitSpace());
	
which provide a `UserInfo` instance with information about user and a `StorageInfo` instance with the information about the size used and available for this account

### Managing a Share

A Share in Ge.tt is a collection of files.

The client can create, destroy and retrieve shares. The information about a share is contained in a `ShareInfo` instance:

	::java
	ShareInfo si = client.createShare("The Test"); // Create a share named "The Test";
	si = client.getShare("The Test"); // Search for a share named "The test"
	List<ShareInfo> shares = client.getShares(); // Retrieve all the shares that belongs to this users
	client.updateShare(si, "The new Test"); // Rename the share
	client.destroyShare(si); // Delete the share "The new test" and all the files that belongs to this share
	
A share basically has a set of information, which can be retrieved with these methods:

	::java
	ShareInfo si = client.getShare("The Test");
	si.getShareTitle();
	si.getShareName();
	si.getFiles();
	si.getCreationDate();
	si.getUrl();  // The URL on the Ge.tt System used to display the share
	
### Managing files

The client can upload, download and remove a file from a share. The information about a file is contained in a `FileInfo` instance:

	::java
	File file = new File();
	FileInfo fi = client.uploadFile(file);					// Overloaded method that creates an anonymous share and upload the file to it
	byte[] data = client.getFileData();						// Retrieve file from Ge.tt system
	fi = client.getFile(fi.getShare(), fi.getFileId());		// Search for a specific file into Ge.tt system
	client.destroyFile(fi);									// Delete the selected file
	
A file also has a set of information, which can be retrieved with these methods:

	::java
	FileInfo fi = client.getFile(client.getShare("Test"), "0");
	fi.getFileName();
	fi.getFileId();
	fi.getNumberOfDownloads();
	fi.getShare();
	fi.getCreationDate();
	fi.getUrl(); // The URL on the Ge.tt System used to download the file thru Ge.tt web interface
	
## Requirements

JGett is built upon:

  - Apache [HttpComponents](http://hc.apache.org/), an HTTP client implementation from Apache Foundation
  - [google-gson](http://code.google.com/p/google-gson/), a Java to JSON serializer - deserializer
  - Apache Commons - [Lang](http://commons.apache.org/lang/) package, commons utility for object serialization
  - [Simple Log Facade for Java](http://www.slf4j.org/), a logging framework for Java
  - [Java Mime Magic Library](http://jmimemagic.sourceforge.net/index.html), a MIME detection library
  
All this dependencies are handled by [Maven](http://maven.apache.org/) by default. 
If you want to use JGett without Maven support, please download this libraries manually.

## Build the code

The build mechanism is entirely managed by [Maven](http://maven.apache.org/).

There is only a caveat to build this package. During the `test` phase, it is mandatory to use a valid Ge.tt account to test the client. 
In order to pass to the test suite the credential you have to write a `testng.xml` file into the `src/test/resources` folder crafted in that way:

	<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >
  
	<suite name="All" verbose="1">
		<parameter name="gettApiKey" value="Ge.tt api key" />
		<parameter name="gettEmail" value="Ge.tt email" />
		<parameter name="gettPassword" value="Ge.tt password" />
		<parameter name="gettPasswordWrong" value="76" />
		
		<test name="All">
			<packages>
				<package name="it.atcetera.jgett" />
			</packages>
		</test>
	</suite>

This file is used by [TestNG](http://testng.org/doc/index.html) to supply the parameters to the test suite.

## License

JGett is distributed under the [LGPL v.3.0](http://www.gnu.org/copyleft/lesser.html) license. 
