import React from 'react';
import './DataSource.css';
import { Select, Upload, Button} from 'antd';
import { UploadOutlined } from '@ant-design/icons'
import intl from 'react-intl-universal';


export default class DataSource extends React.Component {

  render() {
    return (
        <div>
            {/* 数据源 */}
            <h3 style={{"fontSize":"16pt","marginTop":"5pt"}}>{intl.get('DATA_SOURCE')}</h3>
            <div className="Parameter">
            <div className="paramter-title">{intl.get('SELECT_DATA')}</div>
                <Select className="Parameter-Select" defaultValue={this.props.dataSources[0]} style={{width:'225px',textAlign:'center'}}>{
                    this.props.dataSources.length && this.props.dataSources.map((item, index) => (
                        <Select.Option key={index} value={item}>{item}</Select.Option>)
                    )
                }</Select>
            </div>
            <div className="Parameter" style={{justifyContent:'space-between'}}>
                <div className="paramter-title">{intl.get('UPLOAD_DATA')}</div>
                <Upload>
                    <Button style={{ width: '225px' }}>
                        <UploadOutlined /> Click to Upload
                    </Button>
                </Upload>
            </div>
            {/* 空间范围 */}
            <div className="Parameter">
            <div className="paramter-title">{intl.get('Space_Range')}</div>
                <Select className="Parameter-Select" defaultValue={this.props.dataSources[0]} style={{width:'225px',textAlign:'center'}}>{
                    this.props.dataSources.length && this.props.dataSources.map((item, index) => (
                        <Select.Option key={index} value={item}>{item}</Select.Option>)
                    )
                }</Select>
            </div>
            <div className="Parameter" style={{justifyContent:'flex-start'}}>
                <div className="paramter-title">{intl.get('UPLOAD_Range')}</div>
                <Upload>
                    <Button style={{ width: '225px' }}>
                        <UploadOutlined /> Click to Upload
                    </Button>
                </Upload>
            </div>
        </div>
    )
  }
}
