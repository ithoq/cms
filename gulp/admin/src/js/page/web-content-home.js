(function ($) {
  'use strict';

  // Cached variables
  var $table;

  $(function () {
    $.Cms.addCsrfAjaxHeaderToken();


    $('.js-create-webcontent').on('click', function(e){
        e.preventDefault();
        var href = $(this).attr('href') + '&type=' + $('#types').val();
        document.location.href=href;
    });
    
    // init variables
    $table = $('#webContentTable');
  /*
    var isPrivate = $.Cms.getParameterByName('contentPrivate');

    var param = {
        lang: $.Cms.getParameterByName('lang'),
        theme: $.Cms.getParameterByName('theme'),
        tag: $.Cms.getParameterByName('tag'),
        type: $.Cms.getParameterByName('type'),
        contentPrivate: isPrivate,
        year: $.Cms.getParameterByName('year')
    };*/
    
    $.Cms.initDataTableWithSearch({
      tableJqueryElement: $table,
      searchElement: '#web-content-table',
      ajax: {
        url: '/admin/webContent/getJson',
        contentType: 'application/json',
        data: {
          params : JSON.stringify(window.params),

        },
      },
      columnDefs: [
        { // Active
          aTargets: [0],
          className: 'center',
          render: $.Cms.dataTableRenderBoolean,
        },
        { // Lang
          aTargets: [2],
          className: 'center',
        },
        { // Lang
          aTargets: [3],
          className: 'center',
        },
        { // Lang
          aTargets: [4],
          className: 'center',
        },
        { // Edit
          aTargets: [5],
          className: 'center',
        },
        { // Operation
          aTargets: [6],
          className: 'center',
        },

      ],
      columns: [
        { data: 'active', },
        { data: 'title', },
        { data: 'lang', },
        { data: 'dateBegin', },
        { data: 'dateEnd', },
        {
          data: null,
          defaultContent: '<button type="button" class="btn btn-default ' +
                          'btn-modal-edit"><i class="fa fa-pencil"></i></button>',
        },
        {
          data: null,
          defaultContent: $.Cms.tabSwitchSimpleTpl(),
        },
      ],
    });

    $table.on('click', '.btn-modal-edit', function () {
      var id = $(this).closest('tr').data('id');
      var lang = $(this).closest('tr').data('lang');

      var type = $("#types").val();
      var typeStr = '';
      if(type){
        typeStr = 'type=' + type + '&';
      }
      document.location.href = '/admin/webContent/edit/' +  id + '?' + typeStr +'lang=' + lang;
    });

    $.Cms.initTabSwitchYesNo({
      tableElement: '#webContentTable',
      onConfirmation: function ($tr) {
        deleteblock($tr.data('id'));
        //console.log('delete');
      },
    });

    function deleteblock(id) {
      $.Cms.ajax({
        type: 'DELETE',
        url: '/admin/cms/page/deleteContent/' + id,
        successMessage: 'User deleted successfully',
        onSuccess: function (data) {
          $table.DataTable().ajax.reload();
        },
      });
    }

      var renderSelectLanguage = function (data) {
        var $state = $(
          '<span><img src="/resources/cms/img/flags/' + data.id.toLowerCase() + '.png" class="img-flag" /> ' + data.text + '</span>'
        );
        return $state;
      };

      $('#languages').select2({
        minimumResultsForSearch: -1,
        formatSelection: renderSelectLanguage,
        formatResult: renderSelectLanguage,
      });

  });
})(jQuery);
