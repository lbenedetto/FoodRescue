<?php
include '../auth_poster_functions.php';

switch ($_SERVER['REQUEST_METHOD']) {
	//TODO: Make sure this code works the same for adding a new phone number to a user, or updating an existing phone number
	case 'POST':
		if (isset($_POST['number']) && isset($_POST['carrier']) && isset($_POST['uname'])) {
			$conn = getConn();
			$stmt = $conn->prepare("INSERT INTO users (uname, number, carrier) VALUES (?, ?, ?)");  // Look for uid
			$stmt->bindValue(1, $_POST['uname'], PDO::PARAM_STR);
			$stmt->bindValue(2, $_POST['number'], PDO::PARAM_STR);
			$stmt->bindValue(3, $_POST['carrier'], PDO::PARAM_STR);
			try {
				$stmt->execute();
			} catch (PDOException $e) {
				echo "Connection failed: " . $e->getMessage();
			}
		}
		exit;
	default:
		header("HTTP/1.0 405 MethodNotAllowed");
		break;
}
