import React from 'react';
import './StyleSettings.css';
import intl from 'react-intl-universal';
import { ChromePicker } from 'react-color';
import { Select } from 'antd';

// 底图示意图
import dark from '../common/dark.png'
import light from '../common/light.png'
import satellite from '../common/satellite.png'
import outdoors from '../common/outdoors.png'
import navigation from '../common/navigation.png'


export default class StyleSettings extends React.Component {
    constructor(props){
        super(props);
        this.state={
            displayColorPicker: false,
        }
    }
    handleClick = () => {
        this.setState({ displayColorPicker: !this.state.displayColorPicker })
    };
    
    handleClose = () => {
        this.setState({ displayColorPicker: false })
    };

    render() {
        const { Option } = Select;
        return(
            <div>
                {/* 样式设置 */}
                <h3 style={{"fontSize":"16pt","marginTop":"5pt"}}>{intl.get('StyleSettings')}</h3>
                <div>
                    <div className="Parameter">
                    {/* 边界颜色 */}
                        <div className="paramter-title">{intl.get('Border_Color')}</div>
                        <div style={{
                            padding: '2px',
                            background: '#fff',
                            borderRadius: '1px',
                            boxShadow: '0 0 0 1px rgba(0,0,0,.1)',
                            display: 'inline-block',
                            cursor: 'pointer',
                        }}>
                            <div onClick={ this.handleClick } style={{
                                width: '236px',
                                height: '16px',
                                borderRadius: '2px',
                                background: `rgba(${ this.props.boundaryColor.r }, ${ this.props.boundaryColor.g }, ${ this.props.boundaryColor.b },${ this.props.boundaryColor.a })`,}}/>
                        
                        { this.state.displayColorPicker ? <div className="popover">
                            <div onClick={ this.handleClose } style={{
                                position: 'fixed',
                                top: '0px',
                                right: '0px',
                                bottom: '0px',
                                left: '0px',
                            }}/>
                            <ChromePicker color={ this.props.boundaryColor } onChange={(color)=>this.props.handleColorChange(color) } />
                        </div> : null }
                        </div>
                    </div>
                    <div className="Parameter">
                    {/* 类簇颜色 */}
                        <div className="paramter-title">{intl.get('Cluster_Color')}</div>
                        <Select defaultValue="4" className="Parameter-Select" style={{width:"240px"}} onChange={(value) => this.props.handleGradientChange(value)}>
                            <Option value="1" ><div style={{backgroundImage: 'linear-gradient(to right,red,blue,violet)',height:"20px",marginTop:"4px",backgroundRepeat:"no-repeat"}}></div></Option>
                            <Option value="2" ><div style={{backgroundImage: 'linear-gradient(to right,yellow, green)',height:"20px",marginTop:"4px",backgroundRepeat:"no-repeat"}}></div></Option>
                            <Option value="3" ><div style={{backgroundImage: 'linear-gradient(to right,orange, yellow)',height:"20px",marginTop:"4px",backgroundRepeat:"no-repeat"}}></div></Option>
                            <Option value="4" ><div style={{backgroundImage: 'linear-gradient(to right,red, orange, yellow, green, blue, indigo, violet)',height:"20px",marginTop:"4px",backgroundRepeat:"no-repeat"}}></div></Option>
                        </Select>
                    </div>
                    <div className="Parameter">
                    {/* 底图选择 */}
                        <div className="paramter-title">{intl.get('Base_Map')}</div>
                        <Select defaultValue={this.props.layerId} className="Parameter-Select" style={{width:"240px"}} onChange={(value) => this.props.handleLayerChange(value)}>
                            <Option value="dark-v10" ><div className="layerId">dark</div><div className="layerImage" style={{backgroundImage: 'url(' + dark + ')'}}></div></Option>
                            <Option value="light-v10" ><div className="layerId">light</div><div className="layerImage" style={{backgroundImage: 'url(' + light + ')'}}></div></Option>
                            <Option value="satellite-v9" ><div className="layerId">satellite</div><div className="layerImage" style={{backgroundImage: 'url(' + satellite + ')'}}></div></Option>
                            <Option value="outdoors-v11" ><div className="layerId">outdoors</div><div className="layerImage" style={{backgroundImage: 'url(' + outdoors + ')'}}></div></Option>
                            <Option value="navigation-preview-night-v2" ><div className="layerId">navigation</div><div className="layerImage" style={{backgroundImage: 'url(' + navigation + ')'}}></div></Option>
                        </Select>
                    </div>
                </div>
          </div>
        )
    }
}