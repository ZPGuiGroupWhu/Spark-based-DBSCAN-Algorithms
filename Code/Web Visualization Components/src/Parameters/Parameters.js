import React from 'react';
import './Parameters.css';
import { Select,Button } from 'antd';
import { InputNumber } from 'antd';
import intl from 'react-intl-universal';
import { getURLWithParam } from '../common/tool';
import moment from 'moment';

export default class Parameters extends React.Component {
    constructor(props){
        super(props);
        this.state={
            algorithm:"GRKD",
            distanceThreshold:0.01,
            densityThreshold:1000,
            partitionNum:128,
            samplingRate:0.01,
            executorCores:8,
            maxCores:64,
            executorMemory:10
        }
    }

    changeParams = (value, key) => {
        this.setState({ [key]: value });
    }

    submitTask = ()=>{
        const taskParam={
            eps:this.state.distanceThreshold,
            minpts:this.state.densityThreshold,
            numPartition:this.state.partitionNum,
            sampleRate:this.state.samplingRate,
            coresMax:this.state.maxCores,
            executorCores:this.state.executorCores,
            executorMemory:this.state.executorMemory,
            master:this.props.taskApi.master,
            masterHost:this.props.taskApi.masterHost,
            sparkVersion:this.props.taskApi.sparkVersion,
            appResource:"hdfs://192.168.200.148:9000/spark/in/DBSCANAlgorithms-1.0-SNAPSHOT.jar",
            inPath:"hdfs://192.168.200.148:9000/spark/in/experiment/POI.csv",
            outPath:"hdfs://192.168.200.148:9000/spark/out/"+moment(moment().toDate()).format('YYYYMMDD_HH_mm_ss'),
        }
        const taskApiUrl = this.props.taskApi.taskApiUrl;
        const taskApiUrlParam = getURLWithParam(taskApiUrl, taskParam);
        console.log(taskApiUrlParam);
        fetch(taskApiUrlParam)
        .then((response) => response.json())
        .then((responseJson) => {
            console.log(responseJson);
            this.props.handleClusterResultChange(
                responseJson.data,
                responseJson.toTalCount,
            )
        })
        .catch((error) => {
            console.log(error);
        });
    }
    render(){
        const { Option } = Select;
        return (
            <div>
                {/* 算法参数 */}
                <h3 style={{"fontSize":"16pt","marginTop":"5pt"}}>{intl.get('Algorithm_Parameters')}</h3>
                <div>
                    <div className="Parameter">
                    {/* 聚类算法 */}
                        <div className="paramter-title">{intl.get('Clustering_Algorithm')}</div>
                        <Select defaultValue="GRKD" className="Parameter-Select"  onChange={value => { this.changeParams(value, 'algorithm') }}>
                            <Option value="DBSCAN">DBSCAN</Option>
                            <Option value="GRKD">GRKD-DBSCAN</Option>
                            <Option value="KDBSCAN" >KDBSCAN</Option>
                            <Option value="KD" >KD_DBSCAN</Option>
                            <Option value="TLKD" >TLKD_DBSCAN</Option>
                            <Option value="RTree" >RTree_DBSCAN</Option>
                        </Select>
                    </div>
                    <div className="Parameter">
                    {/* 距离阈值 */}
                        <div className="paramter-title">{intl.get('Distance_Threshold')}</div>
                        <InputNumber min={0} max={100000} className="Parameter-Select" defaultValue={this.state.distanceThreshold} step={0.01} onChange={value => { this.changeParams(value, 'distanceThreshold') }}></InputNumber>
                    </div>
                    <div className="Parameter">
                    {/* 密度阈值 */}
                        <div className="paramter-title">{intl.get('Density_Threshold')}</div>
                        <InputNumber min={0} max={100000} className="Parameter-Select" defaultValue={this.state.densityThreshold} step={1} onChange={value => { this.changeParams(value, 'densityThreshold') }}></InputNumber>
                    </div>
                </div>

                {/* 集群参数 */}
                <h3 style={{"fontSize":"16pt","marginTop":"10pt"}}>{intl.get('Cluster_Parameters')}</h3>
                <div>
                    <div className="Parameter">
                    {/* 分区数 */}
                        <div className="paramter-title">{intl.get('Partitions_Num')}</div>
                        <InputNumber min={0} max={100000} className="Parameter-Select" defaultValue={this.state.partitionNum} step={1} onChange={value => { this.changeParams(value, 'partitionNum') }}></InputNumber>
                    </div>
                    <div className="Parameter">
                    {/* 数据采样率 */}
                        <div className="paramter-title">{intl.get('Sampling_Rate')}</div>
                        <InputNumber min={0} max={100000} className="Parameter-Select" defaultValue={this.state.samplingRate} step={0.01} onChange={value => { this.changeParams(value, 'samplingRate') }}></InputNumber>
                    </div>
                    <div className="Parameter">
                    {/* executor核数 */}
                        <div className="paramter-title">{intl.get('Executor_Cores')}</div>
                        <InputNumber min={0} max={100000} className="Parameter-Select" defaultValue={this.state.executorCores} step={1} onChange={value => { this.changeParams(value, 'executorCores') }}></InputNumber>
                    </div>
                    <div className="Parameter">
                    {/* 最大核数 */}
                        <div className="paramter-title">{intl.get('MAX_Cores')}</div>
                        <InputNumber min={0} max={100000} className="Parameter-Select" defaultValue={this.state.maxCores} step={1} onChange={value => { this.changeParams(value, 'maxCores') }}></InputNumber>
                    </div>
                    <div className="Parameter">
                    {/* executor内存 */}
                        <div className="paramter-title">{intl.get('Executor_Memory')}</div>
                        <InputNumber min={0} max={100000} className="Parameter-Select" defaultValue={this.state.executorMemory} step={1} onChange={value => { this.changeParams(value, 'executorMemory') }}></InputNumber>
                    </div>
                </div>
                <Button style={{marginTop:"10px"}} onClick={()=>this.submitTask()}>提交</Button>
            </div>
        )
    }
}