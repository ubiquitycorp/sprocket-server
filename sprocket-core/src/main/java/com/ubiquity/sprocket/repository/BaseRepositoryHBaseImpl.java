package com.ubiquity.sprocket.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.WritableUtils;

/***
 * Base repository implementation implements the basic operations for adding and reading data in HBase. Derived classes
 * are expected to assemble the Put/Get objects as they would normally.  
 * 
 * Only top-level domains should be derived from this class, as this class enforces the convention that a single table
 * will be created for the class passed in to the constructor.
 * 
 * @author chris
 *
 * @param <T>
 */
public class BaseRepositoryHBaseImpl<T> {

	private HTable table;
	private String tableName;

	/**
	 * Returns a table reference or creates one. This method is current not thread-safe.
	 * 
	 * @return
	 */
	protected HTable getTable() {
		if(table == null) {
			table = HBaseConnectionFactory.getTable(tableName);
		}
		return table;
	}

	/***
	 * Derived classes call this constructor that will create a table using the lowercase simple name of 
	 * the class passed in to the constructor.
	 * 
	 * @param type Class of the top-level domain class mapped to the hbase table
	 */
	public BaseRepositoryHBaseImpl(Class<T> type)  {
		tableName = type.getSimpleName().toLowerCase();
	}

	/**
	 * Calls a put into the underlying table and flushes commits
	 * 
	 * @param put
	 * @throws RuntimeException if the put did not succeed
	 */
	protected void put(Put put) {
		HTable table = getTable();
		try {
			table.put(put);
			table.flushCommits();
		} catch (IOException e) {
			throw new RuntimeException("Could not create profile", e);
		} 
	}

	/**
	 * Gets the result set for a Get call, with max versions set to 1
	 * 
	 * @param get
	 * @return a result
	 * 
	 * @throws RuntimeException if the get did not succeed
	 */
	protected Result getResult(Get get) {
		HTable table = getTable();
		try {
			get.setMaxVersions(1);
			return table.get(get);
		} catch (IOException e) {
			throw new RuntimeException("Could not get result from get operation", e);
		}
	}

