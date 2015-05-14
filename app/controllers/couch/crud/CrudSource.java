package controllers.couch.crud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import net.spy.memcached.PersistTo;
import net.spy.memcached.ReplicateTo;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.gson.Gson;

import controllers.couch.CouchbaseProvider;
import controllers.provider.GsonProvider;

/**
 * @author siva
 *
 */
public class CrudSource<T> {

	public final Class<T> clazz;
	public String id;
	
	private static CouchbaseClient bucket = CouchbaseProvider.get();
	private static Gson gson = GsonProvider.get();
	
	public CrudSource(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public F.Promise<Void> save(final String key, final T value) {
		return F.Promise.promise(new Function0<Void>() {
			@Override
			public Void apply() throws Throwable {
				bucket.set(key, gson.toJson(value), PersistTo.ZERO, ReplicateTo.ZERO);
				return null;
			}
		});
	}
	
	public F.Promise<Void> save(final T value) {
		return F.Promise.promise(new Function0<Void>() {
			@Override
			public Void apply() throws Throwable {
				bucket.set(UUID.randomUUID().toString(), gson.toJson(value), PersistTo.ZERO, ReplicateTo.ZERO);
				return null;
			}
		});
	} 
	
	public F.Promise<T> get(final String key, final Class<T> clazz) {
		return F.Promise.promise(new Function0<T>() {
			@Override
			public T apply() throws Throwable {
				return gson.fromJson((String) bucket.get(key), clazz);
			}
		});
	}
	
	public F.Promise<Collection<T>> findByKeys(final Collection<String> keys, final Class<T> clazz) {
		return F.Promise.promise(new Function0<Collection<T>>() {
			@Override
			public Collection<T> apply() throws Throwable {
				Collection<T> docs = new ArrayList<T>();
				for(String key : keys) {
					docs.add(gson.fromJson((String) bucket.get(key), clazz));
				}
				return docs;
			}
		});
	}
	
	public F.Promise<ViewResponse> getViewResponse(final String designName, final String viewName, final Query query, final Class<T> clazz) {
		return F.Promise.promise(new Function0<ViewResponse>() {
			@Override
			public ViewResponse apply() throws Throwable {
				View v = bucket.getView(designName, viewName);
				ViewResponse response = bucket.query(v, query);
				return response;
			}
		});
	}
	
	public F.Promise<Collection<T>> find(final String designName, final String viewName, final Query query, final Class<T> clazz) {
		final F.Promise<ViewResponse> response = getViewResponse(designName, viewName, query, clazz);
		return response.flatMap(new F.Function<ViewResponse, F.Promise<Collection<T>>>() {
			@Override
			public Promise<Collection<T>> apply(final ViewResponse response) throws Throwable {
				return F.Promise.promise(new Function0<Collection<T>>() {
					@Override
					public Collection<T> apply() throws Throwable {
						Collection<T> docs = new ArrayList<T>();
						for(ViewRow row : response) {
							docs.add(gson.fromJson((String) bucket.get(row.getId()), clazz));
						}
						return docs;
					} 
				});
			}
		});
	}
	
	public F.Promise<Void> update(final String id, final T updated, final Class<T> clazz) {
		F.Promise<T> existing = get(id, clazz);
		return existing.flatMap(new F.Function<T, F.Promise<Void>>() {
			@Override
			public Promise<Void> apply(final T t) throws Throwable {
				return F.Promise.promise(new Function0<Void>() {
					@Override
					public Void apply() throws Throwable {
						T merged = null;
						ObjectMapper objectMapper = new ObjectMapper();
						merged = objectMapper.readValue(gson.toJson(t), clazz);
						ObjectReader updater = objectMapper.readerForUpdating(t);
						merged = updater.readValue(gson.toJson(updated));
		                bucket.replace(id, gson.toJson(merged));
						return null;
					}
				});
			}
		});
	}
	
	public F.Promise<Integer> count(final String designName, final String viewName, final Query query, final Class<T> clazz) {
		final F.Promise<ViewResponse> response = getViewResponse(designName, viewName, query, clazz);
		return response.flatMap(new F.Function<ViewResponse, F.Promise<Integer>>() {
			@Override
			public Promise<Integer> apply(final ViewResponse response) throws Throwable {
				return F.Promise.promise(new Function0<Integer>() {
					@Override
					public Integer apply() throws Throwable {
						return response.size();
					}
				});
			}
		});
	}
	
	public F.Promise<Boolean> isExist(final String designName, final String viewName, final Query query, final Class<T> clazz) {
		return F.Promise.promise(new Function0<Boolean>() {
			@Override
			public Boolean apply() throws Throwable {
				View v = bucket.getView(designName, viewName);
				ViewResponse response = bucket.query(v, query);
				if(response.size() > 0) {
					return true;
				}	
				return false;
			}
		});
	}
	
	public F.Promise<Void> delete(final String key) {
		return F.Promise.promise(new Function0<Void>() {
			@Override
			public Void apply() throws Throwable {
				bucket.delete(key, PersistTo.ZERO, ReplicateTo.ZERO);
				return null;
			}
		});
	}
	
}
