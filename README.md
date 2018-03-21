## Producer and Consumer using Kafka for twitter stream API (hosebird) of a geo-location based tweets
------------------------------------------------------------------------------------------------------



# Dependencies
  - Spark 2.2
  - Kafka 2.11
  - Spark streaming Kafka 2.11
  - Spark streaming 2.11
  - gson 2.0.2
  - hbc-twitter 2.2


# How to run
 1- for the producer APP
 	-run the main of TwitterKafkaProducer with JDK 7


 2- for the consumer APP without the spark streaming
    -run TwitterKafkaConsumer with any JDK


 3- for the consumer with the Spark Stream
    -run TwitterKafkaConsumerWithSpark


# Notes
- To chnage the number of threads used in spark, put the number of it inside local[NUMBER]


		SparkConf conf = new SparkConf().setAppName("kafka-twitter").setMaster("local[*]"); 
- To change the patches duration of the spark context, then update it this number (used 2 sec here)


	    JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(2000)); 