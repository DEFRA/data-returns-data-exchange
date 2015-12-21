package uk.gov.ea.datareturns.type;

public enum FileType
{
	CSV("csv", "Comma Separated Values"), XML("xml", "Extensible Markup Language");

	private String fileType;
	private String description;

	FileType(String fileType, String reason)
	{
		this.fileType = fileType;
		this.description = reason;
	}

	public String getFileType()
	{
		return fileType;
	}

	public String getReason()
	{
		return description;
	}
}
