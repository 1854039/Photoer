package photoer.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import photoer.Filter.PhotoBean;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;
import org.dom4j.DocumentHelper;

import org.dom4j.Document;
import org.dom4j.Element;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApiService {

    public static List<PhotoBean> RealResultXML(String resultDataXML) {
        Boolean flag = true;
        Document doc = null;
        try {
            //例子
            doc = (Document) DocumentHelper.parseText(resultDataXML); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            //解析只能一层一层的获取,如得到html标签不能直接得到body内部的标签（假设）
            Element body = rootElt.element("photo"); // 获取根节点下子节点
            //多个p
            Iterator p = rootElt.elementIterator("p");
            List<PhotoBean> lists=new ArrayList<>();

            // 遍历row节点
            while (p.hasNext()) {
                PhotoBean photoBean = new PhotoBean();
                Element curP = (Element) p.next();//当前的p标签
                photoBean.imgUrl = curP.element("son1").getTextTrim(); // 拿到p节点下的子节点row值
                photoBean.viewUrl = curP.element("son2").getTextTrim();
                lists.add(photoBean);
            }
            return lists;
        }catch( Exception e ){
            flag = false;
        }
     return  null;

    }

    public static String httpRequest(String apiPath) {
        //缓冲
        BufferedReader in = null;
        StringBuffer result = null;

        //调用的api的接口地址
        String apiUrl = apiPath;
        try {
            URL url = new URL(apiPath);
            //打开和url之间的连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            connection.connect();
            result = new StringBuffer();
            //读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return result.toString(); //返回json字符串
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

/*由于没有找到返回xml的接口 因此我将接收到的json转化为xml 当作接收到的值 用的是stax框架*/
    public static String json2xml(String json) {
        StringReader input = new StringReader(json);
        StringWriter output = new StringWriter();
        JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).repairingNamespaces(false).build();
        try {
            XMLEventReader reader = new JsonXMLInputFactory(config).createXMLEventReader(input);
            XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(output);
            writer = new PrettyXMLEventWriter(writer);
            writer.add(reader);
            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (output.toString().length() >= 38) {// remove <?xml version="1.0" encoding="UTF-8"?>
            System.out.println(output.toString().substring(39));
            return output.toString().substring(39);
        }
        return output.toString();
    }
    //在service层再把xml转回string
    public static String xml2json(String xml) {
        StringReader input = new StringReader(xml);
        StringWriter output = new StringWriter();
        JsonXMLConfig config = new JsonXMLConfigBuilder().autoArray(true)
                .autoPrimitive(true).prettyPrint(true).build();
        try {
            XMLEventReader reader = XMLInputFactory.newInstance()
                    .createXMLEventReader(input);
            XMLEventWriter writer = new JsonXMLOutputFactory(config)
                    .createXMLEventWriter(output);
            writer.add(reader);
            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output.toString();
    }









    /*unsplash需要在搜索到图片id后 调用getid获取图片url，存入bean中
     * param： 搜索后产生的bean，只带有图片id
     * */
    //https://api.unsplash.com/photos?client_id=S6egruY6cVbQEpiJdo2jn_GBsX4BHsCCpQ4TfxogqCQ&id=eOLpJytrbsQ
    public static String getSinglePic_unsplash( List<PhotoBean> p)
    {
        String all="[";
        //对bean中的每张图片 根据其id 调用get接口 获取url
        for(int i=0;i<p.size();i++)
        {
            //获取单张图片的接口url
            String mykey="S6egruY6cVbQEpiJdo2jn_GBsX4BHsCCpQ4TfxogqCQ";
            String id=p.get(i).id;
            String url="https://api.unsplash.com/photos?client_id="+mykey+"&id="+id+"&lang=en";
            String r=httpRequest(url);//r存储了单张图片信息

            //把获得的string转json
            JSONObject jsonObject = JSON.parseObject(r);
            System.out.println(jsonObject);
//eOLpJytrbsQ



                JSONObject t = jsonObject.parseObject("urls");
                    String imgUrl=jsonObject.getString("full");
                    String viewUrl = jsonObject.getString("regular");
                    String json="{\"imgUrl\":\"" +  imgUrl + "\",\"viewUrl\":\"" +  viewUrl + "\"}";
                    all=all+json;
                if(i == (p.size()-1)){
                    all+="]";
                }
                else{
                    all += ",";
                }

                }

            /* catch (JSONException e) {
                e.printStackTrace();
            }

        }*/

        return all;
    }




    }
