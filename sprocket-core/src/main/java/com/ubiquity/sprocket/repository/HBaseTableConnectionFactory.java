package com.ubiquity.sprocket.repository;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;

public class HBaseTableConnectionFactory {
	
	private static HBaseSchema schema;
	
	public static void initialize(Configuration configuration) {
		
		org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("hbase.zookeeper.quorum", "vm-data");
		conf.set("hbase.master", "vm-data:600000");
		//conf.set("zookeeper.znode.parent", "/hbase-unsecure");
		
		conf.set("hbase.client.retries.number", Integer.toString(1));
        conf.set("zookeeper.session.timeout", Integer.toString(60000));
        conf.set("zookeeper.recovery.retry", Integer.toString(0));
		 
		try {
			schema = new HBaseSchema(conf);
		} catch (IOException e) {
			throw new RuntimeException("Could not establish connection to hbase schema", e);
		}
		
	}
	
	public static synchronized HTable getTable(Class<?> clazz) {
		try {
			// TODO: here's where we check closed, or not...perhaps add in threading
			return schema.getTable(clazz);
		} catch (IOException e) {
			throw new RuntimeException("Could not connect to hbase table", e);
		}
	}

}
