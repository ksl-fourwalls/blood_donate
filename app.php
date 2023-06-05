<?php

$servername = "localhost";
$username = "user";
$password = "password";
$dbname = "hospital";

// TODO: Make Resistant from SQL Injection
// Create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);

// Check Connection
if (!$conn) {
	die("Connection failed: " . mysqli_connect_error());
}

function userExists()
{
	$email = $_REQUEST["email"];
	$password = $_REQUEST["password"];

	if ($email == "" && $password == "")
		return false;

	// check for existance of password
	$sql = sprintf("SELECT email, password FROM register where email='%s' AND password='%s'", 
		$email, 
		$password);

	// verify user
	$result = mysqli_query($GLOBALS['conn'], $sql);
	if (mysqli_num_rows($result) != 1)
		return false;
	return true;
}

// localhost:8000?register&email=email&password=password&username=username&phoneno=phoneno
//
// if register page is called
if (isset($_REQUEST['register'])) 
{
	$sql = sprintf("SELECT email FROM register where email='%s'", htmlspecialchars($_REQUEST["email"]));

	$result = mysqli_query($conn, $sql);
	if (mysqli_num_rows($result) == 0 && $_REQUEST["email"] != "") {

		$sql = sprintf("INSERT INTO register VALUES ('%s', '%s', '%s', '%s')", 
			$_REQUEST['username'], 
			$_REQUEST['email'], 
			$_REQUEST['phoneno'], 
			$_REQUEST['password']);

		mysqli_query($conn, $sql);
		echo "true";
	}
	else {
		echo "false";
	}
	mysqli_free_result($result);
}

else if (isset($_REQUEST["login"]))
{
	$sql = sprintf("SELECT * FROM register where email='%s' AND password='%s'", 
		htmlspecialchars($_REQUEST["email"]), 
		htmlspecialchars($_REQUEST['password']));

	$result = mysqli_query($conn, $sql);
	if (mysqli_num_rows($result) == 1) {
		$row = mysqli_fetch_array($result, MYSQLI_ASSOC);
		printf ("true %s", json_encode($row));
	}
	else {
		echo "false";
	}
	mysqli_free_result($result);
}

// TODO: https://stackoverflow.com/questions/18383182/mysql-table-with-a-varchar-column-as-foreign-key
else if (isset($_REQUEST['hospital']))
{
	if (userExists()) {
		$sql = sprintf("SELECT bloodgroup, COUNT(*) as total FROM hospital GROUP BY bloodgroup");
		$result = mysqli_query($conn, $sql);

		$bloodgroupdata = array();
		if (mysqli_num_rows($result) > 0)
		{
			$rows = mysqli_fetch_all($result, MYSQLI_ASSOC);
			foreach ($rows as $row) {
				$bloodgroupdata[$row["bloodgroup"]] = $row["total"];
			}
		}
		printf("true %s", json_encode($bloodgroupdata));
	}
	else {
		echo "false";
	}
}

else if (isset($_REQUEST['emergency']))
{
	if (userExists())
	{
		$sql = sprintf("SELECT hospital, bloodgroup FROM receiver where dateofreceive='%s'", date('Y-m-d')); 
		$result = mysqli_query($conn, $sql);

		$emergencyneeded = array();

		if (mysqli_num_rows($result) > 0)
		{
			$rows = mysqli_fetch_all($result, MYSQLI_ASSOC);
			foreach($rows as $row) {
				$emergencyneeded[$row["hospital"]] = $row["bloodgroup"];
			}
		}
		printf("true %s", json_encode($emergencyneeded));
	}
	else { echo "false"; }
}

else if (isset($_REQUEST['donor']))
{
	if (userExists()) {
		$bloodgroup = htmlspecialchars($_REQUEST['bloodgroup']);

		$sql = sprintf("INSERT INTO donor(bloodgroup, email, hospital, dateofsubmit) SELECT '%s',email, '%s', '%s' FROM register  WHERE email='%s'",
			$_REQUEST['bloodgroup'], $_REQUEST['hospitalname'], $_REQUEST['dateofsubmit'], $_REQUEST['email']);
		mysqli_query($conn, $sql);
	}
}

else if (isset($_REQUEST['receiver']))
{
	if (userExists()) {
		$sql = sprintf("INSERT INTO receiver(bloodgroup, email, hospital, dateofreceive) SELECT '%s',email, '%s', '%s' FROM register  WHERE email='%s'",
			$_REQUEST['bloodgroup'], $_REQUEST['hospitalname'], $_REQUEST['dateofreceive'], $_REQUEST['email']);
		mysqli_query($conn, $sql);
	}
}

// update hospital table
else if (isset($_REQUEST['adminuser']))
{

}

else if (isset($_REQUEST['test']))
{
	echo "Connected";
}

mysqli_close($conn);
?>


