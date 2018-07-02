package ourck.solr.recommend;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import static ourck.utils.ScreenReader.jin;

public class CategoryRecommender extends SolrRecommender {
	
	private static final String RECOMMEND_BY = "list_title";
	private static final String CATEGORY_KEY = "category_name";
	
	public CategoryRecommender(String coreName) {
		super("ourckCORE");
	}

	/**
	 * 根据输入的关键词进行基于搜索引擎的商品推荐。<p>
	 * 推荐有两种方法：<p>
	 * 	1.根据搜索结果返回后面几条;<p>
	 *  2.根据搜索结果，提取其分类，然后返回该分类中的几条。本方法采用这一手段。<p>
	 * @param words 输入的关键词
	 * @return 推荐结果（有序）
	 * @throws IOException 当查询失败
	 */
	public List<Object> recommend(String... words) {
		List<Object> qList = null;
		try {
			qList = query(RECOMMEND_BY, words);
		} catch (IOException e) {
			System.err.println("[!] Query failed!");
			return null;
		}

		Map<String, Integer> statics = new LinkedHashMap<String, Integer>();
		for(Object obj : qList) {
			@SuppressWarnings("unchecked") 
			JSONObject item = new JSONObject((HashMap<String, String>) obj);
			String categoryName = item.getString(CATEGORY_KEY);
			
			// TODO NullPointerEx! Integer can be "null' while int can't be "null". int's "null" = 0.
			Integer times;
			if((times = statics.get(categoryName)) == null)
				statics.put(categoryName, 1);
			else
				statics.put(categoryName, ++times);
		}
		
		// 获取搜索结果中出现频率最大的分类作为推荐依据
		String maxCategory = null; int maxVal = 0;
		for(String key : statics.keySet()) {
			int i;
			if((i = statics.get(key)) >= maxVal) {
				maxVal = i;
				maxCategory = key;
			}
		}
		
		// 从该分类下随机挑选商品做推荐
		List<Object> result = null;
		try {
			List<Object> maxCategoryItems = query(CATEGORY_KEY, maxCategory);
			Random rand = new Random(System.nanoTime()); int start = rand.nextInt(maxCategoryItems.size() - 5);
			result = maxCategoryItems.subList(start, start + 5);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		CategoryRecommender r = new CategoryRecommender("ourckCORE");
		System.out.print("请输入您的商品关键词，用空格分割："); String keyword = jin();
		System.out.println("---------------------------------------");
		System.out.println("推荐商品如下：");

		List<Object> result = r.recommend(keyword.split(" "));
		for(Object obj : result) {
			System.out.println(new JSONObject((HashMap<String, String>) obj));
		}
	}
}
