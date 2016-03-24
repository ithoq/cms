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
