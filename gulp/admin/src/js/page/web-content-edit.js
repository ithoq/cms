$(function () {

  var $pageForm = $('#contentForm');
  var $tableGallery;
  var $tableFiles;
  $.Cms.addCsrfAjaxHeaderToken();
  //=include include/cms/datatables_event.js

  $('.upload-container').each(function (index, element) {
    var $el = $(element);
    var options = {};
    var formData = {};
    formData.type = $el.data('type');
    formData.contentId = $el.data('id');

    if (formData.type === 'GALLERY') {
      options.maxSize = 5 * 1000 * 1000;
      options.acceptFileTypes = /(\.|\/)(gif|jpe?g|png)$/i;
    }

    options.formData = formData;
    options.$container = $el;
    options.onStop = function () {
      var $el = $(element);
      $('#' + $el.data('table')).DataTable().ajax.reload();
    };

    $.Cms.initFileUpload(options);
  });

  var renderSelectLanguage = function (data) {
    var $state = $(
      '<span><img src="/resources/admin/img/flags/' + data.id.toLowerCase() + '.png" class="img-flag" /> ' + data.text + '</span>'
    );
    return $state;
  };

  $('#selectLanguage').select2({
    minimumResultsForSearch: -1,
    formatSelection: renderSelectLanguage,
    formatResult: renderSelectLanguage,
  });

  var datepickeroptions = {
    format: 'yyyy-mm-dd',
  };
  $('#dateEnd').datepicker(datepickeroptions);

  if ($('#dateBegin').val().length === 0) {
    var d = new Date();
    var currDate = ('0' + d.getDate()).slice(-2);
    var currMonth = ('0' + (d.getMonth() + 1)).slice(-2);
    var currYear = d.getFullYear();
    var parsedDate = currYear + '-' +  currMonth + '-' + currDate;
    $('#dateBegin').val(parsedDate);
  }

  $('#dateBegin').datepicker(datepickeroptions);

  var timepickerConfig = function (e) {
    var $widget = $('.bootstrap-timepicker-widget');
    $widget.find('.glyphicon-chevron-up').removeClass().addClass('pg-arrow_maximize');
    $widget.find('.glyphicon-chevron-down').removeClass().addClass('pg-arrow_minimize');
  };

  $('#dateTimeBegin').timepicker({
    showMeridian: false,
    defaultTime: 'current',
  }).on('show.timepicker', timepickerConfig);

  $('#dateTimeEnd').timepicker({
      defaultTime: false,
      showMeridian: false,
  }).on('show.timepicker', timepickerConfig);

  $('.timepicker').focus(function () {
    $(this).parent().find('span.input-group-addon').trigger('click');
  });

  $('#categories').tagEditor({
    placeholder: 'Enter categories ...',
    initialTags: initialCategories,
    autocomplete: { source: categories, minLength: 2, delay: 0 },
  });

  $('#tags').tagEditor({
    placeholder: 'Enter tags ...',
    initialTags: initialTags,
    autocomplete: { source: tags, minLength: 2, delay: 0 },
  });

  $.Cms.initTinyMce({ selector: '#content' });

  // Save a page
  $('#saveBtn').on('click', function () {
    tinyMCE.triggerSave();
    console.log('submit');
    $pageForm.submit();
  });
});
