import React, { Component } from 'react';
import './App.css';
import { Row, Col, Form, Select, Radio, Upload, Icon, Modal, Checkbox, InputNumber, Input } from 'antd';

const FormItem = Form.Item;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;

class App extends Component {
  state = {
    previewVisible: false,
    previewImage: '',
    fileList: [],
    checkedColors: [],
    listPrice: 19.99,
  };

  items = [
    { name: 'Dark Heather', background: 'url(https://m.media-amazon.com/images/G/01/gear/portal/potter/swatches/DH-swatch._V533395604_.png)' },
    { name: 'Heather Grey', background: 'url(https://m.media-amazon.com/images/G/01/gear/portal/potter/swatches/HG-swatch._V533395604_.png)' },
    { name: 'Heather Blue', background: 'url(https://m.media-amazon.com/images/G/01/gear/portal/potter/swatches/HB-swatch._V533395604_.png)' },
    { name: 'Black', background: '#000' },
    { name: 'Navy', background: '#15232b' },
    { name: 'Silver', background: '#cfd1d1' },
    { name: 'Royal Blue', background: '#1c4086' },
    { name: 'Brown', background: '#31261d' },
    { name: 'Slate', background: '#818189' },
    { name: 'Red', background: '#b71111' },
    { name: 'Asphalt', background: '#3f3e3c' },
    { name: 'Grass', background: '#5e9444' },
    { name: 'Olive', background: '#4a4f26' },
    { name: 'Kelly Green', background: '#006136' },
    { name: 'Baby Blue', background: '#8fb8db' },
    { name: 'White', background: '#f5f5f5' },
    { name: 'Lemon', background: '#f0e87b' },
    { name: 'Cranberry', background: '#6e0a25' },
    { name: 'Pink', background: '#f8a3bc' },
    { name: 'Orange', background: '#ff5c39' },
    { name: 'Purple', background: '#514689' },
  ];

  handleCancel = () => this.setState({ previewVisible: false });

  handlePreview = file => {
    this.setState({
      previewImage: file.url || file.thumbUrl,
      previewVisible: true,
    });
  };

  handleChange = ({ fileList }) => this.setState({ fileList });

  handleChangeColor = checkedValues => {
    if (checkedValues.length <= 5)
      this.setState({
        checkedColors: checkedValues,
      });
  };

  handleChangeListPrice = value => {
    if (/\d+\.\d+$/.test(value)) {
      this.setState({
        listPrice: value,
      });
    }
  };

  render() {
    const { previewVisible, previewImage, fileList } = this.state;
    const { checkedColors, listPrice } = this.state;

    return (
      <Row className="container">
        <Col md={8}>
          <Form>
            <FormItem label="CHOOSE PRODUCT TYPE">
              <Select defaultValue="1">
                <Option value="1">Standard T-Shirt</Option>
                <Option value="2">Premium T-Shirt</Option>
                <Option value="3">Long Sleeve T-Shirt</Option>
                <Option value="4">Sweatshirt</Option>
                <Option value="5">Pullover Hoodie</Option>
                <Option value="6">PopSockets</Option>
              </Select>
            </FormItem>
            <FormItem label="CHOOSE MARKETPLACE">
              <Select defaultValue="1">
                <Option value="1">Amazon.com</Option>
                <Option value="2">Amazon.co.uk</Option>
                <Option value="3">Amazon.de</Option>
              </Select>
            </FormItem>
            <FormItem label="PRINT ARTWORK ON THIS SIDE">
              <Radio.Group defaultValue="1" buttonStyle="solid">
                <Radio.Button value="1">Front</Radio.Button>
                <Radio.Button value="2">Back</Radio.Button>
              </Radio.Group>
            </FormItem>
            <div className="clearfix">
              <Upload
                action="//jsonplaceholder.typicode.com/posts/"
                listType="picture-card"
                fileList={fileList}
                onPreview={this.handlePreview}
                onChange={this.handleChange}
              >
                {fileList.length < 1 && (
                  <div>
                    <Icon type="plus" />
                    <div className="ant-upload-text">Upload</div>
                  </div>
                )}
              </Upload>
              <Modal footer={null} visible={previewVisible} onCancel={this.handleCancel}>
                <img alt="" style={{ width: '100%' }} src={previewImage} />
              </Modal>
            </div>
            <FormItem label="CHOOSE FIT TYPE">
              <CheckboxGroup defaultValue={['1']}>
                <Checkbox value="1">Men</Checkbox>
                <Checkbox value="2">Women</Checkbox>
                <Checkbox value="3">Youth</Checkbox>
              </CheckboxGroup>
            </FormItem>
            <FormItem label="PICK UP TO 5 COLORS">
              <CheckboxGroup className="color-menu" value={checkedColors} onChange={this.handleChangeColor}>
                {this.items.map((item, index) => (
                  <Checkbox key={index} value={index}>
                    <span
                      className="color-item"
                      title={item.name}
                      style={{
                        background: item.background,
                        border: checkedColors.includes(index) ? '3px solid orange' : '1px solid silver',
                      }}
                    />
                  </Checkbox>
                ))}
              </CheckboxGroup>
            </FormItem>
            <Row>
              <Col span={12}>
                <FormItem label="List Price">
                  <InputNumber
                    value={listPrice}
                    formatter={value => `$ ${value}`}
                    parser={value => value.replace(/\$\s?|(,*)/g, '')}
                    onChange={this.handleChangeListPrice}
                  />
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem label="Estimated Royalty">{}</FormItem>
              </Col>
            </Row>

            <FormItem label="Brand name">
              <Input maxLength={50} />
            </FormItem>
            <FormItem label="Title of product">
              <Input maxLength={60} />
            </FormItem>
            <FormItem label="Key product features (optional)">
              <Input maxLength={256} />
              <Input maxLength={256} />
            </FormItem>
          </Form>
        </Col>
      </Row>
    );
  }
}

export default App;
