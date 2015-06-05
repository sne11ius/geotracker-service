(function (document) {
  
  var template = document.querySelector('#t');
  template.pages = [
    {name: 'Map', hash: 'map', icon: 'maps:map'},
    {name: 'Calendar', hash: 'calendar', icon: 'today'},
    {name: 'Settings', hash: 'settings', icon: 'settings'},
  ];
  
  template.menuItemSelected = function(e, detail/*, sender*/) {
    if (detail.isSelected) {
      if (this.$) {
        this.$.scaffold.closeDrawer();
      }
    }
  };
  
  var DEFAULT_ROUTE = 'map';
  
  template.addEventListener('template-bound', function() {
    this.route = this.route || DEFAULT_ROUTE;
  });
})(wrap(document));
