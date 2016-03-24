var config       = require('../config')
var changed      = require('gulp-changed')
var gulp         = require('gulp')
var path         = require('path')
var handleErrors = require('../lib/handleErrors')
var getDestFolder = require('../lib/getDestFolder')
var include      = require("gulp-include")
var browserSync  = require('browser-sync')
var uglify = require('gulp-uglify');
var gulpif       = require('gulp-if')

var exclude = path.normalize('!**/{' + config.tasks.js.excludeFolders.join(',') + '}/**')
var paths = {
  src: [path.join(config.root.src, config.tasks.js.src, '/**/*.js'), exclude]
}

var jsTask = function() {
  paths.dest = path.join(getDestFolder(process.env.NODE_ENV), config.tasks.js.dest);
  return gulp.src(paths.src)
    .pipe(changed(paths.dest)) // Ignore unchanged files
    .pipe(include())
    .on('error', handleErrors)
    //.pipe(gulpif(process.env.NODE_ENV === 'production', uglify().on('error', handleErrors)))
    .pipe(gulp.dest(paths.dest))
    .pipe(browserSync.stream())
}

gulp.task('js', jsTask)
module.exports = jsTask
