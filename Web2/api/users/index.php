<?php

include '../auth_poster_functions.php';

switch ($_SERVER['REQUEST_METHOD']) {
	case 'POST':
		//TODO: Update users permissions
		//Parameters: username=lbenedetto permission=1 auth=authtoken
		//Put the username and permission in the database if the authtoken is from an admin
		break;
	case 'GET':
		//TODO: Return from database with search
		//Parameters: searchByName=lbene auth=authtoken
		//Parameters: searchByPermissions=1 auth=authtoken
		//If the authtoken is from an admin, return a list of users and their permissions from the database
		//If both search is blank, return the first 50 results
		//If search is (for example) "a", then return the first 50 results that start with a
		//If search is (for example) 1, then return all users with permissions level 1 (feeders I think is what you set that as)
		break;
}