import React, { Component } from 'react';
import './App.css';
import { Row, Col, Form, Select, Upload, Icon, Modal, Checkbox, Input, Button } from 'antd';

const FormItem = Form.Item;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
const InputTextArea = Input.TextArea;

class App extends Component {
  constructor() {
    super();
    this.state = {
      productType: '1',

      marketplace: '1',

      previewVisible: false,
      previewImage: '',
      fileList: [],
      path: '',

      previewVisibleBack: false,
      previewImageBack: '',
      fileListBack: [],
      pathBack: '',

      fitType: ['1', '2'],

      checkedColors: [],
      listPrice: '',
      description: '',
    };
  }

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

  fiveColors = [
    { name: 'Heather Grey', background: 'url(https://m.media-amazon.com/images/G/01/gear/portal/potter/swatches/HG-swatch._V533395604_.png)' },
    { name: 'Dark Heather', background: 'url(https://m.media-amazon.com/images/G/01/gear/portal/potter/swatches/DH-swatch._V533395604_.png)' },
    { name: 'Black', background: '#000' },
    { name: 'Navy', background: '#15232b' },
    { name: 'Royal Blue', background: '#1c4086' },
  ];

  dimsFront = [
    { width: 4500, height: 5400 },
    { width: 4500, height: 5400 },
    { width: 4500, height: 5400 },
    { width: 4500, height: 5400 },
    { width: 4500, height: 4050 },
    { width: 485, height: 485 },
  ];

  dimsBack = [
    { width: 4500, height: 5400 },
    { width: 4500, height: 5400 },
    { width: 4500, height: 5400 },
    { width: 4500, height: 5400 },
    { width: 4500, height: 5400 },
  ];

  availValues = [
    [12.87, 14.99, 18.16, 19.99],
    [14.87, 19.99, 20.14, 24.99],
    [18.99, 24.99, 25.39, 32.99],
    [28.46, 35.99, 34.04, 39.99],
    [32.48, 39.99, 38.49, 46.99],
    [11.72, 14.99, 11.72, 14.99],
  ];

  handleSave = () => {
    var saveData = (function() {
      var a = document.createElement('a');
      document.body.appendChild(a);
      a.style = 'display: none';
      return function(data, fileName) {
        var json = JSON.stringify(data),
          blob = new Blob([json], { type: 'octet/stream' }),
          url = window.URL.createObjectURL(blob);
        a.href = url;
        a.download = fileName;
        a.click();
        window.URL.revokeObjectURL(url);
      };
    })();

    saveData(this.state, 'form.json');
  };

  handleChangeProductType = (productType) => {
    this.setState({
      productType,
      marketplace: '1',

      previewVisible: false,
      previewImage: '',
      fileList: [],
      path: '',

      previewVisibleBack: false,
      previewImageBack: '',
      fileListBack: [],
      pathBack: '',

      fitType: ['1', '2'],

      checkedColors: [],
    });
  };

  handleChangeMarketplace = (marketplace) => {
    this.setState({ marketplace });
  };

  handleChangeFitType = (fitType) => this.setState({ fitType });

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

  handleChangePath = (event) => {
    const { value } = event.target;
    this.setState({ path: value });
  };

  handleChange = ({ file, fileList }) => {
    const setFileList = (files) => this.setState({ fileList: files });

    if (fileList[0]) {
      const img = new Image();
      img.onload = function() {
        console.log(this.width + ' ' + this.height);
        if (this.width < 800) {
          setFileList(fileList);
        } else setFileList([]);
      };
      img.src = URL.createObjectURL(file);
    }
  };

  handleRemove = () => this.setState({ fileList: [] });

  handlePreview = (file) => this.setState({ previewImage: file.thumbUrl, previewVisible: true });

  handleCancel = () => this.setState({ previewVisible: false });

  handleChangePathBack = (event) => {
    const { value } = event.target;
    this.setState({ pathBack: value });
  };

  handleChangeBack = ({ file, fileList }) => {
    const setFileList = (files) => this.setState({ fileListBack: files });

    if (fileList[0]) {
      const img = new Image();
      img.onload = function() {
        console.log(this.width + ' ' + this.height);
        if (this.width < 800) {
          setFileList(fileList);
        } else setFileList([]);
      };
      img.src = URL.createObjectURL(file);
    }
  };

  handleRemoveBack = () => this.setState({ fileListBack: [] });

  handlePreviewBack = (file) => this.setState({ previewImageBack: file.thumbUrl, previewVisibleBack: true });

