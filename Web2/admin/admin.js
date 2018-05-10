var TABLE = $('#table');
var CLONE = $('#clone');
var id = 0;
//TODO: Get auth token
var auth;
$.fn.exists = function () {
	return this.length !== 0;
};

$.get("api/users/",
	{
		"auth": auth
	},
	handleResponse
);

function updateRow(id, data) {
	var row = $("#" + id);
	var exists = row.exists();
	//If the row doesn't exist yet, clone a new one
	if (!exists) {
		row = CLONE.find('tr.hide').clone(true).removeClass('hide table-line');
		row.attr('id', id);
	}
	//Modify the data of the row
	var col = row.find('td');
	col[0].innerText = data;
	//Add it to the table if it isn't already in the table
	if (!exists) {
		TABLE.find('table').append(row);
	}
}

function search() {
	var query = $("#usernameField").val();
	$.get("api/users/",
		{
			"search": query,
			"auth": auth
		},
		handleResponse
	);
}

function handleResponse(response) {
	var re = JSON.parse(response);
	for (var i = 0; i < re.length; i++) {
		var r = re[i];
		updateRow(r["id"], [r["name"], r["permission"]])
	}
}

function newUser() {
	var username = $("#usernameField").val();
	$.post("api/users",
		{
			"username": username,
			"permission": "0",
			"auth": auth
		},
		function (response) {
			id++;
			updateRow(id, username);
		}
	);
}
