var config = require('../config')
if (!config.tasks.postcss) return

var gulp = require('gulp')
var browserSync = require('browser-sync')
var handleErrors = require('../lib/handleErrors')
var path = require('path')
var rename = require('gulp-rename');
var postcss = require('gulp-postcss')
var gulpif = require('gulp-if');
var nano = require('gulp-cssnano');
var csscomb = require('gulp-csscomb');
var changed = require('gulp-changed');
var getDestFolder = require('../lib/getDestFolder');

var paths = {
    src: path.join(config.root.src, config.tasks.postcss.src, '/**/*.{' + config.tasks.css.extensions + '}'),
    srcMain: path.join(config.root.src, config.tasks.postcss.src, 'style.scss'),
    csscomb: path.join(config.root.src, config.tasks.postcss.src)
}

var postcssComb = function () {
    return gulp.src(paths.src)
        .pipe(changed(paths.src)) // Ignore unchanged files
        .pipe(csscomb())
        .on('error', handleErrors)
        .pipe(gulp.dest(paths.csscomb))

}

var postcssTask = function () {
    paths.dest = path.join(getDestFolder(process.env.NODE_ENV), config.tasks.css.dest)
    var production = (process.env.NODE_ENV === 'production');
    var processors = [
        require('precss')({'import': {extension: 'scss'}}),
        require('postcss-calc'), // permet d'effectuer des calcules dans calc : calc(100px * $var)
        require('rucksack-css'), // ajoute des tags css, comme "clear:fix"
        require('autoprefixer')(config.tasks.css.autoprefixer),
        require('css-mqpacker') // rassemble toutes les mediaquery en une
    ];

    return gulp.src(paths.srcMain)
        .pipe(postcss(processors))
        .on('error', handleErrors)
        .pipe(gulpif(production, nano({
          discardComments: {removeAll: true},
          zindex: false,
          discardUnused: false,
          mergeIdents: false,
          reduceIdents: false,
        })))
        .pipe(rename("style.css"))
        .pipe(gulp.dest(paths.dest))
        .pipe(browserSync.stream())
}
gulp.task('postcssComb', postcssComb)
gulp.task('postcss', postcssTask)
module.exports = postcssTask
