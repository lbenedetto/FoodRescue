//TODO: Add a little loading icon for all actions?
var TABLE = $('#table');
var CLONE = $('#clone');
var id = 0;

$.fn.exists = function () {
	return this.length !== 0;
};

$.get("../api/users/",
	{
		"auth_token": auth_token
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
	col[0].innerText = data[0];
	if (data[1] === 0)
		col[1].innerHTML = "<td class='wide'><label class='checkboxContainer' onchange='toggleCheckbox(this, " + id + ")'><input type='checkbox'><span class='checkmark'></span></label></td>";
	else
		col[1].innerHTML = "<td class='wide'><label class='checkboxContainer' onchange='toggleCheckbox(this, " + id + ")'><input type='checkbox' checked='checked'><span class='checkmark'></span></label></td>";
	//Add it to the table if it isn't already in the table
	if (!exists) {
		TABLE.find('table').append(row);
	}
}

function toggleCheckbox(box, id) {
	box.checked = !box.checked;
	var row = $("#" + id);
	var exists = row.exists();
	var saveButton = "<button id='saveButton' onclick='save(" + id + "," + box.checked + ")'><span>Save</span><img src='assets/save.png' height='15'></button>";
	//This should always be true
	if (exists) {
		var col = $(row.find('td')[1]);
		if (col[0].innerHTML.indexOf("<button") === -1) {
			col.append(saveButton);
			// col[1].innerHTML = col[1].innerHTML + saveButton;
		} else {
			col.find('button').remove();
		}
	}
}

function save(id, isChecked) {
	var row = $("#" + id);
	var col = row.find('td');
	var username = col[0].innerText;
	$.post("../api/users",
		{
			"username": username,
			"permission": isChecked,
			"auth_token": auth_token
		},
		function (response) {
			//TODO: Check if response was a success
			$(row.find('td')[1]).find('button').remove();
		}
	);
}

function search() {
	var query = $("#usernameField").val();
	$.get("../api/users/",
		{
			search: query,
			auth_token: auth_token
		},
		handleResponse
	);
}

function handleResponse(response) {
	var re = JSON.parse(response);
	for (var i = 0; i < re.length; i++) {
		var r = re[i];
		updateRow(i + 1, [r["name"], r["permission"]])
	}
}

function newUser() {
	var username = $("#usernameField").val();
	id++;
	updateRow(id, [username, 0]);
	$.post("../api/users",
		{
			"username": username,
			"permission": "0",
			"auth_token": auth_token
		},
		function (response) {
			//TODO: Check if it worked
			id++;
			updateRow(id, [username, 0]);
		}
	);
}
