var React = require('react');

module.exports = function(props) {

  console.log("props in js component: ", props);
  console.log("children in js component: ", props.children);

  return React.createElement(
    "p",
    null,
    "jsValue in JS component: " + props.jsValue
  );

};
