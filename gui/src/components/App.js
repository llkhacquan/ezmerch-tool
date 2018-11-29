import React, { Component } from 'react';
import './App.css';
import { Row, Col, Form, Select, Upload, Icon, Modal, Checkbox, Input } from 'antd';

const FormItem = Form.Item;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;

class App extends Component {
  state = {
    productType: '1',
    marketplace: '1',

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

  handleChangeProductType = (productType) => {
    this.setState({ productType, marketplace: '1' });
  };

  handleChangeMarketplace = (marketplace) => {
    this.setState({ marketplace });
  };

  handleCancel = () => this.setState({ previewVisible: false });

  handlePreview = (file) => {
    this.setState({
      previewImage: file.url || file.thumbUrl,
      previewVisible: true,
    });
  };

  handleChange = ({ fileList }) => this.setState({ fileList });

  handleChangeColor = (checkedValues) => {
    if (checkedValues.length <= 5)
      this.setState({
        checkedColors: checkedValues,
      });
  };

  handleChangeListPrice = (event) => {
    const value = event.target.value;
    if (/^[0-9]{1,}\.?[0-9]{0,2}$/.test(value) || value === '') {
      this.setState({
        listPrice: value,
      });
    }
  };

  render() {
    const { previewVisible, previewImage, fileList } = this.state;
    const { checkedColors, listPrice } = this.state;

    const { productType, marketplace } = this.state;

    return (
      <Row className="merch-form">
        <Col md={20}>
          <Form>
            <Col md={5}>
              <FormItem label="CHOOSE PRODUCT TYPE">
                <Select value={productType} onChange={this.handleChangeProductType}>
                  <Option value="1">Standard T-Shirt</Option>
                  <Option value="2">Premium T-Shirt</Option>
                  <Option value="3">Long Sleeve T-Shirt</Option>
                  <Option value="4">Sweatshirt</Option>
                  <Option value="5">Pullover Hoodie</Option>
                  <Option value="6">PopSockets</Option>
                </Select>

              </FormItem>
            </Col>
            <Col md={1}></Col>
            <Col md={5}>
              <FormItem label="CHOOSE MARKETPLACE">
                <Select value={marketplace} onChange={this.handleChangeMarketplace} disabled={productType !== '1'}>
                  <Option value="1">Amazon.com</Option>
                  <Option value="2" disabled={productType !== '1'}>
                    Amazon.co.uk
                </Option>
                  <Option value="3" disabled={productType !== '1'}>
                    Amazon.de
                </Option>
                </Select>
              </FormItem>
            </Col>
            <Col md={1}></Col>
            <div className="clearfix">
              <FormItem label="Front">
                <Upload
                  action="//jsonplaceholder.typicode.com/posts/"
                  beforeUpload={(file, fileList) => {
                    console.log(file);
                    console.log(fileList);
                    return false;
                  }}
                  listType="picture-card"
                  fileList={fileList}
                  onPreview={this.handlePreview}
                  onChange={this.handleChange}>
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
              </FormItem>
            </div>
            <div className="clearfix">
              <FormItem label="Back">
                <Upload
                  action="//jsonplaceholder.typicode.com/posts/"
                  listType="picture-card"
                  fileList={fileList}
                  onPreview={this.handlePreview}
                  onChange={this.handleChange}>
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
              </FormItem>
            </div>
            <FormItem label="CHOOSE FIT TYPE">
              <CheckboxGroup defaultValue={['1', '3']} disabled={productType === '6'}>
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
            <Row gutter={16}>
              <Col span={12}>
                <FormItem label="List Price" className="input-number">
                  <Input addonBefore="$" style={{ width: 100 }} value={listPrice} onChange={this.handleChangeListPrice} />
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
            <FormItem label="Product description (optional)">
              <Input maxLength={2000} />
            </FormItem>
          </Form>
        </Col>
      </Row>
    );
  }
}

export default App;
