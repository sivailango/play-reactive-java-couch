import controllers.couch.CouchbaseProvider;
import play.Application;
import play.GlobalSettings;

/**
 * @author valore
 *
 */
public class Global extends GlobalSettings {
	
	@Override
	public void onStart(Application app) {
		CouchbaseProvider.connect();
	}
	
	@Override
	public void onStop(Application app) {
		CouchbaseProvider.close();
	}

}
