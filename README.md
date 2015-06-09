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


Return by URClient object must implements <b>IResponseObject</b> interface


Add in your <b>Singleton</b> in <b>onCreate()</b>:


	<URClientService.init(getApplicationContext());

<hr>

Create request by builder:
```java
	 public static URClient.Builder authorizate(OnResponseListener onResponseListener, OnCancelListener onCancelListener) {
        return URClient.create()
                .responseListener(onResponseListener)
                .cancelListener(onCancelListener)
                .send("URL_for_send_request", URClient.METHOD.GET, ReturnedObject.class);
    }
```


Example in <b>sample</b> module

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

	 [priority(PRIORITY priority);] - request priority: LOW, NORMAL,HIGH, IMMEDIATE

	 [send(String url, METHOD type, Class returnObject);]  - send request to server
	 
All functions can be use manually


<hr>

#Acknowledgements


Thanks <b>Google</b> for <b>Volley</b> :)


<hr>


#License


 	Copyright 2015 Andrew Prayzner

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

