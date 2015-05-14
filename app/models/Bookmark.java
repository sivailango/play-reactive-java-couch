package models;

import java.io.Serializable;
import java.util.Collection;

import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.Stale;

import play.libs.F;
import controllers.couch.crud.CrudSource;

/**
 * @author siva
 *
 */
public class Bookmark implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String id;
	public String type = "bookmark";
	public String url;
	public String timeStamp;
	
	public static CrudSource<Bookmark> bucket = new CrudSource<>(Bookmark.class);
	
	public static void save(Bookmark bookmark) {
		bucket.save(bookmark.id, bookmark);
	}
	
	public static F.Promise<Bookmark> get(String id) {
		return bucket.get(id, Bookmark.class);
	}
	
	public static F.Promise<Collection<Bookmark>> list() {
		Query query = new Query();
		query.setStale(Stale.FALSE);
		query.setIncludeDocs(true);
		return bucket.find("dev_bookmark", "by_all", query, Bookmark.class);
	}
	
	public static void update(Bookmark bookmark) {
		bucket.update(bookmark.id, bookmark, Bookmark.class);
	}
	
	public static void delete(String id) {
		bucket.delete(id);
	}
	
}
