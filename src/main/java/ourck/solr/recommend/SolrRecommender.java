package ourck.solr.recommend;

import java.io.*;
import java.util.*;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

public abstract class SolrRecommender {
	// SOLR_URL + coreName + queryParam + queryWords;
	private static final String SOLR_URL = "http://localhost:8983/solr/";
	
	private final String coreName;

	private String queryWords = null;
	private String queryParam = 
			"/select"
			+ "?defType=edismax"
			+ "&fl=*,score"
			+ "&mm=30"
			+ "&rows=100&start=0"
			+ "&q=";
	
	/**
	 * 根据提供的关键词进行单字段的K-V搜索。得到的JSON数组作为List返回。
	 * @param words 关键词
	 * @return 搜索结果
	 * @throws IOException 当HTTP请求失败时
	 */
	public List<Object> query(String key, String... words) throws IOException {
		StringBuilder stb = new StringBuilder();
		for(int i = 0; i < words.length; i++) {
			stb.append(key).append(":").append(words[i]);
			if(i < words.length - 1) stb.append(" OR ");
		}
		queryWords = stb.toString();
		
		String requestURl = SOLR_URL + coreName + queryParam + queryWords;
		Connection ct = Jsoup.connect(requestURl);
		Response repo = ct.ignoreContentType(true)
				.method(Method.GET)
				.execute();

		// 结果是有序的。在以此构造JSON对象时仍会保持其有序性。
		JSONObject qResult = new JSONObject(repo.body());
		List<Object> qList = qResult.getJSONObject("response").getJSONArray("docs").toList();
		return qList;
	}

	/**
	 * 构造器接受一个推荐字段，根据该字段的数据进行推荐
	 * @param recommedBy 推荐字段
	 */
	public SolrRecommender(String coreName) {
		this.coreName = coreName;
	}
	
	/**
	 * 根据输入的关键词进行基于搜索引擎的推荐。
	 * @param 输入的关键词
	 * @return 推荐结果（有序）
	 */
	abstract public List<Object> recommend(String... words); 
	
}
