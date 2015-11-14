// load this script at the bottom of the search form html


/*$('#search').submit(function( event ) {
  var payload = $( this ).serialize();
  assistcallback.searchClicked(payload);
  event.preventDefault();
});*/


$('.sortable').click( function() {
    var prop = $(this).data("name");
    assistcallback.sortClick(prop);
} );

function sendWhisper(o) {
    var item = $(o).parents(".item");
    var bo = item.data("buyout") ? " listed for " + item.data("buyout") : "";
    var message = "@" + item.data("ign") + " Hi, I would like to buy your " + item.data("name") + bo + " in " + item.data("league");
    //window.prompt("Copy message to clipboard by pressing Ctrl+C", message);
    assistcallback.copyToClipboard(message);
}