package org.jumutang.giftpay.common.util;

import org.jumutang.giftpay.common.constant.NumConstant;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.*;
import java.util.*;

/**
 * @Description: 初始化敏感词库，将敏感词加入到HashMap中，构建DFA算法模型
 * @Author : chencq
 * @Date ： 2014年4月20日 下午2:27:06
 * @version 1.0
 */
public class SensitiveWordInit {
	private String ENCODING = "UTF-8";    //字符编码
	@SuppressWarnings("rawtypes")
	public static HashMap sensitiveWordMap;
	
	public SensitiveWordInit(){
		super();
	}
	
	/**
	 * @author chenming 
	 * @date 2014年4月20日 下午2:28:32
	 * @version 1.0
	 */
	@SuppressWarnings("rawtypes")
	public Map initKeyWord(){
		try {
			if(null == sensitiveWordMap || NumConstant.ZERO == sensitiveWordMap.size()) {
				//读取敏感词库
				Set<String> keyWordSet = readSensitiveWordFile();
				//将敏感词库加入到HashMap中
				addSensitiveWordToHashMap(keyWordSet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sensitiveWordMap;
	}

	/**
	 * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：<br>
	 * 中 = {
	 *      isEnd = 0
	 *      国 = {<br>
	 *      	 isEnd = 1
	 *           人 = {isEnd = 0
	 *                民 = {isEnd = 1}
	 *                }
	 *           男  = {
	 *           	   isEnd = 0
	 *           		人 = {
	 *           			 isEnd = 1
	 *           			}
	 *           	}
	 *           }
	 *      }
	 *  五 = {
	 *      isEnd = 0
	 *      星 = {
	 *      	isEnd = 0
	 *      	红 = {
	 *              isEnd = 0
	 *              旗 = {
	 *                   isEnd = 1
	 *                  }
	 *              }
	 *      	}
	 *      }
	 * @author chenming 
	 * @date 2014年4月20日 下午3:04:20
	 * @param keyWordSet  敏感词库
	 * @version 1.0
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addSensitiveWordToHashMap(Set<String> keyWordSet) {
		sensitiveWordMap = new HashMap(keyWordSet.size());     //初始化敏感词容器，减少扩容操作
		String key = null;
		Map nowMap = null;
		Map<String, String> newWorMap = null;
		//迭代keyWordSet
		Iterator<String> iterator = keyWordSet.iterator();
		while(iterator.hasNext()){
			key = iterator.next();    //关键字
			nowMap = sensitiveWordMap;
			for(int i = 0 ; i < key.length() ; i++){
				char keyChar = key.charAt(i);       //转换成char型
				Object wordMap = nowMap.get(keyChar);       //获取
				
				if(wordMap != null){        //如果存在该key，直接赋值
					nowMap = (Map) wordMap;
				}
				else{     //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
					newWorMap = new HashMap<String,String>();
					newWorMap.put("isEnd", "0");     //不是最后一个
					nowMap.put(keyChar, newWorMap);
					nowMap = newWorMap;
				}
				
				if(i == key.length() - 1){
					nowMap.put("isEnd", "1");    //最后一个
				}
			}
		}
	}

	/**
	 * 读取敏感词库中的内容，将内容添加到set集合中
	 * @author chenming 
	 * @date 2014年4月20日 下午2:31:18
	 * @return
	 * @version 1.0
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private Set<String> readSensitiveWordFile() throws Exception {
		Set<String> set = null;
		InputStreamReader read = null;
		BufferedReader bufferedReader = null;
		try {
			Resource resource = new ClassPathResource("/keywords/bd_minganci.txt");
			//String fileName = resource.getFilename();
			if (resource.isReadable()) {
				read = new InputStreamReader(resource.getInputStream(),ENCODING);
				set = new HashSet<String>();
				bufferedReader = new BufferedReader(read);
				String txt = null;
				while((txt = bufferedReader.readLine()) != null){    //读取文件，将文件内容放入到set中
					set.add(txt);
				}
			} else {
				throw new RuntimeException("敏感词库文件不存在");
			}
		} catch (Exception e) {
			throw e;
		}finally{
			if(null != bufferedReader) {
				bufferedReader.close();
				bufferedReader = null;
			}
			if(null != read) {
				read.close();     //关闭文件流
				read = null;
			}
		}
		return set;
	}

	/***
	 * 读取本地文件流
	 * @param filePaht
	 * @return
	 * @throws IOException
	 */
	private InputStream readLocalFileInputStream(String filePaht) throws IOException {
		File file = new File(filePaht);    //读取文件
		if(file.isFile() && file.exists()){      //文件流是否存在
			FileInputStream fis = new FileInputStream(file);
			return  fis;
		}else{         //不存在抛出异常信息
			throw new IOException("敏感词库文件不存在");
		}
	}

	/***
	 * 读取类路径文件流
	 * @param classFilePath 类路径文件
	 * @return
	 * @throws IOException
	 */
	private InputStream readLocalClassResourceInputStream(String classFilePath) throws IOException {
		try {
			Resource resource = new ClassPathResource("/keywords/bd_minganci.txt");
			if (resource.isReadable()) {
				return resource.getInputStream();
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("敏感词库文件不存在");
		}
	}


	/****
	 * 读网络文件转化流
	 * @return
	 */
	private InputStream readUrlResourceInputStream(String url) throws IOException {
		try {
			UrlResource resource = new UrlResource(url);
			if (resource.isReadable()) {
				//URLConnection对应的getInputStream()。
				return resource.getInputStream();
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("敏感词库文件不存在");
		}
	}

	/**
	 * 打印输入流的内容
	 * @param is
	 * @throws IOException
	 */
	private static void  printContent(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line=br.readLine()) != null) {
			System.out.println(line);
		}
		if (is != null) {
			is.close();
			is = null;
		}
		if (br != null) {
			br.close();
			br = null;
		}
	}

//	public static void main(String[] args) throws IOException {
//		InputStream is = new SensitiveWordInit().readLocalFileInputStream("/workspace/new_company/tt/chat/chat.service/src/main/java/com/hhsoft/chat/utils/bd_minganci.txt");
//		printContent(is);
//	}

}
