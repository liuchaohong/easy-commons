package com.github.easy.commons.util;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * XStream工具类
 * 对象需要用注解标识
 * @XStreamAlias("message"):别名注解 (作用目标: 类,字段) 
 * @XStreamAsAttribute 转换成属性 (作用目标: 字段)
 * @XStreamImplicit:隐式集合, @XStreamImplicit(itemFieldName="part") (作用目标: 集合字段)
 * @XStreamConverter(SingleValueCalendarConverter.class):注入转换器 (作用目标: 对象)
 * @XStreamOmitField:忽略字段  (作用目标: 字段) 
 * Auto-detect Annotations 自动侦查注解 
 * xstream.autodetectAnnotations(true); 
 * 
 * @author LIUCHAOHONG
 *
 */
public class XStreamUtil{
	
	private static Logger logger = LoggerFactory.getLogger(XStreamUtil.class);
	
	private static String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; 
	private static String PREFIX_CDATA = "<![CDATA[";
	private static String SUFFIX_CDATA = "]]>";
	 
	/**
	 * 对象转化成xml
	 * @param obj
	 * @return
	 */
    public static String toXml(Object obj){
        return toXmlIsAddCDATA(obj, false); 
    }
    
    /**
     * 对象转化成xml,并判断是否需要检测“"<![CDATA  ]]>”特殊字符
     * @param obj
     * @param isAddCDATA
     * @return
     */
    public static String toXmlIsAddCDATA(Object obj, boolean isAddCDATA){
    	XStream xstream = initXStream(isAddCDATA);
        ////如果没有这句，xml中的根元素会是<包.类名>；或者说：注解根本就没生效，所以的元素名就是类的属性
        xstream.processAnnotations(obj.getClass()); //通过注解方式的，一定要有这句话
        return xstream.toXML(obj);
    }
    
    /**
     * xml转化成对象
     * @param xmlStr
     * @param cls
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String xmlStr, Class<T> cls){
        //注意：不是new Xstream(); 否则报错：java.lang.NoClassDefFoundError: org/xmlpull/v1/XmlPullParserFactory
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(cls);
		T obj = (T)xstream.fromXML(xmlStr);
        return obj;         
    } 
    
    /**
     * 对象转化成xml,并写入到文件
     * @param obj
     * @param file
     * @return
     */
    public static boolean toXmlFile(Object obj, File file){
        String strXml = xmlHead + "\n\t" + toXml(obj);
        try {
			FileUtils.writeStringToFile(file, strXml);
		} catch (IOException e) {
			logger.error("写{}文件失败!", file, e);
			return false;
		}
        return true;
    }
     
    /**
     * 从文件读取xml,并转化成对象
     * @param file
     * @param cls
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObjectFromFile(File file, Class<T> cls){
        XStream xstream = new XStream(new DomDriver("utf-8"));
        xstream.processAnnotations(cls);
        T obj = null;
        try {
            obj = (T)xstream.fromXML(file);
        } catch (Exception e) {
            logger.error("解析{}文件失败!", file, e);
        }
        return obj;         
    } 
    
	// 判断是否需要检测“"<![CDATA  ]]>”特殊字符
	private static XStream initXStream(boolean isAddCDATA){
		XStream xstream = null;
		if(isAddCDATA){
			xstream =  new XStream(
		    new XppDriver() {
		           public HierarchicalStreamWriter createWriter(Writer out) {
		              return new PrettyPrintWriter(out) {
		              protected void writeText(QuickWriter writer, String text) {
		              	if(text.startsWith(PREFIX_CDATA) && text.endsWith(SUFFIX_CDATA)) {
		              		writer.write(text);
		                }else{
		                    super.writeText(writer, text);
		                }
		               }
		             };
		           };
		         }
		     );
		}else{
			xstream = new XStream(new XppDriver());
//			XStream xstream = new XStream(new DomDriver("utf-8")); //指定编码解析器,直接用jaxp dom来解释
		}
		return xstream;
	}
	
}
