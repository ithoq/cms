function initFileUpload(params) {
  var defaults = {
    successMessage: 'Files were succesfully uploaded!',
    dropZoneElement: '#dropzone',
    inputFileElement: '#fileupload',
  };

  var options = $.extend({}, defaults, params);
  var $fileProgress = $('#file-progress');
  var jqMultiUploadConf = {
    dataType: 'json',
    stop: function () {
      // $('tr:has(td)').remove();
      $tableFiles.DataTable().ajax.reload();
      $fileProgress.find('.progress-bar').css('width', 0 + '%');
    },

    progressall: function (e, data) {
      var progress = parseInt(data.loaded / data.total * 100, 10);
      $fileProgress.find('.progress-bar').css(
        'width',
        progress + '%'
      );
    },

    error: function () {
      $.Cms.notif({
        type: 'error',
        message: this.genericErrorMessage,
      });
    },

    // jscs:disable requireDollarBeforejQueryAssignment
    dropZone: $('#dropzone'),

    // jscs:enable requireDollarBeforejQueryAssignment
  };

  var dragEvent = function (e) {
    var $dropZone = $(options.dropZoneElement);
    var timeout = window.dropZoneTimeout;
    if (!timeout) {
      $dropZone.addClass('in');
    } else {
      clearTimeout(timeout);
    }

    var found = false;
    var node = e.target;
    do {
      if (node === $dropZone[0]) {
        found = true;
        break;
      }

      node = node.parentNode;
    } while (node !== null);
    if (found) {
      $dropZone.addClass('hover');
    } else {
      $dropZone.removeClass('hover');
    }

    window.dropZoneTimeout = setTimeout(function () {
      window.dropZoneTimeout = null;
      $dropZone.removeClass('in hover');
    }, 100);
  };

  $(document).bind('dragover', dragEvent);
  $(options.inputFileElement).fileupload(jqMultiUploadConf)
      .bind('fileuploaddone');
}
