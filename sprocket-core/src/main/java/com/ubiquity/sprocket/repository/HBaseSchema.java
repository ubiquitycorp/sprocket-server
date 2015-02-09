package com.ubiquity.sprocket.repository;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

/**
 * Schema class for HBase exposing tables and columns for common access as well as administrative
 * methods for creating tables
 * 
 * @author chris
 *
 */
public class HBaseSchema {
		
	public static final class Tables {
		public static final String PROFILE = "profile";
		public static final String CONTENT = "content";
	}
	
	/***
	 * Column families
	 * 
	 * @author chris
	 *
	 */
	public static final class ColumnFamilies {
		public static final String ATTRIBUTES = "a";
		public static final String INTERESTS = "i";
		public static final String HISTORY = "h";
	}
	
	/**
	 * Column qualifiers
	 * 
	 * @author chris
	 *
	 */
	public static final class Qualifiers {
		public static final String GENDER  = "gender";
		public static final String MIN_AGE = "min_age";
		public static final String MAX_AGE = "max_age";
		public static final String SEARCH  = "search";
		public static final String GROUP_MEMBERSHIP = "group_membership";
		public static final String TYPE 			= "type";
		public static final String OWNER_ID 		= "owner_id";
		public static final String NAME 			= "name";
		public static final String DESCRIPTION 		= "body";
		public static final String PREFIX_ENGAGED 	= "engaged";
		public static final String PREFIX_SEARCH_TERM = "search";
	}
	
	private HBaseAdmin admin;
	private Configuration conf;
	
	/***
	 * Creates an HBase schema with a hadoop configuration
	 * 
	 * @param conf
	 * @throws MasterNotRunningException
	 * @throws ZooKeeperConnectionException
	 * @throws IOException
	 */
	public HBaseSchema(Configuration conf) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
		this.conf = conf;
		admin = new HBaseAdmin(conf);	
	}
	
	/**
	 * Creates a table if one does not exist.  This method will validate name against the 
	 * registered tables in {@link ccom.ubiquity.sprocket.repository.HBaseSchema.Tables}
	 * 
	 * @param name
	 * @throws IOException
	 * @throws IllegalArgumentException if the name does not match a registered table
	 */
	public void createTableIfNotExists(String name) throws IOException {
		
		if(name.equals(HBaseSchema.Tables.PROFILE)) {
		
			HTableDescriptor tableDescriptor;
			try  {
				tableDescriptor = admin.getTableDescriptor(TableName.valueOf(name));
			} catch (TableNotFoundException e) {
				tableDescriptor = new HTableDescriptor(TableName.valueOf(name));
				tableDescriptor.addFamily(new HColumnDescriptor(ColumnFamilies.ATTRIBUTES));
				tableDescriptor.addFamily(new HColumnDescriptor(ColumnFamilies.HISTORY));
				tableDescriptor.addFamily(new HColumnDescriptor(ColumnFamilies.INTERESTS));
				admin.createTable(tableDescriptor);
			}
		} else if(name.equals(HBaseSchema.Tables.CONTENT)) {
			HTableDescriptor tableDescriptor;
			try  {
				tableDescriptor = admin.getTableDescriptor(TableName.valueOf(name));
			} catch (TableNotFoundException e) {
				tableDescriptor = new HTableDescriptor(TableName.valueOf(name));
				tableDescriptor.addFamily(new HColumnDescriptor(ColumnFamilies.ATTRIBUTES));
				tableDescriptor.addFamily(new HColumnDescriptor(ColumnFamilies.HISTORY));
				tableDescriptor.addFamily(new HColumnDescriptor(ColumnFamilies.INTERESTS));
				admin.createTable(tableDescriptor);
			}
		} else {
			throw new IllegalArgumentException("Unrecongized table: " + name);
		}
	}

	/**
	 * Returns a table or creates one if it does not exist
	 * 
	 * @param name
	 * 
	 * @return a table reference
	 * 
	 * @throws IOException if a table could not be created
	 * 
	 */
	public HTable getTable(String name) throws IOException {
		createTableIfNotExists(name);
		return new HTable(conf, name);		
	}
	
	/**
	 * Closes the admin connection
	 */
	public void cleanup() {
		try {
			admin.close();
		} catch (IOException e) {}
	}

}
