var config = require('../config')
var compact = require('lodash/array/compact')

// Grouped by what can run in parallel
var assetTasks = ['fonts', 'images', 'svgSprite', 'libs']
var codeTasks = ['sass', 'postcss', 'js', 'html']

module.exports = function(env) {
  process.env.NODE_ENV = env;
  return {
    assetTasks: compact(assetTasks),
    codeTasks: compact(codeTasks)
  }
}
