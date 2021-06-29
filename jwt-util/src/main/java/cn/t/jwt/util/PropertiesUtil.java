package cn.t.jwt.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**资源文件都必须是ISO-8859-1编码 */
public class PropertiesUtil {
  
  /** */
  private ResourceBundle resBundle = null;
  
  /**
   * 使用系统默认的国际化初始化工具类
   * @param propFilePath the base name of the resource bundle, a fully qualified class name
   */
  public PropertiesUtil(String propFilePath) {
    this.resBundle = ResourceBundle.getBundle(propFilePath);
  }
  
  /**
   * 指定国际化参数初始化工具类<br>
   * locale可以为下列值或者符合标准的值:<br>
   * {@code locale = new Locale("zh", "CN");}<br>
   * {@code locale = new Locale("en", "US");}
   * @param propFilePath the base name of the resource bundle, a fully qualified class name
   * @param locale the locale for which a resource bundle is desired
   */
  public PropertiesUtil(String propFilePath, Locale locale) {
    this.resBundle = ResourceBundle.getBundle(propFilePath, locale);
  }
  
  /**
   * 
   * @param key properties文件中的key值
   * @return 读取的值
   */
  public  String readValue(String key) {
    try {
      return this.resBundle.getString(key);
    } catch (MissingResourceException e) {
      return null;
    }
  }

}
