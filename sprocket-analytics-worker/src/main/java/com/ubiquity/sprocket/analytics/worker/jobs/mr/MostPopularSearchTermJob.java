package com.ubiquity.sprocket.analytics.worker.jobs.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Job;

public class MostPopularSearchTermJob extends Configured implements Tool {
	
	public static void main(String[] args) throws Exception {
        // Let <code>ToolRunner</code> handle generic command-line options
        int res = ToolRunner.run(new Configuration(), new MostPopularSearchTermJob(), args);
        System.exit(res);
    }

	@Override
	public int run(String[] arg0) throws Exception {
		 Configuration config = getConf();

	     Job job = new Job(config, "MyJob");
	     job.setJarByClass(MostPopularSearchTermJob.class);

//	        job.setMapperClass(MyMapper.class);
//	        job.setReducerClass(MyReducer.class);
//	        job.setNumReduceTasks(1);
//
//	        job.setOutputKeyClass(Text.class);
//	        job.setOutputValueClass(IntWritable.class);
//
//	        // The reducer writes directly to Lily, so for Hadoop there is no output to produce
//	        job.setOutputFormatClass(NullOutputFormat.class);
//
//	        // The RecordScan defines what subset of the records will be offered as input
//	        // to the map task.
//	        RecordScan scan = new RecordScan();
//	        scan.setRecordFilter(new RecordTypeFilter(new QName("mrsample", "Document")));
//
//	        // Need LilyClient here just to be able to serialize the RecordScan.
//	        // This is a bit lame, will improve in the future.
//	        LilyClient lilyClient = new LilyClient(zkConnectString, 30000);
//	        LRepository repository = lilyClient.getDefaultRepository();
//
//	        // Utility method will configure everything related to LilyInputFormat
//	        LilyMapReduceUtil.initMapperJob(scan, zkConnectString, repository, job);
//
//	        Closer.close(lilyClient);
//
//	        // Launch the job
//	        boolean b = job.waitForCompletion(true);
//	        if (!b) {
//	            throw new IOException("error executing job!");
//	        }
//
	        return 0;
	}


}
