package com.ubiquity.sprocket.analytics.worker.jobs.mr;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.lilyproject.mapreduce.RecordIdWritable;
import org.lilyproject.mapreduce.RecordMapper;
import org.lilyproject.mapreduce.RecordWritable;
import org.lilyproject.repository.api.QName;
import org.lilyproject.repository.api.Record;


public class SearchTermMapper extends RecordMapper<Text, IntWritable> {
	
	private Text keyOut = new Text();
    private IntWritable valueOut = new IntWritable(1);
    private String namespace = "sprocket.schema_1";
    
    
    
	@Override
    protected void map(RecordIdWritable recordIdWritable, RecordWritable recordWritable, Context context)
            throws IOException, InterruptedException {
		
		// get the search history for this profile
		Record record = recordWritable.getRecord();
        String value = record.getField(new QName(namespace, "text"));
        
        

		

//        Record record = recordWritable.getRecord();
//        String value = record.getField(new QName("mrsample", "text"));
//
//        StringTokenizer tokenizer = new StringTokenizer(value);
//        while (tokenizer.hasMoreTokens()) {
//            keyOut.set(tokenizer.nextToken());
//            context.write(keyOut, valueOut);
//        }
    }

}
