$(function () {

  var $pageForm = $('#userForm');
  $.Cms.addCsrfAjaxHeaderToken();

  var datepickeroptions = {
    format: 'yyyy-mm-dd',
  };
  $('#birthday').datepicker(datepickeroptions);

  var timepickerConfig = function (e) {
    var $widget = $('.bootstrap-timepicker-widget');
    $widget.find('.glyphicon-chevron-up').removeClass().addClass('pg-arrow_maximize');
    $widget.find('.glyphicon-chevron-down').removeClass().addClass('pg-arrow_minimize');
  };

  $('#group').select2();
});
