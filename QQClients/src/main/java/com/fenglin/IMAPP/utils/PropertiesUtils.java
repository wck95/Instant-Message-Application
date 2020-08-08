package com.fenglin.IMAPP.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author wangchengkai
 * @description 类描述: properties配置文件的工具类
 */
public class PropertiesUtils {

	private static PropertiesUtils propertiesUtils = null;
	
	private Properties properties ;
	
	
	public PropertiesUtils(String fileName) {
		properties = new Properties();
		InputStream inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**获取properties配置文件的操作对象
	 * 
	 * @param key 
	 * @return
	 */
	public static PropertiesUtils createPropertiesUtils(String fileName) {
		if(propertiesUtils == null) {
			propertiesUtils = new PropertiesUtils(fileName);
		}
		return propertiesUtils;
	}
	
	/**properties配置文件新增属性值
	 * 
	 * @param key 
	 * @return
	 */
	public  void setValue(String key, String value) {
	   properties.setProperty(key, value);
	}
	
	
	/**获取properties配置文件中的属性值
	 * 
	 * @param key 
	 * @return
	 */
	public  String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	
	/**获取properties配置文件中的int属性值
	 * 
	 * @param key 
	 * @return
	 */
    public  Integer getInteger(String key) {
        String value = getProperty(key);
        return null == value ? null : Integer.valueOf(value);
    }

    
    /**获取properties配置文件中的Boolean属性值
	 * 
	 * @param key 
	 * @return
	 */
    public  Boolean getBoolean(String key) {
        String value = getProperty(key);
        return null == value ? null : Boolean.valueOf(value);
    }
	
    
}
