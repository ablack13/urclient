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

Add in your <b>Singleton</b> in <b>onCreate()</b>:


	<URClientService.init(getApplicationContext());

<hr>

Create request by builder:

	URClient.create()


#Demonstration


You can see simple example in <b>Sample</b> module

<hr>

#Supported features:

	 [responseListener(OnResponseListener onResponseListener);] - set listener on response from server
 
	 [startListener(OnStartListener StartRequestListener);] - set listener on start request
 
	 [cancelListener(OnCancelListener CancelListener);] - be call after cancel request 
 
	 [cancel();] - cancel request
 
	 [errorHandler(ErrorHandlerImpl errorHandlerImpl);] - create custom error handler for catch from response
 
	 [save(String AccessKey);] - save & and protect your data from server by encoding response

	 [headers(Map headers);] - set custom headers for request

	 [body(Object Body);] - set body for POST request

	 [retryPolice(int timeoutInMillis, int maximumRetry, float BackoffMultiplier);] - set timeout and max retries

	 priority(PRIORITY priority); - request priority: LOW, NORMAL,HIGH, IMMEDIATE

	 send(String url, METHOD type, Class returnObject);  - send request to server
	 
All functions can be use manually


<hr>


#License

