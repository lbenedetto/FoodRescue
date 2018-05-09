var TABLE = $('#table');
var CLONE = $('#clone');
var id;
//TODO: Most of this code probably doesn't work, it was copy pasted from a CSCD378 assignment and has not yet been adapted
$.fn.exists = function () {
	return this.length !== 0;
};

$.get("api/users/", handleResponse);

function updateRow(id, data) {
	var row = $("#" + id);
	var exists = row.exists();
	if (!exists) {
		row = CLONE.find('tr.hide').clone(true).removeClass('hide table-line');
		row.attr('id', id);
	}
	var col = row.find('td');
	for (var i = 0; i < data.length; i++) {
		col[i + 1].innerText = data[i];
	}
	if (!exists) {//Add it to the table if it isn't already in the table
		TABLE.find('table').append(row);
	}
}

function search() {
	var query = $("#searchBar").val();
	$.get("admin.php/?search=" + query, handleResponse);
}

function handleResponse(response) {
	var re = JSON.parse(response);
	for (var i = 0; i < re.length; i++) {
		var r = re[i];
		updateRow(r["id"], [r["name"], r["permission"]])
	}
}