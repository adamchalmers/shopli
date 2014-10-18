package com.adamschalmers.shoplimono;

public class Url {

	/*
	 * Returns true iff supplied url is a valid Taste.com.au recipe
	 */
	public static boolean isTasteUrl(String url) {
		if (url.startsWith("http://www.taste.com.au/recipes") || url.startsWith("https://www.taste.com.au/recipes") ) {
			return true;
		}
		return false;
	}
	
	public static String addHttp(String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		} else {
			return "http://" + url;
		}
	}
}
