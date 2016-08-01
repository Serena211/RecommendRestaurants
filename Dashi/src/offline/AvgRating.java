package offline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoDatabase;

import db.DBUtil;
/*
function map(String user_id, String item_id, Integer rating):
	  If user_id has the same rating on the same item_id as user_1:
	    emit (user_id, 1)

function reduce(String user_id, Iterable<Integer> values):
  sum = 0
  for value : values:
    sum += value
  emit (user_id, sum)

 * */
// MongoDB MapReducer
public class AvgRating {
	// calculate average rating for **an** item
	private static final String ITEM_ID = "0634055429";
	private static final String COLLECTION_NAME = "ratings";
	private static final String USER_COLUMN = "user";
	private static final String ITEM_COLUMN = "item";
	private static final String RATING_COLUMN = "rating";
	
	public static void main(String [] args) {
		// Init
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase(DBUtil.DB_NAME);
		
		// Get ITEM_ID's purchase records
		List<String> previousUsers = new ArrayList<>();
		List<Double> previousRatings = new ArrayList<>();
		FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME).find(
				new Document(ITEM_COLUMN, ITEM_ID));

		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				previousUsers.add(document.getString(USER_COLUMN));
				previousRatings.add(document.getDouble(RATING_COLUMN));
			}
		});

		// Construct mapper function
		StringBuilder sb = new StringBuilder();
/*		find all users' ratings for target item
 * 		function() {
			for (int i = 0; i < previousUsers.size(); i ++) {
				String user = previousUsers.get(i);
				Double rating = previousRatings.get(i);
//				if (this.user == user && this.item == ITEM_ID) {
					emit(this.rating,1);
//				}
			}
		}*/
		sb.append("function() {");
		
		for (int i = 0; i < previousUsers.size(); i ++) {
			String user = previousUsers.get(i);
			Double rating = previousRatings.get(i);
			sb.append("if (this.user == \"");
			sb.append(user);
			sb.append("\" && this.item == ");
			sb.append(ITEM_ID);
			sb.append(" ){ emit(this.rating, 1); }");
		}
		sb.append("}");
		String map = sb.toString();
		// Construct a reducer function
		String reduce = "function(key, values) {return Array.sum(values)} ";
		
		// MapReduce
		MapReduceIterable<Document> results = db.getCollection(COLLECTION_NAME)
				.mapReduce(map, reduce);	
		
		//  convert Document to HashMap
		// Need a sorting here
		HashMap<Double, Double> ratingMap = new HashMap<>();
		results.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
		        System.out.println(document);
				Double rating = document.getDouble("_id");
				Double count = document.getDouble("value");
				ratingMap.put(rating, count);
			}
		});

		// calculate average rating for target item
		Double sum = 0.0;
		Double count = 0.0;
		for (Entry<Double, Double> entry : ratingMap.entrySet()) {
			sum += entry.getKey() * entry.getValue();
			count += entry.getValue();
		}
		Double avgRating = sum / count;
		System.out. println("The average ratings for item " + ITEM_ID + " is " + avgRating);

		mongoClient.close();		
	}
}
