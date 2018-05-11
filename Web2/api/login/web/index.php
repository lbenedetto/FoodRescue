<?php

include '../../auth_poster_functions.php';

if ($_SERVER['REQUEST_METHOD'] == 'GET')
{		
	
	//echo "got to case get";
	if (isset($_GET['ticket']))
	{
		echo $_GET['ticket'];
		$response = responseForTicket($_GET['ticket'], "web");
		if (!$response)
			echo "no response for tickett<br>";
		else
		{
			$uid = extractUid($response);
			if (!$uid)
				echo "couldn't resolve UID<br>";
			else
			{
				//echo "UID resolved<br>";
				$conn = getConn();
				if ($conn)
				{
					$stmt = $conn->prepare("SELECT * FROM foodrescue.users WHERE uname = ?");  // Look for uid
					$stmt->bindValue(1, $uid, PDO::PARAM_STR);
					try
					{
						$stmt->execute();
					}
					catch (PDOException $e)
					{
						echo "Connection failed: " . $e->getMessage();
					}
					if ($stmt->rowCount() == 1) // Found the id
					{
						$row = $stmt->fetch(PDO::FETCH_ASSOC);
						if(isset($row['auth_token'])) // already have an auth token on file for this uid
						{
							echo json_encode($row['auth_token']);
							header("Location: https://146.187.135.29/web/map.php?token=" . $row['auth_token'] . "&username=" . $uid);
						}
						else // no auth token found, generate one and send it as JSON
						{
							$token = bin2hex(openssl_random_pseudo_bytes(64));
							$stmt = $conn->prepare("UPDATE users SET auth_token=? WHERE uname = ?;");
							$stmt->bindValue(1, $token, PDO::PARAM_STR);
							$stmt->bindValue(2, $uid, PDO::PARAM_STR);
							$stmt->execute();
							echo json_encode($token);
							header("Location: https://146.187.135.29/web/map.php?token=" . $token . "&username=" . $uid);
						}
					}
					if ($stmt->rowCount() == 0) // if you don't find it, make a new entry
					{
						echo "in rowcount";
						$token = bin2hex(openssl_random_pseudo_bytes(64));
						echo $token;
						try
						{
						$stmt = $conn->prepare("INSERT INTO users (auth_token, uname, perm) VALUES (?, ?, 0);");
						$stmt->bindValue(2, $uid, PDO::PARAM_STR);
						$stmt->bindValue(1, $token, PDO::PARAM_STR);
						$stmt->execute();
						echo json_encode($token);
						header("Location: https://146.187.135.29/web/map.php?token=" . $token . "&username=" . $uid);
						}
						catch (PDOException $e)
						{
							echo "Query failed: " . $e->getMessage();
						}
					}
					
				}
				
			}
		}
	}
	else
	{
		header("Location: https://login.ewu.edu/cas/login?service=https://146.187.135.29/web/login");
	}
}
else
{
		header("HTTP/1.0 405 MethodNotAllowed");
}

 
?>
