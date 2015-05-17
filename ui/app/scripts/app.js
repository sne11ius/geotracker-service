(function (document) {
  var template = document.querySelector('#t');
  template.pages = [
    {name: 'Map', hash: 'map', icon: 'maps:map'},
    {name: 'Calendar', hash: 'calendar', icon: 'today'},
    {name: 'settings', hash: 'settings', icon: 'settings'},
  ];
  
  template.menuItemSelected = function(e, detail/*, sender*/) {
    if (detail.isSelected) {
      this.$ && this.$.scaffold.closeDrawer();
    }
  };
  
  var DEFAULT_ROUTE = 'map';
  
  template.addEventListener('template-bound', function(e) {
    this.route = this.route || DEFAULT_ROUTE;
  });
})(wrap(document));