  handleCancelBack = () => this.setState({ previewVisibleBack: false });

  render() {
    const { previewVisible, previewImage, fileList, path } = this.state;
    const { previewVisibleBack, previewImageBack, fileListBack, pathBack } = this.state;

    const { checkedColors, listPrice, description } = this.state;
    const { productType, marketplace, fitType } = this.state;

    const items = productType === '1' || productType === '2' ? this.items : this.fiveColors;

    let avail = '';
    if (path && pathBack) {
      avail = `${this.availValues[productType - '1'][2]} - 80.00`;
    } else if (path || pathBack) {
      avail = `${this.availValues[productType - '1'][0]} - 80.00`;
    } else {
      avail = `${this.availValues[productType - '1'][0]} - 80.00`;
    }

    return (
      <Row className="merch-form">
        <Col xl={12} md={16} sm={20}>
          <Form>
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

            {productType === '1' ? (
              <FormItem label="CHOOSE MARKETPLACE">
                <Select value={marketplace} onChange={this.handleChangeMarketplace}>
                  <Option value="1">Amazon.com</Option>
                  <Option value="2">Amazon.co.uk</Option>
                  <Option value="3">Amazon.de</Option>
                </Select>
              </FormItem>
            ) : (
              <FormItem label="CHOOSE MARKETPLACE" className="input-readonly">
                <Input addonBefore="Amazon.com" />
              </FormItem>
            )}

            <div className="clearfix">
              <Upload
                beforeUpload={() => false}
                listType="picture-card"
                fileList={fileList}
                onChange={this.handleChange}
                onRemove={this.handleRemove}
                onPreview={this.handlePreview}>
                {fileList.length < 1 && (
                  <div>
                    <Icon type="plus" />
                    <div className="ant-upload-text">Choose</div>
                  </div>
                )}
              </Upload>
              <Modal footer={null} visible={previewVisible} onCancel={this.handleCancel}>
                <img alt="" style={{ width: '100%' }} src={previewImage} />
              </Modal>
              <FormItem label="Front" extra={`${this.dimsFront[productType - '1'].width} x ${this.dimsFront[productType - '1'].height}`}>
                <Input value={path} onChange={this.handleChangePath} />
              </FormItem>
            </div>

            {productType !== '6' && (
              <div className="clearfix">
                <Upload
                  beforeUpload={() => false}
                  listType="picture-card"
                  fileList={fileListBack}
                  onChange={this.handleChangeBack}
                  onRemove={this.handleRemoveBack}
                  onPreview={this.handlePreviewBack}>
                  {fileListBack.length < 1 && (
                    <div>
                      <Icon type="plus" />
                      <div className="ant-upload-text">Choose</div>
                    </div>
                  )}
                </Upload>
                <Modal footer={null} visible={previewVisibleBack} onCancel={this.handleCancelBack}>
                  <img alt="" style={{ width: '100%' }} src={previewImageBack} />
                </Modal>
                <FormItem label="Back" extra={`${this.dimsBack[productType - '1'].width} x ${this.dimsBack[productType - '1'].height}`}>
                  <Input value={pathBack} onChange={this.handleChangePathBack} />
                </FormItem>
              </div>
            )}

            {(productType === '1' || productType === '2') && (
              <FormItem label="CHOOSE FIT TYPE">
                <CheckboxGroup value={fitType} onChange={this.handleChangeFitType}>
                  <Checkbox value="1">Men</Checkbox>
                  <Checkbox value="2">Women</Checkbox>
                  <Checkbox value="3">Youth</Checkbox>
                </CheckboxGroup>
              </FormItem>
            )}

            {productType !== '6' && (
              <FormItem label="PICK UP TO 5 COLORS">
                <CheckboxGroup className="color-menu" value={checkedColors} onChange={this.handleChangeColor}>
                  {items.map((item, index) => (
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
            )}

            <Row gutter={16}>
              <Col span={12}>
                <FormItem label="List Price" className="input-number" extra={avail}>
                  <Input prefix="$" value={listPrice} onChange={this.handleChangeListPrice} />
                </FormItem>
              </Col>
            </Row>

            <FormItem label="Description">
              <InputTextArea value={description} autosize={{ minRows: 2 }} />
            </FormItem>
            <div style={{ textAlign: 'right' }}>
              <Button type="primary" onClick={this.handleSave}>
                Save
              </Button>
            </div>

            {false && (
              <div>
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
              </div>
            )}
          </Form>
        </Col>
      </Row>
    );
  }
}

export default Form.create()(App);
