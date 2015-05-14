package controllers.couch;

import play.Configuration;
import play.Play;

/**
 * @author siva
 *
 */
public class CouchbaseConfig {
	
	private static Configuration getConfig() {
		return Play.application().configuration();
	}
	
	public static String get(String key) {
		return getConfig().getString(key);
	}

}
