var config       = require('../config')
if(!config.tasks.sass) return

var gulp         = require('gulp')
var browserSync  = require('browser-sync')
var sass         = require('gulp-sass')
var sourcemaps   = require('gulp-sourcemaps')
var handleErrors = require('../lib/handleErrors')
var autoprefixer = require('gulp-autoprefixer')
var path         = require('path')
var rename       = require('gulp-rename');
var postcss      = require('gulp-postcss')
var gulpif       = require('gulp-if');
var nano         = require('gulp-cssnano');
var changed      = require('gulp-changed')
var csscomb      = require('gulp-csscomb');
var getDestFolder = require('../lib/getDestFolder');

var paths = {
  src: path.join(config.root.src, config.tasks.sass.src, '/**/*.{' + config.tasks.css.extensions + '}'),
  srcMain: path.join(config.root.src, config.tasks.sass.src, 'style.scss'),
  csscomb : path.join(config.root.src, config.tasks.sass.src)
}

var sassComb = function(){
    return gulp.src(paths.src)
               .pipe(changed(paths.src)) // Ignore unchanged files
               .pipe(csscomb({
                "custom_config_path": "/Users/fabricecipolla/Documents/Gulp/csscomb.json"
               }))
               .on('error', handleErrors)
               .pipe(gulp.dest(paths.csscomb))
}

var sassTask = function () {
  paths.dest = path.join(getDestFolder(process.env.NODE_ENV), config.tasks.css.dest);
  return gulp.src(paths.srcMain)
    .pipe(sourcemaps.init())
    .pipe(sass(config.tasks.css.sass))
    .on('error', handleErrors)
    .pipe(autoprefixer(config.tasks.css.autoprefixer))
    .pipe(sourcemaps.write())
    .pipe(rename("style.css"))
    .pipe(gulp.dest(paths.dest))
    .pipe(browserSync.stream())
}

gulp.task('sassComb', sassComb)
gulp.task('sass', sassTask)
module.exports = sassTask
