package org.gzp.team.geocommerceservice.controller;

import io.swagger.annotations.ApiOperation;
import org.gzp.team.geocommerceservice.service.POIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class POIController {
    @Autowired
    POIService poiService;
    @RequestMapping(value = "/poiPoints",method = RequestMethod.GET)
    @CrossOrigin
    @ApiOperation(value = "获取聚类数据")
    public String getNanocubeData(){
        return poiService.getPoints();
    }
}
