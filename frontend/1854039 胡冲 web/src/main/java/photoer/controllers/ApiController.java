package photoer.controllers;


import com.alibaba.fastjson.*;
import photoer.Filter.PhotoBean;
import photoer.services.ApiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin

public class ApiController {

    // @Autowired

    //restTemplate用于发送http请求
    // private RestTemplate restTemplate;

    /***********HTTP GET method*************/
    /*API来源：Pixabay*/
    @GetMapping("/PixabayApi")
  //  @RequestMapping(value="/PixabayApi", method=RequestMethod.GET, produces = "application/json")
 //   @ResponseBody
    public String getPixabayJson(String keyword) throws Exception {

        //api-key 存在mykey中
        String mykey = "18780758-8bbf139d47c79c547ab06e3aa";
       // keyword = "flower";
        //拼出url
        String api = "https://pixabay.com/api/?key=" + mykey + "&q=" + keyword + "&image_type=photo&pretty=true";

        //调用Service层
        String str = ApiService.httpRequest(api);


        //把获得的string转json
        JSONObject jsonObject = JSON.parseObject(str);

        String all="[";

        PhotoBean photoBean;
        try {

            JSONArray jsonArray = jsonObject.getJSONArray("hits");

            if (jsonArray == null) {
                throw new Exception("数据获取有误！");
            }

            for (int i = 0; i < jsonArray.toArray().length; i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String imgUrl=jsonObject.getString("largeImageURL");
                //String likes = jsonObject.getString("likes");
                String viewUrl = jsonObject.getString("previewURL");
                //String json="{\"imgUrl\":\"require('" +  imgUrl + "')\",\"viewUrl\":\"require('" +  viewUrl + "')\"}";
                String json = "{\"imgUrl\":\"" + imgUrl + "\",\"viewUrl\":\"" + viewUrl + "\"}";
                all=all+json;
                if(i == (jsonArray.toArray().length-1)){
                    all+="]";
                }
                else{
                    all += ",";
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return all;
    }


    /*API来源：Unsplash*/
    @GetMapping("/UnsplashApi")

    public String searchUnsplashJson(String keyword) throws Exception {
        String mykey = "S6egruY6cVbQEpiJdo2jn_GBsX4BHsCCpQ4TfxogqCQ";


        //https://api.unsplash.com/search/collections/?client_id=S6egruY6cVbQEpiJdo2jn_GBsX4BHsCCpQ4TfxogqCQ&page=3&query=office
        //拼出url
        String api = "https://api.unsplash.com/search/collections/?client_id=" + mykey + "&query=" + keyword + "&per_page=20";

        //调用Service层
        String str = ApiService.httpRequest(api);


        //把获得的string转json
        JSONObject jsonObject = JSON.parseObject(str);
        String all="[";

        try {

            JSONArray jsonArray = jsonObject.getJSONArray("results");
            //results是一个json数组，每一项存储了一张图片的数据
            if (jsonArray == null) {
                throw new Exception("数据获取有误！");
            }
            for (int i = 0; i < jsonArray.toArray().length; i++) {
                JSONObject jobj = jsonArray.getJSONObject(i);

                //jsonObject是数组中的一项，是一个json串 url是json串中的一个key
               /* String jobj=jsonObject.toString();
                Gson gson=new Gson();
                Map map=gson.fromJson(jobj, Map.class);
                JSONArray Url=(JSONArray) map.get("preview_photos");*/
                JSONArray jobj1 = jobj.getJSONArray("preview_photos");
                JSONObject URLS=jobj1.getJSONObject(0);

                JSONObject IU=URLS.getJSONObject("urls");
                String imgUrl=IU.getString("full");
                String viewUrl=IU.getString("small");

                String json = "{\"imgUrl\":\"" + imgUrl + "\",\"viewUrl\":\"" + viewUrl + "\"}";
                all = all + json;
                if (i == (jsonArray.toArray().length - 1)) {
                    all += "]";
                } else {
                    all += ",";
                }
            }

        }
        catch (JSONException e) {

            e.printStackTrace();
        }
       // return apiService.getSinglePic_unsplash(lists);
        return all;
    }


    /*API来源：splashbase*/
    @GetMapping("SplashbaseApi")
    public String splashbaseJson(String keyword) throws Exception {

        //http://www.splashbase.co/api/v1/images/search?query=flower
        //拼出url
        keyword="flower";
        String api = "http://www.splashbase.co/api/v1/images/search?query=" + keyword;

        //调用Service层
        String str = ApiService.httpRequest(api);

        //把获得的string转json
        JSONObject jsonObject = JSON.parseObject(str);

        String all="[";

        try {
            //    jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("images");
            if (jsonArray == null) {
                throw new Exception("数据获取有误!");
            }

            for (int i = 0; i < jsonArray.toArray().length; i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String imgUrl=jsonObject.getString("large_url");
                //String likes = jsonObject.getString("likes");
                String viewUrl = jsonObject.getString("url");
                String json="{\"imgUrl\":\"" +  imgUrl + "\",\"viewUrl\":\"" +  viewUrl + "\"}";
                all=all+json;
                if(i == (jsonArray.toArray().length-1)){
                    all+="]";
                }
                else{
                    all += ",";
                }
            }
            return all;
        } catch (JSONException e) {
            e.printStackTrace();
        }
return  null;
    }


    /*API来源：青云客*/
    //http://api.qingyunke.com/api.php?key=free&appid=0&msg=你好
    @GetMapping("chatRobotApi")
    public String chatRobot(String msg) throws Exception {

        String api = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + msg;

        //调用Service层
        String str = ApiService.httpRequest(api);

        //把获得的string转json

        JSONObject jsonObject = JSON.parseObject(str);
        String all="{\"content\":\"";

        try {


                all = all+jsonObject.getString("content");
                all=all+"\"}";


        } catch (JSONException e) {
            e.printStackTrace();
        }

        //把获得的string转为xml,再转为json处理 模拟返回xml接口
        ApiService.xml2json(ApiService.json2xml(all));
        return jsonObject.getString("content");
    }























    //563492ad6f9170000100000188dc928c765a4ccba0a01a8cd5a36ef4

    /*API来源：stockvault*/
    /*返回格式：XML*/
    @GetMapping("stockvaultsApi")
    public List<PhotoBean> stockvaultXml(String keyword) throws Exception {
        // String mykey="563492ad6f9170000100000188dc928c765a4ccba0a01a8cd5a36ef4";
        // String id="";
        //  keyword = "flower";
        //http://www.splashbase.co/api/v1/images/search?query=flower
        //拼出url
        String api = "https://www.stockvault.net/api/xml/?type=byUser&query=1130";

        //调用Service层
        String str = ApiService.httpRequest(api);
        try {

            //把获得的string转json
            //JSONObject jsonObject = JSON.parseObject(str);
            JSONObject resultJson = JSONObject.parseObject(str);
            String resultDataXML = resultJson.getString("results");
            if ("".equals(resultDataXML) && resultDataXML != null) {
                List<PhotoBean> lists = ApiService.RealResultXML(resultDataXML);
                if (lists != null) {
                    System.out.println("解析成功");
                    return lists;
                } else {
                    System.out.println("解析失败");
                }
            } else {
                System.out.println("无数据");
            }

        } catch (Exception e) {
            System.out.println(str);
            e.printStackTrace();



        }
        return null;
    }
}




























