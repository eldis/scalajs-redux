var HtmlWebpackPlugin = require('html-webpack-plugin');
var cfg = require('./scalajs.webpack.config');
var path = require('path');
var rootDir = path.dirname(path.dirname(path.dirname(path.dirname(__dirname))));

cfg.output.path = path.join(rootDir, 'build');

cfg.module = cfg.module || {};
cfg.module.loaders = cfg.module.loaders || [];
cfg.module.loaders.push({
  test: /\.html$/,
  loader: 'html'
});

cfg.resolve = cfg.resolve || {};
cfg.resolve.alias = cfg.resolve.alias || {};
cfg.resolve.alias["JsComponent"] = path.join(rootDir, 'src/main/js/JsComponent.js');
cfg.resolve.root = cfg.resolve.root || [];
cfg.resolve.root.push(path.join(__dirname, 'node_modules'));
cfg.resolveLoader = cfg.resolveLoader || {};
cfg.resolveLoader.root = cfg.resolveLoader.root || [];
cfg.resolveLoader.root.push(path.join(__dirname, 'node_modules'));

cfg.plugins = cfg.plugins || [];
cfg.plugins.push(new HtmlWebpackPlugin({
  template: path.join(rootDir, 'src/main/assets/index.html')
}));

module.exports = cfg;
