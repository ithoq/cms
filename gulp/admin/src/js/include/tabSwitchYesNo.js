// jscs:disable maximumLineLength

/**
 * @description Plugin to display Yes/No Switch confirmation in a table.
 * @property {string}  tableElement      - Table ID.
 * @property {string}  containerElement  - Container class.
 * @property {function($tr)}  save  - Function performed during save confirmation.
 * @property {function($tr)}  delete  - Function performed during delete confirmation.
 * @property {function($tr)}  confirmation       - Default function executed upon confirmation.
 */
Cms.prototype.initTabSwitchYesNo = function (params) {
  var defaults = {
    tableElement: '#data-table',
    containerElement: '.op-table-slide-container',
  };
  var settings = $.extend({}, defaults, params);

  function changeClass($container, type) {
    if (type) {
      $container.removeClass('trash save');
      $container.addClass(type);
    }
  }

  $(settings.tableElement + ' tbody').on('click', '.js-move', function () {
    var $this = $(this);
    var $container = $this.closest(settings.containerElement);
    $container.toggleClass('js-switched');
    changeClass($container, $this.data('type'));
  });

  $(settings.tableElement + ' tbody').on('click', '.js-op-ajax', function () {
    var $this = $(this);
    var $container = $this.closest(settings.containerElement);
    var $parent = $this.parent();
    var $tr = $parent.closest('tr');
    if ($parent.hasClass('trash')) {
      settings.onDelete && settings.onDelete($tr);
    } else if ($parent.hasClass('save')) {
      settings.onSave && settings.onSave($tr);
    } else {
      settings.onConfirmation && settings.onConfirmation($tr);
    }

    $container.toggleClass('js-switched');
    changeClass($container, $this.data('type'));
  });
};

/**
 * @description Unbing the plugin event
 * @property {string}  tableElement      - Table ID.
 * @property {string}  containerElement  - Container class.
 */
Cms.prototype.destroyTabSwitchYesNo = function (params) {
  var defaults = {
    tableElement: '#data-table',
    containerElement: '.op-table-slide-container',
  };

  var settings = $.extend({}, defaults, params);
  $(settings.tableElement + ' tbody').off('click');
  $(settings.tableElement + ' tbody').off('click');
};

Cms.prototype.tabSwitchSimpleTpl = function () {
  return '<div class="op-table-slider-wrapper"><div class="op-table-slide-container"><div class="op-table-slide-item"><button id="js-show-controls" type="button" class="btn btn-default button js-move"><i class="fa fa-trash-o"> </i></button></div><div class="op-table-slide-item"><button type="button" class="btn btn-danger js-move operation"><i class="fa fa-times"></i></button><button type="button" class="btn btn-success js-op-ajax operation"> <i class="fa fa-check"></i></button></div></div></div>';
};

Cms.prototype.tabSwitchDoubleTpl = function () {
  return '<div class="op-table-slider-wrapper"><div class="op-table-slide-container"><div class="op-table-slide-item"><button type="button" data-type="trash" class="btn btn-default button js-move"><i class="fa fa-trash"></i></button><button type="button" data-type="save" class="btn btn-default button js-move"><i class="fa fa-save"> </i></button></div><div class="op-table-slide-item save"><button type="button" class="btn btn-danger js-move operation"><i class="fa fa-times"></i></button><button type="button" class="btn btn-success js-op-ajax operation"> <i class="fa fa-save"></i></button></div><div class="op-table-slide-item trash"><button type="button" class="btn btn-danger js-move operation"><i class="fa fa-times"></i></button><button type="button" class="btn btn-success js-op-ajax operation"> <i class="fa fa-trash"></i></button></div></div></div>';
};
