#URClient
URClient is a RESTful client for Android ( based on Google Volley) that provides an easy way to create Client-Server application


<hr>


#Integration

<h3>Gradle</h3>
The lib is available on <b>JitPack.io</b>, you can find it with Gradle, please add next sections into your <b>build.gradle</b>:

	repositories {
	    maven {
	        url "https://jitpack.io"
	    }
	}
	
	
	dependencies {
	        compile 'com.github.scijoker:urclient:0.1.2'
	}
	
<h3>Maven</h3>
Grab via Maven:

	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
	
	
	<dependency>
	    <groupId>com.github.scijoker</groupId>
	    <artifactId>urclient</artifactId>
	    <version>0.1.2</version>
	</dependency>


<hr>


#Usage
<i><b>IMPORTANT!</b></i>: 

Create sigleton (extends Application).Add in <b>onCreate()</b>:


<URClientService.init(getApplicationContext());

<hr>


#Demonstration


You can see si,ple example in <Sample> module

<hr>

#Supported features:
- 
