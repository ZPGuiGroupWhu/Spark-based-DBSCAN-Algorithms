package org.gzp.team.geocommerceservice.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.gzp.team.geocommerceservice.service.NanocubeGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NanocubeGridController {
    @Autowired
    NanocubeGridService nanocubeGridService;
    @RequestMapping(value = "/nanocubeGrid",method = RequestMethod.GET)
    @CrossOrigin
    @ApiOperation(value = "获取nanocube格网数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hostname", value = "服务器IP+端口号，例如 http://192.168.80.128:29512", required = true),
            @ApiImplicitParam(name = "level", value = "尺度", required = true)
    })
    public String getNanocubeData(String hostname,int level){
        return nanocubeGridService.getNanocubeData(hostname, level);
    }

    @RequestMapping(value = "/nanocubeGridChongqing",method = RequestMethod.GET)
    @CrossOrigin
    @ApiOperation(value = "获取nanocube格网数据——重庆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hostname", value = "服务器IP+端口号，例如 http://192.168.80.132:51234", required = true),
            @ApiImplicitParam(name = "level", value = "尺度", required = true)
    })
    public String getNanocubeDataChongqing(String hostname,int level){
        return nanocubeGridService.getNanocubeDataChongqing(hostname, level);
    }
    @RequestMapping(value = "/nanocubeGridHubei",method = RequestMethod.GET)
    @CrossOrigin
    @ApiOperation(value = "获取nanocube格网数据——湖北")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hostname", value = "服务器IP+端口号，例如 http://192.168.80.132:51234", required = true),
            @ApiImplicitParam(name = "level", value = "尺度", required = true)
    })
    public String getNanocubeDataHubei(String hostname,int level){
        return nanocubeGridService.getNanocubeDataHubei(hostname, level);
    }
    @RequestMapping(value = "/nanocubeGridChina",method = RequestMethod.GET)
    @CrossOrigin
    @ApiOperation(value = "获取nanocube格网数据——中国")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hostname", value = "服务器IP+端口号，例如 http://192.168.80.132:51234", required = true),
            @ApiImplicitParam(name = "level", value = "尺度", required = true)
    })
    public String getNanocubeDataChina(String hostname,int level){
        return nanocubeGridService.getNanocubeDataChina(hostname, level);
    }
    @RequestMapping(value = "/nanocubeGridV4",method = RequestMethod.GET)
    @CrossOrigin
    @ApiOperation(value = "获取nanocube格网数据——V4")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hostname", value = "服务器IP+端口号，例如 http://192.168.80.132:51234", required = true),
            @ApiImplicitParam(name = "level", value = "尺度", required = true)
    })
    public String getNanocubeDataV4(String hostname,int level){
        return nanocubeGridService.getNanocubeDataV4(hostname, level);
    }
}
