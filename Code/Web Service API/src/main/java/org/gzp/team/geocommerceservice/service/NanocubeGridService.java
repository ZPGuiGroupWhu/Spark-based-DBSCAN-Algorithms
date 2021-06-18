package org.gzp.team.geocommerceservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.gzp.team.geocommerceservice.model.geojson.Crs;
import org.gzp.team.geocommerceservice.model.geojson.Feature;
import org.gzp.team.geocommerceservice.model.geojson.GeoJson;
import org.gzp.team.geocommerceservice.model.geojson.Geometry;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class NanocubeGridService {
    /**
     * V3版本项目
     * @param hostName
     * @param level
     * @return
     */
    public String getNanocubeData(String hostName,int level){
        /**
         * 第一产业  4
         * 第二产业  5,8,14,17
         * 第三产业  0,1,2,3,6,7,9,10,11,12,13,15,16,18
         */
        //三个产业
        String url=hostName+"/count.a(\"location\",dive([]," +
                level +
                "),\"img\").a(\"Industry\",set(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18))";
//        //第一产业
//        String url1=hostName+"/count.a(\"location\",dive([]," +
//                level +
//                "),\"img\").r(\"Industry\",set(4))";
//        //第二产业
//        String url2=hostName+"/count.a(\"location\",dive([]," +
//                level +
//                "),\"img\").r(\"Industry\",set(5,8,14,17))";
//        //第三产业
//        String url3=hostName+"/count.a(\"location\",dive([]," +
//                level +
//                "),\"img\").r(\"Industry\",set(0,1,2,3,6,7,9,10,11,12,13,15,16,18))";
        RestTemplate template=new RestTemplate();
        ResponseEntity<String> response=template.exchange(url, HttpMethod.GET,null,String.class);
//        ResponseEntity<String> response1=template.exchange(url1, HttpMethod.GET,null,String.class);
//        ResponseEntity<String> response2=template.exchange(url2, HttpMethod.GET,null,String.class);
//        ResponseEntity<String> response3=template.exchange(url3, HttpMethod.GET,null,String.class);
        String   JsonString=response.getBody();
//        String PIValue=response1.getBody();
//        String SIValue=response2.getBody();
//        String TIValue=response3.getBody();
        GeoJson geoJson=new GeoJson();
        geoJson.setType("FeatureCollection");
        Crs crs=new Crs();
        crs.setType("name");
        Map map=new HashMap();
        map.put("name","EPSG:4326");
        crs.setProperties(map);
        geoJson.setCrs(crs);
        List<Feature> list=new ArrayList<>();
        JSONObject jsonObject= JSON.parseObject(JsonString);
        JSONObject root=jsonObject.getJSONObject("root");
        JSONArray  childrens=root.getJSONArray("children");
        for(int i=0;i<childrens.size();i++){
            Feature feature=new Feature();
            feature.setType("Feature");
            feature.setId(i);
            Geometry geometry=new Geometry();
            geometry.setType("Polygon");
            int PIValue=0,SIValue=0,TIValue=0;
            JSONObject children =childrens.getJSONObject(i);
            List coordinates=new ArrayList();
            coordinates=BoundingBox(children.getDouble("x"),children.getDouble("y"),level);
            geometry.setCoordinates(coordinates);
            feature.setGeometry(geometry);
            JSONArray jsonArray=children.getJSONArray("children");
            for(int j=0;j<jsonArray.size();j++){
                JSONObject jsonObject1=jsonArray.getJSONObject(j);
                int path=JSONObject.parseArray(jsonObject1.getJSONArray("path").toJSONString()).getInteger(0);
                int val=jsonObject1.getInteger("val");
//                List list1=Arrays.asList(new int[]{5,8,14,17});
//                List list2=Arrays.asList(new int[]{0,1,2,3,6,7,9,10,11,12,13,15,16,18});
                Set set1=new HashSet();
                set1.add(5); set1.add(8);set1.add(14); set1.add(17);
                Set set2=new HashSet();
                set2.add(0);set2.add(1);set2.add(2);set2.add(3);set2.add(6);
                set2.add(7);set2.add(9);set2.add(10);set2.add(11);set2.add(12);
                set2.add(13);set2.add(15);set2.add(16);set2.add(18);
                if(path==4){
                    PIValue=val;
                }else if(set1.contains(path)){
                    SIValue+=val;
                }else if(set2.contains(path)){
                    TIValue+=val;
                }
            }
            Map map1=new HashMap();
            map1.put("PIValue" , PIValue);
            map1.put("SIValue" , SIValue);
            map1.put("TIValue" , TIValue);
            feature.setProperties(map1);
            list.add(feature);
        }
        geoJson.setFeatures(list);
        return JSON.toJSONString(geoJson);
//        return response.getBody();
    }
    public static List BoundingBox(double x,double y,int level){
        //根据x,y,level获取对应的格网
        //坐标系：web墨卡托 ，起点:左下角，左下角坐标：（-20037508.342789244，-20037508.342789244）
        //瓦片边长
        double d=20037508.342789244/Math.pow(2,level-1);
        //左下角
        double x1=-20037508.342789244+x*d;
        double y1=-20037508.342789244+y*d;

        //右下角
        double x2=-20037508.342789244+(x+1)*d;
        double y2=-20037508.342789244+y*d;

        //左上角
        double x3=-20037508.342789244+x*d;
        double y3=-20037508.342789244+(y+1)*d;

        //右上角
        double x4=-20037508.342789244+(x+1)*d;
        double y4=-20037508.342789244+(y+1)*d;

        List list=new ArrayList();
        list.add(change(x1,y1));
        list.add(change(x2,y2));
        list.add(change(x4,y4));
        list.add(change(x3,y3));
        //因为Geojson是三层的数组，这里外面多加一层
        List listFinal=new ArrayList();
        listFinal.add(list);
        return listFinal;
    }
    //web墨卡托转WGS84
    public static double[] change(double x,double y){
        double lon=x/20037508.342789244*180;
        double lat=y/20037508.342789244*180;
        lat=180/Math.PI*(2*Math.atan(Math.exp(lat*Math.PI/180))-Math.PI/2);
        return new double[]{lon,lat};
    }

    /**
     * V4版本项目
     * @param hostName
     * @param level
     * @return
     */
    public String getNanocubeDataChongqing(String hostName,int level){

        /**
         * 第一产业  2
         * 第二产业  3、10、12、16
         * 第三产业  0,1,4,5,6,7,8,9,11,13,14,15,17,18
         */
        String url=hostName +
                "/q(chongqing.b('location',dive(img2d(0,0,0)," +
                level +
                "),'tile" +
                level +
                "').b('Industry',dive(1)))";
        RestTemplate template=new RestTemplate();
        ResponseEntity<String> response=template.exchange(url, HttpMethod.GET,null,String.class);
        String   JsonString=response.getBody();
        GeoJson geoJson=new GeoJson();
        geoJson.setType("FeatureCollection");
        Crs crs=new Crs();
        crs.setType("name");
        Map map=new HashMap();
        map.put("name","EPSG:4326");
        crs.setProperties(map);
        geoJson.setCrs(crs);
        List<Feature> list=new ArrayList<>();
        JSONArray jsonArray= JSON.parseArray(JsonString);
        JSONObject jsonObject=jsonArray.getJSONObject(0);
        JSONArray jsonArray1=jsonObject.getJSONArray("index_columns");
        JSONArray jsonArray2=jsonObject.getJSONArray("measure_columns");
        JSONArray locationValueArray=jsonArray1.getJSONObject(0).getJSONArray("values");
        JSONArray IndustryValueArray=jsonArray1.getJSONObject(1).getJSONArray("values");
        JSONArray CountValueArray=jsonArray2.getJSONObject(0).getJSONArray("values");
        List<String>list1=new ArrayList<>();
        for(int i=0;i<locationValueArray.size()-1;i++){
            list1.add(locationValueArray.getDouble(i)+","+locationValueArray.getDouble(i+1));
            i++;
        }
        HashMap<String,List<Map>> hashMap=new HashMap<>();
        for(int i=0;i<list1.size();i++){
            Map map1=new HashMap();
            map1.put("Industry",IndustryValueArray.getDouble(i));
            map1.put("count",CountValueArray.getDouble(i));
            List<Map>list2=new ArrayList<>();
            list2.add(map1);
            if(hashMap.containsKey(list1.get(i))){
                hashMap.get(list1.get(i)).add(map1);
            }else hashMap.put(list1.get(i),list2);
        }
        int i=0;
        int PIValueFinal=0,SIValueFinal=0,TIValueFinal=0;
        for(String key:hashMap.keySet()){
            List<Map> values=hashMap.get(key);
            Feature feature=new Feature();
            feature.setType("Feature");
            feature.setId(i);
            i++;
            Geometry geometry=new Geometry();
            geometry.setType("Polygon");
            int PIValue=0,SIValue=0,TIValue=0;
            List coordinates=new ArrayList();
            coordinates=BoundingBox(Double.parseDouble( key.split(",")[0]),Double.parseDouble( key.split(",")[1]),level);
            geometry.setCoordinates(coordinates);
            feature.setGeometry(geometry);
            for(Map map1:values){
                int Industry=Double.valueOf(map1.get("Industry").toString()).intValue();
                int val=Double.valueOf(map1.get("count").toString()).intValue();
                Set set1=new HashSet();
                set1.add(3); set1.add(10);set1.add(12); set1.add(16);
                Set set2=new HashSet();
                set2.add(0);set2.add(1);set2.add(4);set2.add(5);set2.add(6);
                set2.add(7);set2.add(8);set2.add(9);set2.add(11);set2.add(13);
                set2.add(14);set2.add(15);set2.add(17);set2.add(18);
                if(Industry==2){
                    PIValue=val;
                }else if(set1.contains(Industry)){
                    SIValue+=val;
                }else if(set2.contains(Industry)){
                    TIValue+=val;
                }
            }
            Map map2=new HashMap();
            map2.put("PIValue" , PIValue);
            map2.put("SIValue" , SIValue);
            map2.put("TIValue" , TIValue);
            feature.setProperties(map2);
            PIValueFinal+=PIValue;
            SIValueFinal+=SIValue;
            TIValueFinal+=TIValue;
            list.add(feature);
        }
        Map mapFinal=new HashMap();
        mapFinal.put("PIValue" , PIValueFinal);
        mapFinal.put("SIValue" , SIValueFinal);
        mapFinal.put("TIValue" , TIValueFinal);
        geoJson.setProperties(mapFinal);
        geoJson.setFeatures(list);
        return JSON.toJSONString(geoJson);
    }
    public String getNanocubeDataHubei(String hostName,int level){

        /**
         * 第一产业 15
         * 第二产业 14,4,11,7,13
         * 第三产业 0,10,6,12,2,1,9,3,5,8
         */
        String url=hostName +
                "/q(hubei.b('location',dive(img2d(0,0,0)," +
                level +
                "),'tile" +
                level +
                "').b('Industry',dive(1)))";
        RestTemplate template=new RestTemplate();
        ResponseEntity<String> response=template.exchange(url, HttpMethod.GET,null,String.class);
        String   JsonString=response.getBody();
        GeoJson geoJson=new GeoJson();
        geoJson.setType("FeatureCollection");
        Crs crs=new Crs();
        crs.setType("name");
        Map map=new HashMap();
        map.put("name","EPSG:4326");
        crs.setProperties(map);
        geoJson.setCrs(crs);
        List<Feature> list=new ArrayList<>();
        JSONArray jsonArray= JSON.parseArray(JsonString);
        JSONObject jsonObject=jsonArray.getJSONObject(0);
        JSONArray jsonArray1=jsonObject.getJSONArray("index_columns");
        JSONArray jsonArray2=jsonObject.getJSONArray("measure_columns");
        JSONArray locationValueArray=jsonArray1.getJSONObject(0).getJSONArray("values");
        JSONArray IndustryValueArray=jsonArray1.getJSONObject(1).getJSONArray("values");
        JSONArray CountValueArray=jsonArray2.getJSONObject(0).getJSONArray("values");
        List<String>list1=new ArrayList<>();
        for(int i=0;i<locationValueArray.size()-1;i++){
            list1.add(locationValueArray.getDouble(i)+","+locationValueArray.getDouble(i+1));
            i++;
        }
        HashMap<String,List<Map>> hashMap=new HashMap<>();
        for(int i=0;i<list1.size();i++){
            Map map1=new HashMap();
            map1.put("Industry",IndustryValueArray.getDouble(i));
            map1.put("count",CountValueArray.getDouble(i));
            List<Map>list2=new ArrayList<>();
            list2.add(map1);
            if(hashMap.containsKey(list1.get(i))){
                hashMap.get(list1.get(i)).add(map1);
            }else hashMap.put(list1.get(i),list2);
        }
        int i=0;
        int PIValueFinal=0,SIValueFinal=0,TIValueFinal=0;
        for(String key:hashMap.keySet()){
            List<Map> values=hashMap.get(key);
            Feature feature=new Feature();
            feature.setType("Feature");
            feature.setId(i);
            i++;
            Geometry geometry=new Geometry();
            geometry.setType("Polygon");
            int PIValue=0,SIValue=0,TIValue=0;
            List coordinates=new ArrayList();
            coordinates=BoundingBox(Double.parseDouble( key.split(",")[0]),Double.parseDouble( key.split(",")[1]),level);
            geometry.setCoordinates(coordinates);
            feature.setGeometry(geometry);
            for(Map map1:values){
                int Industry=Double.valueOf(map1.get("Industry").toString()).intValue();
                int val=Double.valueOf(map1.get("count").toString()).intValue();
                Set set1=new HashSet();
                set1.add(14); set1.add(4);set1.add(11); set1.add(7);set1.add(13);
                Set set2=new HashSet();
                set2.add(0);set2.add(10);set2.add(6);set2.add(12);set2.add(2);
                set2.add(1);set2.add(9);set2.add(3);set2.add(5);
                set2.add(8);
                if(Industry==15){
                    PIValue=val;
                }else if(set1.contains(Industry)){
                    SIValue+=val;
                }else if(set2.contains(Industry)){
                    TIValue+=val;
                }
            }
            Map map2=new HashMap();
            map2.put("PIValue" , PIValue);
            map2.put("SIValue" , SIValue);
            map2.put("TIValue" , TIValue);
            feature.setProperties(map2);
            PIValueFinal+=PIValue;
            SIValueFinal+=SIValue;
            TIValueFinal+=TIValue;
            list.add(feature);
        }
        Map mapFinal=new HashMap();
        mapFinal.put("PIValue" , PIValueFinal);
        mapFinal.put("SIValue" , SIValueFinal);
        mapFinal.put("TIValue" , TIValueFinal);
        geoJson.setProperties(mapFinal);
        geoJson.setFeatures(list);
        return JSON.toJSONString(geoJson);
    }
    public String getNanocubeDataChina(String hostName,int level){

        /**
         * 第一产业12
         * 第二产业 8,0,2,13,15
         * 第三产业14,10,4,6,11,1,7,9,3,5
         */
        String url=hostName +
                "/q(China.b('location',dive(img2d(0,0,0)," +
                level +
                "),'tile" +
                level +
                "').b('Industry',dive(1)))";
        RestTemplate template=new RestTemplate();
        ResponseEntity<String> response=template.exchange(url, HttpMethod.GET,null,String.class);
        String   JsonString=response.getBody();
        GeoJson geoJson=new GeoJson();
        geoJson.setType("FeatureCollection");
        Crs crs=new Crs();
        crs.setType("name");
        Map map=new HashMap();
        map.put("name","EPSG:4326");
        crs.setProperties(map);
        geoJson.setCrs(crs);
        List<Feature> list=new ArrayList<>();
        JSONArray jsonArray= JSON.parseArray(JsonString);
        JSONObject jsonObject=jsonArray.getJSONObject(0);
        JSONArray jsonArray1=jsonObject.getJSONArray("index_columns");
        JSONArray jsonArray2=jsonObject.getJSONArray("measure_columns");
        JSONArray locationValueArray=jsonArray1.getJSONObject(0).getJSONArray("values");
        JSONArray IndustryValueArray=jsonArray1.getJSONObject(1).getJSONArray("values");
        JSONArray CountValueArray=jsonArray2.getJSONObject(0).getJSONArray("values");
        List<String>list1=new ArrayList<>();
        for(int i=0;i<locationValueArray.size()-1;i++){
            list1.add(locationValueArray.getDouble(i)+","+locationValueArray.getDouble(i+1));
            i++;
        }
        HashMap<String,List<Map>> hashMap=new HashMap<>();
        for(int i=0;i<list1.size();i++){
            Map map1=new HashMap();
            map1.put("Industry",IndustryValueArray.getDouble(i));
            map1.put("count",CountValueArray.getDouble(i));
            List<Map>list2=new ArrayList<>();
            list2.add(map1);
            if(hashMap.containsKey(list1.get(i))){
                hashMap.get(list1.get(i)).add(map1);
            }else hashMap.put(list1.get(i),list2);
        }
        int i=0;
        int PIValueFinal=0,SIValueFinal=0,TIValueFinal=0;
        for(String key:hashMap.keySet()){
            List<Map> values=hashMap.get(key);
            Feature feature=new Feature();
            feature.setType("Feature");
            feature.setId(i);
            i++;
            Geometry geometry=new Geometry();
            geometry.setType("Polygon");
            int PIValue=0,SIValue=0,TIValue=0;
            List coordinates=new ArrayList();
            coordinates=BoundingBox(Double.parseDouble( key.split(",")[0]),Double.parseDouble( key.split(",")[1]),level);
            geometry.setCoordinates(coordinates);
            feature.setGeometry(geometry);
            for(Map map1:values){
                int Industry=Double.valueOf(map1.get("Industry").toString()).intValue();
                int val=Double.valueOf(map1.get("count").toString()).intValue();
                Set set1=new HashSet();
                set1.add(8); set1.add(0);set1.add(2); set1.add(13);set1.add(15);
                Set set2=new HashSet();
                set2.add(14);set2.add(10);set2.add(4);set2.add(11);set2.add(6);
                set2.add(7);set2.add(1);set2.add(9);set2.add(3);set2.add(5);
                if(Industry==12){
                    PIValue=val;
                }else if(set1.contains(Industry)){
                    SIValue+=val;
                }else if(set2.contains(Industry)){
                    TIValue+=val;
                }
            }
            Map map2=new HashMap();
            map2.put("PIValue" , PIValue);
            map2.put("SIValue" , SIValue);
            map2.put("TIValue" , TIValue);
            feature.setProperties(map2);
            PIValueFinal+=PIValue;
            SIValueFinal+=SIValue;
            TIValueFinal+=TIValue;
            list.add(feature);
        }
        Map mapFinal=new HashMap();
        mapFinal.put("PIValue" , PIValueFinal);
        mapFinal.put("SIValue" , SIValueFinal);
        mapFinal.put("TIValue" , TIValueFinal);
        geoJson.setProperties(mapFinal);
        geoJson.setFeatures(list);
        return JSON.toJSONString(geoJson);
    }
    public String getNanocubeDataV4(String hostName,int level){

        /**
         * 第一产业 10
         * 第二产业 0,5,15
         * 第三产业 8,14,4,6,12,11,2,1,7,9,13,3
         */
        String url=hostName +
                "/q(market.b('location',dive(img2d(0,0,0)," +
                level +
                "),'tile" +
                level +
                "').b('Industry',dive(1)))";
        RestTemplate template=new RestTemplate();
        ResponseEntity<String> response=template.exchange(url, HttpMethod.GET,null,String.class);
        String   JsonString=response.getBody();
        GeoJson geoJson=new GeoJson();
        geoJson.setType("FeatureCollection");
        Crs crs=new Crs();
        crs.setType("name");
        Map map=new HashMap();
        map.put("name","EPSG:4326");
        crs.setProperties(map);
        geoJson.setCrs(crs);
        List<Feature> list=new ArrayList<>();
        JSONArray jsonArray= JSON.parseArray(JsonString);
        JSONObject jsonObject=jsonArray.getJSONObject(0);
        JSONArray jsonArray1=jsonObject.getJSONArray("index_columns");
        JSONArray jsonArray2=jsonObject.getJSONArray("measure_columns");
        JSONArray locationValueArray=jsonArray1.getJSONObject(0).getJSONArray("values");
        JSONArray IndustryValueArray=jsonArray1.getJSONObject(1).getJSONArray("values");
        JSONArray CountValueArray=jsonArray2.getJSONObject(0).getJSONArray("values");
        List<String>list1=new ArrayList<>();
        for(int i=0;i<locationValueArray.size()-1;i++){
            list1.add(locationValueArray.getDouble(i)+","+locationValueArray.getDouble(i+1));
            i++;
        }
        HashMap<String,List<Map>> hashMap=new HashMap<>();
        for(int i=0;i<list1.size();i++){
            Map map1=new HashMap();
            map1.put("Industry",IndustryValueArray.getDouble(i));
            map1.put("count",CountValueArray.getDouble(i));
            List<Map>list2=new ArrayList<>();
            list2.add(map1);
            if(hashMap.containsKey(list1.get(i))){
                hashMap.get(list1.get(i)).add(map1);
            }else hashMap.put(list1.get(i),list2);
        }
        int i=0;
        int PIValueFinal=0,SIValueFinal=0,TIValueFinal=0;
        for(String key:hashMap.keySet()){
            List<Map> values=hashMap.get(key);
            Feature feature=new Feature();
            feature.setType("Feature");
            feature.setId(i);
            i++;
            Geometry geometry=new Geometry();
            geometry.setType("Polygon");
            int PIValue=0,SIValue=0,TIValue=0;
            List coordinates=new ArrayList();
            coordinates=BoundingBox(Double.parseDouble( key.split(",")[0]),Double.parseDouble( key.split(",")[1]),level);
            geometry.setCoordinates(coordinates);
            feature.setGeometry(geometry);
            for(Map map1:values){
                int Industry=Double.valueOf(map1.get("Industry").toString()).intValue();
                int val=Double.valueOf(map1.get("count").toString()).intValue();
                Set set1=new HashSet();
                set1.add(0); set1.add(5);set1.add(15);
                Set set2=new HashSet();
                set2.add(8);set2.add(14);set2.add(6);set2.add(4);set2.add(12);
                set2.add(11);set2.add(2);set2.add(1);set2.add(7);
                set2.add(9);set2.add(13);set2.add(3);
                if(Industry==10){
                    PIValue=val;
                }else if(set1.contains(Industry)){
                    SIValue+=val;
                }else if(set2.contains(Industry)){
                    TIValue+=val;
                }
            }
            Map map2=new HashMap();
            map2.put("PIValue" , PIValue);
            map2.put("SIValue" , SIValue);
            map2.put("TIValue" , TIValue);
            feature.setProperties(map2);
            PIValueFinal+=PIValue;
            SIValueFinal+=SIValue;
            TIValueFinal+=TIValue;
            list.add(feature);
        }
        Map mapFinal=new HashMap();
        mapFinal.put("PIValue" , PIValueFinal);
        mapFinal.put("SIValue" , SIValueFinal);
        mapFinal.put("TIValue" , TIValueFinal);
        geoJson.setProperties(mapFinal);
        geoJson.setFeatures(list);
        return JSON.toJSONString(geoJson);
    }
}
