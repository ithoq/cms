var config      = require('../config')
if(!config.tasks.images) return

var browserSync = require('browser-sync')
var changed     = require('gulp-changed')
var gulp        = require('gulp')
var imagemin    = require('gulp-imagemin')
var path        = require('path')
var getDestFolder = require('../lib/getDestFolder')

var paths = {
  src: path.join(config.root.src, config.tasks.images.src, '/**')
}

var imagesTask = function() {
  paths.dest = path.join(getDestFolder(process.env.NODE_ENV), config.tasks.images.dest);
  return gulp.src(paths.src)
    .pipe(changed(paths.dest)) // Ignore unchanged files
    .pipe(imagemin()) // Optimize
    .pipe(gulp.dest(paths.dest))
    .pipe(browserSync.stream())
}

gulp.task('images', imagesTask)
module.exports = imagesTask
