package ourck.tester;

import java.io.IOException;
import java.util.*;
import org.jsoup.*;
import org.jsoup.Connection.Method;

public class HTTPPoster {
	
	private final String targetURL;
	
	public HTTPPoster(String targetURL) {
		this.targetURL = targetURL;
	}
	
	public void sendPost(Map<String, String> body) throws IOException {
		Connection ct = Jsoup.connect(targetURL);

		ct.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) "
		  		+ "AppleWebKit/537.36 (KHTML, like Gecko) "
		  		+ "Chrome/63.0.3239.84 Safari/537.36");
		
		ct.ignoreContentType(true)
		  .data(body)
		  .method(Method.POST)
		  .execute();
	}
	
	public static void main(String[] args) {
		HTTPPoster p = new HTTPPoster("http://localhost:8866/");
		Map<String, String> body = new HashMap<String, String>();
		body.put("CoreName", "ourckCORE");
		body.put("MaskCode", "64");
		
		try {
			p.sendPost(body);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
