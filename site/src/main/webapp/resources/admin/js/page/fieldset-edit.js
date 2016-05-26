$(function () {

  var $inputUl = $('#inputUl');
  var $addButton = $('#addBtnInputs');
  var $inputContent = $('#inputContent');
  var $modal = $('#modalCreateNewFieldset');
  var inputCounter = 0;
  var removeBtnHtml = '<span class="removeField" id="removeButton"><i class="fa fa-minus"></i></span>';
  var aceEditor;

  function addNewInput(input) {
    inputCounter++;
    input.counter = inputCounter;
    input.style = (inputCounter === 1) ? 'display:block;' : '';
    $li = $('<li class="input-li" data-div="' +
        input.counter + '">' +
        input.name +
        (input.existingInput ? '' : removeBtnHtml) + '</li>');
    $li.animate2Css('flipInX');
    $inputUl.append($li);

    input.editorName = $.Cms.slugify(input.name);
    var template = document.getElementById('inputsTemplate').innerHTML;
    var html = Mustache.render(template, input);

    $inputContent.append(html);

    if (input.type) {
      var $select = $('.input-div[data-id=' + input.id + ']').find('.typeSelect');
      $select.val(input.type);
    }

  }

  function getInput(id) {
    var input = {};
    $('#input-' + id).find('input').each(function () {
      input[this.name] = this.value;
    });

    return input;
  }

  $inputUl.sortable({
    revert: true,
  });

  $addButton.on('click', function () {
    var name = 'input ' + (inputCounter + 1);
    var input = {
      id: 0,
      name: name,
    };

    addNewInput(input);
  });

  $inputContent.on('input', '.js-input-name', function () {
    var $input = $(this);
    var id = $input.data('id');
    var $li = $inputUl.find('[data-div="' + id + '"]').first();
    $li.html(this.value).append(removeBtnHtml);

    var $editorName = $('#editor-name-' + id);
    $editorName.html($.Cms.slugify(this.value));

  });

  $inputUl.on('click', '.input-li', function () {
    var id = $(this).data('div');
    $inputUl.children().removeClass('activeInput');
    $(this).addClass('activeInput');
    $inputContent.children().hide();
    $inputContent.find('#input-' + id).first().show();
  });

  $inputUl.on('click', '.removeField', function () {
    var $li = $(this).parent('li');
    var divId = $li.data('div');
    $li.animate2Css('flipOutX', function () {
      $li.remove();
    });

    $inputContent.find('#input-' + divId).remove();
  });

  $inputContent.on('click', '.editor-name', function () {
      $.Cms.copyToClipboard(this);
      $.Cms.notif({
        message: 'Copied to the clipboard',
        type: 'success',
      });
  });

  $('#submitBtn').on('click', function (e) {
    e.preventDefault();

    $inputUl.children().each(function (i) {
      var $inputDiv = $('#input-' + $(this).data('div'));
      $inputDiv.find('input[name=\'order\']').val(i + 1);

      var $validation = $inputDiv.find('input[name=\'inputsValidation\']');
      if ($validation.val().trim() === '') {
        $validation.val('null');
      }
    });

    var checkbox = $('.checkbox-array').each(function () {
      var $this = $(this);
      if ($this.prop('checked')) {
        var id = $this.data('id');
        $('#checkbox-hidden-' + id).prop('disabled', true);
      }
    });

    $('#editor').val(aceEditor.getSession().getValue().trim());

    document.getElementById('fieldsetForm').submit();
  });

  // init  (edition)
  if (window.inputs) {

    for (var i = 0; i < inputs.length; i++) {
      inputs[i].existingInput= true;
      addNewInput(inputs[i]);
    }
  }

  // ace editor
  aceEditor = $.Cms.initAceEditor();
});
