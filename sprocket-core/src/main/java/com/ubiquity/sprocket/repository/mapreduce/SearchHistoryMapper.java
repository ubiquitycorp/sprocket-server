package com.ubiquity.sprocket.repository.mapreduce;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.lilyproject.mapreduce.RecordIdWritable;
import org.lilyproject.mapreduce.RecordMapper;
import org.lilyproject.mapreduce.RecordWritable;
import org.lilyproject.repository.api.FieldNotFoundException;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.RecordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchHistoryMapper extends RecordMapper<Text, IntWritable> {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private Text keyOut = new Text();
    private IntWritable valueOut = new IntWritable(1);
    
 
	@SuppressWarnings("unchecked")
	@Override
    protected void map(RecordIdWritable recordIdWritable, RecordWritable recordWritable, Context context)
            throws IOException, InterruptedException {
		
		log.info("mapping {}", recordIdWritable);
		// get the search history for this profile
		Record record = recordWritable.getRecord();
        List<String> searchHistory;
		try {
			searchHistory = (List<String>)record.getField("search_history");
			  for(String term : searchHistory) {
		        	keyOut.set(term);
		        	context.write(keyOut, valueOut);
		        }
		} catch (FieldNotFoundException | RecordException e) {
			throw new RuntimeException(e);
		}
    }

}
