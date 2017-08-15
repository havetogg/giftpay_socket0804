/**
 * Created by Administrator on 2017/8/9.
 */
var gulp = require('gulp');
    minifycss = require('gulp-minify-css');
    concat = require('gulp-concat');
    uglify = require('gulp-uglify');
    rename = require('gulp-rename');
    del = require('del');
    imagemin=require('gulp-imagemin');
    htmlmin = require('gulp-htmlmin');
gulp.task('css', function() {
    gulp.src(['css/app.css', 'css/common/common.css'])    //- 需要处理的css文件，放到一个字符串数组里
        .pipe(concat('app.css'))                               //- 合并后的文件名
        .pipe(minifycss())                                      //- 压缩处理成一行
        .pipe(gulp.dest('dist/css'));
});
gulp.task('js', function() {
    gulp.src(['js/common/common.js','js/index.js' +
    '' +
    '' +
    ''])
        .pipe(concat('app.js'))    //合并所有js到main.js
        .pipe(gulp.dest('dist/js'))    //输出main.js到文件夹
        .pipe(rename({suffix: '.min'}))   //rename压缩后的文件名
        .pipe(uglify())    //压缩
        .pipe(gulp.dest('dist/js'));  //输出
});
gulp.task('images', function () {
    gulp.src('img/*.*')
        .pipe(imagemin({
            progressive: true
        }))
        .pipe(gulp.dest('dist/img'));
});
gulp.task('testHtmlmin', function () {
    var options = {
        removeComments: true,//清除HTML注释
        collapseWhitespace: true,//压缩HTML
        collapseBooleanAttributes: true,//省略布尔属性的值 <input checked="true"/> ==> <input />
        removeEmptyAttributes: true,//删除所有空格作属性值 <input id="" /> ==> <input />
        removeScriptTypeAttributes: true,//删除<script>的type="text/javascript"
        removeStyleLinkTypeAttributes: true,//删除<style>和<link>的type="text/css"
        minifyJS: true,//压缩页面JS
        minifyCSS: true//压缩页面CSS
    };
    gulp.src('src/html/*.html')
        .pipe(htmlmin(options))
        .pipe(gulp.dest('dist/html'));
});
gulp.task('clean', function(cb) {
    del(['dist/css', 'dist/js'], cb)
});
gulp.task('default',['clean','css' ,'js' ,'images'], function() {
    // 将你的默认的任务代码放在这
});