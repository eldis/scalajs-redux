var React = require('react');

module.exports = function(props) {

  var args = [
    "div",
    null,
    "jsValue in JS component: " + props.jsValue,
  ].concat(props.children)

  return React.createElement.apply(React, args)
};
