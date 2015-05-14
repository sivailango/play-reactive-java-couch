package controllers.provider;

import com.google.gson.Gson;

/**
 * @author siva
 *
 */
public class GsonProvider {
	
	private static Gson gson;
	
	private GsonProvider() {
		
	}
	
	public static Gson get() {
		if(gson == null) {
			synchronized (GsonProvider.class) {
				if(gson == null) {
					gson = new Gson();
				}
			}
		}
		return gson;
	}

}
