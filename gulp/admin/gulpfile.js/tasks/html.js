var config       = require('../config')
if(!config.tasks.html) return

var browserSync  = require('browser-sync')
var data         = require('gulp-data')
var gulp         = require('gulp')
var gulpif       = require('gulp-if')
var handleErrors = require('../lib/handleErrors')
var getDestFolder = require('../lib/getDestFolder')
var htmlmin      = require('gulp-htmlmin')
var path         = require('path')
var render       = require('gulp-jade')
var fs           = require('fs')

var exclude = path.normalize('!**/{' + config.tasks.html.excludeFolders.join(',') + '}/**')

var paths = {
  src: [path.join(config.root.src, config.tasks.html.src, '/**/*.jade'), exclude]
}

var getData = function(file) {
  var dataPath = path.resolve(config.root.src, config.tasks.html.src, config.tasks.html.dataFile)
  return JSON.parse(fs.readFileSync(dataPath, 'utf8'))
}

var htmlTask = function() {
  paths.dest = path.join(getDestFolder(process.env.NODE_ENV), config.tasks.html.dest);

  return gulp.src(paths.src)
    .pipe(data(getData))
    .on('error', handleErrors)
    .pipe(render({pretty:true}))
    .on('error', handleErrors)
    .pipe(gulpif(process.env.NODE_ENV === 'production', htmlmin(config.tasks.html.htmlmin)))
    .pipe(gulp.dest(paths.dest))
    .pipe(browserSync.stream())
}

gulp.task('html', htmlTask)
module.exports = htmlTask
