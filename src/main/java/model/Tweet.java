package model;

import com.google.gson.JsonObject;

public class Tweet {
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	String id;
	String tag;
	String lat;
	String lang;

	public static boolean isValidTweet(JsonObject jsonObj) {
		if (jsonObj == null)
			return false;
		if (!jsonObj.has("coordinates") || jsonObj.get("coordinates").isJsonNull())
			return false;
		if (!jsonObj.get("coordinates").getAsJsonObject().get("type").getAsString().equalsIgnoreCase("point"))
			return false;
		if (jsonObj.get("entities").getAsJsonObject().get("hashtags").getAsJsonArray().size() == 0)
			return false;
		
		return true;
	}
}
