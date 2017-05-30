package com.oliver.spark_drools;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import com.google.common.collect.Lists;
import com.oliver.spark_drools.bo.Message;
import com.oliver.spark_drools.utils.DroolsUtils;

public class App {

	private static Logger log = Logger.getLogger(App.class);

	public static void main(String[] args) throws FileNotFoundException {

		Logger.getLogger("org").setLevel(Level.OFF);
		Logger.getLogger("akka").setLevel(Level.OFF);

		if (args.length != 1) {
			throw new IllegalArgumentException("Necesitas una localizacion de la configuracion");
		}

		final String config = args[0];

		SparkConf conf = new SparkConf();
		conf.setAppName("Oliver App");
		JavaSparkContext jsc = new JavaSparkContext(conf);

		Queue<JavaRDD<Message>> queue = new LinkedList<JavaRDD<Message>>();

		for (int i = 0; i < 1000; i++) {
			List<Message> list = new ArrayList<Message>();
			for (int j = 0; j < 10; j++) {

				Map<String, Object> input = new HashMap<String, Object>();
				input.put("input1", i);
				input.put("input2", "Numero" + String.valueOf(j));

				list.add(new Message("Message " + j + " del batch " + i, j, input));
			}
			JavaRDD<Message> rdd = jsc.parallelize(list);
			queue.add(rdd);
		}

		JavaStreamingContext jssc = new JavaStreamingContext(jsc, new Duration(1000));

		JavaDStream<Message> stream = jssc.queueStream(queue);

		stream.foreachRDD(new VoidFunction<JavaRDD<Message>>() {

			public void call(final JavaRDD<Message> rdd) throws Exception {

				rdd.foreachPartition(new VoidFunction<Iterator<Message>>() {

					public void call(Iterator<Message> iterator) throws Exception {

						List<Message> myList = Lists.newArrayList(iterator);
						
						DroolsUtils.startDrools(myList,config,true);

						for (Message outputMessage : myList) {
							log.info(outputMessage.getOutput());
						}

					}

				});

				log.info("----------------------------------");
				log.info("----------------------------------");
				log.info("--------FIN RDD---------");
				log.info("----------------------------------");
				log.info("----------------------------------");

			}
		});

		jssc.start();

		jssc.awaitTermination();
		jssc.stop();
		jsc.stop();
		jsc.close();
		jssc.close();

	}
}
