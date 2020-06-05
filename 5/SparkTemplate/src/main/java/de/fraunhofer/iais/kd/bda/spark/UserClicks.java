package de.fraunhofer.iais.kd.bda.spark;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class UserClicks {
	public static void main(String[] args) {
		// String inputFile= "/home/livlab/data/last-fm-sample1000000.tsv";

		// put here the path to the input
		String inputFile = "resources/last-fm-sample100000.tsv";
		String appName = "UserClicks";

		SparkConf conf = new SparkConf().setAppName(appName).setMaster("local[*]");

		JavaSparkContext context = new JavaSparkContext(conf);

		// Read file
		JavaRDD<String> input = context.textFile(inputFile);

		// Split lines into words
		JavaRDD<String> words = input.flatMap(line -> {
			String[] parts = line.split("\t");
			return Arrays.asList(parts[3]).iterator();
		});

		JavaPairRDD<String, Integer> wordOne = words.mapToPair(word -> {
			return new Tuple2<String, Integer>(word, Integer.valueOf(1));
		});

		JavaPairRDD<String, Integer> articCount = wordOne.reduceByKey((a, b) -> a + b);

		System.out.println(articCount.count());
		articCount.saveAsTextFile("userclicks");
		context.close();

	}
}
