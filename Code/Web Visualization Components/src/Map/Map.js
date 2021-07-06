import React from 'react';
import './Map.css';
import DeckGL from '@deck.gl/react';
import {GeoJsonLayer} from '@deck.gl/layers';
import {StaticMap} from 'react-map-gl';
import Gradient from '../common/Gradient'
import { getURLWithParam } from '../common/tool';
//build时出现底图无法正常加载时，在node_modules\react-scripts\config\webpack.config.js内的babel-loader里添加ignore: [ './node_modules/mapbox-gl/dist/mapbox-gl.js' ]
//具体原因见https://docs.mapbox.com/mapbox-gl-js/api/#transpiling-v2

const Gradients=[
    new Gradient(['red','blue','violet']),
    new Gradient(['yellow','green']),
    new Gradient(['orange', 'yellow']),
    new Gradient(['red', 'orange', 'yellow', 'green', 'blue', 'indigo', 'violet'])
]
const MAPBOX_ACCESS_TOKEN = 'pk.eyJ1Ijoienp5ZGh0IiwiYSI6ImNrb2xmaHVpaDA0bjUyb2w2YW9td2cxZHAifQ.-Dd65sHDeDDmTD3XVpvvaw';//MAPBOX密钥

//异步请求函数
async function getChunk(resultApi,chunkPath){
    const commitParam = {
        outPath: chunkPath,
    };
    const url = resultApi;
    const urlParam = getURLWithParam(url, commitParam);
    let chunk={}
    await fetch(urlParam)
        .then((response) => response.json())
        .then((responseJson) => {
            chunk = responseJson.features;
        })
        .catch((error) => {
            console.log(error);
        });
    return chunk;
}
//TODO 目前是等待上一请求完成并yield后，才会开始下一请求，是否可以优化为多线程请求以减短加载时间？
async function* getData(pages,resultApi,outPath) {
    for (let i = 0; i < pages; i++) {
        console.log(outPath[i])
        yield (getChunk(resultApi,outPath[i]))
    }
}
export default class Map extends React.Component {
    constructor(props){
        super(props);
        this.state={
            clusterData:getData(this.props.clusterDataPages,this.props.resultApi,this.props.outPath)
        }
    }

    componentDidUpdate(prevProps) {
        //比较前后的页数和输出路径是否一致，若发生变化，则表示数据源发生了变化
        if (this.props.clusterDataPages !== prevProps.clusterDataPages||this.props.outPath[0] !== prevProps.outPath[0]) {
            this.setState({clusterData:getData(this.props.clusterDataPages,this.props.resultApi,this.props.outPath)});
        }
    }

    /* 64位颜色值转RGB颜色值函数
    colorToRGBArray(color16){
        // 16进制颜色值的正则
        var reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/;
        // 把颜色值变成小写
        var color = color16.toLowerCase();
        if (reg.test(color)) {
            // 如果只有三位的值，需变成六位，如：#fff => #ffffff
            if (color.length === 4) {
                var colorNew = "#";
                for (let i = 1; i < 4; i += 1) {
                    colorNew += color.slice(i, i + 1).concat(color.slice(i, i + 1));
                }
                color = colorNew;
            }
            // 处理六位的颜色值，转为RGB
            var colorChange = [];
            for (let i = 1; i < 7; i += 2) {
                colorChange.push(parseInt("0x" + color.slice(i, i + 2)));
            }
            return colorChange;
        } else {
            return color;
        }
    }
    */

    render() {
        return <div>
            <div id="map">
                <DeckGL style={{overflow:'hidden'}}
                    //根据父组件设置初始相机参数
                    initialViewState={this.props.viewState}
                    controller={true}
                    //根据父组件提供的数据生成边界图层
                    //TODO 当data发生变化而id不变时，可能无法正确渲染变化后的data，需要在后续开发中进行测试，若渲染错误，应设定id的更改规则
                    layers={[new GeoJsonLayer({
                        id: 'boundary-layer',
                        data: this.props.boundaryData,
                        filled: false,
                        lineWidthMinPixels: 2,
                        //颜色也由父组件提供
                        getLineColor: [this.props.boundaryColor.r,this.props.boundaryColor.g,this.props.boundaryColor.b,this.props.boundaryColor.a*255],
                    }),new GeoJsonLayer({//根据父组件提供的数据生成聚类结果图层
                        id: 'Cluster-layer',
                        data: this.state.clusterData,
                        pointRadiusMinPixels: 2,
                        filled: true,
                        getFillColor: d => Gradients[parseInt(this.props.gradientNum)-1].getColor(parseInt(d.properties.color)%100+1),//颜色根据色带而改变
                        updateTriggers: {
                            getFillColor:this.props.gradientNum
                        }
                    })]}
                >
                {/* 根据父组件提供的layerId来进行底图的切换 */}
                <StaticMap mapboxApiAccessToken={MAPBOX_ACCESS_TOKEN} mapStyle={'mapbox://styles/mapbox/'+this.props.layerId}/>
                </DeckGL>
            </div>
        </div>
    }
}