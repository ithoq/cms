var config = require('../config')
var array = require('lodash/array')

// Grouped by what can run in parallel
var assetTasks = ['fonts', 'images', 'svgSprite', 'libs']
var codeTasks = ['sass', 'postcss', 'js', 'html']

module.exports = function (env) {
    process.env.NODE_ENV = env;
    return {
        assetTasks: array.compact(assetTasks),
        codeTasks: array.compact(codeTasks)
    }
}
