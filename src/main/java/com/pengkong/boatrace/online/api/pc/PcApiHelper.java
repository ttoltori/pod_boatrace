package com.pengkong.boatrace.online.api.pc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PcApiHelper {

	String getCenterNoFromMeta(Document doc) {
		Elements metaTags = doc.getElementsByTag("meta");
		for (Element metaTag : metaTags) {
			if (metaTag.attr("name").equals("centerNo")) {
				return metaTag.attr("content");
			}
		}
		return null;
	}

	String getCsrfFromMeta(Document doc) {
		Elements metaTags = doc.getElementsByTag("meta");
		for (Element metaTag : metaTags) {
			if (metaTag.attr("name").equals("_csrf_token")) {
				return metaTag.attr("content");
			}
		}
		return null;
	}
	
	String getRFromLoginForm(Document doc) {
		Element loginForm = doc.getElementById("loginForm");
		String attr = loginForm.attr("action");
		String[] token = attr.split("=");
		return token[token.length - 1];
	}

	String getTokenFromInputTag(Document doc) {
		return doc.getElementsByAttributeValue("name", "token").get(0).attr("value");
	}
	
}
