package com.cisco.dft.seed.api.util;

import java.io.IOException;

import com.cisco.dft.seed.api.pojo.HttpResponseEntity;

import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

/**
 * Responsible to make HTTP requests.
 * 
 * @author sujmuthu
 * @version 1.0
 * @date January 22, 2015
 */
public class APIConnectionUtil {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(APIConnectionUtil.class);

	/**
	 * Invokes a HTTP request, the requestBody attribute should be null for GET
	 * and DELETE
	 * 
	 * @param url
	 * @param httpMethod
	 * @param requestBody
	 * @return
	 */
	public static HttpResponseEntity invokeHttpRequest(String url,
			HttpMethod httpMethod, String requestBody, String authStringEnc) {

		LOGGER.info("Invoking http " + httpMethod.toString()
				+ " request on url: " + url);
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpResponseEntity responseEntity = new HttpResponseEntity();
		HttpMessage request = null;
		HttpResponse response;
		try {

			if (httpMethod == HttpMethod.GET) {
				request = new HttpGet(url);
			} else if (httpMethod == HttpMethod.POST) {
				request = new HttpPost(url);
			} else if (httpMethod == HttpMethod.PUT) {
				request = new HttpPut(url);
			} else if (httpMethod == HttpMethod.DELETE) {
				request = new HttpDelete(url);
			}

			request.setHeader("Authorization", "Basic " + authStringEnc);
			request.addHeader("content-type", "application/json");

			if (requestBody != null) {
				StringEntity params = new StringEntity(requestBody);
				((HttpEntityEnclosingRequestBase) request).setEntity(params);
			}

			response = httpClient.execute((HttpUriRequest) request);
			responseEntity.setStatusCode(response.getStatusLine()
					.getStatusCode());
			if (response.getEntity() != null)
				responseEntity.setEntity(EntityUtils.toString(
						response.getEntity(), "UTF-8"));
		} catch (IOException ex) {
			LOGGER.error("An error occurred:\n", ex);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				LOGGER.error("Error(s) occurred:\n", e);
			}
		}
		return responseEntity;
	}
}
