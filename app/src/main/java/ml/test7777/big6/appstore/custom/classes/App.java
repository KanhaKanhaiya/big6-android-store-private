package ml.test7777.big6.appstore.custom.classes;

import java.io.Serializable;
import java.util.*;

public class App implements Serializable {
	
	private String name;
	private String oneLineDescription;
	private String packageName;
	private String version;
	private int versionCode;
	private String whatsNew;
	private ArrayList<String> screenshots;
	private String updatedOn;
	private String size;
	private String description;
	private String icon;
	private boolean isOpenable;
	private String checksum;

	public App(String name, String oneLineDescription, String packageName, String version, int versionCode, String whatsNew, ArrayList<String> screenshots, String updatedOn, String size, String description, String icon, boolean isOpenable, String checksum)
	{
		this.name = name;
		this.oneLineDescription = oneLineDescription;
		this.packageName = packageName;
		this.version = version;
		this.versionCode = versionCode;
		this.whatsNew = whatsNew;
		this.screenshots = screenshots;
		this.updatedOn = updatedOn;
		this.size = size;
		this.description = description;
		this.icon = icon;
		this.isOpenable = isOpenable;
		this.checksum = checksum;
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

	public void setOneLineDescription(String oneLineDescription)
	{
		this.oneLineDescription = oneLineDescription;
	}

	public String getOneLineDescription()
	{
		return oneLineDescription;
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

	public void setUpdatedOn(String updatedOn)
	{
		this.updatedOn = updatedOn;
	}

	public String getUpdatedOn()
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isOpenable() {
		return isOpenable;
	}

	public void setIsOpenable(boolean isOpenable) {
		this.isOpenable = isOpenable;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
}
