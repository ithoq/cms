(function ($) {
  'use strict';

  // Cached variables
  var treeCache;
  var $selectType;
  var $modalCreateNewPage;
  var $tableFiles;
  var $tableGallery;
  var $pageForm;

  function updatePagePosition(pagesToUpdate, parentId) {
    // No need 'encodeURIComponent' because values are numeric
    var data = 'parentId=' + parentId;
    for (var i = 0; i < pagesToUpdate.length; i++) {
      data += '&id=' + pagesToUpdate[i].id;
      data += '&position=' + pagesToUpdate[i].position;
      data += '&level=' + pagesToUpdate[i].level;
    }
  
    $.Cms.ajax({
      url: '/admin/cms/page/updatePosition',
      type: 'POST',
      showSuccessMessage: false,
      errorMessage: 'The page could not be repositioned',
      data: data,
      onError: function () {
        treeCache.reload();
      },
    });
  }
  
  function reloadPage(id, _locale) {
    var options = {
        url: '/admin/cms/page/' + id,
        type: 'GET',
        formReset: false,
        showSuccessMessage: false,
        onSuccess: function (data) {
          // remove TinyMce Plugin
          $.Cms.removeAllTinyMce();
  
          // remove DataTable Plugin
          var $tables = $('table.dataTable');
          if ($tables.length > 0) {
            $tables.each(function (index, element) {
              $(element).DataTable().destroy();
            });
          }
  
          // remove Jquery Upload Plugin
          $('#fileupload').fileupload('destroy');
  
          $pageForm.html(data);
  
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
          $.Cms.initDynamicFields($pageForm);
        },
      };
  
    if (_locale) {
      options.data = { locale: _locale };
    }
  
    $.Cms.ajax(options);
  }
  
  function initTree() {
    $('#tree').fancytree({
      extensions: ['filter', 'dnd'],
      source: {
        url: '/admin/cms/tree',
        cache: 'true',
      },
      click: function (event, data) {
  
        if (data.targetType === 'title') {
          var node = data.node;
          reloadPage(node.key);
        }
      },
  
      quicksearch: true,
      filter: {
        autoApply: true, // Re-apply last filter if lazy data is loaded
        counter: true, // Show a badge with number of matching child nodes near parent icons
        fuzzy: false, // Match single characters in order, e.g. 'fb' will match 'FooBar'
        hideExpandedCounter: true, // Hide counter badge, when parent is expanded
        highlight: true, // Highlight matches by wrapping inside <mark> tags
        mode: 'hide', // Grayout unmatched nodes (pass "hide" to remove unmatched node instead)
      },
      dnd: {
        autoExpandMS: 400,
        focusOnClick: true,
        preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
        preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
        dragStart: function (node, data) {
          return true;
        },
  
        dragEnter: function (node, data) {
          // Prevent dropping a parent below another parent (only sort
          // nodes under the same parent)
          //if(node.parent !== data.otherNode.parent){
          //  return false;
          //}
          return true;
        },
  
        dragDrop: function (node, data) {
          if (data.hitMode === 'over') {
            parentId = node.key;
          } else {
            if (node.parent.getLevel() !== 0) {
              parentId = node.parent.key;
            }
          }
  
          data.otherNode.moveTo(node, data.hitMode);
          var parentId = data.otherNode.parent.getLevel() === 0 ? '' : data.otherNode.parent.key;
  
          //console.log("Son parent est " + parentId);
          //console.log("Tu as bougé le noeud " + data.otherNode.key + ' ' ;
          // + data.hitMode + " le noeud" + node.key);
          //console.log("Parent après " + parentId);
  
          var brother = data.otherNode.parent.getChildren();
          var pagesToUpdate = [];
          for (var i = 0, len = brother.length; i < len; i++) {
            var id = brother[i].key;
            var position = brother[i].getIndex();
            var level = brother[i].getLevel();
            pagesToUpdate[i] = {
              id: id,
              position: position,
              level: level,
            };
          }
  
          updatePagePosition(pagesToUpdate, parentId);
        },
      },
    });
  }
  

  $(function () {
    $.Cms.addCsrfAjaxHeaderToken();

    // init variables
    $pageForm = $('#pageForm');
    initTree();
    treeCache = $('#tree').fancytree('getTree');
    $selectType = $('#selectType');
    $modalCreateNewPage = $('#modalCreateNewPage');

    // Tree event
    $('#searchTree').keyup(function (e) {
      var n;
      var opts = {
        autoExpand: $('#autoExpand').is(':checked'),
        leavesOnly: $('#leavesOnly').is(':checked'),
      };
      var match = $(this).val();
    
      if (e && e.which === $.ui.keyCode.ESCAPE || $.trim(match) === '') {
        $('#btnResetSearch').click();
        return;
      }
    
      // Pass a string to perform case insensitive matching
      n = treeCache.filterNodes(match, opts);
    
      $('#btnResetSearch').attr('disabled', false);
      var result = ' resultat';
      if (n > 1) {
        result += 's';
      }
    
      $('#matches').text(n + result);
    }).focus();
    
    $('#btnResetSearch').click(function (e) {
      $('input[name=search]').val('');
      $('#matches').text('');
      treeCache.clearFilter();
    }).attr('disabled', true);
    
    // Delete a page
    $('#btnDeletePage').click(function () {
      var id = $('#contentDataId').val();
      var contentId = $('#contentId').val();
      var url = (id) ? '/admin/cms/page/delete/' + id : '/admin/cms/page/deleteContent/' + contentId;
      var params = {
        url: url,
        type: 'DELETE',
        successMessage: 'The page was deleted successfully!',
        onSuccess: function (data) {
          $.Cms.removeAllTinyMce(); // must do before dom is deleted (bug Firefox)
          if(data=== "empty"){
            $pageForm.empty();
          } else{
            reloadPage(contentId);
          }
          treeCache.reload();
        },
      };
      $.Cms.ajax(params);
    });
    
    // expand
    $pageForm.on('click', '#btn-expand', function () {
      $('#main-content').toggleClass('expanded');
    });
    
    // Save a page
    $pageForm.on('click', '#savePageBtn', function () {
      var id = $('#contentId').val();
      var dataId = $('#contentDataId').val();
      $.Cms.ajax({
        formJqueryElement: $pageForm,
        successMessage: 'The page was saved successfully!',
        appendToUrl: id,
        formReset: false,
        onSuccess: function () {
          treeCache.reload();
          reloadPage(id, $('#selectLanguage').val());
        },
      });
    });
    
    $pageForm.on('focusout', '#titleInput', function () {
      if($("#pageSlug").val().trim().length === 0){
        $("#pageSlug").val($.Cms.slugify($(this).val()));
      }
    });
    $pageForm.on('focusout', '#pageName', function () {
      if($("#titleInput").val().trim().length === 0){
          $("#titleInput").val($(this).val());
      }
      if($("#pageSlug").val().trim().length === 0){
        $("#pageSlug").val($.Cms.slugify($(this).val()));
      }
    });
    
    $pageForm.on('change', '#pageSlug', function () {
        $(this).val($.Cms.slugify($(this).val()));
    });
    
    $pageForm.on('click', '.creatlang', function () {
      reloadPage($('#contentId').val(), $(this).data('id'));
    });
    
    $pageForm.on('click', '#preview', function () {
      var $form = $(this).closest('form').clone();
      var $csrf = $('[name=_csrf]').first().clone();
      //$csrf.attr('id', 'csrf');
      $form.append($csrf);
      $form.attr('target', '_blank');
      $form.attr('action', '/admin/cms/preview');
      $form.submit();
    
      /*   var dataId = $('#contentDataId').val();
      window.open("/admin/cms/preview/" + dataId); 
      */
    });
    
    // change language
    $pageForm.on('change', '#selectLanguage', function () {
      reloadPage($('#contentId').val(), this.value);
    });
    
    // Create Page
    $modalCreateNewPage.on('click', '#btnFormCeatePage', function () {
      $.Cms.ajax({
        formElement: '#createPageForm',
        successMessage: 'The page was created successfully!',
        onSuccess: function (data, status, response) {
          if (response.getResponseHeader('Validation-Failed')) {
            $.Cms.notif({
              message: 'Validation error',
              type: 'error',
            });
            return;
          }
          $modalCreateNewPage.modal('hide');
          treeCache.reload();
        },
      });
    });
    
    // Open Modal Form
    $('#btnCreatePage').click(function () {
    
      var activeNode = treeCache.getActiveNode();
      $.Cms.ajax({
        url: '/admin/cms/modalCreate',
        type: 'get',
        data : { contentParentId : activeNode ? activeNode.key : null},
        onSuccess: function (data, status, response) {
          $modalCreateNewPage.html(data);
          $modalCreateNewPage.modal('show');
    
          var $elem = $modalCreateNewPage.find("[data-init-plugin='select2']");
          $elem.select2();
    
          $('#radio-root').on('click', function(){
            $elem.find("option").each(function() {
              $(this).prop('disabled', false);
            });
            $('#selectPageLang').select2();
          });
    
          $('#radio-parent').on('click', function(){
            $('#selectPageLang > option').each(function() {
              if($.inArray(this.value, modalCreateChildLangArray) === -1){
                $(this).prop('disabled', true);
              } else{
                $(this).prop('disabled', false);
              }
            });
            $('#selectPageLang').find('option:enabled:first').prop('selected',true);
            $('#selectPageLang').select2();
          });
        },
      });
    });
    
    $selectType.on('change', function () {
      var $selector = $modalCreateNewPage.find('selectHide');
      var anim = 200;
      if (this.value === 'Page') {
        $selector.find('.js-page').show(anim);
        $selector.find('.js-module').hide(anim);
      } else if (this.value === 'Module') {
        $selector.find('.js-page').hide(anim);
        $selector.find('.js-module').show(anim);
      } else {
        $selector.hide(anim);
      }
    });
    
    $pageForm.on('click', '#tabFileBtn', function () {
      var tableFileId = '#tableFiles';
      $tableFiles = $(tableFileId);
    
      if (!$.fn.DataTable.isDataTable($tableGallery)) {
        var options = {};
        options.elementId = tableFileId;
        options.$element = $tableFiles;
        options.type = 'D';
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
        options.type = 'G';
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
              return '<input required type="text" class="light-input fileName" value="' + data + '"/>';
            },
          },
          { // description
            aTargets: [1],
            mRender: function (data, type, full) {
              return '<textarea class="light-input">' + data + '</textarea>';
            },
          },
          { // group
            aTargets: [2],
            mRender: function (data, type, full) {
              return '<input required type="text" class="light-input fileGroup" value="' + data + '"/>';
            },
          },
          { // Active
            aTargets: [3],
            className: 'center',
          },
          { // Type
            aTargets: [4],
            className: 'center',
            mRender: function (data, type, full) {
              return '<img class="imgType" src="/resources/cms/img/files-icons/' +
                      data + '" />';
            },
          },
          { // Size
            aTargets: [5],
            className: 'center',
          },
          { // Operation
            aTargets: [6],
            className: 'center',
          },
        ],
        columns: [
          { data: 'name', },
          { data: 'description', },
          { data: 'group', },
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
          var $name = $tr.find('.fileName').first();
          var $group = $tr.find('.fileGroup').first();
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
            group: $group.val(),
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
    
  });
})(jQuery);
