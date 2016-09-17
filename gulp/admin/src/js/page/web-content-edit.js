$(function () {

  var $pageForm = $('#contentForm');
  var $tableGallery;
  var $tableFiles;
  var initialThemes = window.initialThemes;
  var initialTags = window.initialTags;
  var tags = window.tags;
  var themes = window.themes;

  // back link
  var currentType = $('#types').val();
  if(currentType){
      $('#back').attr('href', "/admin/webContent?type=" + currentType);
  }

  $.Cms.addCsrfAjaxHeaderToken();
  //=include include/cms/datatables_event.js

  $.Cms.initDynamicFields($pageForm);
  
  $('.upload-container').each(function (index, element) {
    var $el = $(element);
    var options = {};
    var formData = {};
    formData.type = $el.data('type');
    formData.contentId = $el.data('id');

    if (formData.type === 'G') {
      options.maxSize = 2 * 1000 * 1000;
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
      '<span><img src="/resources/cms/img/flags/' + data.id.toLowerCase() + '.png" class="img-flag" /> ' + data.text + '</span>'
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

  $('#themes').tagEditor({
    placeholder: 'Enter themes ...',
    initialTags: initialThemes,
    forceLowercase: false,
    autocomplete: { source: themes, minLength: 1, delay: 0 },
    beforeTagSave: function(field, editor, tags, tag, val){
      if(!match(themes, val)) return false;
    },
  });

  $('#tags').tagEditor({
    placeholder: 'Enter tags ...',
    initialTags: initialTags,
    forceLowercase: false,
    autocomplete: { source: tags, minLength: 1, delay: 0 },
    /*beforeTagSave: function(field, editor, tags, tag, val){
      if(!match(window.tags, val)) return false;
    },*/
  });

  function match(array, needle){
    var match = false;
    for(var i = 0; i < array.length; i++) {
      if(array[i] === needle) {
        match = true;
      }
    }
    return match;
  }

  $.Cms.initTinyMce({ selector: '#content' });

  // Save a page
  $('#savePageBtn').on('click', function () {
    if($('#contentForm').parsley()){
     tinyMCE.triggerSave();
      $pageForm.submit();
    }
  });

  $("#titleInput").focusout(function(){
    if($("#pageSlug").val().trim().length === 0){
      $("#pageSlug").val('/' + $.Cms.slugify($(this).val()));
    }
    if($("#pageName").val().trim().length === 0){
      $("#pageName").val($(this).val());
    }
  });

  $("#pageName").focusout(function(){
    if($("#titleInput").val().trim().length === 0){
      $("#titleInput").val($(this).val());
    }
    if($("#pageSlug").val().trim().length === 0){
      $("#pageSlug").val('/' + $.Cms.slugify($(this).val()));
    }
  });

  $("#pageSlug").change(function(){
    var val = $(this).val();
    if(val.length> 0 ){

      if(val.charAt(0) === '/'){
        val = val.substring(1, val.length);
      }
      
      $(this).val('/' + $.Cms.slugify(val));
    }
  });

  /*
  $pageForm.on('click', '#preview', function () {
    var $form = $(this).closest('form').clone();
    console.log($form);
    var $csrf = $('[name=_csrf]').first().clone();
    $form.append($csrf);
    $form.attr('target', '_blank');
    $form.attr('action', '/admin/webContent/preview');

    $form.submit();
  });*/

  $pageForm.on('click', '.creatlang', function () {
    var re = /lang=[a-z]{2}(_[A-Z]{2})?&?/; 
    var lang = $(this).data('id');
    var contain = (window.location.href.indexOf('?') > -1)
    var result = window.location.href.replace(/lang=[a-z]{2}(_[A-Z]{2})?&?/, "");
    if(!contain){
      result = result + '?';
    } else{
      if(result.slice(-1) !== '&'){
        result + '&';
      }
    }
    document.location.href= result + "lang=" + lang;
  });

  // change language
  $pageForm.on('change', '#selectLanguage', function () {
    var re = /lang=[a-z]{2}(_[A-Z]{2})?&?/; 
    var lang = this.value;
    var contain = (window.location.href.indexOf('?') > -1)
    var result = window.location.href.replace(/lang=[a-z]{2}(_[A-Z]{2})?&?/, "");
    if(!contain){
      result = result + '?';
    } else{
      if(result.slice(-1) !== '&'){
        result + '&';
      }
    }
    document.location.href= result + "lang=" + lang;
  });

  // Delete a page
  $('#btnDeletePage').click(function () {
    var id = $('#contentDataId').val();
    var contentId = $('#contentId').val();
    var url = (id) ? '/admin/cms/page/delete/' + id : '/admin/cms/page/deleteContent/' + contentId;
    var params = {
      url: url,
      type: 'DELETE',
      onSuccess: function (data) {
        var str = window.location.href;
        var n = str.lastIndexOf("/");
        var contentType= encodeURIComponent($('#types').val());
        var result = str.substring(0, n).replace("/edit", "");
        result += "?type=" + contentType;
        document.location.href=result;
      },
    };
    $.Cms.ajax(params);
  });

});