	/**
	 * Adds a value to the passed-in put operation for a column family, qualifier, and int
	 * 
	 * @param put
	 * @param family
	 * @param qualifier
	 * @param value
	 */
	protected void addValue(Put put, String family, String qualifier, int value) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		put.add(keys[0], keys[1], Bytes.toBytes(value));
	}

	/**
	 * 	Adds a value to the passed-in put operation for a column family, qualifier, and long
	 *
	 * @param put
	 * @param family
	 * @param qualifier
	 * @param value
	 */
	protected void addValue(Put put, String family, String qualifier, long value) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		put.add(keys[0], keys[1], Bytes.toBytes(value));
	}

	/**
	 * Adds a value to the passed-in put operation for a column family, qualifier, and int
     *
	 * @param put
	 * @param family
	 * @param qualifier
	 * @param value
	 */
	protected void addValue(Put put, String family, String qualifier, String value) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		put.add(keys[0], keys[1], Bytes.toBytes(value));
	}

	/**
	 * Adds a value to the passed-in put operation for a column family, qualifier, and double
     *
	 * @param put
	 * @param family
	 * @param qualifier
	 * @param value
	 */
	protected void addValue(Put put, String family, String qualifier, double value) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		put.add(keys[0], keys[1], Bytes.toBytes(value));
	}

	/**
	 * Increments the count for a row, column family, qualifier, by the amount specified
	 * 
	 * @param row
	 * @param family
	 * @param qualifier
	 * @param amount
	 */
	protected void incrementValue(String row, String family, String qualifier, long amount) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		try {
			getTable().incrementColumnValue(row.getBytes(), keys[0], keys[1], amount);
		} catch (IOException e) {
			throw new RuntimeException("Could not increment column value", e);
		}

	}

	/**
	 * Creates a for a single row to be executed on a row key, column family, and prefix
	 * @param rowKey
	 * @param family
	 * @param prefix
	 * @return
	 */
	protected Scan createScanWithPrefixFilter(String rowKey, String family, String prefix) {
		byte[][] keys = getFamilyAndQualifier(family, prefix);
		byte[] row = rowKey.getBytes();
		Scan scan = new Scan(row, row); // single row
		scan.addFamily(Bytes.toBytes(family));

		Filter f = new ColumnPrefixFilter(keys[1]);
		scan.setFilter(f);
		scan.setBatch(10);

		return scan;
	}

	/**
	 * Creates a scan over all rows matching the column family and prefix
	 * 
	 * @param family
	 * @param prefix
	 * @return
	 */
	protected Scan createScanWithPrefixFilter(String family, String prefix) {
		byte[][] keys = getFamilyAndQualifier(family, prefix);
		Scan scan = new Scan(); // all rows
		scan.addFamily(Bytes.toBytes(family));

		Filter f = new ColumnPrefixFilter(keys[1]);
		scan.setFilter(f);
		scan.setBatch(10);

		return scan;
	}

	/**
	 * Adds a list of values to the put for a column family and qualifier
	 * 
	 * @param put
	 * @param family
	 * @param qualifier
	 * @param values
	 */
	protected void addValue(Put put, String family, String qualifier, List<String> values) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			WritableUtils.writeStringArray(new DataOutputStream(baos), values.toArray(new String[values.size()]));
			put.add(keys[0], keys[1], baos.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Could not write array value", e);
		}
	}

	/**
	 * Loads and converts a list of string values from a result set. This assumes the data has been encoded
	 * from a string array.
	 * 
	 * @param result
	 * @param family
	 * @param qualifier
	 * 
	 * @return a list of string values
	 * 
	 * @throws RuntimeException if the array could not be read
	 */
	protected String[] getStringValues(Result result, String family, String qualifier) {
		byte[] value = getValue(result, family, qualifier);
		if(value == null)
			return null;
		try {
			return WritableUtils.readStringArray(new DataInputStream(new ByteArrayInputStream(value)));
		} catch (IOException e) {
			throw new RuntimeException("Unable to read array", e);
		}
	}

	/**
	 * Returns an integer value from a result set
	 * 
	 * @param result
	 * @param family
	 * @param qualifier
	 * @return
	 */
	protected Integer getIntegerValue(Result result, String family, String qualifier) {
		byte[] value = getValue(result, family, qualifier);
		return value == null ? null : new BigInteger(value).intValue();
	}

	/**
	 * Returns string value from a result set
	 * 
	 * @param result
	 * @param family
	 * @param qualifier
	 * 
	 * @return a string value
	 */
	protected String getStringValue(Result result, String family, String qualifier) {
		byte[] value = getValue(result, family, qualifier);
		return value == null ? null : new String(value);
	}

	/**
	 * Returns a double value from a result set
	 * 
	 * @param result
	 * @param family
	 * @param qualifier
	 * 
	 * @return a double value
	 */
	protected Double getDoubleValue(Result result, String family, String qualifier) {
		byte[] value = getValue(result, family, qualifier);
		return value == null ? null : new BigInteger(value).doubleValue();
	}

	/**
	 * Returns a long value from a result set
	 * 
	 * @param result
	 * @param family
	 * @param qualifier
	 * 
	 * @return a long value
	 */
	protected Long getLongValue(Result result, String family, String qualifier) {
		byte[] value = getValue(result, family, qualifier);
		return value == null ? null : new BigInteger(value).longValue();
	}

	/**
	 * Gets a byte array value from a result set
	 * 
	 * @param result
	 * @param family
	 * @param qualifier
	 * 
	 * @return a byte array
	 */
	private byte[] getValue(Result result, String family, String qualifier) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		return result.getValue(keys[0], keys[1]);
	}

	/**
	 * Gets the byte sequence for a column family and qualifier
	 * 
	 * @param family
	 * @param qualifier
	 * 
	 * @return 2 dimensional byte array representing array[0] as family and array[1] as qualifier
	 */
	private byte[][] getFamilyAndQualifier(String family, String qualifier) {
		return new byte[] [] { Bytes.toBytes(family),
				Bytes.toBytes(qualifier) };
	}

	/**
	 * Closes the underlying table and sets the reference to null. Should be called after all operations are complete.
	 */
	protected void close() {
		try {
			if(table != null) {
				table.close();
				table = null;
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to close table", e);
		} 
	}



}
