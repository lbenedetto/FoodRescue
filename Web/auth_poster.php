<?php
// <!-- Some of this code comes from Berkely-->
//<!--https://calnetweb.berkeley.edu/calnet-technologists/cas/casifying-your-web-application-or-web-server/cas-code-samples/cas-->
$casService = 'https://login.ewu.edu/cas';
$thisService = 'http://foodrescue.ewu.edu' . $_SERVER['PHP_SELF'];
 
$p_ini = parse_ini_file("config.ini", true);
$servername = $p_ini['Database']['servername'];
$username = $p_ini['Database']['username'];
$password = $p_ini['Database']['password'];
$database = "databasetest";
switch($_SERVER['REQUEST_METHOD'])
{
	case 'POST':
		if (isset($_POST['title'])&&isset($_POST['body'])&&isset($_POST['data'])&&isset($_POST['auth']))
		{
			$conn = getConn();
			$stmt = $conn->prepare("SELECT * FROM users WHERE auth_token = ?");  // Look for uid
			$stmt->bindValue(1, $_POST['auth'], PDO::PARAM_STR);
			if ($stmt->rowCount() == 1) // Found the auth token
			{
				$row = $stmt->fetch(PDO::FETCH_ASSOC);
				if ($row['feeder_perm'] == 1)
				{
					sendRequest($_POST['title'], $_POST['body'], $_POST['data']);
					
				}
				else echo "Not allowed!";
			}
			//sendRequest($_POST['title'], $_POST['body'], $_POST['data']);
		}
		else
		{
			header("Location: $casService/login?service=$thisService");
		}
		break;
	case 'GET':
		if (isset($_GET['ticket']))
		{
			$response = responseForTicket($_GET["ticket"]);
			if (!$response)
				echo "no response for ticket<br>";
			else
			{
				$uid = extractUid($response);
				if (!$uid)
					echo "couldn't resolve UID<br>";
				else
				{
					$conn = getConn();
					if ($conn)
					{
						$stmt = $conn->prepare("SELECT * FROM users WHERE uname = ?");  // Look for uid
						$stmt->bindValue(1, $uid, PDO::PARAM_STR);
						$stmt->execute();
						if ($stmt->rowCount() == 1) // Found the id
						{
							$row = $stmt->fetch(PDO::FETCH_ASSOC);
							if(isset($row['auth_token'])) // already have an auth token on file for this uid
							{
								echo json_encode($row['auth_token']);
								
							}
							else // no auth token found, generate one and send it as JSON
							{
								$token = bin2hex(openssl_random_pseudo_bytes(64));
								$stmt = $conn->prepare("UPDATE users SET auth_token=?, WHERE uname = ?;");
								$stmt->bindValue(1, $token, PDO::PARAM_STR);
								$stmt->bindValue(2, $uid, PDO::PARAM_STR);
								$stmt->execute();
								echo json_encode($token);
								
							}
						}
						if ($stmt->rowCount() == 0) // if you don't find it, make a new entry
						{
							$token = bin2hex(openssl_random_pseudo_bytes(64));
							$stmt = $conn->prepare("INSERT INTO users (auth_token, uname, feeder_perm) VALUES (?, ?, 0);");
							$stmt->bindValue(1, $uid, PDO::PARAM_STR);
							$stmt->bindValue(2, $token, PDO::PARAM_STR);
							$stmt->execute();
							echo json_encode($token);
						}
						
					}
					
				}
			}
		}
		else
			header("Location: $casService/login?service=$thisService");
	default:
		header("HTTP/1.0 405 MethodNotAllowed");
		break;
}
function responseForTicket($ticket) {
   global $casService, $thisService;
 
   $casGet = "$casService/serviceValidate?ticket=$ticket&service=" . urlencode($thisService);
 
   // See the PHP docs for warnings about using this method:
   // http://us3.php.net/manual/en/function.file-get-contents.php
   $response = file_get_contents($casGet);
 
   if (preg_match('/cas:authenticationSuccess/', $response)) {
      return $response;
   }
   else {
      return false;
   }
}
 
/*
* Returns the UID from the passed in response, or it
* returns false if there is no UID.
*/
function extractUid($response) {
   // Turn the response into an array
   $responseArray = preg_split("/\n/", $response);
   // Get the line that has the cas:user tag
   $casUserArray = preg_grep("/(\d+)<\/cas:user>/", $responseArray);
   if (is_array($casUserArray)) {
      $uid = trim(strip_tags(implode($casUserArray)));
      if (is_numeric($uid)) {
         return $uid;
      }
   }
   return false;
}
function getConn()
{
	try 
	{
		$conn = new PDO("mysql:host=$servername;dbname=$database", $username, $password);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		return $conn;
	}
	catch(PDOException $e)
	{
		echo "Connection failed: " . $e->getMessage();
		return false;
	}
}
 
?>