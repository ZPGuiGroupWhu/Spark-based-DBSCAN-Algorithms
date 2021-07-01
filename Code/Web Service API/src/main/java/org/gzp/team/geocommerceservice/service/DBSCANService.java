package org.gzp.team.geocommerceservice.service;

import org.apache.hadoop.fs.FileStatus;
import org.gzp.team.geocommerceservice.model.ServiceResult;
import org.gzp.team.geocommerceservice.model.geojson.Feature2;
import org.gzp.team.geocommerceservice.model.geojson.Geometry;
import org.gzp.team.geocommerceservice.model.geojson.JSON;
import org.springframework.stereotype.Service;
import com.github.ywilkof.sparkrestclient.DriverState;
import com.github.ywilkof.sparkrestclient.FailedSparkRequestException;
import com.github.ywilkof.sparkrestclient.SparkRestClient;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
@Service
public class DBSCANService {
    private AtomicBoolean used = new AtomicBoolean();
    public String grkdDBSCANResult(String master,
                                   String eps,
                                   String minpts,
                                   String inPath,
                                   String outPath,
                                   String numPartition,
                                   String sampleRate,
                                   String executorCores,
                                   String coresMax,
                                   String executorMemory,
                                   String masterHost,
                                   String sparkVersion,
                                   String appResource){
        synchronized (used) {
            if (used.get()) {
                return "the spark cluster are in use,please waiting";
            }
            used.set(true);
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
        String cur = df.format(new Date());
        SparkRestClient sparkRestClient = SparkRestClient.builder()
                .masterHost(masterHost)
                .sparkVersion(sparkVersion)
                .build();
        try {
            outPath=outPath+cur;
            executorMemory=executorMemory+'g';
            String[] schemas = {
                    master,
                    eps,
                    minpts,
                    inPath,
                    outPath,
                    numPartition,
                    sampleRate,
                    executorCores,
                    coresMax,
                    executorMemory};
            List<String> arg = Arrays.asList(schemas);
            final String submissionId = sparkRestClient.prepareJobSubmit()
                    .appName("GRKD_DBSCAN_PARALLEL")
                    .appResource(appResource)
                    .mainClass("org.zzy.dbscan.scala.algorithms.GRID.GRKD_DBSCAN_PARALLEL")
                    .appArgs(arg)
                    .withProperties()
                    .submit();
            System.out.println(submissionId);
            while (true) {
                final DriverState driverState = sparkRestClient
                        .checkJobStatus()
                        .withSubmissionId(submissionId);
                System.out.println(driverState);
                Thread.sleep(10000);
                if (driverState.name().equals(DriverState.RUNNING.name())) {
                    continue;
                } else {
                    break;
                }
            }
            Thread.sleep(10000);
            return getServiceResult(outPath);
        } catch (FailedSparkRequestException e) {
            e.printStackTrace();
            return "error";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "error";
        } finally {
            synchronized (used) {
                used.set(false);
            }
        }
    }


    public String kDBSCANResult(String master,
                                   String eps,
                                   String minpts,
                                   String inPath,
                                   String outPath,
                                   String numPartition,
                                   String sampleRate,
                                   String executorCores,
                                   String coresMax,
                                   String executorMemory,
                                   String masterHost,
                                   String sparkVersion,
                                   String appResource){
        synchronized (used) {
            if (used.get()) {
                return "the spark cluster are in use,please waiting";
            }
            used.set(true);
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
        String cur = df.format(new Date());
        SparkRestClient sparkRestClient = SparkRestClient.builder()
                .masterHost(masterHost)
                .sparkVersion(sparkVersion)
                .build();
        try {
            outPath=outPath+cur;
            executorMemory=executorMemory+'g';
            String[] schemas = {
                    master,
                    eps,
                    minpts,
                    inPath,
                    outPath,
                    numPartition,
                    sampleRate,
                    executorCores,
                    coresMax,
                    executorMemory};
            List<String> arg = Arrays.asList(schemas);
            final String submissionId = sparkRestClient.prepareJobSubmit()
                    .appName("KDBSCAN_PARALLEL")
                    .appResource(appResource)
                    .mainClass("org.zzy.dbscan.scala.algorithms.KDSG.KDBSCAN_PARALLEL")
                    .appArgs(arg)
                    .withProperties()
                    .submit();
            System.out.println(submissionId);
            while (true) {
                final DriverState driverState = sparkRestClient
                        .checkJobStatus()
                        .withSubmissionId(submissionId);
                System.out.println(driverState);
                Thread.sleep(10000);
                if (driverState.name().equals(DriverState.RUNNING.name())) {
                    continue;
                } else {
                    break;
                }
            }
            Thread.sleep(10000);
            return getServiceResult(outPath);
        } catch (FailedSparkRequestException e) {
            e.printStackTrace();
            return "error";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "error";
        } finally {
            synchronized (used) {
                used.set(false);
            }
        }
    }


    public String kdDBSCANResult(String master,
                                   String eps,
                                   String minpts,
                                   String inPath,
                                   String outPath,
                                   String numPartition,
                                   String sampleRate,
                                   String executorCores,
                                   String coresMax,
                                   String executorMemory,
                                   String masterHost,
                                   String sparkVersion,
                                   String appResource){
        synchronized (used) {
            if (used.get()) {
                return "the spark cluster are in use,please waiting";
            }
            used.set(true);
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
        String cur = df.format(new Date());
        SparkRestClient sparkRestClient = SparkRestClient.builder()
                .masterHost(masterHost)
                .sparkVersion(sparkVersion)
                .build();
        try {
            outPath=outPath+cur;
            executorMemory=executorMemory+'g';
            String[] schemas = {
                    master,
                    eps,
                    minpts,
                    inPath,
                    outPath,
                    numPartition,
                    sampleRate,
                    executorCores,
                    coresMax,
                    executorMemory};
            List<String> arg = Arrays.asList(schemas);
            final String submissionId = sparkRestClient.prepareJobSubmit()
                    .appName("KD_DBSCAN_PARALLEL")
                    .appResource(appResource)
                    .mainClass("org.zzy.dbscan.scala.algorithms.KDTree_DBSCAN.KD_DBSCAN_PARALLEL")
                    .appArgs(arg)
                    .withProperties()
                    .submit();
            System.out.println(submissionId);
            while (true) {
                final DriverState driverState = sparkRestClient
                        .checkJobStatus()
                        .withSubmissionId(submissionId);
                System.out.println(driverState);
                Thread.sleep(10000);
                if (driverState.name().equals(DriverState.RUNNING.name())) {
                    continue;
                } else {
                    break;
                }
            }
            Thread.sleep(10000);
            return getServiceResult(outPath);
        } catch (FailedSparkRequestException e) {
            e.printStackTrace();
            return "error";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "error";
        } finally {
            synchronized (used) {
                used.set(false);
            }
        }
    }


    public String tlkdDBSCANResult(String master,
                                   String eps,
                                   String minpts,
                                   String inPath,
                                   String outPath,
                                   String numPartition,
                                   String sampleRate,
                                   String executorCores,
                                   String coresMax,
                                   String executorMemory,
                                   String masterHost,
                                   String sparkVersion,
                                   String appResource){
        synchronized (used) {
            if (used.get()) {
                return "the spark cluster are in use,please waiting";
            }
            used.set(true);
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
        String cur = df.format(new Date());
        SparkRestClient sparkRestClient = SparkRestClient.builder()
                .masterHost(masterHost)
                .sparkVersion(sparkVersion)
                .build();
        try {
            outPath=outPath+cur;
            executorMemory=executorMemory+'g';
            String[] schemas = {
                    master,
                    eps,
                    minpts,
                    inPath,
                    outPath,
                    numPartition,
                    sampleRate,
                    executorCores,
                    coresMax,
                    executorMemory};
            List<String> arg = Arrays.asList(schemas);
            final String submissionId = sparkRestClient.prepareJobSubmit()
                    .appName("TLKD_DBSCAN_PARALLEL")
                    .appResource(appResource)
                    .mainClass("org.zzy.dbscan.scala.algorithms.TLKD_DBSCAN.TLKD_DBSCAN_PARALLEL")
                    .appArgs(arg)
                    .withProperties()
                    .submit();
            System.out.println(submissionId);
            while (true) {
                final DriverState driverState = sparkRestClient
                        .checkJobStatus()
                        .withSubmissionId(submissionId);
                System.out.println(driverState);
                Thread.sleep(10000);
                if (driverState.name().equals(DriverState.RUNNING.name())) {
                    continue;
                } else {
                    break;
                }
            }
            Thread.sleep(10000);
            return getServiceResult(outPath);
        } catch (FailedSparkRequestException e) {
            e.printStackTrace();
            return "error";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "error";
        } finally {
            synchronized (used) {
                used.set(false);
            }
        }
    }

    public String getServiceResult(String path) {
        String defaultFs = "hdfs://"+path.split("/")[2];
        String remoteFilePath = path+"ForApis";

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", defaultFs);
        try {
            FileSystem fs = FileSystem.get(conf);
            Path remotePath = new Path(remoteFilePath);
            FileStatus[] fileStatuses=fs.listStatus(remotePath);
            List<String> stringList=new ArrayList<>();
            for(FileStatus fileStatus:fileStatuses){
                String fullpath=fileStatus.getPath().toString();
                if(fullpath.split("/")[6].startsWith("part-"))
                    stringList.add(fullpath);
            }
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime=dateFormat.format(new Date());
            ServiceResult serviceResult=new ServiceResult(stringList,dateTime);
            fs.close();
            return com.alibaba.fastjson.JSON.toJSONString(serviceResult);
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public String getResult(String path) {
        String defaultFs = "hdfs://"+path.split("/")[2];
        String remoteFilePath = path;

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", defaultFs);
        try {
            FileSystem fs = FileSystem.get(conf);
            Path remotePath = new Path(remoteFilePath);
            FSDataInputStream in = fs.open(remotePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            List<String> strings=new ArrayList<>();
            while ((line = br.readLine()) != null) {
               strings.add(line);
            }
            JSON geoJson=new JSON();
            geoJson.setType("FeatureCollection");
            List<Feature2>list=new ArrayList<>();
            for(String s:strings){
                String[] columns=s.split(",");
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
                feature.setProperties(map);
                list.add(feature);
            }
            geoJson.setFeatures(list);
            br.close();
            in.close();
            fs.close();
            return com.alibaba.fastjson.JSON.toJSONString(geoJson);
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

}
