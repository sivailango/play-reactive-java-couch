package controllers.couch;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import play.Logger;

import com.couchbase.client.CouchbaseClient;

/**
 * @author siva
 *
 */
public class CouchbaseProvider {

	private static final String HOST = "couchbase.host";
	private static final String PORT = "couchbase.port";
	private static final String BUCKET = "couchbase.bucket";
	private static final String USER = "couchbase.user";
	private static final String PASSWORD = "couchbase.password";
	
	private static CouchbaseClient client = null;
	
	public static void connect() {
		List<URI> uris = new LinkedList<URI>();
        uris.add(URI.create("http://" + CouchbaseConfig.get(HOST) + ":" + CouchbaseConfig.get(PORT) + "/pools"));
 
        try {
        	client = new CouchbaseClient(uris, CouchbaseConfig.get(BUCKET), CouchbaseConfig.get(USER), CouchbaseConfig.get(PASSWORD));
        } 
        catch(IOException e) {
            Logger.error("Exception: Couchbase connection could not established");
            System.exit(0);
        }
	}
	
	public static void close() {
		client.shutdown(3, TimeUnit.SECONDS);
	}
	
	public static CouchbaseClient get() {
		if(client == null) { 
			connect();
		}	
		return client;
	}
	
}
