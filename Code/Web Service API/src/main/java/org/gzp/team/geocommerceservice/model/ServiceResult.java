package org.gzp.team.geocommerceservice.model;

import com.github.pagehelper.PageInfo;
import org.gzp.team.geocommerceservice.model.enums.ServiceErrorCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ServiceResult implements Serializable {

    /**
     * 服务返回码，服务正常返回为0
     */
    private Integer code = 0;

    /**
     * 服务返回消息
     */
    private String message = "";

    /**
     * 返回数据的时间
     */
    private String DateTime="";

    /**
     * 服务返回数据内容的记录总数
     */
    private Long ToTalCount;


    private int pages;

    /**
     * 当前服务返回数据的元数据信息
     */
    private List<?> metadata;

    /**
     * 服务返回数据内容
     */
    private List<?> data;



    public ServiceResult(){ }


    public ServiceResult(List<?> data,String dateTime) {
        this.DateTime=dateTime;
        this.data = data;
        if (data != null) {
            ToTalCount = (long) data.size();
        }
    }
    public ServiceResult(long toTalCount,String dateTime,int pages) {
        this.ToTalCount=toTalCount;
        this.DateTime=dateTime;
        this.pages=pages;
    }


    public ServiceResult(List<?> metadata, List<?> data,String dateTime) {
        this.DateTime=dateTime;
        this.metadata = metadata;
        this.data = data;
        if (data != null) {
            ToTalCount = (long) data.size();
        }
    }

    public ServiceResult(String message, List<?> data) {
        this.message = message;
        this.data = data;
    }


    public ServiceResult(ServiceErrorCode errorCode) {
        code = errorCode.getCode();
        message = errorCode.getMessage();
    }

    public ServiceResult(ServiceErrorCode errorCode, String errorDetails) {
        code = errorCode.getCode();
        message = errorCode.getMessage() + "：" + errorDetails;
    }

    public ServiceResult(ServiceErrorCode errorCode, String errorDetails, List<?> data) {
        code = errorCode.getCode();
        message = errorCode.getMessage() + "：" + errorDetails;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<?> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<?> metadata) {
        this.metadata = metadata;
    }

    public List<?> getData() {
        return data;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public Long getToTalCount() {
        return ToTalCount;
    }

    public void setToTalCount(Long toTalCount) {
        ToTalCount = toTalCount;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
