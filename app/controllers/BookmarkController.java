package controllers;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import models.Bookmark;
import play.data.Form;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * @author siva
 *
 */
public class BookmarkController extends Controller {

	public static Form<Bookmark> bookmarkForm = Form.form(Bookmark.class);
	
	public static Result save() {
		Bookmark bookmark = bookmarkForm.bindFromRequest().get();
		bookmark.id = UUID.randomUUID().toString();
		bookmark.timeStamp = new Date().toString();
		Bookmark.save(bookmark);
		return redirect(routes.BookmarkController.list());
	}
	
	public static F.Promise<Result> get(String id) {
		F.Promise<Bookmark> futureBookmark = Bookmark.get(id);
		return futureBookmark.map(new F.Function<Bookmark, Result>() {
			@Override
			public Result apply(Bookmark bookmark) throws Throwable {
				return ok(views.html.bookmark.bookmark.render(bookmark));
			}
		});
	}
	
	public static F.Promise<Result> list() {
		F.Promise<Collection<Bookmark>> futureBookmarks = Bookmark.list();
		return futureBookmarks.map(new F.Function<Collection<Bookmark>, Result>() {
			@Override
			public Result apply(Collection<Bookmark> bookmarks) throws Throwable {
				return ok(views.html.bookmark.list.render(bookmarkForm, bookmarks));
			}
		});
	}

	public static F.Promise<Result> edit(String id) {
		F.Promise<Bookmark> futureBookmark = Bookmark.get(id);
		return futureBookmark.map(new F.Function<Bookmark, Result>() {
			@Override
			public Result apply(Bookmark bookmark) throws Throwable {
				return ok(views.html.bookmark.edit.render(bookmarkForm.fill(bookmark)));
			}
		});
	}
	
	public static Result update() {
		Bookmark bookmark = bookmarkForm.bindFromRequest().get();
		Bookmark.update(bookmark);
		return redirect(routes.BookmarkController.list());
	}
	
	public static Result delete(String id) {
		Bookmark.delete(id);
		return redirect(routes.BookmarkController.list());
	}
		
}
