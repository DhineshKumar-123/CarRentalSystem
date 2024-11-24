package org.carrental.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBPropertyUtil 
{
	public static String getPropertyString(String filename) 
	{
//		String constring=null;
		String url = null;
		try {
			Properties prop = new Properties();
			FileInputStream fis = new FileInputStream(filename);
			prop.load(fis);
			url = prop.getProperty("url")+"/"+prop.getProperty("database")+"?"+"user="+prop.getProperty("user")+"&password="+prop.getProperty("password");
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		
//		System.out.print(url);
		
		return url;
	}
//	public static void main(String[] args)
//	{
//		String URL = DBPropertyUtil.connectionstring("db.properties");
//		System.out.println("..................Here is the Your URL.............\n");
//		System.out.println(URL);
//	}
		
}
