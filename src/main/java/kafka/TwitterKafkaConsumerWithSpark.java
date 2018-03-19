package kafka;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.Tweet;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import db.RedShiftDataEmitter;

public class TwitterKafkaConsumerWithSpark {
	private final static String topic = KafkaConstants.TOPIC;

	public static void main(String[] args) throws Exception {
		SparkConf conf = new SparkConf().setAppName("kafka-twitter").setMaster(
				"local[*]");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(
				2000));
		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put("metadata.broker.list", KafkaConstants.BROKER_HOST);
		kafkaParams.put("bootstrap.servers", KafkaConstants.BROKER_HOST);
		kafkaParams.put("group.id", "twittergroup");
		kafkaParams.put("key.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put("value.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");
		Set<String> topics = Collections.singleton(topic);

		JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils
				.createDirectStream(ssc, LocationStrategies.PreferConsistent(),
						ConsumerStrategies.Subscribe(topics, kafkaParams));

		// processing pipeline
		stream.foreachRDD(rdd -> {
			System.out.println("--- New RDD with " + rdd.partitions().size()
					+ " partitions and " + rdd.count() + " records");
			rdd.foreach(record -> {

				JsonObject jsonObj = new JsonParser().parse(record.value())
						.getAsJsonObject();
				if (Tweet.isValidTweet(jsonObj)) {
					Tweet tweet = new Tweet();
					tweet.setId(jsonObj.get("id").getAsString());
					tweet.setTag(jsonObj.get("entities").getAsJsonObject()
							.get("hashtags").getAsJsonArray().get(0)
							.getAsJsonObject().get("text").getAsString());
					tweet.setLat(jsonObj.get("coordinates").getAsJsonObject()
							.get("coordinates").getAsJsonArray().get(1)
							.getAsString());
					tweet.setLang(jsonObj.get("coordinates").getAsJsonObject()
							.get("coordinates").getAsJsonArray().get(0)
							.getAsString());
					RedShiftDataEmitter.getInstance().insert(tweet);
				}
			});
		});

		ssc.start();
		ssc.awaitTermination();

	}
}