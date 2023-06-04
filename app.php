<?php
$servername = "localhost";
$username = "user";
$password = "password";
$dbname = "hospital";

// Create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);

// Check Connection
if (!$conn) {
	die("Connection failed: " . $conn->connect_error);
}

// localhost:8000?register&email=email&password=password&username=username&phoneno=phoneno

// if register page is called
if (isset($_REQUEST['register'])) 
{
	$sql = sprintf("SELECT email FROM register where email='%s'", htmlspecialchars($_REQUEST["email"]));

	$result = mysqli_query($conn, $sql);
	if (mysqli_num_rows($result) == 0 && $_REQUEST["email"] != "") {

		$sql = sprintf("INSERT INTO register VALUES ('%s', '%s', '%s', '%s')", $_REQUEST['username'], $_REQUEST['email'], $_REQUEST['phoneno'], $_REQUEST['password']);

		mysqli_query($conn, $sql);
		echo "true";
	}
	else {
		echo "false";
	}
}

else if (isset($_REQUEST["login"]))
{
	$sql = sprintf("SELECT * FROM register where email='%s' AND password='%s'", 
		htmlspecialchars($_REQUEST["email"]), 
		htmlspecialchars($_REQUEST['password']));

	$result = mysqli_query($conn, $sql);
	if (mysqli_num_rows($result) == 1) {
		$row = mysqli_fetch_array($result, MYSQLI_ASSOC);
		printf ("true %s\n", json_encode($row));


	}
	else {
		echo "false";
	}

	mysqli_free_result($result);
}

if (isset($_REQUEST['test']))
{
	echo "Connected";
}


mysqli_close($conn);

?>


