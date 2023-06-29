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

	$stmt = mysqli_prepare($GLOBALS['conn'], "SELECT email, password FROM register where email=? AND password=?");
	mysqli_stmt_bind_param($stmt, 'ss', $email, $password);

	mysqli_stmt_execute($stmt);

	/* store the result in an internal buffer */
	mysqli_stmt_store_result($stmt);
	$count = mysqli_stmt_num_rows($stmt);

	// free memory for object associated
	// with stmt object
	mysqli_stmt_free_result($stmt);

	// verify user
	if ($count != 1)
		return false;
	return true;
}

// localhost:8000?register&email=email&password=password&username=username&phoneno=phoneno
//
// if register page is called
if (isset($_REQUEST['register'])) 
{
	$stmt = mysqli_prepare($conn, "SELECT email FROM register where email=?");
	mysqli_stmt_bind_param($stmt, 's', $_REQUEST['email']);

	mysqli_stmt_execute($stmt);

	// store the result in an internal buffer
	mysqli_stmt_store_result($stmt);

	if (mysqli_stmt_num_rows($stmt) == 0 && filter_var($_REQUEST["email"], FILTER_VALIDATE_EMAIL)) {


		$stmt1 = mysqli_prepare($conn, "INSERT INTO register VALUES (?, ?, ?, ?)");
		mysqli_stmt_bind_param($stmt1, 'ssss', 
			$_REQUEST['username'], 
			$_REQUEST['email'], 
			$_REQUEST['phoneno'], 
			$_REQUEST['password']);

		mysqli_stmt_execute($stmt1);
		echo "true";
	}
	else {
		echo "false";
	}
	mysqli_stmt_free_result($stmt);
}

else if (isset($_REQUEST["login"]))
{
	$stmt = mysqli_prepare($conn, "SELECT * FROM register where email=? AND password=?");
	mysqli_stmt_bind_param($stmt, 'ss', $_REQUEST['email'], $_REQUEST['password']);

	// execute query
	mysqli_stmt_execute($stmt);

	$result = mysqli_stmt_get_result($stmt);
	if ($row = mysqli_fetch_array($result, MYSQLI_ASSOC)) {
		printf ("true %s", json_encode($row));
	}
	else {
		echo "false";
	}
}

// TODO: https://stackoverflow.com/questions/18383182/mysql-table-with-a-varchar-column-as-foreign-key
else if (isset($_REQUEST['hospital']))
{
	if (userExists()) {

		$sql = "SELECT bloodgroup, COUNT(*) as total FROM hospital GROUP BY bloodgroup";
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
		$stmt = mysqli_prepare($conn, "SELECT hospital, bloodgroup FROM receiver where dateofreceive=?");
		mysqli_stmt_bind_param($stmt, 's', date('Y-m-d'));

		mysqli_stmt_execute($stmt);

		$emergencyneeded = array();
		$result = mysqli_stmt_get_result($stmt);
		while ($row = mysqli_fetch_array($result, MYSQLI_ASSOC))
			$emergencyneeded[$row["hospital"]] = $row["bloodgroup"];

		printf("true %s", json_encode($emergencyneeded));
	}
	else { echo "false"; }
}

else if (isset($_REQUEST['donor']))
{
	echo $sql;
	if (userExists()) {
		$stmt = mysqli_prepare($conn, "INSERT INTO donor(bloodgroup, email, hospital, dateofsubmit) SELECT ?, email, ?, ? FROM register  WHERE email=?");
		mysqli_stmt_bind_param($stmt, "ssss", $_REQUEST['bloodgroup'], $_REQUEST['hospitalname'], $_REQUEST['dateofsubmit'], $_REQUEST['email']);
		mysqli_stmt_execute($stmt);
	}
}

else if (isset($_REQUEST['receiver']))
{
	if (userExists()) {
		$stmt = mysqli_prepare($conn, "INSERT INTO receiver(bloodgroup, email, hospital, dateofreceive) SELECT ?,email, ?, ? FROM register  WHERE email=?");
		mysqli_stmt_bind_param($stmt, "ssss", $_REQUEST['bloodgroup'], $_REQUEST['hospitalname'], $_REQUEST['dateofreceive'], $_REQUEST['email']);
		mysqli_stmt_execute($stmt);
	}
}

else if (isset($_REQUEST['reset']))
{
	if (userExists())
	{
		$stmt = mysqli_prepare($conn, "update register set password=? where email=?");
		mysqli_stmt_bind_param($stmt, "ss", $_REQUEST['newpassword'], $_REQUEST['email']);
		mysqli_stmt_execute($stmt);
	}
}

else if (isset($_REQUEST['test']))
{
	echo "Connected";
}

mysqli_close($conn);
?>


