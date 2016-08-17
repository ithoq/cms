(function ($) {
  'use strict';

  // Cached variables
  var treeCache;
  var $selectType;
  var $modalCreateNewPage;
  var $tableFiles;
  var $tableGallery;
  var $pageForm;

  //=include include/cms/page_function.js
  //=include include/cms/tree_function.js

  $(function () {
    $.Cms.addCsrfAjaxHeaderToken();

    // init variables
    $pageForm = $('#pageForm');
    initTree();
    treeCache = $('#tree').fancytree('getTree');
    $selectType = $('#selectType');
    $modalCreateNewPage = $('#modalCreateNewPage');

    //=include include/cms/tree_event.js
    //=include include/cms/page_event.js
    //=include include/cms/datatables_event.js
  });
})(jQuery);
