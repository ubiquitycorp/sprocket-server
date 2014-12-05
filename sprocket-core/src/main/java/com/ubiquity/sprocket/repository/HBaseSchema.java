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

public class HBaseSchema {
		
	public static final class Tables {
		public static final String PROFILE = "profile";
		public static final String CONTENT = "content";
	}
	
	public static final class ColumnFamilies {
		public static final String ATTRIBUTES = "a";
		public static final String INTERESTS = "i";
		public static final String HISTORY = "h";
	}
	
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
	}
	
	private HBaseAdmin admin;
	private Configuration conf;
	
	public HBaseSchema(Configuration conf) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
		this.conf = conf;
		admin = new HBaseAdmin(conf);	
	}
	
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

	public HTable getTable(String name) throws IOException {
		createTableIfNotExists(name);
		return new HTable(conf, name);		
	}

}
