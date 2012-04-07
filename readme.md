# JGett 

JGett is a Client API implementation for Ge.tt ([http://www.ge.tt](http://www.ge.tt)) service.

Thru the client class it is possible to manage, upload and download files to the Ge.tt system to share them into the web.

## Download
This library is built with [Maven](http://maven.apache.org/) and it can be downloaded from the [Sonatype Open Source Repository](https://oss.sonatype.org/content/groups/public/).

To do so you have to include this code into your POM file under the `repositories` section:

	<repositories>
		<repository>
			<id>Codehaus Snapshots</id>
			<url>http://nexus.codehaus.org/snapshots/</url>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
			<releases>
				<enabled>false</enabled>
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


`String apiKey = "yourApiKey";`
`String userName = "Ge.tt username";`
`String password = "Ge.tt password";`
``
`JGettClient client = new JGettClient();`
`client.authenticate(apiKey, userName, password);`


once your authentication is successful, you could manage your shares thru the client methods.

## Requirements

JGett is built upon:

  - Apache [HttpComponents](http://hc.apache.org/), an HTTP client implementation from Apache Foundation
  - [google-gson](http://code.google.com/p/google-gson/), a Java to JSON serializer - deserializer
  - Apache Commons - [Lang](http://commons.apache.org/lang/) package, commons utility for object serialization
  - [Simple Log Facade for Java](http://www.slf4j.org/), a logging framework for Java
  - [Java Mime Magic Library](http://jmimemagic.sourceforge.net/index.html), a MIME detection library
  
All this dependencies are handled by [Maven](http://maven.apache.org/) by default. 
If you want to use JGett without Maven support, please download this libraries manually.