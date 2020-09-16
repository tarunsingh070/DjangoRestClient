/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */
package tarun.djangorestclient.com.djangorestclient.model

/**
 * Model class to store all information received as response for a particular request.
 */
class RestResponse
/**
 * Constructor.
 *
 * @param responseCode    The response code received.
 * @param responseTime    The response time.
 * @param url             The URL of the response.
 * @param responseHeaders The list of response Headers received as a String.
 * @param responseBody    The response body received.
 */(val responseCode: Int, val responseTime: Long, val url: String,
    val responseHeaders: CharSequence, val responseBody: String)