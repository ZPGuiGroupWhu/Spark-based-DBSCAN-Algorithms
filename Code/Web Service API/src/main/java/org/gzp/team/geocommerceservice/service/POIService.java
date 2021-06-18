package org.gzp.team.geocommerceservice.service;

import org.gzp.team.geocommerceservice.model.geojson.Feature2;
import org.gzp.team.geocommerceservice.model.geojson.Geometry;
import org.gzp.team.geocommerceservice.model.geojson.JSON;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class POIService {
    public String getPoints(){
        JSON geoJson=new JSON();
        String fullFilename = "D:/C盘文件/桌面/xxx2.json";
        try {
            File csv=new File("D:\\C盘文件\\桌面/0.05  100.csv");
            BufferedReader br=new BufferedReader(new FileReader(csv));
            String line;
            geoJson.setType("FeatureCollection");
            List<Feature2>list=new ArrayList<>();
            while ((line = br.readLine()) != null){
                String[] columns=line.split(",");
                Feature2 feature=new Feature2();
                feature.setType("Feature");
                Geometry geometry=new Geometry();
                geometry.setType("Point");
                List<Double> list1=new ArrayList();
                list1.add(Double.parseDouble(columns[0]));
                list1.add(Double.parseDouble(columns[1]));
                geometry.setCoordinates(list1);
                feature.setGeometry(geometry);
                Map map=new HashMap();
                map.put("color",columns[2]);
//                if(Integer.parseInt(columns[2])%6==0){
//                    map.put("color","#00939C");
//                }else
//                if(Integer.parseInt(columns[2])%6==1){
//                    map.put("color","#5DBABF");
//                }else
//                if(Integer.parseInt(columns[2])%6==2){
//                    map.put("color","#BAE1E2");
//                }else
//                if(Integer.parseInt(columns[2])%6==3){
//                    map.put("color","#F8C0AA");
//                }else
//                if(Integer.parseInt(columns[2])%6==4){
//                    map.put("color","#DD7755");
//                }else
//                if(Integer.parseInt(columns[2])%6==5){
//                    map.put("color","#C22E00");
//                }
                feature.setProperties(map);
                list.add(feature);
            }
            geoJson.setFeatures(list);
            File newTextFile = new File(fullFilename);
            FileWriter fw;
            fw = new FileWriter(newTextFile);
            fw.write(com.alibaba.fastjson.JSON.toJSONString(geoJson));
            fw.close();
        }catch (Exception e){
            System.out.println(e);
        }

        return "chenggong";
    }
}
