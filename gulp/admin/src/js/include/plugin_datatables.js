/**
 * @description Function to help to init the datables Jquery Plugin.
 */

Cms.prototype.dataTableRenderCheckboxDisabled = function (data, type, full) {
  var isChecked = data === true ? 'checked' : '';
  return '<input disabled type="checkbox" class="checkbox" ' + isChecked + ' />';
};

Cms.prototype.dataTableRenderCheckbox = function (data, type, full) {
  var isChecked = data === true ? 'checked' : '';
  return '<input type="checkbox" class="checkbox" ' + isChecked + ' />';
};

Cms.prototype.dataTableRenderBoolean = function (data, type, full) {
  // text-success
  var css;
  if (data === true) {
    css = ' text-success';
  } else {
    css = ' style="opacity:0.15"';
  }

  return '<i class="fa fa-check-circle' + css + '></i>';
};

Cms.prototype.initDataTableWithSearch = function (params) {
  var defaults = {
    hasSearchInput: true,
    iDisplayLength: 5,
    tableElement: '#data-table',
    searchElement: '#search-table',
  };

  var options = $.extend({}, defaults, params);

  var columnDefsNoSort = {
    targets: 'no-sort',
    orderable: false,
  };

  var columnDefs = [columnDefsNoSort];
  if (options.columnDefs) {
    options.columnDefs.unshift(columnDefsNoSort);
    columnDefs = options.columnDefs;
  }

  var settings = {
    sDom: '<\'table-responsive\'t><\'row\'<p i>>',
    order: [],
    autoWidth: false,
    destroy: true,
    scrollCollapse: true,
    iDisplayLength: options.iDisplayLength, //rows to display on a single page (pagination)
    columnDefs: columnDefs,
  };

  var locale = options.locale || this.locale;
  if (locale) {
    settings.language = {
      url: '/resources/admin/libs/dataTables/i18n/' +
           languagesNativeName[locale].capitalizeFirstLetter() + '.json',
    };
  }

  if (options.ajax) {
    settings.ajax = options.ajax;

    if (options.columns) {
      if (options.appendOperationColumns === 'simple') {
        options.columns.push({
          data: null,
          defaultContent: this.tabSwitchSimpleTpl(),
        });
      } else if (options.appendOperationColumns === 'double') {
        options.columns.push({
          data: null,
          defaultContent: this.tabSwitchDoubleTpl(),
        });
      }

      settings.columns = options.columns;
    }
  }

  var $table;
  if (options.tableJqueryElement) {
    $table = options.tableJqueryElement;
  } else {
    $table = $(options.tableElement);
  }

  $table.dataTable(settings);

  if (options.hasSearchInput) {
    $(options.searchElement).keyup(function () {
      $table.fnFilter($(this).val());
    });
  }

  return $table;
};
