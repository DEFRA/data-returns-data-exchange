package uk.gov.ea.datareturns.dao;

import java.io.IOException;
import java.util.Collections;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class PermitDAO
{
	private Database db;
	private String tableName;
	private String columnName;

	public PermitDAO(Database db, String permitTableName, String permitColumnName)
	{
		this.db = db;
		this.tableName = permitTableName;
		this.columnName = permitColumnName;
	}

	public boolean permitNoExists(String permitNo)
	{
		boolean found = false;

		try
		{
			Table table = db.getTable(this.tableName);
			Cursor cursor = CursorBuilder.createCursor(table);
			found = cursor.findNextRow(Collections.singletonMap(this.columnName, permitNo));
		} catch (IOException e)
		{
			// TODO create new exception here
			e.printStackTrace();
		}

		return found;
	}
}
