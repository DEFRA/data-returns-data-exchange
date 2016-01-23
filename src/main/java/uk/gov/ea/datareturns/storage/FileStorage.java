package uk.gov.ea.datareturns.storage;

import java.util.UUID;

import redis.clients.jedis.Jedis;

public class FileStorage
{
	private Jedis storage;
	private String host;
	private int port;

	public FileStorage(String host, int port)
	{
		this.host = host;
		this.port = port;
		this.storage = new Jedis(this.host, this.port);
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public Jedis getStorage()
	{
		return storage;
	}

	/**
	 * Generate a unique key (uses UUID class for now)
	 * @return
	 */
	public String generateFileKey()
	{
		return UUID.randomUUID().toString();
	}

	public String saveLocation(String location)
	{
		String key = generateFileKey();

		this.storage.set(key, location);

		return key;
	}

	public String getLocation(String key)
	{
		return this.storage.get(key);
	}
}
