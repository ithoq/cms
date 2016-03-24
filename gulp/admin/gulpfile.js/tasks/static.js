var config  = require('../config')
var changed = require('gulp-changed')
var gulp    = require('gulp')
var path    = require('path')
var getDestFolder = require('../lib/getDestFolder')

var paths = {
  src: path.join(config.root.src, config.tasks.static.src, '/**')
}

var staticTask = function() {
  paths.dest = path.join(getDestFolder(process.env.NODE_ENV), config.tasks.static.dest);
  return gulp.src(paths.src)
    .pipe(changed(paths.dest)) // Ignore unchanged files
    .pipe(gulp.dest(getDestFolder(process.env.NODE_ENV)))
}

gulp.task('static', staticTask)
module.exports = staticTask
