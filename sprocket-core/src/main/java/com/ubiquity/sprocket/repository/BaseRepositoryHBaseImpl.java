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

public class BaseRepositoryHBaseImpl<T> {

	private HTable table;
	private String tableName;

	protected HTable getTable() {
		if(table == null) {
			table = HBaseConnectionFactory.getTable(tableName);
		}
		return table;
	}

	public BaseRepositoryHBaseImpl(Class<T> type)  {
		tableName = type.getSimpleName().toLowerCase();
	}

	protected void put(Put put) {
		HTable table = getTable();
		try {
			table.put(put);
			table.flushCommits();
		} catch (IOException e) {
			throw new RuntimeException("Could not create profile", e);
		} 
	}


	protected Result getResult(Get get) {
		HTable table = getTable();
		try {
			get.setMaxVersions(1);
			return table.get(get);
		} catch (IOException e) {
			throw new RuntimeException("Could not get result from get operation", e);
		}
	}

	protected void addValue(Put put, String family, String qualifier, int value) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		put.add(keys[0], keys[1], Bytes.toBytes(value));
	}

	protected void addValue(Put put, String family, String qualifier, long value) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		put.add(keys[0], keys[1], Bytes.toBytes(value));
	}

	protected void addValue(Put put, String family, String qualifier, String value) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		put.add(keys[0], keys[1], Bytes.toBytes(value));
	}

	protected void addValue(Put put, String family, String qualifier, double value) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		put.add(keys[0], keys[1], Bytes.toBytes(value));
	}

	protected void incrementValue(String row, String family, String qualifier, long amount) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		try {
			getTable().incrementColumnValue(row.getBytes(), keys[0], keys[1], amount);
		} catch (IOException e) {
			throw new RuntimeException("Could not increment column value", e);
		}

	}

	protected Scan createScanWithPrefixFilter(String rowKey, String family, String prefix) {
		byte[][] keys = getFamilyAndQualifier(family, prefix);
		byte[] row = rowKey.getBytes();
		Scan scan = new Scan(row, row); // single row
		scan.addFamily(Bytes.toBytes(HBaseSchema.ColumnFamilies.HISTORY));

		Filter f = new ColumnPrefixFilter(keys[1]);
		scan.setFilter(f);
		scan.setBatch(10);

		return scan;
	}

	protected Scan createScanWithPrefixFilter(String family, String prefix) {
		byte[][] keys = getFamilyAndQualifier(family, prefix);
		Scan scan = new Scan(); // all rows
		scan.addFamily(Bytes.toBytes(family));

		Filter f = new ColumnPrefixFilter(keys[1]);
		scan.setFilter(f);
		scan.setBatch(10);

		return scan;
	}

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

	protected Integer getIntegerValue(Result result, String family, String qualifier) {
		byte[] value = getValue(result, family, qualifier);
		return value == null ? null : new BigInteger(value).intValue();
	}

	protected String getStringValue(Result result, String family, String qualifier) {
		byte[] value = getValue(result, family, qualifier);
		return value == null ? null : new String(value);
	}

	protected Double getDoubleValue(Result result, String family, String qualifier) {
		byte[] value = getValue(result, family, qualifier);
		return value == null ? null : new BigInteger(value).doubleValue();
	}

	protected Long getLongValue(Result result, String family, String qualifier) {
		byte[] value = getValue(result, family, qualifier);
		return value == null ? null : new BigInteger(value).longValue();
	}

	private byte[] getValue(Result result, String family, String qualifier) {
		byte[][] keys = getFamilyAndQualifier(family, qualifier);
		return result.getValue(keys[0], keys[1]);
	}


	private byte[][] getFamilyAndQualifier(String family, String qualifier) {
		return new byte[] [] { Bytes.toBytes(family),
				Bytes.toBytes(qualifier) };
	}

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
