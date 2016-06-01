function initFileUpload(params) {
  var defaults = {
    $container: '#upload-file',
    successMessage: 'Files were succesfully uploaded!',
    dropZoneElement: '#dropzone',
    inputFileElement: '#fileupload',
  };

  $(document).on('drop dragover', function (e) {
    e.preventDefault();
  });

  var options = $.extend({}, defaults, params);
  var $fileProgress = $('#file-progress');
  var $ul = $('#upload-file ul');
  var jqMultiUploadConf = {
    formData: { contentId: 2 },
    dataType: 'json',

    // This function is called when a file is added to the queue;
    // either via the browse button, or via drag/drop:
    add: function (e, data) {
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
      console.log('stop');
      $tableFiles.DataTable().ajax.reload();
      $fileProgress.find('.progress-bar').css('width', 0 + '%');
    },

    progressall: function (e, data) {
      var progress = parseInt(data.loaded / data.total * 100, 10);
      if (progress == 100) {
        console.log('finish');
      }
    },

    // jscs:disable requireDollarBeforejQueryAssignment
    // This element will accept file drag/drop uploading
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
