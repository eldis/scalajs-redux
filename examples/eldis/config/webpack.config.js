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

cfg.plugins = cfg.plugins || [];
cfg.plugins.push(new HtmlWebpackPlugin({
  template: path.join(rootDir, 'src/main/assets/index.html')
}));

module.exports = cfg;
