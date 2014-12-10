package com.ubiquity.sprocket.repository.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchTermReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
	}
	
	@Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
    }

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {

    	log.info("bleh");
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        
        context.write(key, new IntWritable(sum));

       
    }

}
