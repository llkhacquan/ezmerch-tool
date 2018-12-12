// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

var outerHTMLForCustomFilter = '<div class="a-column a-span2">\n' +
  '      <span class="a-dropdown-container">\n' +
  '         <select name="" autocomplete="off" id="filter_products_dropdown_2" tabindex="-1" class="a-native-dropdown a-button-span12">\n' +
  '            <option class="a-prompt" value="">Filter</option>\n' +
  '            <option value="All" >All products</option>\n' +
  '            <option value="Standard">Standard T-Shirt</option>\n' +
  '\t        <option value="Premium">Premium T-Shirt</option>\n' +
  '\t        <option value="Long">Long Sleeve T-Shirt</option>\n' +
  '\t        <option value="Sweatshirt">Sweatshirt</option>\n' +
  '\t        <option value="Pullover">Pullover Hoodie</option>\n' +
  '\t        <option value="PopSockets">PopSockets</option>\n' +
  '         </select>\n' +
  '         <span tabindex="-1" class="a-button a-button-dropdown a-button-span12"><span class="a-button-inner"><span class="a-button-text a-declarative" data-action="a-dropdown-button" role="button" tabindex="0" aria-hidden="true" ><span class="a-dropdown-prompt" >Filter</span></span><i class="a-icon a-icon-dropdown" ></i></span></span>\n' +
  '      </span>\n' +
  '\t</div>';

var outerHTML2 =
  '<div class="a-column a-span4 a-span-last" data-reactid=".0.1.0.0.3.1">' +
  '<span class="a-button a-button-primary" data-reactid=".0.1.0.0.3.1.0">' + '<span class="a-button-inner" data-reactid=".0.1.0.0.3.1.0.0">' + '<button class="a-button-text" type="button" data-reactid=".0.1.0.0.3.1.0.0.0">Add a new product</button></span></span>' +
  '<span class="a-button a-button-primary edit-all-button"><span class="a-button-inner"><button class="a-button-text" type="button" onclick="clickAllEdit()">Edit all</button></span></span>' +
  '<span class="a-button a-button-primary submit-all-button"><span class="a-button-inner"><button class="a-button-text" type="button" onclick="clickAllSubmit()">Submit all</button></span></span>' +
  '</div>';

function clickAllEdit() {
  var filter = document.getElementById('filter_products_dropdown_2').value;
  var elements;
  if (filter === '' || filter == 'All') {
    elements = Array.from(document.querySelectorAll(".custom-edit"));
  } else {
    var temp = '.custom-edit' + '.' + filter;
    console.log(temp);
    elements = Array.from(document.querySelectorAll(temp));
  }
  for (let element of elements) {
    element.click();
  }
}

function clickAllSubmit() {
  var filter = document.getElementById('filter_products_dropdown_2').value;
  var elements;
  if (filter === '' || filter == 'All') {
    elements = Array.from(document.querySelectorAll(".custom-submit"));
  } else {
    var temp = '.custom-submit' + '.' + filter;
    elements = Array.from(document.querySelectorAll(temp));
  }
  for (let element of elements) {
    element.click();
  }
}

var clickAllEditFunc = clickAllEdit;
var clickAllSubmitFunc = clickAllSubmit;

setTimeout(run, 2000);

function run() {
  setTimeout(run, 2000);
  let temp = Array.from(document.querySelectorAll(".custom-edit"));
  if (temp.length > 0) {
    return;
  }
  let element = Array.from(document.querySelectorAll('div[data-reactid=".0.1.0.0.3.1"]'))[0];
  if (element) {
    element.outerHTML = outerHTML2;
  }

  element = Array.from(document.querySelectorAll('div[data-reactid=".0.1.0.0.5.1"]'))[0];
  if (element && element.parentElement.childNodes.length == 3) {
    element.insertAdjacentHTML('afterend', outerHTMLForCustomFilter);
  }

  let elements = Array.from(document.querySelectorAll('td'));
  for (let element of elements) {
    var dataReactId = element.getAttribute('data-reactid');
    if (dataReactId != null && dataReactId.startsWith('.0.1.0.0.6.0.0.1:') && dataReactId.endsWith('.5')) {
      var t1 = element.firstChild.getAttribute('data-reactid').split('.');
      var id = t1[t1.length - 1].split('_')[1];
      var status = element.previousSibling.textContent;
      if (status.includes('Removed')) {
        continue;
      }
      var productType = element.previousSibling.previousSibling.previousSibling.previousSibling.textContent;
      var editLink;
      var submitLink;
      if (productType === 'PopSockets') {
        submitLink = 'https://merch.amazon.com/merch-popsocket/title-setup/' + id + '/review_details';
        editLink = 'https://merch.amazon.com/merch-popsocket/title-setup/' + id + '/add_details';
      } else {
        editLink = 'https://merch.amazon.com/merch-tshirt/title-setup/' + id + '/add_details';
        submitLink = 'https://merch.amazon.com/merch-tshirt/title-setup/' + id + '/review_details';
      }
      var editButton = document.createElement('span');
      element.appendChild(editButton);
      editButton.outerHTML = '<span class="a-button" style="margin-left: .385em;"><span class="a-button-inner"><button class="ml-1 a-button-text custom-edit ' + productType + '" type="button"' + ' onclick="window.open(\'' + editLink + '\', \'_blank\');">Edit</button></span></span>'
      if (status === 'Draft') {
        var submitButton = document.createElement('span');
        element.appendChild(submitButton);
        submitButton.outerHTML = '<span class="a-button"><span class="a-button-inner"><button class="a-button-text custom-submit ' + productType + '" type="button"' + ' onclick="window.open(\'' + submitLink + '\', \'_blank\');">Submit</button></span></span>'
      }
    }
  }

  element = Array.from(document.querySelectorAll('th[data-reactid=".0.1.0.0.6.0.0.0.5"]'))[0];
  if (element) {
    var dataReactId = element.getAttribute('data-reactid');
    if (dataReactId != null && dataReactId === '.0.1.0.0.6.0.0.0.5') {
      element.classList.remove('a-span2');
      element.classList.add('a-span4');
    }
  }
}