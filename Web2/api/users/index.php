<?php

include '../auth_poster_functions.php';

switch ($_SERVER['REQUEST_METHOD']) {
	case 'POST':
		if (isset($_POST['auth']))
			//make sure the auth comes from the admin
		else
		{
			echo "Only the admin can make changes to the users in this way.";
			break;
		}
		if (isset($_POST['permission']))
			$perm = $_POST['permission'];
		else
			$perm = 0;
		if (isset($_POST['username'])) 
		{
			$token = bin2hex(openssl_random_pseudo_bytes(64));
			$conn = getConn();
			$stmt = $conn->prepare("INSERT INTO users (uname, auth_token, feeder_perm) VALUES (?, ?, ?)");  
			$stmt->bindValue(1, $_POST['username'], PDO::PARAM_STR);
			$stmt->bindValue(2, $token, PDO::PARAM_STR);
			$stmt->bindValue(3, $_POST['permission'], PDO::PARAM_STR);
			try {
				$stmt->execute();
			} catch (PDOException $e) {
				echo "Connection failed: " . $e->getMessage();
			}
		}
		else
			echo "No username specified.";
		
		// $token = bin2hex(openssl_random_pseudo_bytes(64));
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
