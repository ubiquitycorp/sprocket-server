package com.ubiquity.sprocket.repository;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;

/**
 * Factory for managing HBase connections in a thread-safe manner
 * 
 * @author chris
 *
 */
public class HBaseConnectionFactory {
	
	private static HBaseSchema schema;
	private static org.apache.hadoop.conf.Configuration conf;
	
	public static org.apache.hadoop.conf.Configuration getConfiguration() {
		if(conf == null)
			throw new IllegalArgumentException("HBase not initialized");
		return conf;
	}
	
	/***
	 * Initializes the HBase client connection to zookeeper using the configuration
	 * 
	 * @param configuration
	 * 
	 * @throws RuntimeException if no connection could be established
	 * 
	 */
	public static void initialize(Configuration configuration) {
		
		conf = HBaseConfiguration.create();
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

	/**
	 * Returns a single table reference
	 * 
	 * @param tableName
	 * 
	 * @return an HTable reference
	 */
	public static HTable getTable(String tableName) {
		try {
			
			return schema.getTable(tableName);
		} catch (IOException e) {
			throw new RuntimeException("Could not connect to hbase table", e);
		}
	}

	/**
	 * Close the connections and clean up resources
	 * 
	 */
	public static void close() {
		schema.cleanup();
	}

}
