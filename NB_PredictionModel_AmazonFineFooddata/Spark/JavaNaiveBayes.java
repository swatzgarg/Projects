import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.util.MLUtils;
import org.apache.spark.rdd.RDD;

import scala.Tuple2;
import scala.reflect.ClassManifestFactory;

public class JavaNaiveBayes {

	public static void main(String[] args) {
		// setup conf
		SparkConf sparkConf = new SparkConf();
		JavaSparkContext sc = new JavaSparkContext(sparkConf);
		
		// load up input file
		JavaRDD<String> data = sc.textFile(args[0]);
		JavaRDD<LabeledPoint> parsedData = data.map(new Function<String, LabeledPoint>() {
			@Override
			public LabeledPoint call(String line) throws Exception {
				String[] tokens = line.split("\t");
				double label = (int)((Double.parseDouble(tokens[0])/Double.parseDouble(tokens[1]))*10); 
				
				double[] values = new double[tokens.length-2];
				for(int i = 2; i < tokens.length; i++) {
					values[i - 2] = Double.parseDouble(tokens[i]);
				}
				return new LabeledPoint(label, Vectors.dense(values));
			}
		});
		
		JavaRDD<LabeledPoint>[] dataDivided = parsedData.randomSplit(new double[]{0.75, 0.25});
		
		// train the model
		final NaiveBayesModel model = NaiveBayes.train(dataDivided[0].rdd(), 1.0);

		// test the prediction
		JavaPairRDD<Double, Double> predictionAndLabel = dataDivided[1]
				.mapToPair(new PairFunction<LabeledPoint, Double, Double>() {
		    @Override public Tuple2<Double, Double> call(LabeledPoint p) {
		      return new Tuple2<Double, Double>(model.predict(p.features()), p.label());
		    }
		  });

		// compute mse
		Double trainMSE = 
				predictionAndLabel.map(new Function<Tuple2<Double, Double>, Double>() {
			        @Override public Double call(Tuple2<Double, Double> pl) {
			          Double diff = pl._1() - pl._2();
			          return diff * diff;
			        }
			      }).reduce(new Function2<Double, Double, Double>() {
			        @Override public Double call(Double a, Double b) {
			          return a + b;
			        }
			      }) / dataDivided[1].count();

		System.out.println(Math.sqrt(trainMSE));
	}
	
}
