import React from 'react';
import intl from 'react-intl-universal';
import './App.css';
import Map from './Map/Map';
import ModuleContainer from './ModuleContainer/ModuleContainer';
import Parameters from './Parameters/Parameters';
import StyleSettings from './StyleSettings/StyleSettings';
import DataSource from './DataSource/DataSource';
import china from './common/china.json';

const locales = {
    "en": require('./locales/en-US.json'),
    "zh": require('./locales/zh-CN.json'),
};

//APP.js 项目内的父组件，负责子组件间的数据交互
export default class App extends React.Component {
    constructor(props){
        super(props);
        this.state={
            //语言切换
            initDone: false,
            langButton: localStorage.defaultLng === 'zh' ? 'English' : '中文',
            //地图底图及相机参数
            layerId:"dark-v10",
            viewState:{
                longitude: 105,
                latitude: 30,
                zoom: 3,
                pitch: 0,
                bearing: 0
            },
            //边界参数
            boundaryData:china,
            boundaryColor:{
                r: 255,
                g: 215,
                b: 0,
                a: 1,
            },
            //数据源
            dataSources:['全国企业POI','湖北企业POI','重庆企业POI','...'],
            //色带
            gradientNum:'4',
            //聚类结果链接及页数
            resultApi:"http://192.168.200.136:8011/getResult",
            outPath:[],
            clusterDataPages:0,
            //提交任务API及部分参数
            taskApi:{
                taskApiUrl:"http://192.168.200.136:8011/GRKD_DBSCAN",
                master:"spark://192.168.200.148:7077",
                masterHost:"192.168.200.148",
                sparkVersion:"2.3.3",
            }
        }
    }
    componentDidMount() {
        let lang = (navigator.language || navigator.browserLanguage).toLowerCase();
        if (lang.indexOf('zh') >= 0) {
          // 假如浏览器语言是中文
          localStorage.setItem("defaultLng", "zh")
        } else {
          // 假如浏览器语言是其它语言
          localStorage.setItem("defaultLng", "en")
        }
        this.loadLocales();
    }
    loadLocales() {
        intl.init({
          currentLocale: localStorage.getItem('locale') || localStorage.getItem("defaultLng") || 'zh',
          locales,
        })
          .then(() => {
            this.setState({ initDone: true });
          });
    }
    //更改语言
    changeLanguage(){
        let lang = intl.options.currentLocale;
        if (lang === 'en') {
          this.setState({ langButton: 'English' })
          intl.options.currentLocale = 'zh'
        } else {
          this.setState({ langButton: '中文' })
          intl.options.currentLocale = 'en'
        }
    }
    // 更改边界颜色
    handleColorChange(color){
        this.setState({ boundaryColor: color.rgb })
    };
    //更改底图
    handleLayerChange(value){
        this.setState({ layerId:value})
    };
    //更改聚类结果色带
    handleGradientChange(value){
        this.setState({gradientNum:value})
    }
    //更改聚类结果URL
    handleClusterResultChange(outPath,clusterDataPages){
        this.setState({
            outPath:outPath,
            clusterDataPages:clusterDataPages,
        })
    }
    render(){
        return (
            this.state.initDone && <div>
                <div>
                    {/* 地图组件，提供底图和边界可视化、原始数据可视化功能 
                        viewState : 相机参数
                        layerId : 底图id
                        boundaryColor : 边界颜色
                        boundaryData : 边界数据
                        gradientNum ： 类簇结果色带ID
                        clusterDataPages : 聚类结果页数
                    */}
                    <Map viewState={this.state.viewState} layerId={this.state.layerId} boundaryColor={this.state.boundaryColor} boundaryData={this.state.boundaryData} gradientNum={this.state.gradientNum} clusterDataPages={this.state.clusterDataPages} resultApi={this.state.resultApi} outPath={this.state.outPath}/>
                </div>

                <div className="left-moudles">{/* 左侧模块 */}
                    {/* 语言切换按钮 */}
                    <div className="language-change" onClick={()=>this.changeLanguage()}>{this.state.langButton}</div>
                    {/* 算法参数设置模块，包含算法类型、距离阈值与密度阈值三个参数
                        taskApi : 任务提交API的部分参数
                        handleClusterResultChange : 当聚类完成后返回聚类结果URL时被调用
                    */}
                    <ModuleContainer title={intl.get('PARAMETERS')} autowidth="true">
                        <Parameters taskApi={this.state.taskApi} handleClusterResultChange={(outPath,clusterDataPages)=>this.handleClusterResultChange(outPath,clusterDataPages)}/>
                    </ModuleContainer>
                </div>

                <div className="right-moudles">{/* 右侧模块 */}
                    {/* 数据源模块，包括数据源和边界文件的选择和上传 */}
                    <ModuleContainer title={intl.get('DATA_SOURCE')} autowidth="true" right="true">
                        <DataSource dataSources = {this.state.dataSources}/>
                    </ModuleContainer>
                    {/* 样式设置模块，包含边界颜色、类簇颜色和底图选择三个功能
                        boundaryColor : 边界颜色
                        handleColorChange : 当颜色发生变化时被调用的函数
                        layerId : 底图id
                        handleLayerChange ： 当底图id发生变化时被调用的函数
                        handleGradientChange ： 当聚类结果色带更换时被调用的函数
                    */}
                    <ModuleContainer title={intl.get('StyleSettings')} autowidth="true" right="true">
                        <StyleSettings boundaryColor={this.state.boundaryColor} handleColorChange={(color)=>this.handleColorChange(color)} layerId={this.state.layerId} handleLayerChange={(value)=>this.handleLayerChange(value)} handleGradientChange={(value)=>this.handleGradientChange(value)} />
                    </ModuleContainer>
                </div>
            </div>
        )
    }
}