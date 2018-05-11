<?php
//TODO: Respond to get request containing only an auth token with an array of <li> containing links to the pages this
//User is allowed to access based on their permission level
//Admins get Admin, Feeder, and Eater pages
//Eater gets only Eater page, ect
if ($_SERVER['REQUEST_METHOD'] == 'GET') 
{
  $perm = 0;
  if (isset($_GET['auth'])) 
	{
    $conn = getConn();
    if ($conn) 
    {
      $stmt = $conn->prepare("SELECT * FROM users WHERE auth_token = ?");  // Look for uid
      $stmt->bindValue(1, $_GET['auth'], PDO::PARAM_STR);
      try 
      {
        $stmt->execute();
      } catch (PDOException $e) {
        echo "Connection failed: " . $e->getMessage();
      }
      if ($stmt->rowCount() == 1) // Found the id
      {
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $perm = $row['perm'];
      }
    }
  }
  
  if ($perm > 0)
    echo '<li><a href="../announce/"></li>\n';
  if ($perm > 1)
    echo '<li><a href="../admin/"></li>\n';
    
    
  
  
}
else
  header("HTTP/1.0 405 MethodNotAllowed");
?>
