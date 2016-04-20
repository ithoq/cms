var config = require('../config')
if (!config.tasks.svgSprite) return

var browserSync = require('browser-sync')
var gulp = require('gulp')
var imagemin = require('gulp-imagemin')
var svgstore = require('gulp-svgstore')
var path = require('path')
var getDestFolder = require('../lib/getDestFolder')

var svgSpriteTask = function () {

    var settings = {
        src: path.join(config.root.src, config.tasks.svgSprite.src, '/*.svg'),
        dest: path.join(getDestFolder(process.env.NODE_ENV), config.tasks.svgSprite.dest)
    }

    return gulp.src(settings.src)
        .pipe(imagemin())
        .pipe(svgstore())
        .pipe(gulp.dest(settings.dest))
        .pipe(browserSync.stream())
}

gulp.task('svgSprite', svgSpriteTask)
module.exports = svgSpriteTask
