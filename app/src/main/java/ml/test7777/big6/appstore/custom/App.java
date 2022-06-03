package ml.test7777.big6.appstore.custom;

import java.util.*;

public class App {
	
	private String name;
	private String oneLinedescription;
	private String packageName;
	private String version;
	private int versionCode;
	private String whatsNew;
	private ArrayList<String> screenshots;
	private Date updatedOn;
	private String size;

	public App(String name, String oneLinedescription, String packageName, String version, int versionCode, String whatsNew, ArrayList<String> screenshots, Date updatedOn, String size)
	{
		this.name = name;
		this.oneLinedescription = oneLinedescription;
		this.packageName = packageName;
		this.version = version;
		this.versionCode = versionCode;
		this.whatsNew = whatsNew;
		this.screenshots = screenshots;
		this.updatedOn = updatedOn;
		this.size = size;
	}
	
	public App() {}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setOneLinedescription(String oneLinedescription)
	{
		this.oneLinedescription = oneLinedescription;
	}

	public String getOneLinedescription()
	{
		return oneLinedescription;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersionCode(int versionCode)
	{
		this.versionCode = versionCode;
	}

	public int getVersionCode()
	{
		return versionCode;
	}

	public void setWhatsNew(String whatsNew)
	{
		this.whatsNew = whatsNew;
	}

	public String getWhatsNew()
	{
		return whatsNew;
	}

	public void setScreenshots(ArrayList<String> screenshots)
	{
		this.screenshots = screenshots;
	}

	public ArrayList<String> getScreenshots()
	{
		return screenshots;
	}

	public void setUpdatedOn(Date updatedOn)
	{
		this.updatedOn = updatedOn;
	}

	public Date getUpdatedOn()
	{
		return updatedOn;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	public String getSize()
	{
		return size;
	}
	
}
