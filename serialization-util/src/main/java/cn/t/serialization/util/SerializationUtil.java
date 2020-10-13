package cn.t.serialization.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 序列号相关工具类<br/>
 * 生成的序列化文件位于{@code serialization/tmp/}目录下<br/>
 * 1. 实现了Serializable接口的Pojo类的深度克隆<br/>
 * 2. 序列化对象<br/>
 * 3. 反序列化对象<br/>
 * 
 * @author tmq
 * @version 1.0
 */
public class SerializationUtil {

	/**
	 * 文件夹名
	 */
	private static final String FOLDER_NM = "serialization/tmp/";

	/**
	 * Java 的标准约定的扩展名
	 */
	private static final String EXTENSION_NM = ".ser";

	public static void main(String[] args) throws Exception {
		String path = "tmq.xxx";
		
		serializeObj("aaaaa", path);
	}

	/**
	 * 深度克隆(复制)传入的对象<br/>
	 * 对象中所包含的所有引用对象都必须实现Serializable接口
	 * 
	 * @param <T> 对象类型
	 * @param obj 被克隆(复制)的对象
	 * @return 克隆(复制)后的对象
	 * @throws Exception 异常 | 对象为null时
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T cloneObj(T obj) throws Exception {
		if (obj == null) {
			throw new NullPointerException("parameter can not be null");
		}
		// 字节数组输出流
		ByteArrayOutputStream bos = null;
		// 序列化机制的输出流
		ObjectOutputStream oos = null;
		// 字节数组输入流
		ByteArrayInputStream bis = null;
		// 序列化机制的输入流
		ObjectInputStream ois = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			// 写入对象
			oos.writeObject(obj);
			oos.flush();
			bis = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bis);
			return (T) (ois.readObject());
		} catch (Exception e) {
			throw e;
		} finally {
			// ByteArrayOutputStream和ByteArrayInputStream理论上在jvm垃圾回收时候会自动关闭回收，可以不用调用close()
			// 这一点和文件流之类的不一样
			try {
				if (bis != null) {
					bis.close();
				}
				if (ois != null) {
					ois.close();
				}
				if (oos != null) {
					oos.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
	}

	/**
	 * 序列号对象到文件(当前目录下)
	 * 
	 * @param <T> 对象类型
	 * @param obj 序列化到文件的对象
	 * @return 序列化成功后的文件路径
	 * @throws Exception 异常
	 */
	public static <T extends Serializable> String serializeObj(T obj) throws Exception {
		return serialize(obj, createFile(FOLDER_NM + idGenerator() + EXTENSION_NM));
	}

	/**
	 * 序列号对象到文件(指定目录)
	 * 
	 * @param <T>      对象类型
	 * @param obj      序列化到文件的对象
	 * @param filePath 目录或者文件名
	 * @return 序列化成功后的文件路径
	 * @throws Exception 异常
	 */
	public static <T extends Serializable> String serializeObj(T obj, String filePath) throws Exception {
		return serialize(obj, createFile(filePath));
	}

	/**
	 * 创建文件
	 * 
	 * @param filePath 文件目录/文件名
	 * @return 创建完成后的文件全部路径
	 * @throws Exception 异常
	 */
	private static String createFile(String filePath) throws Exception {
		File file;
		String path = null;
		// 创建前缀文件夹
		file = new File(FOLDER_NM);
		if (!file.exists()) {
			file.mkdirs();
		}
		if (filePath == null || filePath.isEmpty()) {
			// case1: 空路径
			path = FOLDER_NM + idGenerator() + EXTENSION_NM;
		} else {
			// 替换掉文件名中非法的./ ../ .\ ..\  最终的目录都会在指定的前缀文件夹中
			filePath = filePath.replaceAll("[\\.]+/|[\\.]+\\\\", "");
			
			int index = filePath.lastIndexOf("/");
			if (index == -1) {
				index = filePath.lastIndexOf("\\");
			}
			
			// 截取文件夹的名字
			if (index != -1) {
				file = new File(FOLDER_NM + filePath.substring(0, index));
				if (!file.exists()) {
					file.mkdirs();
				}
			}
			
			if (filePath.contains(".")) {
				// case2: 给定了文件名
				path = FOLDER_NM + filePath;
			} else {
				// case3: 只给了一个目录 例如 tmp tmp/ /tmp
				path = FOLDER_NM + filePath +  "/" + idGenerator() + EXTENSION_NM;
			}
		}
		file = new File(path);
		file.createNewFile();
		return path;
	}

	/**
	 * 序列号对象到文件
	 * 
	 * @param <T>      对象类型
	 * @param obj      序列化到文件的对象
	 * @param filePath 文件名
	 * @return 序列化成功后的文件路径
	 * @throws Exception
	 */
	private static <T extends Serializable> String serialize(T obj, String filePath) throws Exception {
		try (FileOutputStream fos = new FileOutputStream(filePath);
				ObjectOutputStream oos = new ObjectOutputStream(fos);) {
			oos.writeObject(obj);
			// close的时候会自动刷新/缓冲区满的时候会自动刷新
			// oos.flush();
		} catch (Exception e) {
			throw e;
		}
		return filePath;
	}

	/**
	 * 从文件中反序列化对象
	 * 
	 * @param <T>      对象类型
	 * @param filePath 目录以及文件名
	 * @return 反序列化的对象
	 * @throws Exception 异常
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deserializeObj(String filePath) throws Exception {
		try (FileInputStream fis = new FileInputStream(filePath); ObjectInputStream ois = new ObjectInputStream(fis);) {
			return (T) ois.readObject();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Id生成器
	 * 
	 * @return 根据日期时间生成的Id
	 */
	private synchronized static String idGenerator() {
		// 设置日期格式--精确到毫秒
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String timeId = df.format(new Date());
		return timeId;
	}
}
