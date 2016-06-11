Cms.prototype.initFileUpload = function initFileUpload(params) {

  if (!params.$container) {
    alert('container element must be provided!');
    return;
  }

  var defaults = {
    // 50 MB
    maxSize: 50 * 1000  * 1000,
    formData: {},
    acceptFileTypes: false,
  };

  var options = $.extend({}, defaults, params);
  var $container = options.$container;
  var $ul = $container.find('ul').first();
  var $dropZone = $container.find('.dropzone').first();
  var $inputFile = $container.find('.fileupload').first();
  var $btnUpload = $dropZone.find('.btn-browse').first();

  $btnUpload.on('click', function () {
    $inputFile.click();
  });

  var jqMultiUploadConf = {
    dataType: 'json',
    formData: options.formData,

    // This function is called when a file is added to the queue;
    // either via the browse button, or via drag/drop:
    add: function (e, data) {

      // validation
      var file = data.originalFiles[0];
      var fileType = file.name.split('.').pop();
      if (options.maxSize) {
        if (file.size > options.maxSize) {
          $.Cms.notif({
            message: 'Max size is ' + $.Cms.formatFileSize(options.maxSize),
            type: 'error',
          });
          return;
        }
      }

      if (options.acceptFileTypes) {
        if (options.acceptFileTypes.test(fileType)) {
          $.Cms.notif({
            message: 'The file type is not allowed',
            type: 'error',
          });
          return;
        }
      }

      var tpl = $('<li class="working"><input type="text" value="0" data-width="18" data-height="18"'+
         ' data-fgColor="#0788a5" data-readOnly="1" data-bgColor="#3e4043" /><p></p><span></span></li>');

      // Append the file name and file size
      tpl.find('p').text(data.files[0].name)
                  .append('<i>' + $.Cms.formatFileSize(data.files[0].size) + '</i>');

      // Add the HTML to the UL element
      data.context = tpl.appendTo($ul);

      // Initialize the knob plugin
      tpl.find('input').knob();

      // Listen for clicks on the cancel icon
      tpl.find('span').click(function () {
        if (tpl.hasClass('working')) {
          jqXHR.abort();
        }

        tpl.fadeOut(function () {
          tpl.remove();
        });
      });

      // Automatically upload the file once it is added to the queue
      var jqXHR = data.submit();
    },

    progress: function (e, data) {
      // Calculate the completion percentage of the upload
      var progress = parseInt(data.loaded / data.total * 100, 10);

      // Update the hidden input field and trigger a change
      // so that the jQuery knob plugin knows to update the dial
      data.context.find('input').val(progress).change();

      if (progress == 100) {
        data.context.removeClass('working');
      }
    },

    fail: function (e, data) {
      // Something has gone wrong!
      data.context.addClass('error');
    },

    stop: function () {
      options.onStop && options.onStop();
    },

    /*progressall: function (e, data) {
      var progress = parseInt(data.loaded / data.total * 100, 10);
      if (progress == 100) {
        console.log('finish');
      }
    },*/

    // jscs:disable requireDollarBeforejQueryAssignment
    // This element will accept file drag/drop uploading
    dropZone: $dropZone,

    // jscs:enable requireDollarBeforejQueryAssignment
  };

  var dragEvent = function (e) {
    var $target = $(e.target);
    var $drop = $target.closest('.dropzone');
    var timeout = window.dropZoneTimeout;
    if (!timeout) {
      $drop.addClass('in');
    } else {
      clearTimeout(timeout);
    }

    var found = false;
    var node = e.target;
    do {
      if (node === $drop[0]) {
        found = true;
        break;
      }

      node = node.parentNode;
    } while (node !== null);
    if (found) {
      $drop.addClass('hover');
    } else {
      $drop.removeClass('hover');
    }

    window.dropZoneTimeout = setTimeout(function () {
      window.dropZoneTimeout = null;
      $drop.removeClass('in hover');
    }, 100);
  };

  $(document).bind('dragover', dragEvent);
  $($inputFile).fileupload(jqMultiUploadConf);
};
