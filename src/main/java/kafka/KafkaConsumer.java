package kafka;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import model.Tweet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
 
public class KafkaConsumer {
    private ConsumerConnector consumerConnector = null;
    private final String topic = KafkaConstants.TOPIC;
 
    public void initialize() {
    	// initialize the kafka consumer
          Properties props = new Properties();
          props.put("zookeeper.connect", KafkaConstants.ZOOKEEPER_HOST);
          props.put("group.id", "twittergroup");
          props.put("zookeeper.session.timeout.ms", "400");
          props.put("zookeeper.sync.time.ms", "300");
          props.put("auto.commit.interval.ms", "100");
          ConsumerConfig conConfig = new ConsumerConfig(props);
          consumerConnector = Consumer.createJavaConsumerConnector(conConfig);
        // initialize the redshift connector
          
    }
 
    public void consume() {
          //Key = topic name, Value = No. of threads for topic
          Map<String, Integer> topicCount = new HashMap<String, Integer>();       
          topicCount.put(topic, new Integer(1));
         
          //ConsumerConnector creates the message stream for each topic
          Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams =
                consumerConnector.createMessageStreams(topicCount);         
         
          // Get Kafka stream for topic 'twitter'
          List<KafkaStream<byte[], byte[]>> kStreamList =
                                               consumerStreams.get(topic);
          // Iterate stream using ConsumerIterator
          for (final KafkaStream<byte[], byte[]> kStreams : kStreamList) {
                 ConsumerIterator<byte[], byte[]> consumerIte = kStreams.iterator();
                
                 while (consumerIte.hasNext()) {
                	 JsonObject jsonObj = new JsonParser().parse(new String(consumerIte.next().message())).getAsJsonObject();
                     if (Tweet.isValidTweet(jsonObj)) {
                    	 Tweet tweet = new Tweet();
//                    	 tweet.setId(jsonObj.get);
                     }
                 }
          }
          //Shutdown the consumer connector
          if (consumerConnector != null)   consumerConnector.shutdown();          
    }
 
    public static void main(String[] args) throws InterruptedException {
          KafkaConsumer kafkaConsumer = new KafkaConsumer();
          // Configure Kafka consumer
          kafkaConsumer.initialize();
          // Start consumption
          kafkaConsumer.consume();
    }
}