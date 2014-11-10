package com.changhong.asynctransfer;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public class XMLHelper {
	
	/**
	 * 创建一个标准的记录下载信息的xml文档
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <file fileName="" url="">
	 * </file>
	 * @param fileName
	 * @param url
	 * @return
	 */
	public static Document createDocument(){
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} 
		return doc;
		
	}
	
	/**
	 * 将XML文档（Document）保存到本地文件
	 * @param document 构建好的Document 
	 * @param fileName 保存位置和文件名/path/.../file.xml
	 * @throws TransformerException
	 */
	public static void saveDocument(Document document,String fileName) throws TransformerException{
		//创建Transformer对象，它的作用是将Document对象以流的方式输出
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer();
	    //将Document对象转为DOMSource
	    DOMSource source = new DOMSource(document);
	    //创建流
	    StreamResult result = new StreamResult(new File(fileName));
	    //设置输出格式
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    //输出保存
	    transformer.transform(source, result);
	}
}
