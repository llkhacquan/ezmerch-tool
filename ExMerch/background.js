// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// Create a rule that will show the page action when the conditions are met.
const kMatchRule = {
  // Declare the rule conditions.
  conditions: [new chrome.declarativeContent.PageStateMatcher({
    pageUrl: { urlPrefix: 'https://merch.amazon.com/manage/products' },
  })],
  // Shows the page action when the condition is met.
  actions: [new chrome.declarativeContent.ShowPageAction(), new chrome.declarativeContent.RequestContentScript({ js: ["content_script.js"] })]
}

// Register the runtime.onInstalled event listener.
chrome.runtime.onInstalled.addListener(function () {
  chrome.brw
  // Overrride the rules to replace them with kMatchRule.
  chrome.declarativeContent.onPageChanged.removeRules(undefined, function () {
    chrome.declarativeContent.onPageChanged.addRules([kMatchRule]);
  });
});
