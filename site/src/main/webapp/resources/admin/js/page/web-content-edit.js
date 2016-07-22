$(function () {

  var $pageForm = $('#contentForm');
  var $tableGallery;
  var $tableFiles;
  $.Cms.addCsrfAjaxHeaderToken();
  $pageForm.on('click', '#tabFileBtn', function () {
    var tableFileId = '#tableFiles';
    $tableFiles = $(tableFileId);
  
    if (!$.fn.DataTable.isDataTable($tableGallery)) {
      var options = {};
      options.elementId = tableFileId;
      options.$element = $tableFiles;
      options.type = 'DOWNLOAD';
      options.searchElementId = '#search-table-files';
  
      initDataTable(options);
      initSwitchYesNo(options);
    }
  });
  
  $pageForm.on('click', '#tabGalleryBtn', function () {
    var tableGalleryId = '#tableGallery';
    $tableGallery = $(tableGalleryId);
    if (!$.fn.DataTable.isDataTable($tableGallery)) {
      var options = {};
      options.elementId = tableGalleryId;
      options.$element = $tableGallery;
      options.type = 'GALLERY';
      options.searchElementId = '#search-table-gallery';
  
      initDataTable(options);
      initSwitchYesNo(options);
    }
  });
  
  function initDataTable(options) {
    var $contentId = $('#contentDataId');
    $.Cms.initDataTableWithSearch({
      tableJqueryElement: options.$element,
      searchElement: options.searchElementId,
      ajax: {
        url: '/admin/file/getJson/' + $contentId.val(),
        data: {
          type: options.type,
        },
      },
      appendOperationColumns: 'double',
      columnDefs: [
        { // name
          aTargets: [0],
          mRender: function (data, type, full) {
            return '<input required type="text" class="light-input" value="' + data + '"/>';
          },
        },
        { // description
          aTargets: [1],
          mRender: function (data, type, full) {
            return '<textarea class="light-input">' + data + '</textarea>';
          },
        },
        { // Active
          aTargets: [2],
          className: 'center',
        },
        { // Type
          aTargets: [3],
          className: 'center',
          mRender: function (data, type, full) {
            return '<img class="imgType" src="/resources/admin/img/files-icons/' +
                    data + '" />';
          },
        },
        { // Size
          aTargets: [4],
          className: 'center',
        },
        { // Operation
          aTargets: [5],
          className: 'center',
        },
      ],
      columns: [
        { data: 'name', },
        { data: 'description', },
        { data: 'active', },
        { data: 'type', },
        { data: 'size', },
      ],
    });
  }
  
  function initSwitchYesNo(options) {
    $.Cms.initTabSwitchYesNo({
      tableElement: options.elementId,
      onDelete: function ($tr) {
        var id = $tr.data('id');
        $.Cms.ajax({
          url: '/admin/file/delete/' + id,
          type: 'DELETE',
          successMessage: 'File deleted successfully!',
          onSuccess: function () {
            //$tr.hide(200, function () { $tr.remove(); });
            options.$element.DataTable().ajax.reload();
          },
        });
      },
  
      onSave: function ($tr) {
        var id = $tr.data('id');
        var $name = $tr.find('input').first();
        var $desc = $tr.find('textarea').first();
        $.Cms.trimAllInputs($tr);
        $name.prop('required', true);
        var validationName = $name.parsley();
        validationName.reset();
        validationName.validate();
        $name.prop('required', false);
        if ($name.val() === '') {
          return;
        }
  
        var data = {
          id: id,
          name: $name.val(),
          description: $desc.val(),
        };
        $.Cms.ajax({
          url: '/admin/file/edit',
          type: 'POST',
          data: data,
          successMessage: 'File edited successfully!',
        });
      },
    });
  }
  

  $.Cms.initDynamicFields($pageForm);
  
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
