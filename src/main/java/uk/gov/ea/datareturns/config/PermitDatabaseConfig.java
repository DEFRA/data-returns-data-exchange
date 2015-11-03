package uk.gov.ea.datareturns.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PermitDatabaseConfig
{
	@NotEmpty
	private String location;

	@NotEmpty
	private String name;

	@NotEmpty
	private String permitTableName;

	@NotEmpty
	private String permitColumnName;

	public PermitDatabaseConfig()
	{
		this.location = "";
		this.name = "";
		this.permitTableName = "";
		this.permitColumnName = "";
	}

	public PermitDatabaseConfig(String location, String name, String permitTableName, String permitColumnName)
	{
		super();
		this.location = location;
		this.name = name;
		this.permitTableName = permitTableName;
		this.permitColumnName = permitColumnName;
	}

	@JsonProperty
	public String getLocation()
	{
		return location;
	}

	@JsonProperty
	public void setLocation(String location)
	{
		this.location = location;
	}

	@JsonProperty
	public String getName()
	{
		return name;
	}

	@JsonProperty
	public void setName(String name)
	{
		this.name = name;
	}

	@JsonProperty
	public String getPermitTableName()
	{
		return permitTableName;
	}

	@JsonProperty
	public void setPermitTableName(String permitTableName)
	{
		this.permitTableName = permitTableName;
	}

	@JsonProperty
	public String getPermitColumnName()
	{
		return permitColumnName;
	}

	@JsonProperty
	public void setPermitColumnName(String permitFieldName)
	{
		this.permitColumnName = permitFieldName;
	}
}