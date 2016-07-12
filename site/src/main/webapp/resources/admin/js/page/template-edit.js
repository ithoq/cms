$(function () {

  $.Cms.addCsrfAjaxHeaderToken();

  var $select2 = $('#fieldsets');
  var removeBtnHtml = '<span class="removeField"><i class="fa fa-minus"></i></span>';
  var liTemplate = '<li class="liTab{{cssClass}}" data-div="{{counter}}"><a data-toggle="tab" href="#{{counter}}"><span>{{name}}</span></a></li>';
  var $inputUl = $('#inputUl');
  var counter = 0;
  var $inputContent = $('#inputContent');
  var data = [];
  var aceEditor;
  var editorIncludeTop;
  var editorIncludeBottom;

  function loadFieldsetData() {
    for (var i = 0; i < window.fieldsetDef.length; i++) {
      data.push({ id: window.fieldsetDef[i].name, text: window.fieldsetDef[i].name });
    }
  }

  function findFieldsetDefByName(name) {
    var fieldset = {};
    for (var i = 0; i < window.fieldsetDef.length; i++) {
      if (window.fieldsetDef[i].name === name) {
        return window.fieldsetDef[i];
      }
    }
  }

  function addLi(name) {
    $li = $('<li class="input-li" data-div="' + counter + '">' + name + removeBtnHtml + '</li>');
    $li.animate2Css('flipInX');
    $inputUl.append($li);
  }

  function addNewInput(fieldsetName) {
    counter++;
    var fieldset = findFieldsetDefByName(fieldsetName);

    var fieldsetLabel = fieldset.name + '-' + counter;

    var cssClass = '';
    var renderedLi = '';
    var renderedInputs = '';
    var inputsTemplate = document.getElementById('inputsTemplate').innerHTML;

    addLi(fieldsetLabel);

    for (var i = 0, s = fieldset.inputs.length; i < s; i++) {

      var inputcounter = counter + '' + i;
      cssClass = '';
      if (i === 0) {
        cssClass = ' active';
      }

      renderedLi += Mustache.render(liTemplate, {
        cssClass: cssClass,
        counter: inputcounter,
        name: fieldset.inputs[i].name,
      });

      renderedInputs += Mustache.render(inputsTemplate, {
        cssClass: cssClass,
        counter: inputcounter,
        inputDefId: fieldset.inputs[i].id,
      });
    }

    var template = document.getElementById('templateView').innerHTML;
    var html = Mustache.render(template, {
      counter: counter,
      style: (counter === 1) ? 'display:block;' : '',
      fieldsetId: fieldset.id,
      templateFieldsetId: 0,
      renderedLi: renderedLi,
      canBeAnArray: fieldset.array,
      renderedInputs: renderedInputs,
      name: fieldsetLabel,
    });
    $inputContent.append(html);
  }

  function addExistingInput(templateFieldset) {
    counter++;
    var cssClass = '';
    var renderedLi = '';
    var renderedInputs = '';
    var inputsTemplate = document.getElementById('inputsTemplate').innerHTML;

    addLi(templateFieldset.name);

    for (var i = 0, s = templateFieldset.dataEntities.length; i < s; i++) {

      var inputcounter = counter + '' + i;
      cssClass = '';
      if (i === 0) {
        cssClass = ' active';
      }

      renderedLi += Mustache.render(liTemplate, {
        cssClass: cssClass,
        counter: inputcounter,
        name: templateFieldset.dataEntities[i].inputDefinition.name,
      });

      if (templateFieldset.dataEntities[i].validation.length === 0) {
        templateFieldset.dataEntities[i].validation = templateFieldset.dataEntities[i].inputDefinition.validation;
      }

      renderedInputs += Mustache.render(inputsTemplate, {
        cssClass: cssClass,
        counter: inputcounter,
        inputDefId: templateFieldset.dataEntities[i].inputDefinition.id,
        data: templateFieldset.dataEntities[i],
      });
    }

    var template = document.getElementById('templateView').innerHTML;
    var html = Mustache.render(template, {
      counter: counter,
      style: (counter === 1) ? 'display:block;' : '',
      fieldsetId: templateFieldset.fieldset.id,
      templateFieldsetId: templateFieldset.id,
      canBeAnArray: templateFieldset.fieldset.array,
      isArray: templateFieldset.array ,
      name: templateFieldset.name,
      np: templateFieldset.namespace,
      renderedLi: renderedLi,
      renderedInputs: renderedInputs,
    });
    $inputContent.append(html);
  }

  $inputContent.on('input', '.js-input-name', function () {
    var $input = $(this);
    var id = $input.data('id');
    var $li = $inputUl.find('[data-div="' + id + '"]').first();
    $li.html(this.value).append(removeBtnHtml);

  });

  $('#addBtnInputs').on('click', function () {
    var data = $select2.val();
    if (data && data !== '0') {
      addNewInput(data);
    }
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

  $('#submitBtn').click(function () {

    var fieldsetList = [];
    var inputasDataList = [];
    var names = [];
    var contentFieldsetId = [];
    var np = [];
    var array = [];
    $inputUl.children().each(function () {

      var $div = $('#input-' + $(this).data('div'));
      var $tabs = $div.find('.tab-pane');
      var fieldsetItem = {};
      fieldsetItem.id = $div.find('.fieldset-id').val();
      fieldsetList.push(fieldsetItem);
      var inputDataListTemp = [];
      $tabs.each(function () {

        var iData = {};
        var inputDefinition = {};
        var $this = $(this);

        var inputDataId = $this.find('.inputDataId').val();
        iData.id = (inputDataId ? inputDataId : 0);
        iData.validation = $this.find('.input-valid').val();
        iData.title = $this.find('.input-title').val();
        iData.hint = $this.find('.input-hint').val();
        iData.defaultValue = $this.find('.input-defaultValue').val();
        iData.array = $this.find('.checkbox-array').is(':checked');
        var inputDefId = $this.find('.inputDefId').val();
        inputDefinition.id = (inputDefId ? inputDefId : 0);
        iData.inputDefinition = inputDefinition;
        inputDataListTemp.push(iData);
      });

      var contentFieldsetIdItem = $div.find('.templateFieldsetId').val();
      contentFieldsetId.push(contentFieldsetIdItem ? contentFieldsetIdItem : 0);
      names.push($('#name-' + $(this).data('div')).val());
      var $isArray = $('#checkbox-' + $(this).data('div'));
      if ($isArray.length !== 0) {
        array.push($isArray.prop('checked'));
      } else {
        array.push(false);
      }

      np.push($('#np-' + $(this).data('div')).val());
      inputasDataList.push(inputDataListTemp);
    });

    var pageTemplate = {};
    pageTemplate.id = ($('#template-id').val()) ?  $('#template-id').val() : 0;
    pageTemplate.name = $('#templateName').val();
    pageTemplate.description = $('#fieldsetDescription').val();
    pageTemplate.includeTop = editorIncludeTop.getSession().getValue().trim();
    pageTemplate.includeBottom = editorIncludeBottom.getSession().getValue().trim();
    pageTemplate.active = $('#active').is(':checked');

    var blockData = {};
    blockData.name =  $('#blockName').val();
    blockData.displayName = $('#blockDisplayName').val();
    blockData.content = aceEditor.getSession().getValue().trim();

    $.Cms.ajax({
      type: 'POST',
      url: '/admin/contentTemplate/edit',
      data: {
        names: JSON.stringify(names),
        namespaces: JSON.stringify(np),
        arrays: JSON.stringify(array),
        template: JSON.stringify(pageTemplate),
        fieldsets: JSON.stringify(fieldsetList),
        inputsData: JSON.stringify(inputasDataList),
        contentFieldsetId: JSON.stringify(contentFieldsetId),
        blockData: JSON.stringify(blockData),
      },
      successMessage: 'The template was saved successfully!',
      onSuccess: function (e) {
        document.location.href = '/admin/contentTemplate/edit/' + e;
      },
    });
  });

  // Init
  if (window.fieldsetData) {

    for (var i = 0; i < window.fieldsetData.length; i++) {
      addExistingInput(window.fieldsetData[i]);
    }
  }

  loadFieldsetData();

  $select2.select2({
    data: data,
    placeholder: window.placeholder,
  });

  $inputUl.sortable({
    revert: true,
  });

  aceEditor = $.Cms.initAceEditor();

  editorIncludeTop = $.Cms.initAceEditor({
      selector: '#ace-editor-top',
      theme: 'ace/theme/chrome',
      mode: 'ace/mode/html',
  });

  editorIncludeBottom = $.Cms.initAceEditor({
      selector: '#ace-editor-bottom',
      theme: 'ace/theme/chrome',
      mode: 'ace/mode/html',
  });

});
