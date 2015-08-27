package uk.gov.ea.datareturns.domain;

import java.util.HashMap;
import java.util.Map;

public class ReturnType
{
	private String name;
	private Map<Integer, String> userPermits;

	public ReturnType()
	{
		super();
		this.userPermits = new HashMap<Integer, String>();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Map<Integer, String> getUserPermits()
	{
		return userPermits;
	}

	public void setUserPermits(Map<Integer, String> userPermits)
	{
		this.userPermits = userPermits;
	}

	public void addUserPermit(Integer id, String permitId)
	{
		this.userPermits.put(id, permitId);
	}
}
