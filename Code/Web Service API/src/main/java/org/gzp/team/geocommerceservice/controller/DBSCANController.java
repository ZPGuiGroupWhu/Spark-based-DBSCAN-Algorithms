package org.gzp.team.geocommerceservice.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.gzp.team.geocommerceservice.service.DBSCANService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DBSCANController {
    @Autowired
    DBSCANService dbscanService;
    @RequestMapping(value = "/GRKD_DBSCAN",method = RequestMethod.GET)
    @CrossOrigin
    @ApiOperation(value = "获取GRKD_DBSCAN算法计算结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "master", value = "master节点，例如：spark://192.168.200.148:7077", required = true),
            @ApiImplicitParam(name = "eps", value = "距离阈值，例如：0.03", required = true),
            @ApiImplicitParam(name = "minpts", value = "密度阈值，例如：100", required = true),
            @ApiImplicitParam(name = "inPath", value = "输入数据地址，例如：hdfs://192.168.200.148:9000/spark/in/experiment/POI.csv", required = true),
            @ApiImplicitParam(name = "outPath", value = "输出数据地址，例如：hdfs://192.168.200.148:9000/spark/out/POI_GRKD_DBSCAN", required = true),
            @ApiImplicitParam(name = "numPartition", value = "分区数，例如：8,16,32,64,128", required = true),
            @ApiImplicitParam(name = "sampleRate", value = "数据采样率，例如：0.01", required = true),
            @ApiImplicitParam(name = "executorCores", value = "单节点核数,例如：8", required = true),
            @ApiImplicitParam(name = "coresMax", value = "集群总核数，例如：64", required = true),
            @ApiImplicitParam(name = "executorMemory", value = "单节点内存，例如：10g", required = true),
            @ApiImplicitParam(name = "defaultDFS", value = "HDFS地址，例如：hdfs://192.168.200.148:9000", required = true),
            @ApiImplicitParam(name = "masterHost", value = "主节点IP，例如：192.168.200.148", required = true),
            @ApiImplicitParam(name = "appResource", value = "Jar包地址，例如：hdfs://192.168.200.148:9000/spark/in/DBSCANAlgorithms-1.0-SNAPSHOT.jar", required = true)

    })
    public String getGRKDDBSCANData(String master,
                                  String eps,
                                  String minpts,
                                  String inPath,
                                  String outPath,
                                  String numPartition,
                                  String sampleRate,
                                  String executorCores,
                                  String coresMax,
                                  String executorMemory,
                                  String defaultDFS,
                                  String masterHost,
                                  String sparkVersion,
                                  String appResource
                            ){
        return dbscanService.grkdDBSCANResult(  master,
                                                eps,
                                                minpts,
                                                inPath,
                                                outPath,
                                                numPartition,
                                                sampleRate,
                                                executorCores,
                                                coresMax,
                                                executorMemory,
                                                defaultDFS,
                                                masterHost,
                                                sparkVersion,
                                                appResource);
    }


    @RequestMapping(value = "/KDBSCAN",method = RequestMethod.GET)
    @CrossOrigin
    @ApiOperation(value = "获取KDBSCAN算法计算结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "master", value = "master节点，例如：spark://192.168.200.148:7077", required = true),
            @ApiImplicitParam(name = "eps", value = "距离阈值，例如：0.03", required = true),
            @ApiImplicitParam(name = "minpts", value = "密度阈值，例如：100", required = true),
            @ApiImplicitParam(name = "inPath", value = "输入数据地址，例如：hdfs://192.168.200.148:9000/spark/in/experiment/POI.csv", required = true),
            @ApiImplicitParam(name = "outPath", value = "输出数据地址，例如：hdfs://192.168.200.148:9000/spark/out/POI_GRKD_DBSCAN", required = true),
            @ApiImplicitParam(name = "numPartition", value = "分区数，例如：8,16,32,64,128", required = true),
            @ApiImplicitParam(name = "sampleRate", value = "数据采样率，例如：0.01", required = true),
            @ApiImplicitParam(name = "executorCores", value = "单节点核数,例如：8", required = true),
            @ApiImplicitParam(name = "coresMax", value = "集群总核数，例如：64", required = true),
            @ApiImplicitParam(name = "executorMemory", value = "单节点内存，例如：10g", required = true),
            @ApiImplicitParam(name = "defaultDFS", value = "HDFS地址，例如：hdfs://192.168.200.148:9000", required = true),
            @ApiImplicitParam(name = "masterHost", value = "主节点IP，例如：192.168.200.148", required = true),
            @ApiImplicitParam(name = "appResource", value = "Jar包地址，例如：hdfs://192.168.200.148:9000/spark/in/DBSCANAlgorithms-1.0-SNAPSHOT.jar", required = true)

    })
    public String getKDBSCANData(String master,
                                    String eps,
                                    String minpts,
                                    String inPath,
                                    String outPath,
                                    String numPartition,
                                    String sampleRate,
                                    String executorCores,
                                    String coresMax,
                                    String executorMemory,
                                    String defaultDFS,
                                    String masterHost,
                                    String sparkVersion,
                                    String appResource
    ){
        return dbscanService.kDBSCANResult(  master,
                eps,
                minpts,
                inPath,
                outPath,
                numPartition,
                sampleRate,
                executorCores,
                coresMax,
                executorMemory,
                defaultDFS,
                masterHost,
                sparkVersion,
                appResource);
    }


    @RequestMapping(value = "/KD_DBSCAN",method = RequestMethod.GET)
    @CrossOrigin
    @ApiOperation(value = "获取KD_DBSCAN算法计算结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "master", value = "master节点，例如：spark://192.168.200.148:7077", required = true),
            @ApiImplicitParam(name = "eps", value = "距离阈值，例如：0.03", required = true),
            @ApiImplicitParam(name = "minpts", value = "密度阈值，例如：100", required = true),
            @ApiImplicitParam(name = "inPath", value = "输入数据地址，例如：hdfs://192.168.200.148:9000/spark/in/experiment/POI.csv", required = true),
            @ApiImplicitParam(name = "outPath", value = "输出数据地址，例如：hdfs://192.168.200.148:9000/spark/out/POI_GRKD_DBSCAN", required = true),
            @ApiImplicitParam(name = "numPartition", value = "分区数，例如：8,16,32,64,128", required = true),
            @ApiImplicitParam(name = "sampleRate", value = "数据采样率，例如：0.01", required = true),
            @ApiImplicitParam(name = "executorCores", value = "单节点核数,例如：8", required = true),
            @ApiImplicitParam(name = "coresMax", value = "集群总核数，例如：64", required = true),
            @ApiImplicitParam(name = "executorMemory", value = "单节点内存，例如：10g", required = true),
            @ApiImplicitParam(name = "defaultDFS", value = "HDFS地址，例如：hdfs://192.168.200.148:9000", required = true),
            @ApiImplicitParam(name = "masterHost", value = "主节点IP，例如：192.168.200.148", required = true),
            @ApiImplicitParam(name = "appResource", value = "Jar包地址，例如：hdfs://192.168.200.148:9000/spark/in/DBSCANAlgorithms-1.0-SNAPSHOT.jar", required = true)

    })
    public String getKDDBSCANData(String master,
                                    String eps,
                                    String minpts,
                                    String inPath,
                                    String outPath,
                                    String numPartition,
                                    String sampleRate,
                                    String executorCores,
                                    String coresMax,
                                    String executorMemory,
                                    String defaultDFS,
                                    String masterHost,
                                    String sparkVersion,
                                    String appResource
    ){
        return dbscanService.kdDBSCANResult(  master,
                eps,
                minpts,
                inPath,
                outPath,
                numPartition,
                sampleRate,
                executorCores,
                coresMax,
                executorMemory,
                defaultDFS,
                masterHost,
                sparkVersion,
                appResource);
    }

    @RequestMapping(value = "/TLKD_DBSCAN",method = RequestMethod.GET)
    @CrossOrigin
    @ApiOperation(value = "获取TLKD_DBSCAN算法计算结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "master", value = "master节点，例如：spark://192.168.200.148:7077", required = true),
            @ApiImplicitParam(name = "eps", value = "距离阈值，例如：0.03", required = true),
            @ApiImplicitParam(name = "minpts", value = "密度阈值，例如：100", required = true),
            @ApiImplicitParam(name = "inPath", value = "输入数据地址，例如：hdfs://192.168.200.148:9000/spark/in/experiment/POI.csv", required = true),
            @ApiImplicitParam(name = "outPath", value = "输出数据地址，例如：hdfs://192.168.200.148:9000/spark/out/POI_GRKD_DBSCAN", required = true),
            @ApiImplicitParam(name = "numPartition", value = "分区数，例如：8,16,32,64,128", required = true),
            @ApiImplicitParam(name = "sampleRate", value = "数据采样率，例如：0.01", required = true),
            @ApiImplicitParam(name = "executorCores", value = "单节点核数,例如：8", required = true),
            @ApiImplicitParam(name = "coresMax", value = "集群总核数，例如：64", required = true),
            @ApiImplicitParam(name = "executorMemory", value = "单节点内存，例如：10g", required = true),
            @ApiImplicitParam(name = "defaultDFS", value = "HDFS地址，例如：hdfs://192.168.200.148:9000", required = true),
            @ApiImplicitParam(name = "masterHost", value = "主节点IP，例如：192.168.200.148", required = true),
            @ApiImplicitParam(name = "appResource", value = "Jar包地址，例如：hdfs://192.168.200.148:9000/spark/in/DBSCANAlgorithms-1.0-SNAPSHOT.jar", required = true)

    })
    public String getTLKDDBSCANData(String master,
                                    String eps,
                                    String minpts,
                                    String inPath,
                                    String outPath,
                                    String numPartition,
                                    String sampleRate,
                                    String executorCores,
                                    String coresMax,
                                    String executorMemory,
                                    String defaultDFS,
                                    String masterHost,
                                    String sparkVersion,
                                    String appResource
    ){
        return dbscanService.tlkdDBSCANResult(  master,
                eps,
                minpts,
                inPath,
                outPath,
                numPartition,
                sampleRate,
                executorCores,
                coresMax,
                executorMemory,
                defaultDFS,
                masterHost,
                sparkVersion,
                appResource);
    }







    @RequestMapping(value = "/getResult",method = RequestMethod.GET)
    @CrossOrigin
    @ApiOperation(value = "获取算法计算结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "outPath", value = "输出数据地址，例如：hdfs://192.168.200.148:9000/spark/out/POI_GRKD_DBSCAN", required = true),
            @ApiImplicitParam(name = "defaultDFS", value = "HDFS地址，例如：hdfs://192.168.200.148:9000", required = true),
            @ApiImplicitParam(name = "pageNum", value = "获取数据的页码，例如：0表示第一页数据", required = true)

    })
    public String getNanocubeData(
                                  String outPath,
                                  String defaultDFS,
                                  int pageNum
    ){
        return dbscanService.getResult(outPath,defaultDFS,pageNum);
    }
}
