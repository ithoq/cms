process.env.NODE_ENV = 'production';
var config = require('../config')
var gulp = require('gulp')
var path = require('path')
var watch = require('gulp-watch')
var runSequence = require('gulp-sequence')

var paths = {
    postcss: path.join(config.root.src, config.tasks.postcss.src, '/**/*.{' + config.tasks.css.extensions + '}'),
    sass: path.join(config.root.src, config.tasks.sass.src, '/**/*.{' + config.tasks.css.extensions + '}')
}

gulp.task('watch-postcss', function () {
    var watcher = watch(paths.postcss, function () {
        watcher.unwatch(paths.postcss);
        runSequence(
            'postcssComb',
            'postcss',
            function () {
                watcher.add(paths.postcss);
            }
        );
    });
});

gulp.task('watch-sass', function () {
    var watcher = watch(paths.sass, function () {
        watcher.unwatch(paths.sass);
        runSequence(
            'sassComb',
            'sass',
            function () {
                watcher.add(paths.sass);
            }
        );
    });
});

var watchTask = function () {
    //var watchableTasks = ['fonts', 'iconFont', 'images', 'svgSprite','html', 'css']

    var watchableTasks = ['fonts', 'html', 'images', 'js', 'static', 'libs']
    watchableTasks.forEach(function (taskName) {
        var task = config.tasks[taskName]
        if (task) {
            var glob = path.join(config.root.src, task.src, '**/*.{' + task.extensions.join(',') + '}')
            var watcher = watch(glob, function () {
                require('./' + taskName)()
            })
        }
    })
}

var prodTask = function () {
    process.env.NODE_ENV = 'production';
    watchTask();
}

gulp.task('watch', ['browserSync', 'watch-postcss', 'watch-sass'], watchTask)
gulp.task('watch-prod', ['watch-postcss', 'watch-sass'], prodTask)
module.exports = watchTask
